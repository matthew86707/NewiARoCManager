package org.jointheleague.iaroc.model;

import java.sql.Connection;
import java.sql.PreparedStatement;

import org.hsqldb.Row;

public abstract class DAO {
	
	public DAO(Connection con){
		this.con = con;
	}
	
	protected Connection con;
	
	public abstract void createTable();
	public abstract void dropTable();
	public abstract void insert();
	public abstract void update();
	public abstract void delete();


}
