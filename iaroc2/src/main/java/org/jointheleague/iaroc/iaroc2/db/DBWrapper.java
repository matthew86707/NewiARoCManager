package org.jointheleague.iaroc.iaroc2.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import org.jointheleague.iaroc.model.TeamDAO;

public class DBWrapper{
	
	private static final String STARTUP_MEMBERS = "CREATE TABLE MEMBERS "
			+ "(id INTEGER NOT NULL, "
			+ "first VARCHAR(255), "
			+ "last VARCHAR(255), "
			+ "email VARCHAR(255), "
			+ "teamId INTEGER, "
			+ "PRIMARY KEY (id))";
			
	
	private static final String CLEAR_MEMBERS = "DROP TABLE MEMBERS";
	
	private Connection con;
	
	public DBWrapper(){
		try {
			con = DriverManager.getConnection("jdbc:hsqldb:file:testdb", "SA", "");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void addTeam(String name, )
	
	public void createTables() throws SQLException{
		Statement stmt = con.createStatement();
		//stmt.executeUpdate(TeamDAO.createTable());
		stmt.executeUpdate(STARTUP_MEMBERS);
	}
	
	
	public void dropTables() throws SQLException{
		Statement stmt = con.createStatement();
		stmt.executeUpdate(CLEAR_TEAMS);
		stmt.executeUpdate(CLEAR_MEMBERS);
	}

}
