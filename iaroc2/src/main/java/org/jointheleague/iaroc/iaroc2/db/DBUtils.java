package org.jointheleague.iaroc.iaroc2.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import org.jointheleague.iaroc.model.MemberDAO;
import org.jointheleague.iaroc.model.TeamDAO;

public class DBUtils{	
	
	public static Connection createConnection(){
		try {
			return DriverManager.getConnection("jdbc:hsqldb:file:testdb", "SA", "");
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
}
