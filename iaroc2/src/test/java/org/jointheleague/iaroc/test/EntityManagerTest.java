package org.jointheleague.iaroc.test;

import static org.junit.Assert.*;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import org.jointheleague.iaroc.db.DBUtils;
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


		assertEquals(5, allMatches.size());

		List<EntityManager.MatchResultData> matchResults = EntityManager.getAllMatchResults(con);

		assertEquals(7, matchResults.size());

		//The first entry should be the gold rush
		assertEquals(MatchDAO.TYPES.GOLD_RUSH, allMatches.get(0).getType());


		//We expect the gold rush to have 3 teams.
		assertEquals(3, EntityManager.getTeamsByMatch(con, allMatches.get(0).getId()).size());

		assertEquals(MatchDAO.TYPES.MAZE, allMatches.get(1).getType());

		//Then, the two mazes
		assertEquals(MatchDAO.TYPES.MAZE, allMatches.get(2).getType());

		//Aaand the drag races
		assertEquals(MatchDAO.TYPES.DRAG_RACE, allMatches.get(3).getType());

		assertEquals(MatchDAO.TYPES.DRAG_RACE, allMatches.get(4).getType());

		//Which we expect to have only one team, since they are essentially solo events.
		assertEquals(1, EntityManager.getTeamsByMatch(con, allMatches.get(4).getId()).size());

	}

	@Test
	public void testCalculateEventResults() {
		EntityManager.addDummyData(con);

		boolean includeNonFinalResults = false;

		List<EntityManager.MatchResultData> results =
				EntityManager.calculateEventResults(con, MatchDAO.TYPES.DRAG_RACE, includeNonFinalResults);

		assertNotNull(results);

		//We are expecting only 2 results, as only two teams have been logged for this event.
		assertEquals(2, results.size());

		//Now that we have established we have the correct #, let's go ahead and make sure points are calculated correctly.

		//Team 2 (Which we expect to get assigned ID 1) got the better score.
		// So, it should show up first in the results and have higher placement pts.
		assertEquals(1, results.get(0).teamId);
		//There are only 2 results. So, we expect team 2 to have one placement point.
		assertEquals(1, results.get(0).placementPoints);
		//We expect the other participant in this match to not have any placement points, as they came in last.
		assertEquals(0, results.get(1).placementPoints);
		//But, they did get 5 bonus points for not touching the walls.
		assertEquals(5, results.get(1).bonusPoints);
		//And therefore a total of 5
		assertEquals(5, results.get(1).totalPoints);


		//For the other event's dummy data, we have one team whose results have not come in yet. So,
		//We won't want to count that as a low-scoring team and boost teams that have finished,
		//As that means those teams scores might drop when the scores do come in.
		List<EntityManager.MatchResultData> results2 =
				EntityManager.calculateEventResults(con, MatchDAO.TYPES.GOLD_RUSH, includeNonFinalResults);

		assertNotNull(results2);

		//We are expecting only 2 results, as only two teams have been logged for this event.
		assertEquals(2, results2.size());

		//We expect the top team to have a time of 30 seconds and 1 placement point for beating
		//the other finalized result, which failed to finish the event.
		assertEquals(30000, results2.get(0).time);
		assertEquals(1, results2.get(0).placementPoints);
	}

}
