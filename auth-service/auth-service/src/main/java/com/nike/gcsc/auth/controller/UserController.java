package com.nike.gcsc.auth.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.validation.Valid;

import org.apache.commons.lang3.StringUtils;
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

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.nike.gcsc.auth.constant.AuthConstant;
import com.nike.gcsc.auth.constant.ErrorEnum;
import com.nike.gcsc.auth.constant.UserTypeEnum;
import com.nike.gcsc.auth.controller.request.PageBaseRequest;
import com.nike.gcsc.auth.controller.request.UserRequest;
import com.nike.gcsc.auth.entity.User;
import com.nike.gcsc.auth.service.UserService;
import com.nike.gcsc.auth.utils.Md5Utils;
import com.nike.gcsc.authapi.request.UserRequestBean;
import com.nike.gcsc.authapi.response.ChangePwdDto;
import com.nike.gcsc.authapi.response.UserDto;
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
@RequestMapping("/v1/api/user")
public class UserController {
	
    @Autowired
    UserService userService;

    @PostMapping("/check_exist_or_add")
    public GlobalResponse<Long> checkExistOrAdd(@RequestBody @Valid UserRequestBean bean) {
        if(log.isDebugEnabled()) {
            log.debug("checkExistOrAdd method call, request param is :{}" , JSONObject.toJSONString(bean));
        }
        bean.setUsername(bean.getUsername().trim().toLowerCase());
        User user = userService.findByUsername(bean.getUsername());
        if (null != user) {
            return GlobalResponse.buildSuccess(user.getId());
        }
        user = BeanMapper.map(bean, User.class);
        user.setType(UserTypeEnum.NIKE_USER.getCode());
        user.setStatus(AuthConstant.USER_STATUS.NORMAL);
        Long result = userService.add(user);
        return GlobalResponse.buildSuccess(result);
    }

    @PostMapping("/add")
    public GlobalResponse<Long> add(@RequestBody UserRequest bean) {
        log.debug("request param is :{}" , bean);
        if (StringUtils.isEmpty(bean.getUsername())) {
            return GlobalResponse.buildFail(ErrorEnum.PARAM_INVALID, "user name cannot be empty");
        }
        bean.setUsername(bean.getUsername().trim().toLowerCase());
        
        User user = userService.findByUsername(bean.getUsername());
        if(!Objects.isNull(user)) {
            return GlobalResponse.buildSuccess(user.getId());
        }
        
        user = BeanMapper.map(bean, User.class);
        
        if (StringUtils.isNotBlank(bean.getPassword())) {
            String encryptPassword = Md5Utils.encryptPassword(bean.getPassword(), Md5Utils.SECRET_KEY);
            user.setPassword(encryptPassword);
        }
        Long id = userService.add(user);
        return GlobalResponse.buildSuccess(id);
    }

    @DeleteMapping("/delete/{id}")
    public GlobalResponse<Boolean> delete(@PathVariable(name = "id") Long id) {
        try {
            userService.delete(id);
            log.debug("request param is :{}" , id);
            return GlobalResponse.buildSuccess(Boolean.TRUE);
        } catch (Exception e) {
            log.error(String.format("delete user fail ,user id:{}", id), e);
            return GlobalResponse.buildFail(ErrorEnum.NOT_CHANGE);
        }
    }
    
    @DeleteMapping("/disable/{id}")
    public GlobalResponse<Boolean> disable(@PathVariable(name = "id") Long id) {
        try {
            userService.disable(id);
            log.debug("request param is :{}" , id);
            return GlobalResponse.buildSuccess(Boolean.TRUE);
        } catch (Exception e) {
            log.error(String.format("disable user fail ,user id:{}", id), e);
            return GlobalResponse.buildFail(ErrorEnum.NOT_CHANGE);
        }
    }

