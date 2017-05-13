package org.jointheleague.iaroc.test;

import static org.junit.Assert.*;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import org.jointheleague.iaroc.iaroc2.db.DBUtils;
import org.jointheleague.iaroc.model.EntityManager;
import org.jointheleague.iaroc.model.MatchDAO;
import org.jointheleague.iaroc.model.TeamDAO;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class EntityManagerTest {
	
	Connection con;
	
	//TODO : We are here...
	
	@Before
	public void initialize() {
		con = DBUtils.createConnection();
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
	public void testEntityRelationships(){
		EntityManager.addDummyData(con);
		List<MatchDAO> allMatches = MatchDAO.retrieveAllEntries(con);
		
		assertNotNull(allMatches);
		
		
		assertEquals(3, allMatches.size());
		
		assertEquals(2, EntityManager.getTeamsByMatch(con, allMatches.get(0).getId()).size());
		assertEquals(3, EntityManager.getTeamsByMatch(con, allMatches.get(1).getId()).size());
		assertEquals(1, EntityManager.getTeamsByMatch(con, allMatches.get(2).getId()).size());
		
	}

}
