package com.nike.gcsc.auth.service.impl;

import com.nike.gcsc.auth.entity.Permission;
import com.nike.gcsc.auth.repository.MiddleGroupPermissionRepository;
import com.nike.gcsc.auth.repository.PermissionRepository;
import com.nike.gcsc.auth.service.PermissionService;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class PermissionServiceImpl implements PermissionService {

    @Autowired
    PermissionRepository permissionRepository;

    @Autowired
    MiddleGroupPermissionRepository middleGroupPermissionRepository;

    @Override
    public Long add(Permission param) {
        if(null != param.getId()){
            param.setId(null);
        }
        @NonNull Permission saved = permissionRepository.save(param);
        return saved.getId();
    }

    @CacheEvict(value = "groupCache", allEntries = true, cacheManager="redisCache")
    @Override
    @Transactional
    public void delete(Long id) {
        permissionRepository.deleteById(id);
        middleGroupPermissionRepository.deleteByPermissionId(id);
    }

    @Override
    public void edit(Permission param) {
        permissionRepository.save(param);
    }

    @Override
    public Page<Permission> findAll(Integer pageNumber, Integer pageSize) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        return permissionRepository.findAll(pageable);
    }

    @Override
    public Page<Permission> findByAttributes(Permission queryBean, Integer pageNumber, Integer pageSize) {
        ExampleMatcher matcher = ExampleMatcher.matchingAll();
        Example<Permission> example = Example.of(queryBean, matcher);
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        return permissionRepository.findAll(example, pageable);
    }

    @Override
    public Permission findById(Long id) {
        Optional<Permission> opt = permissionRepository.findById(id);
        if(opt.isPresent()){
            return opt.get();
        }
        log.debug("PermissionServiceImpl:findById:{}", id);
        return null;
    }

    @Override
    public Permission findByName(String name) {
        return permissionRepository.findByName(name);
    }

    @Override
    public List<Permission> findByGroupId(@NonNull Long groupId) {
        return permissionRepository.findByGroupId(groupId);
    }

    @Override
    public List<Permission> findExcludeByGroupId(@NonNull Long groupId) {
        return permissionRepository.findExcludeByGroupId(groupId);
    }

    @Override
    public List<Permission> findByGroupName(String groupName) {
        return permissionRepository.findByGroupName(groupName);
    }

    @Override
    public List<Permission> findByProjectidAndUsername(Long projectId, String username) {
        return permissionRepository.findByProjectidAndUsername(projectId, username);
    }

    @Override
    public List<Permission> findByProjectnameAndUsername(String projectName, String username) {
        return permissionRepository.findByProjectnameAndUsername(projectName, username);
    }

    @Override
    public List<Permission> findByGroupIdAndProjectId(Long groupId, Long projectId) {
        return permissionRepository.findByGroupIdAndProjectId(groupId, projectId);
    }

    @Override
    public List<Permission> findExcludeByGroupIdAndProjectId(Long groupId, Long projectId) {
        return permissionRepository.findExcludeByGroupIdAndProjectId(groupId, projectId);
    }
}
