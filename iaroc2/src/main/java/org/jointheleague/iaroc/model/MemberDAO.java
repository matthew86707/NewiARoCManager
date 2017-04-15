package org.jointheleague.iaroc.model;

import org.hsqldb.Row;

public class MemberDAO implements DAO{
	
	private static final String DROP_MEMBERS = "DROP TABLE MEMBERS";
	
	private static final String CREATE_MEMBERS = "CREATE TABLE MEMBERS "
			+ "(id INTEGER NOT NULL, "
			+ "first VARCHAR(255), "
			+ "last VARCHAR(255), "
			+ "email VARCHAR(255), "
			+ "teamId INTEGER, "
			+ "PRIMARY KEY (id))";
			

	public String createTable() {
		return CREATE_MEMBERS;
	}

	public String deleteTable() {
		return DROP_MEMBERS;
	}

	public String update() {
		return null;
	}

	public String delete() {
		return null;
	}

	public void loadObject(Row r) {
		
	}

	public String findById(int id) {
		return null;
	}

}
