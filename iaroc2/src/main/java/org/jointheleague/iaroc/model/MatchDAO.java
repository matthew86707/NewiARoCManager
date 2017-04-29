package org.jointheleague.iaroc.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.jointheleague.iaroc.iaroc2.db.DBUtils;

public class MatchDAO extends DAO{
	
	private static final String DROP_MATCHS = "DROP TABLE MATCHS";
	
	private static final String CREATE_MATCHS = "CREATE TABLE MATCHS"
			+ "(id INTEGER NOT NULL, "
			+ "teamA INTEGER, "
			+ "teamB INTEGER, "
			+ "status INTEGER, "  //0 = Upcoming, 1 = Current, 2 = Finished
			+ "unixTime BIGINT, " //BIGINT to protect against this from crashing in 20 years, although this code will probably only be used in 2017
			+ "PRIMARY KEY (id))";
	
	private static final String UPDATE_MATCHS = "UPDATE MATCHS SET teamA = ?, teamB = ?, status = ?, unixTime = ?, WHERE id = ?";
	
	private static final String DELETE_MATCH = "DELETE FROM MATCHS WHERE id = ?";
	
	private static final String SELECT_MATCH = "SELECT * FROM MATCHS WHERE id = ?";
	private static final String SELECT_ALL_MATCHS = "SELECT * FROM MATCHS";
	
	private static final String INSERT_MATCH = "INSERT INTO MATCHS (teamA, teamB, status, unixTime) VALUES (?, ?, ?, ?)";
	
	private static final String TABLE_NAME = "MATCHS";
	
	private int id;
	private int teamA;
	private int teamB;
	private int status;
	private long unixTime; //This long will be pushed into a SQL BIGINT
	
	public MatchDAO(Connection con){
		super(con);
	}
	
	public MatchDAO(Connection con, int id, int teamA, int teamB, int status, long unixTime){
		super(con);
		this.teamA = teamA;
		this.teamB = teamB;
		this.status = status;
		this.unixTime = unixTime;
		this.id = id;
	}
	

	@Override
	public void createTable() {
		try {
			//If table already exists, drop and recreate.
			if(DBUtils.doesTableExist(TABLE_NAME, con)) {
				dropTable();
			}
			this.con.prepareStatement(CREATE_MATCHS).executeUpdate();
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
		PreparedStatement stmt = con.prepareStatement(UPDATE_MATCHS);
			stmt.setInt(1, this.teamA);
			stmt.setInt(2, this.teamB);
			stmt.setInt(3, this.status);
			stmt.setLong(4, this.unixTime);
			stmt.setLong(5, this.id);
			stmt.executeUpdate();
			con.commit();
		} catch (SQLException e) {
			e.printStackTrace();
		}
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
			int teamA = result.getInt(result.findColumn("teamA"));
			int teamB = result.getInt(result.findColumn("teamB"));
			int status = result.getInt(result.findColumn("iconUrl"));
			long unixTime = result.getLong(result.findColumn("unixTime"));
			return new MatchDAO(con, id, teamA, teamB, status, unixTime);
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
		PreparedStatement stmt = con.prepareStatement(INSERT_MATCH);
		stmt.setInt(1, teamA);
		stmt.setInt(2, teamB);
		stmt.setInt(3, status);
		stmt.setLong(4, unixTime);
		stmt.executeUpdate();
		con.commit();
		}catch (SQLException e){
			e.printStackTrace();
		}
		
	}		
}
