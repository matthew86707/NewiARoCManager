package org.jointheleague.iaroc.iaroc2.db;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.jointheleague.iaroc.model.DAO;
import org.jointheleague.iaroc.model.MatchDAO;
import org.jointheleague.iaroc.model.MemberDAO;
import org.jointheleague.iaroc.model.TeamDAO;

public class DBUtils{	
	
	public static Connection createConnection(){
		try {
			return DriverManager.getConnection("jdbc:hsqldb:file:testdb", "SA", "");
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static boolean doesTableExist(Connection con, String tableName) {
		try {
			DatabaseMetaData dbm = con.getMetaData();
			// check if "employee" table is there
			ResultSet tables = dbm.getTables(null, null, tableName, null);
			return tables.next();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}
}
