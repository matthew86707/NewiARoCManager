package org.jointheleague.iaroc.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.ws.rs.DELETE;

import org.hsqldb.Row;

public class MemberDAO extends DAO{
	
	private static final String DROP_MEMBERS = "DROP TABLE MEMBERS";
	
	private static final String CREATE_MEMBERS = "CREATE TABLE MEMBERS "
			+ "(id INTEGER NOT NULL, "
			+ "first VARCHAR(255), "
			+ "last VARCHAR(255), "
			+ "email VARCHAR(255), "
			+ "teamId INTEGER, "
			+ "PRIMARY KEY (id))";
	
	private static final String UPDATE_MEMBERS = "UPDATE MEMBERS SET first = ?, last = ?, email = ?, teamId = ? WHERE id = ?";
	
	private static final String DELETE_MEMBER = "DELETE FROM MEMBERS WHERE id = ?";
	
	private static final String SELECT_MEMBER = "SELECT * FROM MEMBERS WHERE id = ?";
	
	private static final String INSERT_MEMBER = "INSERT INTO MEMBERS (first, last, email, teamId) VALUES (?, ?, ?, ?)";
	
	private String first;
	private String last;
	private String email;
	private int teamId;
	private int id;
	
	
	public MemberDAO(Connection con){
		super(con);
	}
	
	public MemberDAO(Connection con, int id, String first, String last, String email, int teamId){
		super(con);
		this.first = first;
		this.last = last;
		this.email = email;
		this.teamId = teamId;
		this.id = id;
	}
	


	@Override
	public void createTable() {
		try {
			this.con.prepareStatement(CREATE_MEMBERS).executeUpdate();
			con.commit();
		} catch (SQLException e) {
			e.printStackTrace();
		}
			
	}


	@Override
	public void dropTable() {
		try {
			this.con.prepareStatement(DROP_MEMBERS).executeUpdate();
			con.commit();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}


	@Override
	public void update() {
		try {
		PreparedStatement stmt = con.prepareStatement(UPDATE_MEMBERS);
			stmt.setString(0, this.first);
			stmt.setString(1, this.last);
			stmt.setString(2, this.email);
			stmt.setInt(3, this.teamId);
			stmt.executeUpdate();
			con.commit();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}


	@Override
	public void delete() {
		try {
		PreparedStatement stmt = con.prepareStatement(DELETE_MEMBER);
			stmt.setInt(0, this.id);
			stmt.executeUpdate();
			con.commit();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}


	public static MemberDAO loadById(int id, Connection con) {
		try{
		PreparedStatement stmt = con.prepareStatement(SELECT_MEMBER);
		stmt.setInt(0, id);
		ResultSet result = stmt.executeQuery();
		String first = result.getString(result.findColumn("first"));
		String last = result.getString(result.findColumn("last"));
		String email = result.getString(result.findColumn("email"));
		int teamId = result.getInt(result.findColumn("teamId"));
		return new MemberDAO(con, id, first, last, email, teamId);
		}catch (SQLException e){
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public void insert() {
		try{
		PreparedStatement stmt = con.prepareStatement(INSERT_MEMBER);
		stmt.setString(0, first);
		stmt.setString(1, last);
		stmt.setString(2, email);
		stmt.setInt(3, teamId);
		stmt.executeUpdate();
		con.commit();
		}catch (SQLException e){
			e.printStackTrace();
		}
		
	}		

	


}
