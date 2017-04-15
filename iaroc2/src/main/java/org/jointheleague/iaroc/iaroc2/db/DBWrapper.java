package org.jointheleague.iaroc.iaroc2.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import org.jointheleague.iaroc.model.MemberDAO;
import org.jointheleague.iaroc.model.TeamDAO;

public class DBWrapper{
	
	private Connection con;
	
	public DBWrapper(){
		try {
			con = DriverManager.getConnection("jdbc:hsqldb:file:testdb", "SA", "");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void createTables() throws SQLException{
		Statement stmt = con.createStatement();
		stmt.executeUpdate(new TeamDAO().createTable());
		stmt.executeUpdate(new MemberDAO().createTable());
		stmt.close();
	}
	
	public void dropTables() throws SQLException{
		Statement stmt = con.createStatement();
		stmt.executeUpdate(new TeamDAO().deleteTable());
		stmt.executeUpdate(new MemberDAO().delete());
		stmt.close();
	}

}
