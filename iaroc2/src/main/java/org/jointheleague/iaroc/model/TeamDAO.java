package org.jointheleague.iaroc.model;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.jointheleague.iaroc.db.DBUtils;

import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TeamDAO extends DAO{

	private static final String DROP_TEAMS = "DROP TABLE TEAMS";

	private static final String CREATE_TEAMS = "CREATE TABLE TEAMS "
			+ "(id INTEGER IDENTITY, "
			+ "name VARCHAR(255), "
			+ "iconUrl VARCHAR(255), "
			+ "PRIMARY KEY (id))";

	private static final String UPDATE_TEAMS = "UPDATE TEAMS SET name = ?, iconUrl = ? WHERE id = ?";

	private static final String DELETE_TEAM = "DELETE FROM TEAMS WHERE id = ?";

	private static final String SELECT_TEAM = "SELECT * FROM TEAMS WHERE id = ?";

	private static final String INSERT_TEAM = "INSERT INTO TEAMS (name, iconUrl) VALUES (?, ?)";

	private static final String SELECT_ALL_TEAMS = "SELECT * FROM TEAMS ORDER BY name ASC";

	public static final String TABLE_NAME = "TEAMS";

	private int id;
	private String name;
	private String iconUrl;

	public TeamDAO(Connection con){
		super(con);
	}

	public TeamDAO(Connection con, String name, String iconUrl){
		super(con);
		this.name = name;
		this.iconUrl = iconUrl;
	}

	public TeamDAO(Connection con, int id, String name, String iconUrl){
		super(con);
		this.name = name;
		this.iconUrl = iconUrl;
		this.id = id;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getIconUrl() {
		return iconUrl;
	}

	public void setIconUrl(String iconUrl) {
		this.iconUrl = iconUrl;
	}

	public static TeamDAO fromJSON(Connection con, String jsonString) {
		try {
			JsonNode node = new ObjectMapper().readTree(jsonString);
			int id = 0;
			if(node.has("id")) {
				id = node.get("id").asInt();
			}
			String name = node.get("name").asText();
			String iconURL = node.get("icon").asText();
			return new TeamDAO(con, id, name, iconURL);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public String toJSONString() {
		try {
			ObjectMapper mapper = new ObjectMapper();
			ObjectNode jsonRoot = toJSON();
			return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(jsonRoot);
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public ObjectNode toJSON() {
		ObjectMapper mapper = new ObjectMapper();
		ObjectNode jsonRoot = mapper.createObjectNode();
		jsonRoot.put("id", this.id).
				put("name", this.name).
				put("icon", this.iconUrl);
		return jsonRoot;
	}

	@Override
	public void createTable() {
		try {
			//If table already exists, drop and recreate.
			if(DBUtils.doesTableExist(con, TABLE_NAME)) {
				dropTable();
			}
			this.con.prepareStatement(CREATE_TEAMS).executeUpdate();
			con.commit();
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

	@Override
	public void dropTable() {
		try {
			if(DBUtils.doesTableExist(con, TABLE_NAME)) {
				this.con.prepareStatement(DROP_TEAMS).executeUpdate();
				con.commit();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void update() {
		if(this.id < 0) {
			//Can't really execute an update by ID if it doesn't exist.
			//Granted, I suppose we could turn this into an upsert operation. But, seems like that could
			//be just confusing behavior.
			return;
		}
		try {
			PreparedStatement stmt = con.prepareStatement(UPDATE_TEAMS);
			stmt.setString(1, this.name);
			stmt.setString(2, this.iconUrl);
			stmt.setInt(3, this.id);
			stmt.executeUpdate();
			con.commit();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}


	@Override
	public void delete() {
		try {
			PreparedStatement stmt = con.prepareStatement(DELETE_TEAM);
			stmt.setInt(1, this.id);
			stmt.executeUpdate();
			con.commit();
			EntityManager.removeTeamFromRelationships(con, id);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public static TeamDAO loadById(int id, Connection con) {
		try{
			PreparedStatement stmt = con.prepareStatement(SELECT_TEAM);
			stmt.setInt(1, id);
			ResultSet result = stmt.executeQuery();

			if(!result.next()) {
				return null;
			}
			return loadFromResult(result, con);
		}catch (SQLException e){
			e.printStackTrace();
		}
		return null;
	}

	private static TeamDAO loadFromResult(ResultSet result, Connection con) {
		int id;
		try {
			id = result.getInt(result.findColumn("id"));
			String name = result.getString(result.findColumn("name"));
			String icon = result.getString(result.findColumn("iconUrl"));
			return new TeamDAO(con, id, name, icon);
		} catch (SQLException e) {
			return null;
		}
	}

	public static List<TeamDAO> retrieveAllEntries(Connection con) {
		try{
			PreparedStatement stmt = con.prepareStatement(SELECT_ALL_TEAMS);
			ResultSet result = stmt.executeQuery();

			List<TeamDAO> teams = new ArrayList<TeamDAO>();
			while(result.next()) {
				TeamDAO curResult = loadFromResult(result, con);
				teams.add(curResult);
			}
			return teams;
		}catch (SQLException e){
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public void insert() {
		try{
			//if ID is 0, assume we want it to autogen one.
			PreparedStatement stmt = con.prepareStatement(INSERT_TEAM, Statement.RETURN_GENERATED_KEYS);

			stmt.setString(1, name);
			stmt.setString(2, iconUrl);

			stmt.executeUpdate();
			ResultSet rs = stmt.getGeneratedKeys();
			if(rs.next()) {
				this.id = rs.getInt(1);
			}

			con.commit();
		}catch (SQLException e){
			e.printStackTrace();
		}
	}
}
