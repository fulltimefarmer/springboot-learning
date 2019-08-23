package com.nike.gcsc.auth.controller;

import com.alibaba.fastjson.JSON;
import com.nike.gcsc.auth.constant.ErrorEnum;
import com.nike.gcsc.auth.controller.request.PageBaseRequest;
import com.nike.gcsc.auth.dto.Group;
import com.nike.gcsc.auth.dto.Project;
import com.nike.gcsc.auth.service.GroupService;
import com.nike.gcsc.auth.service.PermissionService;
import com.nike.gcsc.auth.service.ProjectService;
import com.nike.gcsc.auth.service.UserService;
import com.nike.gcsc.auth.utils.BeanValidationUtil;
import com.nike.gcsc.authapi.request.AmountPermissionsBean;
import com.nike.gcsc.authapi.request.AmountUserBean;
import com.nike.gcsc.authapi.request.AmountUsersBean;
import com.nike.gcsc.authapi.request.GroupRequestBean;
import com.nike.gcsc.authapi.response.GroupDto;
import com.nike.gcsc.common.GlobalResponse;
import com.nike.gcsc.util.BeanMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Group rest controller
 *
 * @author tom
 * @date 2019/7/15
 */
@Slf4j
@RestController
@RequestMapping("/v1/api/group")
public class GroupController {

    @Autowired
    GroupService groupService;

    @Autowired
    ProjectService projectService;

    @Autowired
    UserService userService;

    @Autowired
    PermissionService permissionService;

    @PostMapping("/check_exist_or_add")
    public GlobalResponse<Long> checkExistOrAdd(@RequestBody GroupRequestBean bean) {
        log.debug("request param is :{}", bean);
        try {
            BeanValidationUtil.checkSaveGroupBean(bean);
        } catch (Exception e) {
            return GlobalResponse.buildFail(ErrorEnum.PARAM_INVALID, e.getMessage());
        }
        Group group = groupService.findByProjectnameAndGroupname(bean.getProjectName(),bean.getGroupName());
        if (null != group) {
            return GlobalResponse.buildSuccess(group.getId());
        }
        Project project = projectService.findByName(bean.getProjectName());
        group = new Group();
        group.setName(bean.getGroupName());
        group.setProjectId(project.getId());
        group.setRemark(bean.getRemark());
        Long result = groupService.add(group);
        return GlobalResponse.buildSuccess(result);
    }

    @DeleteMapping("/delete/{id}")
    public GlobalResponse<Boolean> delete(@PathVariable(name = "id") Long id) {
        try {
            groupService.delete(id);
            log.trace("success delete group by id {}", id);
            return GlobalResponse.buildSuccess(true);
        } catch (Exception e) {
            log.error(String.format("delete group by id %s fail", id), e);
            return GlobalResponse.buildSuccess(false);
        }
    }

    @PutMapping("/update/{id}")
    public GlobalResponse<Boolean> update(@PathVariable(name = "id") Long id, @RequestBody GroupRequestBean bean) {
        Group exist = groupService.findById(id);
        if (null == exist) {
            return GlobalResponse.buildFail(ErrorEnum.NOT_FOUND.getCode(), String.format("group id:%s is not found", id));
        }
        Project project = projectService.findByName(bean.getProjectName());
        if (null == project) {
            return GlobalResponse.buildFail(ErrorEnum.NOT_FOUND.getCode(), String.format("project name:%s is not found", bean.getProjectName()));
        }
        exist.setName(bean.getGroupName());
        exist.setProjectId(project.getId());
        exist.setRemark(bean.getRemark());
        groupService.edit(exist);
        return GlobalResponse.buildSuccess(true);
    }

    @GetMapping("/find_all")
    public GlobalResponse<List<GroupDto>> findAll() {
        List<Group> groupList = groupService.findAll(0, Integer.MAX_VALUE).getContent();
        List<GroupDto> groupDtoList = BeanMapper.mapList(groupList, GroupDto.class);
        return GlobalResponse.buildSuccess(groupDtoList);
    }

