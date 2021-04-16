package org.max.exam.dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.max.exam.entity.User;

public class UserDao extends DBConnect {

	PreparedStatement pstmt = null;
	Statement stmt = null;
	
	public void create(User user) {
		try {
			String sql = "INSERT INTO `user` (`username`, `password`, `role`) VALUES (?, ?, ?)";
			pstmt = getConnection().prepareStatement(sql);
			pstmt.setString(1, user.getUsername());
			pstmt.setString(2, user.getPassword());
			pstmt.setString(3, user.getRole());
			pstmt.executeUpdate();
			pstmt.close();
		} catch(SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void delete(int id) {
		try {
			String sql = "DELETE FROM `user` WHERE `id` = ?";
			pstmt = getConnection().prepareStatement(sql);
			pstmt.setInt(1, id);
			pstmt.executeUpdate();
			pstmt.close();
		} catch(SQLException e) {
			e.printStackTrace();
		}
	}
	
	public List<User> retrieveAll() {
		List<User> userList = new ArrayList<User>();
		try {
			String sql = "SELECT `id`, `username`, `password`, `role` FROM `user`";
			stmt = getConnection().createStatement();
			ResultSet rs = stmt.executeQuery(sql);
			while (rs.next()) {
				User u = new User();
				u.setId(rs.getInt("id"));
				u.setUsername(rs.getString("username"));
				u.setPassword(rs.getString("password"));
				u.setRole(rs.getString("role"));
				userList.add(u);
			}
			stmt.close();
			rs.close();
		} catch(SQLException e) {
			e.printStackTrace();
		}
		return userList;
	}
	
}
