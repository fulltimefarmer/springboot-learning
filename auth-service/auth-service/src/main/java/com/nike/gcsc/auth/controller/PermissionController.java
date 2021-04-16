package com.nike.gcsc.auth.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nike.gcsc.auth.constant.ErrorEnum;
import com.nike.gcsc.auth.controller.request.PageBaseRequest;
import com.nike.gcsc.auth.entity.Permission;
import com.nike.gcsc.auth.entity.Project;
import com.nike.gcsc.auth.service.PermissionService;
import com.nike.gcsc.auth.service.ProjectService;
import com.nike.gcsc.auth.utils.BeanValidationUtil;
import com.nike.gcsc.authapi.request.PermissionRequestBean;
import com.nike.gcsc.authapi.response.PermissionDto;
import com.nike.gcsc.common.GlobalResponse;
import com.nike.gcsc.util.BeanMapper;

import lombok.extern.slf4j.Slf4j;

/**
 * Group rest controller
 *
 * @author tom
 * @date 2019/7/15
 */
@Slf4j
@RestController
@RequestMapping("/v1/api/permission")
public class PermissionController {

    @Autowired
    PermissionService permissionService;

    @Autowired
    ProjectService projectService;

    @PostMapping("/check_exist_or_add_permission")
    public GlobalResponse<Long> checkExistOrAdd(@RequestBody PermissionRequestBean bean) {
        try {
            BeanValidationUtil.checkSavePermissionBean(bean);
        } catch (Exception e) {
            return GlobalResponse.buildFail(ErrorEnum.ALREADY_EXIST);
        }
        Project project = projectService.findById(bean.getProjectId());
        if (null == project || project.getId() == 0) {
            return GlobalResponse.buildFail(ErrorEnum.NOT_FOUND,"project not found.");
        }
        Permission permission = permissionService.findByName(bean.getName());
        if (null == permission || permission.getId() == 0) {
            Permission model = new Permission();
            model.setName(bean.getName());
            model.setMethod(bean.getMethod());
            model.setType(bean.getType());
            model.setProjectId(project.getId());
            model.setUriRegPattern(bean.getUriRegPattern());
            model.setRemark(bean.getRemark());
            long id = permissionService.add(model);
            return GlobalResponse.buildSuccess(id);
        } else {
            return GlobalResponse.buildSuccess(permission.getId());
        }
    }

    @DeleteMapping("/delete/{id}")
    public GlobalResponse<Boolean> delete(@PathVariable(name="id") Long id) {
        try {
            permissionService.delete(id);
            return GlobalResponse.buildSuccess(Boolean.TRUE);
        } catch (Exception e) {
            log.error(String.format("delete permission fail id:%s", id), e);
            return GlobalResponse.buildFail(ErrorEnum.SERVER_EXCEPTION);
        }
    }

    @PutMapping("/update/{id}")
    public GlobalResponse<Boolean> update(
            @PathVariable(name="id") Long id,
            @RequestBody PermissionRequestBean bean) {
        Permission permission = permissionService.findById(id);
        permission.setName(bean.getName());
        permission.setMethod(bean.getMethod());
        permission.setType(bean.getType());
        permission.setUriRegPattern(bean.getUriRegPattern());
        permission.setRemark(bean.getRemark());
        permission.setProjectId(bean.getProjectId());
        permissionService.edit(permission);
        return GlobalResponse.buildSuccess(Boolean.TRUE);
    }

    @GetMapping("/find_all")
    public GlobalResponse<List<PermissionDto>> findAll() {
        List<Permission> permissionList = permissionService.findAll(0, Integer.MAX_VALUE).getContent();
        List<PermissionDto> dtoList = BeanMapper.mapList(permissionList, PermissionDto.class);
        return GlobalResponse.buildSuccess(dtoList);
    }

    @GetMapping("/find_by_name/{name}")
    public GlobalResponse<PermissionDto> findByName(@PathVariable(name="name") String name) {
        PermissionDto permissionDto = new PermissionDto();
        Permission permission = permissionService.findByName(name);
        BeanMapper.copy(permission, permissionDto);
        return GlobalResponse.buildSuccess(permissionDto);
    }

    @PostMapping("/find_by_page")
    public GlobalResponse<Page<Permission>> findByPage(@RequestBody PageBaseRequest<Permission> pageRequest) {
        Page<Permission> result = permissionService.findByAttributes(pageRequest.getOptions(), pageRequest.getPageNum(), pageRequest.getPageSize());
        return GlobalResponse.buildSuccess(result);
    }

    @GetMapping("/find_by_group/{groupName}")
    GlobalResponse<List<PermissionDto>> findByGroupName(@PathVariable(name = "groupName") String groupName) {
    	List<Permission> permissionList = permissionService.findByGroupName(groupName);
    	List<PermissionDto> permissionDtoList = BeanMapper.mapList(permissionList, PermissionDto.class);
        return GlobalResponse.buildSuccess(permissionDtoList);
    }
    
    @GetMapping("/find_by_groupid/{groupId}")
    GlobalResponse<List<PermissionDto>> findByGroupId(@PathVariable Long groupId) {
        List<Permission> permissionList = permissionService.findByGroupId(groupId);
        List<PermissionDto> permissionDtoList = BeanMapper.mapList(permissionList, PermissionDto.class);
        return GlobalResponse.buildSuccess(permissionDtoList);
    }
    
    @GetMapping("/find_by_groupid/{groupId}/{projectId}")
    GlobalResponse<List<PermissionDto>> findByGroupIdAndProjectId(@PathVariable Long groupId, @PathVariable Long projectId) {
        List<Permission> permissionList = permissionService.findByGroupIdAndProjectId(groupId, projectId);
        List<PermissionDto> permissionDtoList = BeanMapper.mapList(permissionList, PermissionDto.class);
        return GlobalResponse.buildSuccess(permissionDtoList);
    }
    
    @GetMapping("/find_exclude_by_groupid/{groupId}")
    GlobalResponse<List<PermissionDto>> findExcludeByGroupId(@PathVariable Long groupId) {
        List<Permission> permissionList = permissionService.findExcludeByGroupId(groupId);
        List<Permission> selectedList = permissionService.findByGroupId(groupId);
        if(null != permissionList && null != selectedList) {
        	permissionList.removeAll(selectedList);
        }
        List<PermissionDto> permissionDtoList = BeanMapper.mapList(permissionList, PermissionDto.class);
        return GlobalResponse.buildSuccess(permissionDtoList);
    }
    
    @GetMapping("/find_exclude_by_groupid/{groupId}/{projectId}")
    GlobalResponse<List<PermissionDto>> findExcludeByGroupIdAndProjectId(@PathVariable Long groupId, @PathVariable Long projectId) {
        List<Permission> permissionList = permissionService.findExcludeByGroupIdAndProjectId(groupId, projectId);
        List<Permission> selectedList = permissionService.findByGroupId(groupId);
        if(null != permissionList && null != selectedList) {
            permissionList.removeAll(selectedList);
        }
        List<PermissionDto> permissionDtoList = BeanMapper.mapList(permissionList, PermissionDto.class);
        return GlobalResponse.buildSuccess(permissionDtoList);
    }

}
