package org.max.exam.service;

import java.util.List;

import org.max.exam.dao.ItemDao;
import org.max.exam.dao.QuestionDao;
import org.max.exam.entity.Item;
import org.max.exam.entity.Question;

public class ItemService {

	ItemDao itemDao = new ItemDao();
	QuestionDao questionDao = new QuestionDao();
	
	public void add(Item item) {
		itemDao.create(item);
	}
	
	public void removeByExamId(int examId) {
		itemDao.deleteByExamId(examId);
	}
	
	public List<Item> findByExamId(int examId) {
		List<Item> result = itemDao.retrieveByExamId(examId);
		for (Item i : result) {
			Question q = questionDao.retrieveByIdFromCache(i.getQuestionId());
			i.setQuestion(q);
		}
		return result;
	}
}
