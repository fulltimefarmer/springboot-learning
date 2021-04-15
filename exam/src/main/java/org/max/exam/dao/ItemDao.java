package org.max.exam.dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.max.exam.entity.Item;

public class ItemDao extends DBConnect {

	PreparedStatement pstmt = null;
	Statement stmt = null;
	
	public void create(Item item) {
		try {
			String sql = "INSERT INTO `item` (`exam_id`, `question_id`, `correct_answer`, `student_answer`) VALUES (?, ?, ?, ?)";
			pstmt = getConnection().prepareStatement(sql);
			pstmt.setInt(1, item.getExamId());
			pstmt.setInt(2, item.getQuestionId());
			pstmt.setString(3, item.getCorrectAnswer());
			pstmt.setString(4, item.getStudentAnswer());
			pstmt.executeUpdate();
		} catch(SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void deleteByExamId(int id) {
		try {
			String sql = "DELETE FROM `item` WHERE `id` = ?";
			pstmt = getConnection().prepareStatement(sql);
			pstmt.setInt(1, id);
			pstmt.executeUpdate();
		} catch(SQLException e) {
			e.printStackTrace();
		}
	}
	
	public List<Item> retrieveByExamId(int id) {
		List<Item> itemList = new ArrayList<Item>();
		try {
			String sql = "SELECT `id`, `exam_id`, `question_id`, `correct_answer`, `student_answer` FROM `item`";
			stmt = getConnection().createStatement();
			ResultSet rs = stmt.executeQuery(sql);
			while (rs.next()) {
				Item i = new Item();
				i.setId(rs.getInt("id"));
				i.setExamId(rs.getInt("exam_id"));
				i.setQuestionId(rs.getInt("question_id"));
				i.setCorrectAnswer(rs.getString("correct_answer"));
				i.setStudentAnswer(rs.getString("student_answer"));
				itemList.add(i);
			}
			stmt.close();
			rs.close();
		} catch(SQLException e) {
			e.printStackTrace();
		}
		return itemList;
	}
	
}
