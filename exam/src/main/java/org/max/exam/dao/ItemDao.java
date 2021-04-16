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
			String sql = "INSERT INTO `item` (`exam_id`, `question_id`, `student_answer`) VALUES (?, ?, ?)";
			pstmt = getConnection().prepareStatement(sql);
			pstmt.setInt(1, item.getExamId());
			pstmt.setInt(2, item.getQuestionId());
			pstmt.setString(3, item.getStudentAnswer());
			pstmt.executeUpdate();
			pstmt.close();
		} catch(SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void deleteByExamId(int id) {
		try {
			String sql = "DELETE FROM `item` WHERE `exam_id` = ?";
			pstmt = getConnection().prepareStatement(sql);
			pstmt.setInt(1, id);
			pstmt.executeUpdate();
			pstmt.close();
		} catch(SQLException e) {
			e.printStackTrace();
		}
	}
	
	public List<Item> retrieveByExamId(int examId) {
		List<Item> itemList = new ArrayList<Item>();
		try {
			String sql = "SELECT `id`, `question_id`, `student_answer` FROM `item` WHERE `exam_id` = ?";
			pstmt = getConnection().prepareStatement(sql);
			pstmt.setInt(1, examId);
			ResultSet rs = pstmt.executeQuery();
			while (rs.next()) {
				Item i = new Item();
				i.setId(rs.getInt("id"));
				i.setExamId(examId);
				i.setQuestionId(rs.getInt("question_id"));
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