    @PutMapping("/update/{id}")
    public GlobalResponse<Boolean> update(@PathVariable(name = "id") Long id, @RequestBody UserDto userDto) {
        try {
            User user = userService.findById(id);
            String username = (StringUtils.isNotEmpty(userDto.getUsername()) ? userDto.getUsername() : user.getUsername());
            String displayName = (StringUtils.isNotEmpty(userDto.getDisplayName()) ? userDto.getDisplayName() : user.getDisplayName());
            String email = (StringUtils.isNotEmpty(userDto.getEmail()) ? userDto.getEmail() : user.getEmail());
            String remark = StringUtils.isNotEmpty(userDto.getRemark()) ? userDto.getRemark() : user.getRemark();
            Integer type = (null != userDto.getType() ? userDto.getType() : user.getType());
            user.setDisplayName(displayName);
            user.setUsername(username);
            user.setEmail(email);
            user.setRemark(remark);
            user.setType(type);
            user.setStatus(userDto.getStatus());
            userService.edit(user);
            return GlobalResponse.buildSuccess(Boolean.TRUE);
        } catch (Exception e) {
            log.error(String.format("update user fail,user id:{},dto:{}", id, JSON.toJSONString(userDto)), e);
            return GlobalResponse.buildFail(ErrorEnum.SERVER_EXCEPTION, e.getMessage());
        }
    }
    
    @PutMapping("/update_pwd")
    public GlobalResponse<Boolean> updatePwd(@RequestBody @Valid ChangePwdDto dto) {
        if(null == dto.getUserId() && StringUtils.isBlank(dto.getUsername())) {
            log.error("update pwd method is Illegal entry,input:{}", JSONObject.toJSONString(dto));
            return GlobalResponse.buildFail(ErrorEnum.BAD_REQUEST);
        }
        try {
            User user = null;
            if(null != dto.getUserId()) {
                user = userService.findById(dto.getUserId());
            } else {
                user = userService.findByUsername(dto.getUsername().toLowerCase());
            }
            if(null == user) {
                log.error("update password method, user not found");
                return GlobalResponse.buildFail(ErrorEnum.BAD_REQUEST);
            }
            user.setPassword(Md5Utils.encryptPassword(dto.getPassword().trim(), Md5Utils.SECRET_KEY));
            userService.edit(user);
            return GlobalResponse.buildSuccess(Boolean.TRUE);
        } catch (Exception e) {
            return GlobalResponse.buildFail(ErrorEnum.SERVER_EXCEPTION, e.getMessage());
        }
    }

    @GetMapping("/find_all")
    public GlobalResponse<List<UserDto>> findAll() {
        List<User> userList = userService.findAll(0, Integer.MAX_VALUE).getContent();
        List<UserDto> userDtoList = BeanMapper.mapList(userList, UserDto.class);
        return GlobalResponse.buildSuccess(userDtoList);
    }

    @GetMapping("/find_by_username/{username}")
    public GlobalResponse<UserDto> findByUsername(@PathVariable(name = "username") String username) {
        User user = userService.findByUsername(username);
        UserDto dto = BeanMapper.map(user, UserDto.class);
        return GlobalResponse.buildSuccess(dto);
    }

    @GetMapping("/find_by_projectname_and_groupname/{projectname}/{groupname}")
    GlobalResponse<List<UserDto>> findByProjectnameAndGroupname(
            @PathVariable(name = "projectname") String projectname,
            @PathVariable(name = "groupname") String groupname) {
        List<User> userList = userService.findByProjectNameAndGroupName(projectname, groupname);
        List<UserDto> userDtoList = BeanMapper.mapList(userList, UserDto.class);
        return GlobalResponse.buildSuccess(userDtoList);
    }
    
    @PostMapping("/find_by_page")
    public GlobalResponse<Page<User>> findByPage(@RequestBody PageBaseRequest<User> pageRequest) {
        Page<User> result = userService.findByAttributes(pageRequest.getOptions(),pageRequest.getPageNum(),pageRequest.getPageSize());
        return GlobalResponse.buildSuccess(result);
    }

    @GetMapping("/find_exclude_by_groupid/{groupId}")
    GlobalResponse<List<UserDto>> findExcludeByGroupId(@PathVariable Long groupId) {
        List<User> userList = userService.findExcludeByGroupId(groupId);
        List<User> selectedList = userService.findByGroupId(groupId);
        List<User> newList = new ArrayList<>(userList);
        if (!newList.isEmpty()&&null!=selectedList&&!selectedList.isEmpty()) {
            newList.removeAll(selectedList);
        }
        List<UserDto> userDtoList = BeanMapper.mapList(newList, UserDto.class);
        return GlobalResponse.buildSuccess(userDtoList);
    }

    @GetMapping("/find_by_groupid/{groupId}")
    GlobalResponse<List<UserDto>> findByGroupId(@PathVariable Long groupId) {
        List<User> userList = userService.findByGroupId(groupId);
        List<UserDto> userDtoList = BeanMapper.mapList(userList, UserDto.class);
        return GlobalResponse.buildSuccess(userDtoList);
    }
}