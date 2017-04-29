package org.jointheleague.iaroc.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.jointheleague.iaroc.iaroc2.db.DBUtils;

public class MatchDAO extends DAO{
	
	private static final String DROP_MATCHS = "DROP TABLE MATCHS";
	
	private static final String CREATE_MATCHS = "CREATE TABLE MATCHS"
			+ "(id INTEGER IDENTITY, "
			+ " "
			+ "status INTEGER, "  //0 = Upcoming, 1 = Current, 2 = Finished
			+ "unixTime BIGINT, " //BIGINT to protect against this from crashing in 20 years, although this code will probably only be used in 2017
			+ "PRIMARY KEY (id))";
	
	private static final String UPDATE_MATCHS = "UPDATE MATCHS SET status = ?, unixTime = ?, WHERE id = ?";
	
	private static final String DELETE_MATCH = "DELETE FROM MATCHS WHERE id = ?";
	
	private static final String SELECT_MATCH = "SELECT * FROM MATCHS WHERE id = ?";
	private static final String SELECT_ALL_MATCHS = "SELECT * FROM MATCHS";
	
	private static final String INSERT_MATCH = "INSERT INTO MATCHS (status, unixTime) VALUES (?, ?)";
	
	private static final String TABLE_NAME = "MATCHS";
	
	private static final String DELETE_TEAMS_RELATIONSHIP_TABLE = "DELETE FROM MATCH_TO_TEAMS WHERE matchId = ?";
	
	private static final String DROP_TEAMS_RELATIONSHIP_TABLE = "DROP TABLE MATCH_TO_TEAMS";
	
	private static final String SELECT_TEAMS_RELATIONSHIP_ROWS = "SELECT * IN MATCH_TO_TEAMS WHERE matchId = ?";
	
	private static final String INSERT_TEAMS_RELATIONSHIP_ROW = "INSERT INTO MATCH_TO_TEAMS (matchId, teamId) values (?, ?) WHERE id = ?";
	
	private static final String CREATE_TEAMS_RELATIONSHIP_TABLE = "CREATE TABLE MATCH_TO_TEAMS"
			+ "(id INTEGER NOT NULL, "
			+ "matchId INTEGER, "  
			+ "teamID INTEGER, " 
			+ "PRIMARY KEY (id))";
	
	private int id;
	private List<Integer> teams;
	private int status;
	private long unixTime; //This long will be pushed into a SQL BIGINT
	
	public MatchDAO(Connection con){
		super(con);
	}
	
	public MatchDAO(Connection con, int id, List<Integer> teams, int status, long unixTime){
		this(con, teams, status, unixTime);
		this.id = id;
	}
	
	public MatchDAO(Connection con, List<Integer> teams, int status, long unixTime){
		super(con);
		this.teams = teams;
		this.status = status;
		this.unixTime = unixTime;
	}
	

	@Override
	public void createTable() {
		try {
			//If table already exists, drop and recreate.
			if(DBUtils.doesTableExist(TABLE_NAME, con)) {
				dropTable();
			}
			this.con.prepareStatement(CREATE_MATCHS).executeUpdate();
			this.con.prepareStatement(CREATE_TEAMS_RELATIONSHIP_TABLE).executeUpdate();
			con.commit();
		} catch (SQLException e) {
			e.printStackTrace();
		}
			
	}
	
	@Override
	public void dropTable() {
		try {
			if(DBUtils.doesTableExist(TABLE_NAME, con)) {
				this.con.prepareStatement(DROP_MATCHS).executeUpdate();
				this.con.prepareStatement(DROP_TEAMS_RELATIONSHIP_TABLE).executeUpdate();
				con.commit();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}


	@Override
	public void update() {
		if(id == 0) {
			return;
		}
		try {
			
		PreparedStatement stmt;
		updateRelationships();
			
		stmt = con.prepareStatement(UPDATE_MATCHS);
			
			stmt.setInt(1, this.status);
			stmt.setLong(2, this.unixTime);
			stmt.setLong(3, this.id);
			stmt.executeUpdate();
			
			con.commit();
			
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	private void updateRelationships() throws SQLException {
		PreparedStatement stmt = con.prepareStatement(DELETE_TEAMS_RELATIONSHIP_TABLE);
		stmt.setInt(1, id);
		stmt.executeUpdate();
		
		stmt = con.prepareStatement(INSERT_TEAMS_RELATIONSHIP_ROW);
		for(Integer i : teams){
			stmt.setInt(1, this.id);
			stmt.setInt(2, i);
			stmt.setInt(3, this.id);
			stmt.executeUpdate();
		}
		con.commit();
	}


	@Override
	public void delete() {
		try {
		PreparedStatement stmt = con.prepareStatement(DELETE_MATCH);
			stmt.setInt(1, this.id);
			stmt.executeUpdate();
			con.commit();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public static List<MatchDAO> retrieveAllEntries(Connection con) {
		try{
			PreparedStatement stmt = con.prepareStatement(SELECT_ALL_MATCHS);
			ResultSet result = stmt.executeQuery();
			
			List<MatchDAO> matchs = new ArrayList<MatchDAO>();
			while(result.next()) {
				MatchDAO curResult = loadFromResult(result, con);
				matchs.add(curResult);
			}
			return matchs;
		}catch (SQLException e){
			e.printStackTrace();
		}
		return null;
	}

	private static MatchDAO loadFromResult(ResultSet result, Connection con) {
		try {
			int id = result.getInt(result.findColumn("id"));
			int status = result.getInt(result.findColumn("iconUrl"));
			long unixTime = result.getLong(result.findColumn("unixTime"));
			
			PreparedStatement stmt = con.prepareStatement(SELECT_TEAMS_RELATIONSHIP_ROWS);
			ResultSet rs = stmt.executeQuery();
			List<Integer> teams = new ArrayList<Integer>();
			while(rs.next()){
				teams.add(rs.getInt("teamId"));
			}
			return new MatchDAO(con, id, teams, status, unixTime);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static MatchDAO loadById(int id, Connection con) {
		try{
		PreparedStatement stmt = con.prepareStatement(SELECT_MATCH);
		stmt.setInt(1, id);
		ResultSet result = stmt.executeQuery();
		return loadFromResult(result, con);
		}catch (SQLException e){
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public void insert() {
		try{
		PreparedStatement stmt = con.prepareStatement(INSERT_MATCH, Statement.RETURN_GENERATED_KEYS);
		
		stmt.setInt(1, status);
		stmt.setLong(2, unixTime);
		stmt.executeUpdate();
		
		ResultSet rs = stmt.getGeneratedKeys();
		if(rs.next()){
			this.id = rs.getInt(1);
		}
		
		con.commit();
		
		updateRelationships();
		
		}catch (SQLException e){
			e.printStackTrace();
		}
		
	}		
}
