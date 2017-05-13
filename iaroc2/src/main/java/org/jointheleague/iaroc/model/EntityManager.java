package org.jointheleague.iaroc.model;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.jointheleague.iaroc.iaroc2.db.DBUtils;
import org.omg.CosNaming.NamingContextExtPackage.AddressHelper;

public class EntityManager {
	
	private static final String[] relationshipTableNames = {"MATCH_TO_TEAM", "TEAM_TO_MEMBER"};
	
	private static final String DELETE_TEAM_TO_MATCH_TABLE = "DELETE FROM MATCH_TO_TEAMS WHERE matchId = ?";
	
	private static final String DROP_TEAM_TO_MATCH_TABLE = "DROP TABLE MATCH_TO_TEAMS";
	
	private static final String SELECT_BY_MATCH = "SELECT * IN MATCH_TO_TEAMS WHERE matchId = ?";
	
	private static final String SELECT_BY_TEAM_AND_MATCH = "SELECT * IN MATCH_TO_TEAMS WHERE teamId = ? AND matchId = ?";
	
	private static final String INSERT_TEAM_TO_MATCH = "INSERT INTO MATCH_TO_TEAMS (teamId, matchId) values (?, ?)";
	
	private static final String DELETE_BY_TEAM_AND_MATCH = "DELETE * IN MATCH_TO_TEAMS WHERE teamId = ? AND matchId = ?";
	
	private static final String CREATE_TEAM_TO_MATCH_TABLE = "CREATE TABLE MATCH_TO_TEAMS"
			+ "(matchId INTEGER, "  
			+ "teamId INTEGER) ";
	
	
	public static void addDummyData(Connection con){
		
		createTables(con);
		
		TeamDAO t1 = new TeamDAO(con, "Red Team", "Red is ahead", "red.jpeg");
		TeamDAO t2 = new TeamDAO(con, "Blue Team", "Blue is cool", "blue.bin");
		TeamDAO t3 = new TeamDAO(con, "Purple Team", "mehh", "File Not Found");
		t1.insert();
		t2.insert();
		t3.insert();
		
		MemberDAO roger = new MemberDAO(con, "Roger", "Rabbit", "r.rabbit@reddit.com", t1.getId());
		MemberDAO bob = new MemberDAO(con, "Bob", "Barn", "b.barn@reddit.com", t1.getId());
		
		MemberDAO johnathan = new MemberDAO(con,  "johnathan", "jackrabbit", "j.rabbit@reddit.com", t2.getId());
		MemberDAO robert = new MemberDAO(con, "Robert", "Barner", "r.barner@reddit.com", t2.getId());
		
		MemberDAO ronathan = new MemberDAO(con, "Ronathan", "Babbit", "r.b@whitehouse.gov", t3.getId());
		
		roger.insert();
		bob.insert();
		johnathan.insert();
		robert.insert();
		ronathan.insert();
		
		//TODO: Add relationships to dummy data using new system
		
		List<Integer> teams1 = new ArrayList<Integer>();
		teams1.add(t1.getId());
		teams1.add(t2.getId());
		MatchDAO m1 = new MatchDAO(con, 0, 0);
		m1.insert();
		
		List<Integer> teams2 = new ArrayList<Integer>();
		teams2.add(t3.getId());
		MatchDAO m2 = new MatchDAO(con, 0, 12);
		m2.insert();

	}
	
	public static List<Integer> getTeamsByMatch(Connection con, int matchId){
		List<Integer> teamIds = new ArrayList<>();
		try {
			PreparedStatement stmt = con.prepareStatement(SELECT_BY_MATCH);
			stmt.setInt(1, matchId);

			ResultSet rs = stmt.executeQuery();
			while(rs.next()){
				teamIds.add(new Integer(rs.findColumn("teamId")));
			}
		} catch (SQLException e) {
		e.printStackTrace();
		}
		return teamIds;
	}
	
	public static void removeTeamFromRelationships(Connection con, int teamId){
		try{
		for(String tName : relationshipTableNames){
			PreparedStatement stmt = con.prepareStatement("DELETE FROM " + tName + " WHERE teamId = ?");
			stmt.setInt(0, teamId);
			stmt.executeQuery();
		}
		con.commit();
		}catch(SQLException e){
			e.printStackTrace();
		}
	}
	
	public static void insertRelationshipTeamToMatch(Connection con, int teamId, int matchId){
		try{
		PreparedStatement stmt = con.prepareStatement(SELECT_BY_TEAM_AND_MATCH);
		stmt.setInt(1, teamId);
		stmt.setInt(2, matchId);
		ResultSet rs = stmt.executeQuery();
		if(rs.next()){
			return;
		}
		PreparedStatement stmt2 = con.prepareStatement(INSERT_TEAM_TO_MATCH);
		stmt2.setInt(1, teamId);
		stmt2.setInt(2, matchId);
		stmt2.executeUpdate();
		con.commit();
		
		}catch (SQLException e){
			return;
		}
		
		
	}
	
	public static void deleteRelationshipTeamToMatchByTeam(Connection con, int teamId) {
		try{
		PreparedStatement stmt = con.prepareStatement(DELETE_BY_TEAM_AND_MATCH);
		stmt.setInt(1, teamId);
		stmt.executeUpdate();
		con.commit();
		}catch (SQLException e){
			
		}
	}
	
	public static void deleteRelationshipTeamToMatch(Connection con, int teamId, int matchId) {
		try{
		PreparedStatement stmt = con.prepareStatement(DELETE_BY_TEAM_AND_MATCH);
		stmt.setInt(1, teamId);
		stmt.setInt(2, matchId);
		stmt.executeUpdate();
		con.commit();
		}catch (SQLException e){
			
		}
	}
	
	public static void createRelationshipTables(Connection con) {
		try {
			//If table already exists, drop and recreate.
			for(String tName : relationshipTableNames){
				if(DBUtils.doesTableExist(tName, con))
				con.prepareStatement("DROP TABLE " + tName);
			}
			con.prepareStatement(CREATE_TEAM_TO_MATCH_TABLE).executeUpdate();
			con.commit();
		} catch (SQLException e) {
			e.printStackTrace();
		}	
	}
	public static void createTables(Connection con) {
		//Get Instances
		TeamDAO teams = new TeamDAO(con);
		MemberDAO members = new MemberDAO(con);
		MatchDAO matchs = new MatchDAO(con);
		//Create
		EntityManager.createRelationshipTables(con);
		teams.createTable();
		members.createTable();
		matchs.createTable();
	}
}
