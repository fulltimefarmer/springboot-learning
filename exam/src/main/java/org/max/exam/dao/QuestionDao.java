package org.max.exam.dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.max.exam.entity.Question;

public class QuestionDao extends DBConnect {

	PreparedStatement pstmt = null;
	Statement stmt = null;
	Map<Integer, Question> questions = new HashMap<>();
	
	public void create(Question question) {
		try {
			String sql = "INSERT INTO `question` (`subject`, `item1`, `item2`, `item3`, `item4`, `answer`) VALUES (?, ?, ?, ?, ?, ?)";
			pstmt = getConnection().prepareStatement(sql);
			pstmt.setString(1, question.getSubject());
			pstmt.setString(2, question.getItem1());
			pstmt.setString(3, question.getItem2());
			pstmt.setString(4, question.getItem3());
			pstmt.setString(5, question.getItem4());
			pstmt.setString(6, question.getAnswer());
			pstmt.executeUpdate();
			pstmt.close();
		} catch(SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void delete(int id) {
		try {
			String sql = "DELETE FROM `question` WHERE `id` = ?";
			pstmt = getConnection().prepareStatement(sql);
			pstmt.setInt(1, id);
			pstmt.executeUpdate();
			pstmt.close();
		} catch(SQLException e) {
			e.printStackTrace();
		}
	}
	
	public List<Question> retrieveAll() {
		this.questions.clear();
		List<Question> questionList = new ArrayList<Question>();
		try {
			String sql = "SELECT `id`, `subject`, `item1`, `item2`, `item3`, `item4`, `answer` FROM `question`";
			stmt = getConnection().createStatement();
			ResultSet rs = stmt.executeQuery(sql);
			while (rs.next()) {
				Question q = new Question();
				q.setId(rs.getInt("id"));
				q.setSubject(rs.getString("subject"));
				q.setItem1(rs.getString("item1"));
				q.setItem2(rs.getString("item2"));
				q.setItem3(rs.getString("item3"));
				q.setItem4(rs.getString("item4"));
				q.setAnswer(rs.getString("answer"));
				questionList.add(q);
				this.questions.put(q.getId(), q);
			}
			stmt.close();
			rs.close();
		} catch(SQLException e) {
			e.printStackTrace();
		}
		return questionList;
	}
	
	public Map<Integer, Question> retrieveAllFromCache() {
		if (this.questions.size() == 0) {
			retrieveAll();
		}
		return this.questions;
	}
	
	public Question retrieveById(int id) {
		Question result = null;
		try {
			String sql = "SELECT `id`, `subject`, `item1`, `item2`, `item3`, `item4`, `answer` FROM `question` WHERE `id` = ?";
			pstmt = getConnection().prepareStatement(sql);
			pstmt.setInt(1, id);
			ResultSet rs = pstmt.executeQuery();
			while (rs.next()) {
				result = new Question();
				result.setId(rs.getInt("id"));
				result.setSubject(rs.getString("subject"));
				result.setItem1(rs.getString("item1"));
				result.setItem2(rs.getString("item2"));
				result.setItem3(rs.getString("item3"));
				result.setItem4(rs.getString("item4"));
				result.setAnswer(rs.getString("answer"));
				this.questions.put(result.getId(), result);
			}
			pstmt.close();
			rs.close();
		} catch(SQLException e) {
			e.printStackTrace();
		}
		return result;
	}
	
	public Question retrieveByIdFromCache(int id) {
		if (this.questions.get(id) == null) {
			return retrieveById(id);
		}
		return this.questions.get(id);
	}
}
