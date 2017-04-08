package org.jointheleague.iaroc.model;

import org.hsqldb.Row;

public interface DAO {
	
	public String createTable();
	public String deleteTable();
	public String update();
	public String delete();
	public void loadObject(Row r);
	public String findById(int id);

}
