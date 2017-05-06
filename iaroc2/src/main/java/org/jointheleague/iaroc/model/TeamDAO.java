package org.jointheleague.iaroc.model;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.jointheleague.iaroc.iaroc2.db.DBUtils;

public class TeamDAO extends DAO{
	
	private static final String DROP_TEAMS = "DROP TABLE TEAMS";
	
	private static final String CREATE_TEAMS = "CREATE TABLE TEAMS "
			+ "(id INTEGER IDENTITY, "
			+ "name VARCHAR(255), "
			+ "iconUrl VARCHAR(255), "
			+ "slogan VARCHAR(255), "
			+ "PRIMARY KEY (id))";
	
	private static final String UPDATE_TEAMS = "UPDATE TEAMS SET name = ?, slogan = ?, iconUrl = ? WHERE id = ?";
	
	private static final String DELETE_TEAM = "DELETE FROM TEAMS WHERE id = ?";
	
	private static final String SELECT_TEAM = "SELECT * FROM TEAMS WHERE id = ?";
	
	private static final String INSERT_TEAM = "INSERT INTO TEAMS (name, slogan, iconUrl) VALUES (?, ?, ?)";
	
	private static final String SELECT_ALL_TEAMS = "SELECT * FROM TEAMS ORDER BY name ASC";
	
	private int id;
	private String name;
	private String slogan;
	private String iconUrl;
	
	public TeamDAO(Connection con){
		super(con);
	}
	
	public TeamDAO(Connection con, String name, String slogan, String iconUrl){
		super(con);
		this.name = name;
		this.slogan = slogan;
		this.iconUrl = iconUrl;
	}
	
	public TeamDAO(Connection con, int id, String name, String slogan, String iconUrl){
		super(con);
		this.name = name;
		this.slogan = slogan;
		this.iconUrl = iconUrl;
		this.id = id;
	}
	
	public int getId() {
		return id;
	}

	private void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSlogan() {
		return slogan;
	}

	public void setSlogan(String slogan) {
		this.slogan = slogan;
	}

	public String getIconUrl() {
		return iconUrl;
	}

	public void setIconUrl(String iconUrl) {
		this.iconUrl = iconUrl;
	}

	@Override
	public void createTable() {
		try {
			//If table already exists, drop and recreate.
			if(DBUtils.doesTableExist("TEAMS", con)) {
				dropTable();
			}
			this.con.prepareStatement(CREATE_TEAMS).executeUpdate();
			con.commit();
		} catch (SQLException e) {
			e.printStackTrace();
		}
			
	}
	
	@Override
	public void dropTable() {
		try {
			if(DBUtils.doesTableExist("TEAMS", con)) {
				this.con.prepareStatement(DROP_TEAMS).executeUpdate();
				con.commit();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void update() {
		if(this.id < 0) {
			//Can't really execute an update by ID if it doesn't exist.
			//Granted, I suppose we could turn this into an upsert operation. But, seems like that could
			//be just confusing behavior.
			return;
		}
		try {
		PreparedStatement stmt = con.prepareStatement(UPDATE_TEAMS);
			stmt.setString(1, this.name);
			stmt.setString(2, this.slogan);
			stmt.setString(3, this.iconUrl);
			stmt.setInt(4, this.id);
			stmt.executeUpdate();
			con.commit();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}


	@Override
	public void delete() {
		try {
		PreparedStatement stmt = con.prepareStatement(DELETE_TEAM);
			stmt.setInt(1, this.id);
			stmt.executeUpdate();
			con.commit();
			EntityManager.removeTeamFromRelationships(con, id);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}


	public static TeamDAO loadById(int id, Connection con) {
		try{
			PreparedStatement stmt = con.prepareStatement(SELECT_TEAM);
			stmt.setInt(1, id);
			ResultSet result = stmt.executeQuery();
			
			if(!result.next()) {
				return null;
			}
			return loadFromResult(result, con);
		}catch (SQLException e){
			e.printStackTrace();
		}
		return null;
	}
	
	private static TeamDAO loadFromResult(ResultSet result, Connection con) {
		int id;
		try {
			id = result.getInt(result.findColumn("id"));
			String first = result.getString(result.findColumn("name"));
			String last = result.getString(result.findColumn("slogan"));
			String email = result.getString(result.findColumn("iconUrl"));
			return new TeamDAO(con, id, first, last, email);
		} catch (SQLException e) {
			return null;
		}
	}
	
	public static List<TeamDAO> retrieveAllEntries(Connection con) {
		try{
			PreparedStatement stmt = con.prepareStatement(SELECT_ALL_TEAMS);
			ResultSet result = stmt.executeQuery();
			
			List<TeamDAO> teams = new ArrayList<TeamDAO>();
			while(result.next()) {
				TeamDAO curResult = loadFromResult(result, con);
				teams.add(curResult);
			}
			return teams;
		}catch (SQLException e){
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public void insert() {
		try{
			//if ID is 0, assume we want it to autogen one.
			PreparedStatement stmt = con.prepareStatement(INSERT_TEAM, Statement.RETURN_GENERATED_KEYS);
		
			stmt.setString(1, name);
			stmt.setString(2, slogan);
			stmt.setString(3, iconUrl);

			ResultSet rs = stmt.getGeneratedKeys();
			if(rs.next()){
				this.id = rs.getInt(1);
			}
			stmt.executeUpdate();
			
			con.commit();
			}catch (SQLException e){
				e.printStackTrace();
			}
	}		
}
