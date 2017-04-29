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
	
	public static void addDummyData(Connection con){
		
		DAO clearing = new TeamDAO(con);
		clearing.createTable();
		clearing = new MemberDAO(con);
		clearing.createTable();
		clearing = new MatchDAO(con);
		clearing.createTable();
		
		TeamDAO t1 = new TeamDAO(con, "Red Team", "Red is ahead", "red.jpeg");
		TeamDAO t2 = new TeamDAO(con, "Blue Team", "Blue is cool", "blue.bin");
		TeamDAO t3 = new TeamDAO(con, "Purple Team", "mehh", "File Not Found");
		t1.insert();
		t2.insert();
		t3.insert();
		
		MemberDAO roger = new MemberDAO(con, "Roger", "Rabbit", "r.rabbit@reddit.com", t1.getId());
		MemberDAO bob = new MemberDAO(con, "Bob", "Barn", "b.barn@reddit.com", t1.getId());
		
		MemberDAO johnathan = new MemberDAO(con,  "johnathan", "jackrabbit", "j.rabbit@reddit.com", t2.getId());
		MemberDAO robert = new MemberDAO(con, "Robert", "Barner", "r.barner@reddit.com", t2.getId());
		
		MemberDAO ronathan = new MemberDAO(con, "Ronathan", "Babbit", "r.b@whitehouse.gov", t3.getId());
		
		roger.insert();
		bob.insert();
		johnathan.insert();
		robert.insert();
		ronathan.insert();
		
		List<Integer> teams1 = new ArrayList<Integer>();
		teams1.add(t1.getId());
		teams1.add(t2.getId());
		MatchDAO m1 = new MatchDAO(con, teams1, 0, 0);
		m1.insert();
		
		List<Integer> teams2 = new ArrayList<Integer>();
		teams2.add(t3.getId());
		MatchDAO m2 = new MatchDAO(con, teams2, 0, 12);
		m2.insert();
		
		
	}
	
	public static Connection createConnection(){
		try {
			return DriverManager.getConnection("jdbc:hsqldb:file:testdb", "SA", "");
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static boolean doesTableExist(String tableName, Connection con) {
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
