package com.nike.gcsc.auth.controller;

import com.alibaba.fastjson.JSONObject;
import com.nike.gcsc.auth.exception.HttpBadRequestException;
import com.nike.gcsc.auth.constant.AuthConstant;
import com.nike.gcsc.auth.constant.ErrorEnum;
import com.nike.gcsc.auth.constant.JwtConstant;
import com.nike.gcsc.auth.entity.Okta;
import com.nike.gcsc.auth.service.AuthService;
import com.nike.gcsc.auth.service.GroupService;
import com.nike.gcsc.auth.service.PermissionService;
import com.nike.gcsc.auth.service.ProjectService;
import com.nike.gcsc.authapi.request.ServiceTokenRequest;
import com.nike.gcsc.authapi.request.ServiceTokenRquestBean;
import com.nike.gcsc.authapi.request.TokenRequestBean;
import com.nike.gcsc.authapi.request.UserTokenRequest;
import com.nike.gcsc.authapi.response.UserPermissionDto;
import com.nike.gcsc.common.GlobalResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.time.Duration;
import java.time.Instant;
import java.util.Objects;

@Slf4j
@RestController
@RequestMapping("/v1/public")
public class PublicController {

	@Autowired
	AuthService authService;
	
	@Autowired
	PermissionService permissionService;

	@Autowired
	GroupService groupService;

	@Autowired
	ProjectService projectService;

	@Resource(name="stringRedisTemplate")
	private RedisTemplate<String, String> stringRedisTemplate;

	@PostMapping("/get_token_by_pwd")
	public GlobalResponse<String> getTokenByPwd(
			HttpServletRequest req,
			HttpServletResponse resp,
			@RequestBody UserTokenRequest request) {
		String token = authService.getTokenByPwd(request.getUsername(), request.getPassword(), request.getProjectId());
		if(StringUtils.isBlank(token)) {
			return GlobalResponse.buildFail(ErrorEnum.AUTH_INVALIDATED);
		}
		if(AuthConstant.USER_DISABLE.equals(token)) {
		    return GlobalResponse.buildFail(ErrorEnum.USER_DISABLED);
		}
		//store token into cookie
		Cookie cookie = new Cookie(AuthConstant.OKTA_TOKEN, token);
		cookie.setHttpOnly(true);
		resp.addCookie(cookie);
		return GlobalResponse.buildSuccess(token);
	}

	@GetMapping("/get_token_by_code")
	public GlobalResponse<String> getTokenByCode(
			HttpServletRequest req,
			HttpServletResponse resp,
			@RequestParam(name = "code", required = false) String code,
			@RequestParam(name = "clientId", required = false) String clientId,
			@RequestParam(name = "redirectUrl", required = false) String redirectUrl) {
		log.info(String.format("the request param is code : %s ,clientId : %s ,redirectUrl : %s", code,clientId,redirectUrl));
		Okta okta = authService.findByClientId(clientId);
		if(null == okta) {
			return GlobalResponse.buildFail(ErrorEnum.PARAM_INVALID);
		}
		String token = "";
		try {
		    token = authService.getTokenByCode(okta, redirectUrl, code);
		    log.info("the return token is : " +token);
        } catch (HttpBadRequestException e) {
            log.error("call okta error", e);
            return GlobalResponse.buildFail(ErrorEnum.OKTA_SERVICE_WRONG_PARAM.getCode(), ErrorEnum.OKTA_SERVICE_WRONG_PARAM.getDesc());
        } catch (Exception e) {
            log.error("get token by code error", e);
        }
		if(StringUtils.isBlank(token)) {
		    return GlobalResponse.buildFail(ErrorEnum.OKTA_SERVICE_WRONG_PARAM);
		}
		if(AuthConstant.USER_DISABLE.equals(token)) {
            return GlobalResponse.buildFail(ErrorEnum.USER_DISABLED);
        }
		//store token into cookie
		Cookie cookie = new Cookie(AuthConstant.OKTA_TOKEN, token);
		cookie.setHttpOnly(true);
		resp.addCookie(cookie);
		return GlobalResponse.buildSuccess(token);
	}
	
	@PostMapping("/get_permissons_by_token")
	public GlobalResponse<UserPermissionDto> getPermissonByToken(@RequestBody TokenRequestBean reqBean) {
		log.info(String.format("getPermissonByToken method param is : %s", reqBean));
		return GlobalResponse.buildSuccess(authService.getUserPermissionByToken(reqBean.getToken()));
	}

	@PostMapping("/get_token_by_secret")
	public GlobalResponse<String> getServiceTokenBySecret(HttpServletResponse resp, @RequestBody ServiceTokenRequest request){
		String token = authService.getTokenBySecret(request.getClientId(), request.getSecret());
		if(StringUtils.isBlank(token)) {
			return GlobalResponse.buildFail(ErrorEnum.AUTH_INVALIDATED);
		}
		token = JwtConstant.SERVICE_AUTH_TOKEN_PREFIX.concat(token);
		//store token into cookie
		Cookie cookie = new Cookie(AuthConstant.OKTA_TOKEN, token);
		cookie.setHttpOnly(true);
		resp.addCookie(cookie);
		return GlobalResponse.buildSuccess(token);
	}

	@PostMapping("/validate_service_token")
	public GlobalResponse<Void> validateServiceToken(@RequestBody @Validated ServiceTokenRquestBean requestBean) {
		if(log.isDebugEnabled()) {
			log.debug("validate service call, request param:{}", JSONObject.toJSONString(requestBean));
		}
		Boolean result = Boolean.FALSE;
		String clientId = requestBean.getClientId();
		String token = requestBean.getToken();
		if(StringUtils.isNotBlank(clientId) && Boolean.TRUE.equals(requestBean.getIsAuthToken())){
			result = authService.validateServiceToken(token);
		} else if (StringUtils.isNotBlank(clientId) && Boolean.FALSE.equals(requestBean.getIsAuthToken())) {
			String redisKey = AuthConstant.SERVICE_TOKEN_REDIS_PRIFIX.concat(token).concat(":").concat(clientId);
			String redisValue = stringRedisTemplate.opsForValue().get(redisKey);
			if(Objects.equals("1", redisValue)) {
				return GlobalResponse.buildSuccess();
			} else if(Objects.equals("0", redisValue)) {
				log.warn("clientId :{} is blocking access", clientId);
				return GlobalResponse.buildFail(ErrorEnum.AUTH_INVALIDATED);
			}
			Okta okta = authService.findByClientId(clientId);
			if(Objects.isNull(okta)) {
				log.warn("validate service fail, clientId {} is not config in table", clientId);
				return GlobalResponse.buildFail(ErrorEnum.OKTA_SERVICE_WRONG_ACCOUNT);
			}
			Instant start = Instant.now();
			result = authService.validateOktaServiceToken(okta, requestBean);
			Instant end = Instant.now();
			Duration dur = Duration.between(start, end);
			log.info("############# time for verify OKTA token: ", dur.toMillis());
		}
		if(Objects.equals(Boolean.TRUE, result)) {
			return GlobalResponse.buildSuccess();
		}
		return GlobalResponse.buildFail(ErrorEnum.AUTH_INVALIDATED);
	}

	@GetMapping("/reload_redis_token_cache")
	public GlobalResponse<Boolean> reflashTokenCache(
			@RequestParam(name = "secret", required = false) String secret,
			@RequestParam(name = "second", required = false) int second) {
		return GlobalResponse.buildSuccess(authService.reflashTokenCache(secret, second));
	}

}
