package com.nike.gcsc.auth.service;

import com.nike.gcsc.auth.entity.User;
import org.springframework.data.domain.Page;

import java.util.List;

public interface UserService {

    Long add(User param);
    Long checkNikeUserExistOrAdd(String username);

    void delete(Long id);
    void deleteByIdIn(List<Long> ids);

    User edit(User param);
    
    User disable(Long id);

    Page<User> findAll(Integer pageNumber, Integer pageSize);
    Page<User> findByAttributes(User queryBean, Integer pageNumber, Integer pageSize);
    User findById(Long id);
    User findByUsername(String username);
    List<User> findByIds(List<Long> ids);
    List<User> findByGroupId(Long groupId);
    List<User> findExcludeByGroupId(Long groupId);
    List<User> findByProjectNameAndGroupId(String projectName, Long groupId);
    List<User> findByProjectNameAndGroupName(String projectName, String groupName);

}
