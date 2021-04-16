package com.nike.gcsc.auth.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.google.common.base.Objects;
import com.google.common.collect.Maps;
import com.nike.gcsc.auth.constant.AuthConstant;
import com.nike.gcsc.auth.constant.JwtConstant;
import com.nike.gcsc.auth.entity.*;
import com.nike.gcsc.auth.model.okta.Jwt;
import com.nike.gcsc.auth.model.okta.OktaIntrospect;
import com.nike.gcsc.auth.model.okta.OktaToken;
import com.nike.gcsc.auth.repository.*;
import com.nike.gcsc.auth.service.AuthService;
import com.nike.gcsc.auth.utils.HttpUtils;
import com.nike.gcsc.auth.utils.JwtUtils;
import com.nike.gcsc.auth.utils.Md5Utils;
import com.nike.gcsc.auth.utils.SnowFlake;
import com.nike.gcsc.authapi.request.ServiceTokenRquestBean;
import com.nike.gcsc.authapi.response.GroupDto;
import com.nike.gcsc.authapi.response.PermissionDto;
import com.nike.gcsc.authapi.response.UserPermissionDto;
import com.nike.gcsc.util.BeanMapper;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.*;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.time.Instant;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author mzho15
 *
 */
@Slf4j
@Service
public class AuthServiceImpl implements AuthService {

	@Value("${okta.introspect.url}")
	private String introspectUrl;

	@Value("${okta.token.url}")
	private String tokenUrl;
	
	@Value("${session.timeout.seconds:3600}")
	private long userTokenTimeout;

	@Autowired
	private RestTemplate restTemplate;

	@Autowired
	private RedisTemplate<String, Object> redisTemplate;
	
	@Resource(name="stringRedisTemplate")
	private RedisTemplate<String, String> stringRedisTemplate;

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
    
    @Cacheable(value = "oktaCache",key = "#p0", cacheManager="caffeineCache",unless = "#result==null")
    @Override
    public Okta findByDescribe(String describe) {
        return oktaRepository.findByDescribe(describe);
    }

	@Override
	public List<Okta> findByProjectName(String projectName) {
		return oktaRepository.findByProjectName(projectName);
	}
	
