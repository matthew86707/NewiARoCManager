package org.jointheleague.iaroc.test;

import static org.junit.Assert.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLSyntaxErrorException;

import org.jointheleague.iaroc.iaroc2.db.DBUtils;
import org.jointheleague.iaroc.model.DAO;
import org.jointheleague.iaroc.model.TeamDAO;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class TeamDAOTest {
	
	private Connection con;
	
	@Before
	public void initialize() {
		con = DBUtils.createConnection();
		TeamDAO tmp = new TeamDAO(con);
		
		PreparedStatement stmt;
		try {
			tmp.dropTable();
		} catch (Exception e) {
			// This means you have no table to drop.
			// And hopefully not a different Exception.
		}
		tmp.createTable();
	}
	
	@After
	public void finish() {
		try {
			TeamDAO tmp = new TeamDAO(con);
			tmp.dropTable();
			con.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void TestTeamDAO() {
		
		TeamDAO team = new TeamDAO(con);
		assertEquals(0, team.getId());
		assertEquals(null, team.getName());
		assertEquals(null, team.getSlogan());
		assertEquals(null, team.getIconUrl());
		
		team = new TeamDAO(con, 10101, "The Only Team", "One and Only", "singleton.io");
		assertEquals(10101, team.getId());
		assertEquals("The Only Team", team.getName());
		assertEquals("One and Only", team.getSlogan());
		assertEquals("singleton.io", team.getIconUrl());
		
	}
	
	@Test
	public void TestTeamDAOInDatabase() {		
		try {
			PreparedStatement stmt;
			stmt = con.prepareStatement("SELECT COUNT(*) AS num FROM TEAMS;");
			ResultSet rs = stmt.executeQuery();
			rs.next();
			assertEquals(0, rs.getInt("num"));
			
			TeamDAO team = new TeamDAO(con, "33", "333", "3333");
			team.insert();
			stmt = con.prepareStatement("SELECT * FROM TEAMS;");
			rs = stmt.executeQuery();
			rs.next();
			assertEquals("33", rs.getString("name"));
			assertEquals("333", rs.getString("slogan"));
			assertEquals("3333", rs.getString("iconUrl"));
			
			assertEquals(false, rs.next());
			
			assertEquals(null, TeamDAO.loadById(0, con));
			assertEquals(3, TeamDAO.loadById(3, con).getId());
			
			team.setName("H4CK3D");
			assertEquals("33", TeamDAO.loadById(3, con).getName());
			team.update();
			assertEquals("H4CK3D", TeamDAO.loadById(3, con).getName());
			
			team.delete();
			rs = stmt.executeQuery();
			rs.next();
			assertEquals(0, rs.getInt("num"));
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

}
