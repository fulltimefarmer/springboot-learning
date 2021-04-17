package org.max.exam.service;

import java.util.List;

import org.max.exam.dao.UserDao;
import org.max.exam.entity.User;
import org.max.exam.util.MD5Util;

public class UserService {

	UserDao userDao = new UserDao();
	
	public void add(User user) {
		String plain = user.getPassword();
		user.setPassword(MD5Util.encrypt(plain));
		userDao.create(user);
	}
	
	public void removeById(int id) {
		userDao.delete(id);
	}
	
	public List<User> findAll() {
		return userDao.retrieveAll();
	}
	
	public boolean check(String username, String password) {
		boolean isValid = false;
		User user = userDao.retrieveByUsername(username);
		if (user != null && user.getPassword().equals(MD5Util.encrypt(password))) {
			isValid = true;
		}
		return isValid;
	}
	
}
