package org.jointheleague.iaroc.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.DELETE;

import org.hsqldb.Row;
import org.jointheleague.iaroc.iaroc2.db.DBUtils;

public class MemberDAO extends DAO{
	
	private static final String DROP_MEMBERS = "DROP TABLE MEMBERS";
	
	private static final String CREATE_MEMBERS = "CREATE TABLE MEMBERS "
			+ "(id INTEGER IDENTITY, "
			+ "first VARCHAR(255), "
			+ "last VARCHAR(255), "
			+ "email VARCHAR(255), "
			+ "teamId INTEGER, "
			+ "PRIMARY KEY (id))";
	
	private static final String UPDATE_MEMBERS = "UPDATE MEMBERS SET first = ?, last = ?, email = ?, teamId = ? WHERE id = ?";
	
	private static final String DELETE_MEMBER = "DELETE FROM MEMBERS WHERE id = ?";
	
	private static final String SELECT_MEMBER = "SELECT * FROM MEMBERS WHERE id = ?";
	
	private static final String INSERT_MEMBER = "INSERT INTO MEMBERS (first, last, email, teamId) VALUES (?, ?, ?, ?)";
	
	private static final String SELECT_ALL_MEMBERS = "SELECT * FROM MEMBERS";
	
	private static final String TABLE_NAME = "MEMBERS";
	
	private String first;
	private String last;
	private String email;
	private int teamId;
	private int id;
	
	
	public MemberDAO(Connection con){
		super(con);
	}
	
	public MemberDAO(Connection con, int id, String first, String last, String email, int teamId){
		this(con, first, last, email, teamId);
		this.id = id;
	}
	
	public MemberDAO(Connection con, String first, String last, String email, int teamId){
		super(con);
		this.con = con;
		this.first = first;
		this.last = last;
		this.email = email;
		this.teamId = teamId;
	}


	@Override
	public void createTable() {
		try {
			if(DBUtils.doesTableExist(con, TABLE_NAME)) {
				dropTable();
				this.con.prepareStatement(CREATE_MEMBERS).executeUpdate();
				con.commit();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
			
	}


	@Override
	public void dropTable() {
		try {
			if(DBUtils.doesTableExist(con, TABLE_NAME)) {
				this.con.prepareStatement(DROP_MEMBERS).executeUpdate();
				con.commit();				
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}


	@Override
	public void update() {
		if(this.id >= 0){
		try {
		PreparedStatement stmt = con.prepareStatement(UPDATE_MEMBERS);
			stmt.setString(1, this.first);
			stmt.setString(2, this.last);
			stmt.setString(3, this.email);
			stmt.setInt(4, this.teamId);
			stmt.executeUpdate();
			con.commit();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		}
	}


	@Override
	public void delete() {
		try {
		PreparedStatement stmt = con.prepareStatement(DELETE_MEMBER);
			stmt.setInt(1, this.id);
			stmt.executeUpdate();
			con.commit();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	private static MemberDAO loadFromResult(ResultSet result, Connection con) {
		try {
			int id = result.getInt(result.findColumn("id"));
			String first = result.getString(result.findColumn("first"));
			String last = result.getString(result.findColumn("last"));
			String email = result.getString(result.findColumn("email"));
			int teamId = result.getInt(result.findColumn("teamId"));
			return new MemberDAO(con, id, first, last, email, teamId);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static List<MemberDAO> retrieveAllEntries(Connection con) {
		try{
			PreparedStatement stmt = con.prepareStatement(SELECT_ALL_MEMBERS);
			ResultSet result = stmt.executeQuery();
			
			List<MemberDAO> members = new ArrayList<MemberDAO>();
			while(result.next()) {
				MemberDAO curResult = loadFromResult(result, con);
				members.add(curResult);
			}
			return members;
		}catch (SQLException e){
			e.printStackTrace();
		}
		return null;
	}

	public static MemberDAO loadById(int id, Connection con) {
		try{
		PreparedStatement stmt = con.prepareStatement(SELECT_MEMBER);
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
		PreparedStatement stmt = con.prepareStatement(INSERT_MEMBER, Statement.RETURN_GENERATED_KEYS);
		stmt.setString(1, first);
		stmt.setString(2, last);
		stmt.setString(3, email);
		stmt.setInt(4, teamId);
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
