package org.jointheleague.iaroc.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.hsqldb.Row;

public class TeamDAO extends DAO{
	
	
	
	private static final String DROP_TEAMS = "DROP TABLE TEAMS";
	
	private static final String CREATE_TEAMS = "CREATE TABLE TEAMS "
			+ "(id INTEGER NOT NULL, "
			+ "name VARCHAR(255), "
			+ "url VARCHAR(255), "
			+ "slogan VARCHAR(255), "
			+ "PRIMARY KEY (id))";
	
	private static final String UPDATE_TEAMS = "UPDATE TEAMS SET name = ?, slogan = ?, iconUrl = ? WHERE id = ?";
	
	private static final String DELETE_TEAM = "DELETE FROM TEAMS WHERE id = ?";
	
	private static final String SELECT_TEAM = "SELECT * FROM TEAMS WHERE id = ?";
	
	private static final String INSERT_TEAM = "INSERT INTO TEAMS (name, slogan, iconUrl) VALUES (?, ?, ?)";
	
	private int id;
	private String name;
	private String slogan;
	private String iconUrl;
	
	public TeamDAO(Connection con){
		super(con);
	}
	
	public TeamDAO(Connection con, int id, String name, String slogan, String iconUrl){
		super(con);
		this.name = name;
		this.slogan = slogan;
		this.iconUrl = iconUrl;
		this.id = id;
	}
	

	@Override
	public void createTable() {
		try {
			this.con.prepareStatement(CREATE_TEAMS).executeUpdate();
			con.commit();
		} catch (SQLException e) {
			e.printStackTrace();
		}
			
	}


	@Override
	public void dropTable() {
		try {
			this.con.prepareStatement(DROP_TEAMS).executeUpdate();
			con.commit();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}


	@Override
	public void update() {
		try {
		PreparedStatement stmt = con.prepareStatement(UPDATE_TEAMS);
			stmt.setString(0, this.name);
			stmt.setString(1, this.slogan);
			stmt.setString(2, this.iconUrl);
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
			stmt.setInt(0, this.id);
			stmt.executeUpdate();
			con.commit();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}


	public static TeamDAO loadById(int id, Connection con) {
		try{
		PreparedStatement stmt = con.prepareStatement(SELECT_TEAM);
		stmt.setInt(0, id);
		ResultSet result = stmt.executeQuery();
		String first = result.getString(result.findColumn("name"));
		String last = result.getString(result.findColumn("slogan"));
		String email = result.getString(result.findColumn("iconUrl"));
		return new TeamDAO(con, id, first, last, email);
		}catch (SQLException e){
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public void insert() {
		try{
		PreparedStatement stmt = con.prepareStatement(INSERT_TEAM);
		stmt.setString(0, name);
		stmt.setString(1, slogan);
		stmt.setString(2, iconUrl);
		stmt.executeUpdate();
		con.commit();
		}catch (SQLException e){
			e.printStackTrace();
		}
		
	}		
}
