package org.jointheleague.iaroc.model;

import com.fasterxml.jackson.databind.node.ObjectNode;

import java.sql.Connection;

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


	public abstract ObjectNode toJSON();
}
