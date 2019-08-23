package com.nike.gcsc.auth.service;

import com.nike.gcsc.auth.dto.Permission;
import org.springframework.data.domain.Page;

import java.util.List;

public interface PermissionService {

    Long add(Permission param);

    void delete(Long id);

    void edit(Permission param);

    Page<Permission> findAll(Integer pageNumber, Integer pageSize);
    Page<Permission> findByAttributes(Permission queryBean, Integer pageNumber, Integer pageSize);
    Permission findById(Long id);
    Permission findByName(String name);
    List<Permission> findByGroupId(Long groupId);
    List<Permission> findExcludeByGroupId(Long groupId);
    List<Permission> findByGroupName(String groupName);
    List<Permission> findByProjectidAndUsername(Long projectId, String username);
    List<Permission> findByProjectnameAndUsername(String projectName, String username);

}