    @Override
	public String getTokenByPwd(String username, String pwd, Long projectId) {
		String token = "";
		log.info("getTokenByPwd method begin, user is: {}, projectId is :{}", username,projectId);
		try {
		    User user = validateUsernameAndPassword(username, pwd);
			// check username&password
			if (null == user) {
				return token;
			}
	        if(!AuthConstant.USER_STATUS.NORMAL.equals(user.getStatus())) {
	            log.warn("user:{} status is: {}", username, user.getStatus());
	            return AuthConstant.USER_DISABLE;
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
			redisTemplate.opsForValue().set(AuthConstant.TOKEN_REDIS_PREFIX + token, userPermissionDto, userTokenTimeout, TimeUnit.SECONDS);
			return token;
		} catch (Exception e) {
			log.error("OktaServiceImpl:getTokenByPwd:"+username, e);
			return token;
		}
	}

	@Override
	public String getTokenByCode(Okta okta, String redirectUrl, String code) {
		String token = null;
        Long now = Instant.now().getEpochSecond();
		OktaToken oktaToken = loadToken(code, redirectUrl, okta);
		if(null == oktaToken || StringUtils.isBlank(oktaToken.getAccessToken())) {
		    return token;
		}
		token = oktaToken.getAccessToken();
		OktaIntrospect oktaIntrospect = loadOktaIntrospect(token, okta);
		if(null == oktaIntrospect) {
		    return null;
		}
		String username = oktaIntrospect.getUsername().toLowerCase();
		User user = userRepository.findByUsername(username);
		if(!AuthConstant.USER_STATUS.NORMAL.equals(user.getStatus())) {
            log.warn("user:{} status is: {}", username, user.getStatus());
            return AuthConstant.USER_DISABLE;
        }
		Token tokenEntity = new Token();
		tokenEntity.setUsername(username);
		tokenEntity.setProjectId(okta.getProjectId());
		tokenEntity.setToken(token);
		tokenEntity.setCreateTime(Instant.now().getEpochSecond());
		log.info("AuthServiceImpl:getTokenByCode:oktaIntrospect:{}", oktaIntrospect.toString());
		// tokenEntity.setExpireTime(oktaIntrospect.getExp());
		tokenEntity.setExpireTime(now + userTokenTimeout);
		tokenRepository.save(tokenEntity);
		UserPermissionDto userPermissionDto = getUserPermissionDtoByUsernameAndProject(oktaIntrospect.getUsername(), okta.getProjectId());
		// userPermissionDto.setExt(oktaIntrospect.getExp());
		userPermissionDto.setExt(now + userTokenTimeout);
		// store into Redis
		redisTemplate.opsForValue().set(AuthConstant.TOKEN_REDIS_PREFIX + token, userPermissionDto, userTokenTimeout, TimeUnit.SECONDS);
		return token;
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
		log.info(String.format("the result permission is : %s", result));
		return result;
	}

	@Override
	public String getTokenBySecret(String clientId, String secret) {
		String token = "";
		log.debug("getTokenBySecret method begin, clientId is: {}, secret is :{}", clientId, secret);
		try {
			// check clientId&secret
			if (!validateClientIdAndSecret(clientId, secret)) {
				return token;
			}
			Jwt jwt = generateAuthJwt(clientId);
			token = JwtUtils.createToken(jwt);
			return token;
		} catch (Exception e) {
			log.error("OktaServiceImpl:getTokenBySecret:" + clientId, e);
			return token;
		}
	}

	@Override
	public Boolean validateServiceToken(@NonNull String token) {
		try{
			return JwtUtils.verifyJwt(JwtUtils.parseToken(token));
		} catch (Exception e) {
			return Boolean.FALSE;
		}
	}

	@Override
    public Boolean validateOktaServiceToken(Okta okta, ServiceTokenRquestBean requestBean) {
		OktaIntrospect result = null;
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
		MultiValueMap<String, String> params = new LinkedMultiValueMap();
		params.add(AuthConstant.OKTA_TOKEN, requestBean.getToken());
		params.add(AuthConstant.OKTA_CLIENT_ID, okta.getClientId());
		params.add(AuthConstant.OKTA_CLIENT_SECRET, okta.getSecret());
		params.add(AuthConstant.OKTA_GRANT_TYPE, okta.getGrantTypes());
		HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);
        try {
			result = restTemplate.postForEntity(introspectUrl, request, OktaIntrospect.class).getBody();
		} catch (Exception e) {
            if(e instanceof HttpClientErrorException) {
                HttpClientErrorException exception = (HttpClientErrorException)e;
                log.warn("call okta result， code:{}, body:{}", exception.getStatusCode().value(), exception.getResponseBodyAsString());
            } else {
                log.error(e.getMessage(), e);
            }
        }
        String redisKey = AuthConstant.SERVICE_TOKEN_REDIS_PRIFIX.concat(requestBean.getToken()).concat(":").concat(requestBean.getClientId());
        if(null!=result && Objects.equal(Boolean.TRUE, result.getActive())) {
            long exp = result.getExp();
            if (exp > 0) {
                stringRedisTemplate.opsForValue().set(redisKey, "1", userTokenTimeout, TimeUnit.SECONDS);
                log.info("put service token :{} to redis success", requestBean.getToken());
                return Boolean.TRUE;
            }
            return Boolean.FALSE;
        } else {
            stringRedisTemplate.opsForValue().set(redisKey, "0", 10, TimeUnit.MINUTES);
            log.warn("customer:{} token is warn, set 10 minutes to blocking access", requestBean.getClientId());
        }
        return Boolean.FALSE;
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

	private boolean validateClientIdAndSecret(@NonNull String clientId, @NonNull String secret) {
		try{
			Okta okta = findByClientId(clientId);
			if(secret.equalsIgnoreCase(okta.getSecret())){
				return Boolean.TRUE;
			}
		}catch(Exception e){
			return Boolean.FALSE;
		}
		return Boolean.FALSE;
	}

	private User validateUsernameAndPassword(String requestUsername, String requestPassword) {
        boolean hasKey = stringRedisTemplate.hasKey(requestUsername);
        if(hasKey) {
            String val = stringRedisTemplate.opsForValue().get(requestUsername);
            //Only 5 logins are allowed in 5 minutes
            if(null!=val && Integer.valueOf(val) >= 5) {
                log.warn("user:{} is password error more then 5", requestUsername);
                return null;
            }
        }
        User user = new User();
        user.setUsername(requestUsername);
        user.setPassword(Md5Utils.encryptPassword(requestPassword, Md5Utils.SECRET_KEY));
        ExampleMatcher matcher = ExampleMatcher.matchingAll();
        Example<User> example = Example.of(user, matcher);
        List<User> list = userRepository.findAll(example);
        if(CollectionUtils.isEmpty(list)) {
            log.warn("user:{} is not match the password", requestUsername);
            //Only 5 logins are allowed in 5 minutes
            stringRedisTemplate.opsForValue().increment(requestUsername);
            if(!hasKey) {
                stringRedisTemplate.expire(requestUsername, 5, TimeUnit.MINUTES);
            }
            return null;
        }
        user = list.get(0);
        stringRedisTemplate.delete(requestUsername);
        return user;
    }

    private Jwt generateAuthJwt(String username) {
        Long now = Instant.now().getEpochSecond();
        Jwt jwt = new Jwt();
        jwt.setIss(JwtConstant.JWT_ISSUER);
        jwt.setSub(username);
        jwt.setAud(JwtConstant.JWT_AUDIENCE);
        jwt.setExp(now + userTokenTimeout);
        jwt.setNbf(0L);
        jwt.setIat(now);
        jwt.setJti(String.valueOf(SnowFlake.nextId()));
        return jwt;
    }
	
    private OktaToken loadToken(String code, String redirectUrl, Okta okta) {
        OktaToken oktaToken = null;
        try{
            Map<String, String> header = Maps.newHashMapWithExpectedSize(2);
            Map<String, String> body = Maps.newHashMapWithExpectedSize(5);
            header.put(AuthConstant.OKTA_CONTENT_TYPE, "application/x-www-form-urlencoded");
            body.put(AuthConstant.OKTA_CLIENT_ID, okta.getClientId());
            body.put(AuthConstant.OKTA_CLIENT_SECRET, okta.getSecret());
            body.put(AuthConstant.OKTA_GRANT_TYPE, AuthConstant.OKTA_AUTHORIZATION_CODE);
            body.put(AuthConstant.OKTA_CODE, code);
            body.put(AuthConstant.OKTA_REDIRECT_URI, redirectUrl);
            
            log.info("call okta to get token, param is:{}", JSONObject.toJSONString(body));
            oktaToken = HttpUtils.httpPost(tokenUrl, header, body, OktaToken.class);
            return oktaToken;
        } catch (InstantiationException | IllegalAccessException e){
            log.error("OktaServiceImpl:loadToken:{}:{}", code, e.getMessage());
            return oktaToken;
        }
    }

    private OktaIntrospect loadOktaIntrospect(String token, Okta okta) {
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

            //transfor username upper to lower
            if(oktaIntrospect.getUsername() != null){
                oktaIntrospect.setUsername(oktaIntrospect.getUsername().toLowerCase());
            }
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
        User user = userRepository.findByUsername(username);
        return UserPermissionDto.builder().username(username).displayName(user.getDisplayName()).groupList(groupDtoList).permissionList(permissionDtoList).build();
    }

}
