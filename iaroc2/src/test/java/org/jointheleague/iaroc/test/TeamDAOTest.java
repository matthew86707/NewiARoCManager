package org.jointheleague.iaroc.test;

import static org.junit.Assert.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.jointheleague.iaroc.db.DBUtils;
import org.jointheleague.iaroc.model.EntityManager;
import org.jointheleague.iaroc.model.TeamDAO;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class TeamDAOTest {

	private Connection con;

	@Before
	public void initialize() {
		con = DBUtils.createConnection();
		TeamDAO tmp = new TeamDAO(con);
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
		assertEquals(null, team.getIconUrl());

		team = new TeamDAO(con, 10101, "One and Only", "singleton.io", 0);
		assertEquals(10101, team.getId());
		assertEquals("The Only Team", team.getName());
		assertEquals("singleton.io", team.getIconUrl());

	}

	@Test
	public void TestFindAllTeams(){
		EntityManager.addDummyData(con);
		List<TeamDAO> teams = TeamDAO.retrieveAllEntries(con);
		assertNotNull(teams);
		assertEquals(3, teams.size());
		assertEquals("Blue Team", teams.get(0).getName());
	}

	@Test
	public void TestTeamDAOInDatabase() {
		try {
			PreparedStatement stmt;
			stmt = con.prepareStatement("SELECT COUNT(*) AS num FROM TEAMS;");
			ResultSet rs = stmt.executeQuery();
			rs.next();
			assertEquals(0, rs.getInt("num"));

			TeamDAO team = new TeamDAO(con, "33", "3333", 1);
			team.insert();
			stmt = con.prepareStatement("SELECT * FROM TEAMS;");
			rs = stmt.executeQuery();
			rs.next();
			assertEquals("33", rs.getString("name"));
			assertEquals("333", rs.getString("slogan"));
			assertEquals("3333", rs.getString("iconUrl"));

			assertEquals(false, rs.next());

			assertEquals(null, TeamDAO.loadById(-1, con));

			team.setName("H4CK3D");
			assertEquals("33", TeamDAO.loadById(team.getId(), con).getName());
			team.update();
			assertEquals("H4CK3D", TeamDAO.loadById(team.getId(), con).getName());

			team.delete();

			List<TeamDAO> teams = TeamDAO.retrieveAllEntries(con);
			assertEquals(0, teams.size());

		} catch (SQLException e) {
			Assert.fail("Failed insertion with id");
			e.printStackTrace();
		}
	}

}
