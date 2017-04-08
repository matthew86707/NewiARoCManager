package org.jointheleague.iaroc.model;

import org.hsqldb.Row;

public class TeamDAO implements DAO{
	
	private static final String CLEAR_TEAMS = "DROP TABLE TEAMS";
	
	private static final String STARTUP_TEAMS = "CREATE TABLE TEAMS "
			+ "(id INTEGER NOT NULL, "
			+ "name VARCHAR(255), "
			+ "url VARCHAR(255), "
			+ "slogan VARCHAR(255), "
			+ "PRIMARY KEY (id))";
	private int id;
	private String name;
	private String slogan;
	private String iconUrl;
	

	public String createTable() {
		return STARTUP_TEAMS;
	}

	public String deleteTable() {
		return CLEAR_TEAMS;
	}

	public String update() {
		return "UPDATE TEAMS SET (name, slogan, iconUrl) = (?, ?, ?) WHERE id = ?";
	}

	public String delete() {
		return "DELETE FROM TEAMS WHERE id = '"+ id +"'"; 
	}

	public void loadObject(Row r) {

	}

	public String findById(int id) {
			//TODO : fix
			return "nope";
	}

}
