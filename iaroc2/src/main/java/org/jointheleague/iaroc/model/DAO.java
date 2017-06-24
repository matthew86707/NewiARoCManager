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
	public abstract void update();
	public abstract void delete();


	public abstract ObjectNode toJSON();

	/**
	 *
	 * @param includeId If true, set the ID of the newly inserted DAO.
	 *                  Else, generate one on the spot.
	 */
    public abstract void insert(boolean includeId);
}
