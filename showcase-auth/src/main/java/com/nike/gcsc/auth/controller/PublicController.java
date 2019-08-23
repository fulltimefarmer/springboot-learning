package com.nike.gcsc.auth.controller;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.nike.gcsc.auth.controller.request.UserTokenRequest;
import com.nike.gcsc.authapi.request.TokenRequestBean;
import com.nike.gcsc.authapi.response.UserPermissionDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.nike.gcsc.auth.constant.AuthConstant;
import com.nike.gcsc.auth.constant.ErrorEnum;
import com.nike.gcsc.auth.dto.Okta;
import com.nike.gcsc.auth.service.GroupService;
import com.nike.gcsc.auth.service.AuthService;
import com.nike.gcsc.auth.service.PermissionService;
import com.nike.gcsc.auth.service.ProjectService;
import com.nike.gcsc.common.GlobalResponse;

import lombok.extern.slf4j.Slf4j;

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

	@PostMapping("/get_token_by_pwd")
	public GlobalResponse<String> getTokenByPwd(
			HttpServletRequest req,
			HttpServletResponse resp,
			@RequestBody UserTokenRequest request) {
		String token = authService.getTokenByPwd(request.getUsername(), request.getPassword(), request.getProjectId());
		if(null == token) {
			return GlobalResponse.buildFail(ErrorEnum.AUTH_INVALIDATED);
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
		log.debug(String.format("the request param is code : %s ,clientId : %s ,redirectUrl : %s", code,clientId,redirectUrl));
		Okta okta = authService.findByClientId(clientId);
		if(null == okta) {
			GlobalResponse.buildFail(ErrorEnum.PARAM_INVALID);
		}
		String token = authService.getTokenByCode(okta, redirectUrl, code);
		log.debug("the return token is : " +token);
		//store token into cookie
		Cookie cookie = new Cookie(AuthConstant.OKTA_TOKEN, token);
		cookie.setHttpOnly(true);
		resp.addCookie(cookie);
		return GlobalResponse.buildSuccess(token);
	}

	@PostMapping("/get_permissons_by_token")
	public GlobalResponse<UserPermissionDto> getPermissonByToken(@RequestBody TokenRequestBean reqBean) {
		log.debug(String.format("getPermissonByToken method param is : %s", reqBean));
		return GlobalResponse.buildSuccess(authService.getUserPermissionByToken(reqBean.getToken()));
	}

	@GetMapping("/reload_redis_token_cache")
	public GlobalResponse<Boolean> reflashTokenCache(
			@RequestParam(name = "secret", required = false) String secret,
			@RequestParam(name = "second", required = false) int second) {
		return GlobalResponse.buildSuccess(authService.reflashTokenCache(secret, second));
	}

}
