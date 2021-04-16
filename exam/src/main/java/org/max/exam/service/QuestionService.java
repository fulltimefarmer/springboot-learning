package org.max.exam.service;

import java.util.List;

import org.max.exam.dao.QuestionDao;
import org.max.exam.entity.Question;

public class QuestionService {

	QuestionDao questionDao = new QuestionDao();
	
	public void add(Question question) {
		questionDao.create(question);
	}
	
	public void removeById(int id) {
		questionDao.delete(id);
	}
	
	public List<Question> findAll() {
		return questionDao.retrieveAll();
	}
	
	public Question retrieveById(int id) {
		return questionDao.retrieveById(id);
	}
	
}
