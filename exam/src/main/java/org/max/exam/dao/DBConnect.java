package org.max.exam.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnect {

	protected Connection connection;

	public Connection getConnection() {
		return connection;
	}

	private static String url = "jdbc:mysql://localhost:3306/510labs";
	private static String username = "admin";
	private static String password = "1234";

	public DBConnect() {
		try {
			connection = DriverManager.getConnection(url, username, password);
		} catch (SQLException e) {
			System.out.println("Error creating connection to database: " + e);
			System.exit(-1);
		}
	}
}
