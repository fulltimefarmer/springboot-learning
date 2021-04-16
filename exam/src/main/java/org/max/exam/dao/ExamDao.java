package org.max.exam.dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.max.exam.entity.Exam;

public class ExamDao extends DBConnect {

	PreparedStatement pstmt = null;
	Statement stmt = null;
	
	public void create(Exam exam) {
		try {
			String sql = "INSERT INTO `exam` (`username`, `score`) VALUES (?, ?)";
			pstmt = getConnection().prepareStatement(sql);
			pstmt.setString(1, exam.getUsername());
			pstmt.setString(2, exam.getScore());
			pstmt.executeUpdate();
			pstmt.close();
		} catch(SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void delete(int id) {
		try {
			String sql = "DELETE FROM `exam` WHERE `id` = ?";
			pstmt = getConnection().prepareStatement(sql);
			pstmt.setInt(1, id);
			pstmt.executeUpdate();
			pstmt.close();
		} catch(SQLException e) {
			e.printStackTrace();
		}
	}
	
	public List<Exam> retrieveAll() {
		List<Exam> examList = new ArrayList<Exam>();
		try {
			String sql = "SELECT `id`, `username`, `score` FROM `exam`";
			stmt = getConnection().createStatement();
			ResultSet rs = stmt.executeQuery(sql);
			while (rs.next()) {
				Exam e = new Exam();
				e.setId(rs.getInt("id"));
				e.setUsername(rs.getString("username"));
				e.setScore(rs.getString("score"));
				examList.add(e);
			}
			stmt.close();
			rs.close();
		} catch(SQLException e) {
			e.printStackTrace();
		}
		return examList;
	}
	
	public int countByUsername(String username) {
		int count = 0;
		try {
			String sql = "SELECT COUNT(1) FROM `exam` WHERE `username` = ?";
			pstmt = getConnection().prepareStatement(sql);
			pstmt.setString(1, username);
			ResultSet rs = pstmt.executeQuery();
			while (rs.next()) {
				count = rs.getInt(1);
			}
			pstmt.close();
			rs.close();
		} catch(SQLException e) {
			e.printStackTrace();
		}
		return count;
	}
	
}
