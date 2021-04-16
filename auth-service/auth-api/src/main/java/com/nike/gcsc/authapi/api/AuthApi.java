package com.nike.gcsc.authapi.api;

import java.util.List;

import javax.validation.Valid;

import com.nike.gcsc.authapi.request.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import com.nike.gcsc.authapi.response.ChangePwdDto;
import com.nike.gcsc.authapi.response.GroupDto;
import com.nike.gcsc.authapi.response.OktaDto;
import com.nike.gcsc.authapi.response.PermissionDto;
import com.nike.gcsc.authapi.response.ProjectDto;
import com.nike.gcsc.authapi.response.UserDto;
import com.nike.gcsc.authapi.response.UserPermissionDto;
import com.nike.gcsc.common.GlobalResponse;

public interface AuthApi {

    @GetMapping("/v1/api/okta/find_by_project/{projectName}")
    GlobalResponse<List<OktaDto>> getOktaListByProjectName(@PathVariable(name = "projectName") String projectName);

    @GetMapping("/v1/api/okta/find_by_describe/{describe}")
    GlobalResponse<OktaDto> getOktaByDescribe(@PathVariable(name = "describe") String describe);

    @GetMapping("/v1/api/project/find_by_name/{name}")
    GlobalResponse<ProjectDto> getProjectByName(@PathVariable(name = "name") String name);

    @PostMapping("/v1/api/user/check_exist_or_add")
    GlobalResponse<Long> checkExistOrAddUser(@RequestBody UserRequestBean userRequestBean);

    @GetMapping("/v1/api/user/find_by_projectname_and_groupname/{projectName}/{groupName}")
    GlobalResponse<List<UserDto>> getUserListByProjectNameAndGroupName(@PathVariable(name = "projectName") String projectName, @PathVariable(name = "groupName") String groupName);
   
    @PutMapping("/v1/api/user/update_pwd")
    GlobalResponse<Boolean> updatePwd(@RequestBody @Valid ChangePwdDto dto);
    
    @PostMapping("/v1/api/group/check_exist_or_add")
    GlobalResponse<Long> checkExistOrAddGroup(@RequestBody GroupRequestBean groupRequestBean);

    @GetMapping("/v1/api/group/find_by_projectname/{projectName}")
    GlobalResponse<List<GroupDto>> getGroupListByProjectName(@PathVariable(name = "projectName") String projectName);

    @GetMapping("/v1/api/group/find_by_projectname_and_groupname/{projectName}/{groupName}")
    GlobalResponse<GroupDto> getGroupByProjectNameAndGroupName(@PathVariable(name = "projectName") String projectName, @PathVariable(name = "groupName") String groupName);

    @GetMapping("/v1/api/group/find_by_projectname_and_username/{projectName}/{username}")
    GlobalResponse<List<GroupDto>> getGroupListByProjectNameAndUsername(@PathVariable(name = "projectName") String projectName, @PathVariable(name = "username") String username);

    @GetMapping("/v1/api/group/find_by_projectname_and_permissionname/{projectName}/{permissionName}")
    GlobalResponse<List<GroupDto>> getGroupListByProjectNameAndPermissionName(@PathVariable(name = "projectName") String projectName, @PathVariable(name = "permissionName") String permissionName);
    
    @PostMapping("/v1/api/group/amount_user_by_project")
    GlobalResponse<Void> amountUserByProject(@RequestBody AmountUserBean amountUserBean);

    @PostMapping("/v1/api/group/unamount_user_by_project")
    GlobalResponse<Void> unamountUserByProject(@RequestBody AmountUserBean amountUserBean);

    @PostMapping("/v1/api/group/reset_users_to_group")
    GlobalResponse<Boolean> resetUserToGroup(@RequestBody AmountUsersBean bean);

    @PostMapping("/v1/api/group/reset_permissions_to_group")
    GlobalResponse<Boolean> resetPermissionToGroup(@RequestBody AmountPermissionsBean bean);

    @GetMapping("/v1/api/permission/find_by_group/{groupName}")
    GlobalResponse<List<PermissionDto>> getPermissionListByGroupName(@PathVariable(name = "groupName") String groupName);

    @PostMapping("/v1/public/get_token_by_pwd")
    GlobalResponse<String> getTokenByPwd(@RequestBody UserTokenRequest request);

    @GetMapping("/v1/public/get_token_by_code")
    GlobalResponse<String> getTokenByCode(@RequestParam(name = "code", required = false) String code, @RequestParam(name = "clientId", required = false) String clientId, @RequestParam(name = "redirectUrl", required = false) String redirectUrl);

    @PostMapping("/v1/public/get_permissons_by_token")
    GlobalResponse<UserPermissionDto> getPermissonByToken(@RequestBody TokenRequestBean reqBean);

    @PostMapping("/v1/public/get_token_by_secret")
    GlobalResponse<String> getServiceTokenBySecret(@RequestBody ServiceTokenRequest request);

    @PostMapping("/v1/public/validate_service_token")
    GlobalResponse<Void> validateServiceToken(@RequestBody ServiceTokenRquestBean requestBean);
    
}
