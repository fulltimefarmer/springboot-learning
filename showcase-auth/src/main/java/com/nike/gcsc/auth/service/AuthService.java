package com.nike.gcsc.auth.service;

import com.nike.gcsc.auth.dto.Okta;
import com.nike.gcsc.authapi.response.UserPermissionDto;
import org.springframework.data.domain.Page;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface AuthService {

    Long add(Okta param);

    void delete(Long id);
	@Transactional
	void deleteBatchByIdList(List<Long> ids);

    void edit(Okta param);

    Page<Okta> findAll(Integer pageNumber, Integer pageSize);
    Page<Okta> findByAttributes(Okta queryBean, Integer pageNumber, Integer pageSize);
	Okta findByClientId(String clientId);

	String getTokenByPwd(String username, String password, Long projectId);
	String getTokenByCode(Okta okta, String redirectUrl, String code);

    UserPermissionDto getUserPermissionByToken(String token);
    Boolean reflashTokenCache(String secret, int second);

}
