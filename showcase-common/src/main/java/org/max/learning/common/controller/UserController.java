package org.max.learning.common.controller;

import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.max.learning.common.dto.User;
import org.max.learning.common.service.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.ApiOperation;

@CrossOrigin
@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    UserServiceImpl userServiceImpl;

    @ApiOperation(value = "add user", notes = "add user")
    @PostMapping("/add")
    public ResponseEntity<Long> addUser(
            HttpServletRequest req,
            HttpServletResponse resp,
            @RequestBody User requestBean) {
        Long id = userServiceImpl.add(requestBean);
        return new ResponseEntity<Long>(id, HttpStatus.OK);
    }

    @SuppressWarnings("rawtypes")
	@ApiOperation(value = "delete user", notes = "delete user")
    @DeleteMapping("/delete/{id}")
    public ResponseEntity delete(
            HttpServletRequest req,
            HttpServletResponse resp,
            @PathVariable("id") Long id) {
    	userServiceImpl.delete(id);
        return new ResponseEntity(HttpStatus.OK);
    }
    
    @ApiOperation(value = "edit user", notes = "edit user")
    @PutMapping("/edit")
    public ResponseEntity<User> edit(
            HttpServletRequest req,
            HttpServletResponse resp,
            @RequestBody User requestBean) {
        User result = userServiceImpl.save(requestBean);
        return new ResponseEntity<User>(result, HttpStatus.OK);
    }
    
    @ApiOperation(value = "list user", notes = "list user")
    @GetMapping("/find_all")
    public ResponseEntity<Page<User>> listAll(
            HttpServletRequest req,
            HttpServletResponse resp,
            @RequestParam int pageNumber,
            @RequestParam int pageSize) {
        Page<User> result = userServiceImpl.findAll(pageNumber, pageSize);
        return new ResponseEntity<Page<User>>(result, HttpStatus.OK);
    }

    @ApiOperation(value = "find user", notes = "find user")
    @GetMapping("/find_by_id/{id}")
    public ResponseEntity<User> findById(
            HttpServletRequest req,
            HttpServletResponse resp,
            @PathVariable("id") Long id) {
        User result = userServiceImpl.findById(id);
        return new ResponseEntity<User>(result, HttpStatus.OK);
    }
    
    @ApiOperation(value = "find user", notes = "find user")
    @GetMapping("/find_username_by_type/{type}")
    public ResponseEntity<Set<String>> findUsernameByType(
            HttpServletRequest req,
            HttpServletResponse resp,
            @PathVariable("type") String type) {
        Set<String> result = userServiceImpl.findUsernameByType(type);
        return new ResponseEntity<Set<String>>(result, HttpStatus.OK);
    }
    
}

