package org.max.exam.controller;

import java.util.List;

import org.max.exam.dao.UserDao;
import org.max.exam.entity.User;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
public class UserController {

	UserDao userDao = new UserDao();
	
    @PostMapping(value = "/create")
    public ResponseEntity<String> create(@RequestBody User user) {
    	userDao.create(user);
        return new ResponseEntity<>("created", HttpStatus.OK);
    }
	
    @DeleteMapping(value = "/delete/{id}")
    public ResponseEntity<String> delete(@PathVariable(name = "id") int id) {
    	userDao.delete(id);
        return new ResponseEntity<>("deleted", HttpStatus.OK);
    }
    
    @PostMapping(value = "/find")
    public ResponseEntity<List<User>> findAll() {
    	List<User> userList = userDao.retrieveAll();
        return new ResponseEntity<>(userList, HttpStatus.OK);
    }
    
}
