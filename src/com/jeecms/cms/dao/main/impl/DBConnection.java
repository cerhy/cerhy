package com.jeecms.cms.dao.main.impl;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

public class DBConnection {
	private static String username;
	private static String url;
	private static String password;

	public static Connection getConnection(String path) {
		getConfig(path);
		Connection con = null;
		try {
			con = DriverManager.getConnection(url, username, password);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return con;
	}

	public static void closeDB(Connection con, Statement stmt, ResultSet rs) {
		try {
			if (rs != null)
				rs.close();
			if (stmt != null)
				stmt.close();
			if (con != null)
				con.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public static void getConfig(String path) {
//		path = DBConnection.class.getClass().getResource("/").getPath();
//		path = path.substring(1, path.indexOf("classes"));
		Properties props = new Properties();
		try {
			props.load(new FileInputStream(path + "/WEB-INF/config/jdbc.properties"));
			url = (String) props.get("jdbc.url");
			username = (String) props.get("jdbc.username");
			password = (String) props.get("jdbc.password");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
