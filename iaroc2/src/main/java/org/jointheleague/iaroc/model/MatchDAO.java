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
			+ "status INTEGER, "  //0 = Upcoming, 1 = Current, 2 = Finished
			+ "unixTime BIGINT, " //BIGINT to protect against this from crashing in 20 years, although this code will probably only be used in 2017
			+ "PRIMARY KEY (id))";
	
	private static final String UPDATE_MATCHS = "UPDATE MATCHS SET status = ?, unixTime = ?, WHERE id = ?";
	
	private static final String DELETE_MATCH = "DELETE FROM MATCHS WHERE id = ?";
	
	private static final String SELECT_MATCH = "SELECT * FROM MATCHS WHERE id = ?";
	private static final String SELECT_ALL_MATCHS = "SELECT * FROM MATCHS";
	
	private static final String INSERT_MATCH = "INSERT INTO MATCHS (status, unixTime) VALUES (?, ?)";
	
	private static final String TABLE_NAME = "MATCHS";
	
	private int id;
	private int status;
	private long unixTime; //This long will be pushed into a SQL BIGINT
	
	public MatchDAO(Connection con){
		super(con);
	}
	
	public MatchDAO(Connection con, int id, int status, long unixTime){
		this(con, status, unixTime);
		this.id = id;
	}
	
	public MatchDAO(Connection con, int status, long unixTime){
		super(con);
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
		if(id < 0) {
			return;
		}
		try {
			
		PreparedStatement stmt;
			
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
			return new MatchDAO(con, id, status, unixTime);
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
		
		}catch (SQLException e){
			e.printStackTrace();
		}
		
	}		
}
