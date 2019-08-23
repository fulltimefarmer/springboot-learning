package com.nike.gcsc.auth.service.impl;

import com.google.common.collect.Lists;
import com.nike.gcsc.auth.constant.ErrorEnum;
import com.nike.gcsc.auth.dto.Group;
import com.nike.gcsc.auth.dto.MiddleGroupPermission;
import com.nike.gcsc.auth.dto.MiddleGroupUser;
import com.nike.gcsc.auth.repository.GroupRepository;
import com.nike.gcsc.auth.repository.MiddleGroupPermissionRepository;
import com.nike.gcsc.auth.repository.MiddleGroupUserRepository;
import com.nike.gcsc.auth.service.GroupService;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class GroupServiceImpl implements GroupService {

    @Autowired
    GroupRepository groupRepository;

    @Autowired
    MiddleGroupPermissionRepository middleGroupPermissionRepository;

    @Autowired
    MiddleGroupUserRepository middleGroupUserRepository;

    @Override
    public Long add(@NonNull Group param) {
        if (null != param.getId()) {
            param.setId(null);
        }
        @NonNull Group saved = groupRepository.save(param);
        return saved.getId();
    }

    @CacheEvict(value = "groupCache", allEntries = true, cacheManager="redisCache")
    @Override
    public void delete(@NonNull Long id) {
        middleGroupPermissionRepository.deleteByGroupId(id);
        middleGroupUserRepository.deleteByGroupId(id);
        groupRepository.deleteById(id);
    }

    @CacheEvict(value = "groupCache", allEntries = true, cacheManager="redisCache")
    @Override
    public void deleteByIdIn(List<Long> ids) {
        groupRepository.deleteByIdIn(ids);
    }

    @Override
    public void edit(@NonNull Group param) {
        groupRepository.save(param);
    }

    @Override
    public Page<Group> findAll(Integer pageNumber, Integer pageSize) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        return groupRepository.findAll(pageable);
    }

    @Override
    public Page<Group> findByAttributes(Group queryBean, Integer pageNumber, Integer pageSize) {
        ExampleMatcher matcher = ExampleMatcher.matchingAll();
        Example<Group> example = Example.of(queryBean, matcher);
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        return groupRepository.findAll(example, pageable);
    }

    @Override
    public Group findById(Long id) {
        return groupRepository.findById(id).orElse(new Group());
    }

    @Override
    public Group findByName(String name) {
        return groupRepository.findByName(name);
    }

    @Override
    public List<Group> findByProjectname(String projectName) {
        return groupRepository.findByProjectname(projectName);
    }

    @Cacheable(value = "groupCache", key = "#p0.concat(':').concat(#p1)", cacheManager="redisCache", unless = "#result==null")
    @Override
    public List<Group> findByProjectnameAndUsername(String projectName, String username) {
        return groupRepository.findByProjectnameAndUsername(projectName, username);
    }

    @Override
    public List<Group> findByProjectnameAndPermissionname(String projectName, String permissionName) {
        return groupRepository.findByProjectnameAndPermissionname(projectName, permissionName);
    }

    @Override
    public Group findByProjectnameAndGroupname(String projectName, String groupname) {
        return groupRepository.findByProjectnameAndGroupname(projectName, groupname);
    }

    @Override
    public void resetUsersToGroup(Long groupId, List<Long> userIds) {
        middleGroupUserRepository.deleteByGroupId(groupId);
        List<MiddleGroupUser> mguList = Lists.newArrayListWithCapacity(userIds.size());
        for (Long userId : userIds) {
            MiddleGroupUser groupUser = new MiddleGroupUser(groupId, userId);
            mguList.add(groupUser);
        }
        middleGroupUserRepository.saveAll(mguList);
    }

    @Override
    public ErrorEnum amountUserByProject(Long projectId, Long groupId, Long userId) {
    	log.debug("GroupServiceImpl:amountUserByProject:{}:{}:{}", projectId, groupId, userId);
        if(!checkGroupProject(projectId, groupId)){
            return ErrorEnum.PARAM_INVALID;
        }
        if(middleGroupUserRepository.countByGroupIdAndUserId(groupId, userId) > 0){
            return ErrorEnum.ALREADY_EXIST;
        }
        MiddleGroupUser groupUser = new MiddleGroupUser(groupId, userId);
        middleGroupUserRepository.save(groupUser);
        return ErrorEnum.SUCCESS;
    }

    @Override
    public ErrorEnum unamountUserByProject(Long projectId, Long groupId, Long userId) {
    	log.debug("GroupServiceImpl:unamountUserByProject:{}:{}:{}", projectId, groupId, userId);
        if(!checkGroupProject(projectId, groupId)){
            return ErrorEnum.PARAM_INVALID;
        }
        middleGroupUserRepository.deleteByGroupIdAndUserId(groupId, userId);
        return ErrorEnum.SUCCESS;
    }

    private Boolean checkGroupProject(Long projectId, Long groupId){
        Optional<Group> opt = groupRepository.findById(groupId);
        if(opt.isPresent()){
            Group group = opt.get();
            if(projectId.equals(group.getProjectId())){
                return Boolean.TRUE;
            }
        }
        return Boolean.FALSE;
    }

    @Override
    public void resetPermissionsToGroup(Long groupId, List<Long> permissionIds) {
        middleGroupPermissionRepository.deleteByGroupId(groupId);
        List<MiddleGroupPermission> mgpList = Lists.newArrayListWithCapacity(permissionIds.size());
        for (Long permissionId : permissionIds) {
            MiddleGroupPermission groupPermission = new MiddleGroupPermission(groupId, permissionId);
            mgpList.add(groupPermission);
        }
        middleGroupPermissionRepository.saveAll(mgpList);
    }

}
