package org.max.exam.dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * @author: Max zhou
 * @date: March 30th, 2021
 */
public class DaoModel {

	// Declare DB objects
	DBConnect conn = null;
	Statement stmt = null;
	PreparedStatement pstmt = null;
	private static String TABLE_NAME = "j_zhou_tab";

	// constructor
	public DaoModel() { // create db object instance
		conn = new DBConnect();
	}

	/**
	 * @description Create Table
	 * @exception SQLException if createTable method fails
	 */
	public void createTable() {
		try {
			// Open a connection
			System.out.println("Connecting to database to create Table...");
			System.out.println("Connected database successfully...");

			// Execute create query
			System.out.println("Creating table in given database...");
			stmt = conn.getConnection().createStatement();
			
			// drop TABLE if table already exist.
			String sql = "DROP TABLE IF EXISTS " + TABLE_NAME;
			stmt.executeUpdate(sql);
			
			sql = "CREATE TABLE " 
					+ TABLE_NAME 
					+ " (pid INTEGER not NULL AUTO_INCREMENT, " 
					+ " id VARCHAR(10), " 
					+ " income numeric(8,2), " 
					+ " pep VARCHAR(3), " 
					+ " PRIMARY KEY ( pid ))";
			stmt.executeUpdate(sql);
			System.out.println("Created table in given database...");
		} catch (SQLException se) { // Handle errors for JDBC
			System.err.println("DaoModel::createTable::SQLException while create table.");
			se.printStackTrace();
		} finally {
			try {
				// close statement & connection
				stmt.close();
				conn.getConnection().close();
			} catch (SQLException e) {
				System.err.println("DaoModel::createTable::SQLException while closing db connection.");
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * @description Insert Records
	 * @param robjs
	 * @exception SQLException if insertRecords method fails
	 */
	public void insertRecords(BankRecords[] robjs) {
		try {
			// Execute a query
			System.out.println("Inserting records into the table...");
			
			String sql = "INSERT INTO " + TABLE_NAME + " (id, income, pep) VALUES (?, ?, ?)";
			pstmt = conn.getConnection().prepareStatement(sql);
			// Include all object data to the database table
			for (int i = 0; i < robjs.length; ++i) {
				BankRecords record = robjs[i];
				// finish string assignment below to insert all array object data
				// (id, income, pep) into your database table
				pstmt.setString(1, record.getId());
				pstmt.setDouble(2, record.getIncome());
				pstmt.setString(3, record.getPep());
				pstmt.executeUpdate();
			}
			System.out.println("Records inserted!");
		} catch (SQLException se) {
			System.err.println("DaoModel::insertRecords::SQLException while insert records.");
			se.printStackTrace();
		} finally {
			try {
				// close statement & connection
				stmt.close();
				conn.getConnection().close();
			} catch (SQLException e) {
				System.err.println("DaoModel::insertRecords::SQLException while closing db connection.");
				e.printStackTrace();
			}
		}
	}
		
	/**
	 * @description Retrieve all records
	 * @return ResultSet
	 * @exception SQLException if retrieveRecords method fails
	 */
	public ResultSet retrieveRecords() {
		ResultSet rs = null;
		try {
			String sql = "SELECT pid, id,income, pep FROM " + TABLE_NAME + " ORDER BY pep DESC, id";
			stmt = conn.getConnection().createStatement();
			rs = stmt.executeQuery(sql);
		} catch (SQLException se) {
			System.err.println("DaoModel::retrieveRecords::SQLException while retrieve records.");
			se.printStackTrace();
		} finally {
			try {
				// close connection
				conn.getConnection().close();
			} catch (SQLException e) {
				System.err.println("DaoModel::retrieveRecords::SQLException while closing db connection.");
				e.printStackTrace();
			}
		}
		return rs;
	}
	
}
