package org.max.exam.service;

import java.util.List;

import org.max.exam.dao.UserDao;
import org.max.exam.entity.User;

public class UserService {

	UserDao userDao = new UserDao();
	
	public void add(User user) {
		userDao.create(user);
	}
	
	public void removeById(int id) {
		userDao.delete(id);
	}
	
	public List<User> findAll() {
		return userDao.retrieveAll();
	}
	
}