    @GetMapping("/find_by_name/{name}")
    public GlobalResponse<GroupDto> findByName(@PathVariable(name = "name") String name) {
        Group group = groupService.findByName(name);
        if (null != group) {
            GroupDto groupDto = new GroupDto();
            BeanMapper.copy(group, groupDto);
            return GlobalResponse.buildSuccess(groupDto);
        } else {
            return GlobalResponse.buildFail(ErrorEnum.NOT_FOUND);
        }
    }

    @GetMapping("/find_by_projectname/{projectName}")
    public GlobalResponse<List<GroupDto>> getGroupListByProjectName(@PathVariable(name = "projectName") String projectName) {
        List<Group> groupList = groupService.findByProjectname(projectName);
        List<GroupDto> groupDtoList = BeanMapper.mapList(groupList, GroupDto.class);
        return GlobalResponse.buildSuccess(groupDtoList);
    }

    @GetMapping("/find_by_projectname_and_username/{projectname}/{username}")
    GlobalResponse<List<GroupDto>> findByProjectnameAndUsername(
            @PathVariable(name = "projectname") String projectname,
            @PathVariable(name = "username") String username) {
        List<Group> groupList = groupService.findByProjectnameAndUsername(projectname, username);
        List<GroupDto> groupDtoList = BeanMapper.mapList(groupList, GroupDto.class);
        return GlobalResponse.buildSuccess(groupDtoList);
    }

    @PostMapping("/find_by_page")
    public GlobalResponse<Page<Group>> findByPage(@RequestBody PageBaseRequest<Group> pageRequest) {
        Page<Group> result = groupService.findByAttributes(pageRequest.getOptions(), pageRequest.getPageNum(), pageRequest.getPageSize());
        return GlobalResponse.buildSuccess(result);
    }

    @PostMapping("/amount_user_by_project")
    GlobalResponse<String> amountUserByProject(@RequestBody AmountUserBean amountUserBean) {
        ErrorEnum result = groupService.amountUserByProject(amountUserBean.getProjectId(), amountUserBean.getGroupId(), amountUserBean.getUserId());
        if (ErrorEnum.SUCCESS.equals(result)) {
            return GlobalResponse.buildSuccess(result.getDesc());
        }
        return GlobalResponse.buildFail(result);
    }

    @PostMapping("/unamount_user_by_project")
    GlobalResponse<String> unamountUserByProject(@RequestBody AmountUserBean amountUserBean) {
        ErrorEnum result = groupService.unamountUserByProject(amountUserBean.getProjectId(), amountUserBean.getGroupId(), amountUserBean.getUserId());
        if (ErrorEnum.SUCCESS.equals(result)) {
            return GlobalResponse.buildSuccess(result.getDesc());
        }
        return GlobalResponse.buildFail(result);
    }

    @PostMapping("/reset_users_to_group")
    public GlobalResponse<Boolean> resetUserToGroup(@RequestBody AmountUsersBean bean) {
        try {
            BeanValidationUtil.checkAmountUserBean(bean);
        } catch (Exception e) {
            log.error(String.format("param AmountUsersBean:{} is not valid", JSON.toJSONString(bean)), e);
            return GlobalResponse.buildFail(ErrorEnum.PARAM_INVALID);
        }
        groupService.resetUsersToGroup(bean.getGroupId(), bean.getUserIds());
        return GlobalResponse.buildSuccess(true);
    }

    @PostMapping("/reset_permissions_to_group")
    public GlobalResponse<Boolean> resetPermissionToGroup(@RequestBody AmountPermissionsBean bean) {
        if (null != bean.getPermissionIds() && bean.getPermissionIds().size() > 0 && bean.getGroupId() > 0) {
            groupService.resetPermissionsToGroup(bean.getGroupId(), bean.getPermissionIds());
            return GlobalResponse.buildSuccess(true);
        } else {
            log.error("ammount permission fail:{}", JSON.toJSONString(bean));
            return GlobalResponse.buildFail(ErrorEnum.SERVER_EXCEPTION, false);
        }
    }

}
