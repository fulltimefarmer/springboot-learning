package com.nike.gcsc.auth.controller;

import com.alibaba.fastjson.JSON;
import com.nike.gcsc.auth.constant.ErrorEnum;
import com.nike.gcsc.auth.constant.UserTypeEnum;
import com.nike.gcsc.auth.controller.request.PageBaseRequest;
import com.nike.gcsc.auth.controller.request.UserRequest;
import com.nike.gcsc.auth.dto.User;
import com.nike.gcsc.auth.service.UserService;
import com.nike.gcsc.auth.utils.BeanValidationUtil;
import com.nike.gcsc.auth.utils.Md5Utils;
import com.nike.gcsc.authapi.request.UserRequestBean;
import com.nike.gcsc.authapi.response.UserDto;
import com.nike.gcsc.common.GlobalResponse;
import com.nike.gcsc.util.BeanMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
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
@RequestMapping("/v1/api/user")
public class UserController {

    @Autowired
    UserService userService;

    @PostMapping("/check_exist_or_add")
    public GlobalResponse<Long> checkExistOrAdd(@RequestBody UserRequestBean bean) {
        log.debug("request param is :{}" , bean);
        try {
            BeanValidationUtil.checkSaveUserBean(bean);
        } catch (Exception e) {
            return GlobalResponse.buildFail(ErrorEnum.PARAM_INVALID, e.getMessage());
        }
        User user = userService.findByUsername(bean.getUsername());
        if (null != user) {
            return GlobalResponse.buildSuccess(user.getId());
        }
        user = BeanMapper.map(bean, User.class);
        user.setType(UserTypeEnum.NIKE_USER.getCode());
        Long result = userService.add(user);
        return GlobalResponse.buildSuccess(result);
    }

    @PostMapping("/add")
    public GlobalResponse<Long> add(@RequestBody UserRequest requestBean) {
        log.debug("request param is :{}" , requestBean);
        if (StringUtils.isEmpty(requestBean.getUsername())) {
            return GlobalResponse.buildFail(ErrorEnum.PARAM_INVALID, "user name cannot be empty");
        }
        User user = BeanMapper.map(requestBean, User.class);
        if (StringUtils.isNotEmpty(requestBean.getPassword())) {
            String encryptPassword = Md5Utils.encryptPassword(requestBean.getPassword(), Md5Utils.SECRET_KEY);
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
            userService.edit(user);
            return GlobalResponse.buildSuccess(Boolean.TRUE);
        } catch (Exception e) {
            log.error(String.format("update user fail,user id:{},dto:{}", id, JSON.toJSONString(userDto)), e);
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

}