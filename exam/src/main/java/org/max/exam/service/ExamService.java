package org.max.exam.service;

import java.util.List;

import org.max.exam.dao.ExamDao;
import org.max.exam.dao.ItemDao;
import org.max.exam.entity.Exam;
import org.max.exam.entity.Item;

public class ExamService {

	ExamDao examDao = new ExamDao();
	ItemDao itemDao = new ItemDao();
	
	public void add(Exam exam) {
		examDao.create(exam);
	}
	
	public void removeById(int id) {
		examDao.delete(id);
	}
	
	public List<Exam> findAll() {
		List<Exam> result = examDao.retrieveAll();
		for (Exam e : result) {
			List<Item> items = itemDao.retrieveByExamId(e.getId());
			e.setItemList(items);
		}
		return result;
	}
	
	public boolean checkIfExist(String username) {
		boolean result = false;
		if(examDao.countByUsername(username) > 0) {
			result = true;
		}
		return result;
	}
	
}
