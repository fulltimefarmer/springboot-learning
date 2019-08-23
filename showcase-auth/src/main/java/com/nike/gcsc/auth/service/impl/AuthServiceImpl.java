package com.nike.gcsc.auth.service.impl;

import com.google.common.collect.Maps;
import com.nike.gcsc.auth.constant.AuthConstant;
import com.nike.gcsc.auth.constant.JwtConstant;
import com.nike.gcsc.auth.dto.*;
import com.nike.gcsc.auth.model.okta.Jwt;
import com.nike.gcsc.auth.model.okta.OktaIntrospect;
import com.nike.gcsc.auth.model.okta.OktaToken;
import com.nike.gcsc.auth.repository.*;
import com.nike.gcsc.auth.service.AuthService;
import com.nike.gcsc.auth.utils.HttpUtils;
import com.nike.gcsc.auth.utils.JwtUtils;
import com.nike.gcsc.auth.utils.Md5Utils;
import com.nike.gcsc.auth.utils.SnowFlake;
import com.nike.gcsc.authapi.response.GroupDto;
import com.nike.gcsc.authapi.response.PermissionDto;
import com.nike.gcsc.authapi.response.UserPermissionDto;
import com.nike.gcsc.util.BeanMapper;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.*;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class AuthServiceImpl implements AuthService {

	@Value("${okta.introspect.url}")
	private String introspectUrl;

	@Value("${okta.token.url}")
	private String tokenUrl;

	@Autowired
	private RedisTemplate<String, Object> redisTemplate;

	@Autowired
	TokenRepository tokenRepository;

	@Autowired
	OktaRepository oktaRepository;

	@Autowired
	UserRepository userRepository;

	@Autowired
	GroupRepository groupRepository;

	@Autowired
	PermissionRepository permissionRepository;

	@Override
	public Long add(@NonNull Okta param) {
		if (null != param.getId()) {
			param.setId(null);
		}
		Okta saved = oktaRepository.save(param);
		return saved.getId();
	}

	@CacheEvict(value = "oktaCache", allEntries = true, cacheManager="caffeineCache")
	@Override
	public void delete(@NonNull Long id) {
		oktaRepository.deleteById(id);
	}

	@CacheEvict(value = "oktaCache", allEntries = true, cacheManager="caffeineCache")
	@Override
	public void deleteBatchByIdList(List<Long> ids) {
		oktaRepository.deleteBatchByIdList(ids);
	}

	@Override
	public void edit(@NonNull Okta param) {
		oktaRepository.save(param);
	}

	@Override
	public Page<Okta> findAll(@NonNull Integer pageNumber, @NonNull Integer pageSize) {
		Pageable pageable = PageRequest.of(pageNumber, pageSize);
		return oktaRepository.findAll(pageable);
	}

	@Override
	public Page<Okta> findByAttributes(@NonNull Okta queryBean,
									   @NonNull Integer pageNumber,
									   @NonNull Integer pageSize) {
		ExampleMatcher matcher = ExampleMatcher.matchingAll();
		Example<Okta> example = Example.of(queryBean, matcher);
		Pageable pageable = PageRequest.of(pageNumber, pageSize);
		return oktaRepository.findAll(example, pageable);
	}

    @Cacheable(value = "oktaCache",key = "#p0", cacheManager="caffeineCache",unless = "#result==null")
    @Override
    public Okta findByClientId(String clientId) {
        return oktaRepository.findByClientId(clientId);
    }

    @Override
	public String getTokenByPwd(String username, String pwd, Long projectId) {
		String token = "";
		try {
			// check username&password
			if (!validateUsernameAndPassword(username, pwd)) {
				return token;
			}
			Jwt jwt = generateAuthJwt(username);
			token = JwtUtils.createToken(jwt);
			Token tokenEntity = new Token();
			tokenEntity.setUsername(username);
			tokenEntity.setProjectId(projectId);
			tokenEntity.setToken(token);
			tokenEntity.setCreateTime(Instant.now().getEpochSecond());
			tokenEntity.setExpireTime(jwt.getExp());
			tokenRepository.save(tokenEntity);
			UserPermissionDto userPermissionDto = getUserPermissionDtoByUsernameAndProject(username, projectId);
			userPermissionDto.setExt(jwt.getExp());
			// store into Redis
			redisTemplate.opsForValue().set(AuthConstant.TOKEN_REDIS_PREFIX + token, userPermissionDto, JwtConstant.JWT_ACTIVE_SECOND, TimeUnit.SECONDS);
			return token;
		} catch (Exception e) {
			log.error("OktaServiceImpl:getTokenByPwd:{}:{}", username, e.getMessage());
			return token;
		}
	}

	private Boolean validateUsernameAndPassword(String requestUsername, String requestPassword) {
		if(redisTemplate.hasKey(requestUsername) && (Integer)redisTemplate.opsForValue().get(requestUsername) > 5) {
			return Boolean.FALSE;
		}
		User user = new User();
		user.setUsername(requestUsername);
		user.setPassword(Md5Utils.encryptPassword(requestPassword, Md5Utils.SECRET_KEY));
		ExampleMatcher matcher = ExampleMatcher.matchingAll();
		Example<User> example = Example.of(user, matcher);
		long count = userRepository.count(example);
		if(count!=1l) {
			//Only 5 logins are allowed in 5 minutes
			if(!redisTemplate.hasKey(requestUsername)) {
				redisTemplate.opsForValue().set(requestUsername, 0);
				redisTemplate.expire(requestUsername, 5, TimeUnit.MINUTES);
			}
			redisTemplate.opsForValue().increment(requestUsername);
			return Boolean.FALSE;
		}
		redisTemplate.delete(requestUsername);
		return Boolean.TRUE;
	}

	private Jwt generateAuthJwt(String username) {
		Long now = Instant.now().getEpochSecond();
		Jwt jwt = new Jwt();
		jwt.setIss(JwtConstant.JWT_ISSUER);
		jwt.setSub(username);
		jwt.setAud(JwtConstant.JWT_AUDIENCE);
		jwt.setExp(now + JwtConstant.JWT_ACTIVE_SECOND);
		jwt.setNbf(0L);
		jwt.setIat(now);
		jwt.setJti(String.valueOf(SnowFlake.nextId()));
		return jwt;
	}

	@Override
	public String getTokenByCode(Okta okta, String redirectUrl, String code) {
		String token = "";
        try {
			OktaToken oktaToken = loadToken(code, okta);
			token = oktaToken.getAccessToken();
			OktaIntrospect oktaIntrospect = loadOktaIntrospect(token, okta);
			Token tokenEntity = new Token();
			tokenEntity.setUsername(oktaIntrospect.getUsername());
			tokenEntity.setId(okta.getProjectId());
			tokenEntity.setToken(token);
			tokenEntity.setCreateTime(Instant.now().getEpochSecond());
			tokenEntity.setExpireTime(oktaIntrospect.getExp());
			tokenRepository.save(tokenEntity);
			UserPermissionDto userPermissionDto = getUserPermissionDtoByUsernameAndProject(oktaIntrospect.getUsername(), okta.getProjectId());
			userPermissionDto.setExt(oktaIntrospect.getExp());
			// store into Redis
			redisTemplate.opsForValue().set(AuthConstant.TOKEN_REDIS_PREFIX + token, userPermissionDto, JwtConstant.JWT_ACTIVE_SECOND, TimeUnit.SECONDS);
			return token;
		} catch (Exception e) {
			log.error("OktaServiceImpl:getTokenByCode:{}", e);
			return token;
		}
	}

	private OktaToken loadToken(String code, Okta okta) {
		//TODO change to RestTemplate
		OktaToken oktaToken = new OktaToken();
		try{
			Map<String, String> header = Maps.newHashMapWithExpectedSize(2);
			Map<String, String> body = Maps.newHashMapWithExpectedSize(5);
			header.put(AuthConstant.OKTA_CONTENT_TYPE, "application/x-www-form-urlencoded");
			body.put(AuthConstant.OKTA_CLIENT_ID, okta.getClientId());
			body.put(AuthConstant.OKTA_CLIENT_SECRET, okta.getSecret());
			body.put(AuthConstant.OKTA_GRANT_TYPE, AuthConstant.OKTA_AUTHORIZATION_CODE);
			body.put(AuthConstant.OKTA_CODE, code);
			body.put(AuthConstant.OKTA_REDIRECT_URI, okta.getRedirectUrl());
			oktaToken = HttpUtils.httpPost(tokenUrl, header, body, OktaToken.class);
			return oktaToken;
		} catch (InstantiationException | IllegalAccessException e){
			log.error("OktaServiceImpl:loadToken:{}:{}", code, e.getMessage());
			return oktaToken;
		}
	}

	private OktaIntrospect loadOktaIntrospect(String token, Okta okta) {
		//TODO change to RestTemplate
		log.debug("the okta  info is :"+okta + " and : token"+token);
		OktaIntrospect oktaIntrospect = new OktaIntrospect();
		try{
			Map<String, String> header = Maps.newHashMapWithExpectedSize(1);
			Map<String, String> body = Maps.newHashMapWithExpectedSize(4);
			header.put(AuthConstant.OKTA_CONTENT_TYPE, AuthConstant.OKTA_CONTENT_TYPE_VALUE);
			body.put(AuthConstant.OKTA_CLIENT_ID, okta.getClientId());
			body.put(AuthConstant.OKTA_CLIENT_SECRET, okta.getSecret());
			body.put(AuthConstant.OKTA_GRANT_TYPE, AuthConstant.OKTA_CLIENT_CREDENTIALS);
			body.put(AuthConstant.OKTA_TOKEN, token);
			oktaIntrospect = HttpUtils.httpPost(introspectUrl, header, body, OktaIntrospect.class);
		} catch (InstantiationException | IllegalAccessException e){
			log.error("OktaServiceImpl:getIntrospect:{}:{}", token, e.getMessage());
			return oktaIntrospect;
		}
		return oktaIntrospect;
	}

	private UserPermissionDto getUserPermissionDtoByUsernameAndProject(String username, Long projectId){
		List<Group> groupList = groupRepository.findByProjectidAndUsername(projectId, username);
		List<GroupDto> groupDtoList = BeanMapper.mapList(groupList, GroupDto.class);
		List<Permission> permissionList = permissionRepository.findByProjectidAndUsername(projectId, username);
		List<PermissionDto> permissionDtoList = BeanMapper.mapList(permissionList, PermissionDto.class);
		return UserPermissionDto.builder().username(username).groupList(groupDtoList).permissionList(permissionDtoList).build();
	}

	@Override
	public UserPermissionDto getUserPermissionByToken(String token) {
		UserPermissionDto result = (UserPermissionDto)redisTemplate.opsForValue().get(AuthConstant.TOKEN_REDIS_PREFIX + token);
		if(null == result){
			Token t = tokenRepository.findByToken(token);
			if(null != t){
				result = getUserPermissionDtoByUsernameAndProject(t.getUsername(), t.getProjectId());
			}
		}
		return result;
	}

	@Override
	public Boolean reflashTokenCache(String secret, int second) {
		Instant now = Instant.now();
		String key = "superNova" + LocalDate.now().getDayOfMonth();
		if(key.equals(secret)){
			List<Token> tokenList = tokenRepository.findByCreateTime(now.minusSeconds(second).getEpochSecond());
			tokenList.stream()
				.filter(token -> token.getExpireTime() > Instant.now().getEpochSecond())
				.forEach(
					token -> {
						UserPermissionDto userPermissionDto = getUserPermissionDtoByUsernameAndProject(token.getUsername(),token.getProjectId());
						redisTemplate.opsForValue().set(AuthConstant.TOKEN_REDIS_PREFIX + token.getToken(), userPermissionDto, token.getExpireTime()-Instant.now().getEpochSecond(), TimeUnit.SECONDS);
					}
			);
		}
		return Boolean.TRUE;
	}
	
}
