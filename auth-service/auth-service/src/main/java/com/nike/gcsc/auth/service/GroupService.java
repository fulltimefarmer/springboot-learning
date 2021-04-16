package com.nike.gcsc.auth.service;

import java.util.List;
import java.util.Set;

import org.springframework.data.domain.Page;

import com.nike.gcsc.auth.constant.ErrorEnum;
import com.nike.gcsc.auth.entity.Group;

public interface GroupService {

    Long add(Group param);

    void delete(Long id);

    void deleteByIdIn(List<Long> ids);

    void edit(Group param);

    Page<Group> findAll(Integer pageNumber, Integer pageSize);

    Page<Group> findByAttributes(Group queryBean, Integer pageNumber, Integer pageSize);

    Group findById(Long id);

    Group findByName(String name);

    List<Group> findByProjectname(String projectName);

    List<Group> findByProjectnameAndUsername(String projectName, String username);

    List<Group> findByProjectnameAndPermissionname(String projectName, String permissionName);

    Group findByProjectnameAndGroupname(String projectName, String groupname);
    
    Group findByProjectIdAndGroupname(Long projectId, String groupname);

    void resetUsersToGroup(Long groupId, Set<Long> userIds);

    ErrorEnum amountUserByProject(Long projectId, Long groupId, Long userId);

    ErrorEnum unamountUserByProject(Long projectId, Long groupId, Long userId);

    void resetPermissionsToGroup(Long groupId, Set<Long> permissionIds);

}
