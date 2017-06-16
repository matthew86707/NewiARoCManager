//package org.jointheleague.iaroc.iaroc2;
//
//import java.io.IOException;
//import java.sql.Connection;
//import java.sql.PreparedStatement;
//import java.sql.ResultSet;
//import java.sql.SQLException;
//import java.sql.Statement;
//import java.util.Arrays;
//
//import javax.print.attribute.standard.Media;
//import javax.servlet.ServletException;
//import javax.servlet.http.HttpServlet;
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//import javax.swing.text.html.parser.Entity;
//import javax.ws.rs.*;
//import javax.ws.rs.core.MediaType;
//
//import org.eclipse.persistence.internal.expressions.SQLStatement;
//import org.jointheleague.iaroc.iaroc2.db.DBUtils;
//import org.jointheleague.iaroc.model.Announcements;
//import org.jointheleague.iaroc.model.EntityManager;
//import org.jointheleague.iaroc.model.MatchDAO;
//import org.jointheleague.iaroc.model.TeamDAO;
//
//@Path("/webservices")
//public class FormHandler {
//
//	@GET
//	@Path("addTeam")
//	@Produces(MediaType.APPLICATION_JSON)
//    public String addTeam(@QueryParam("name") String name, @QueryParam("slogan") String slogan, @QueryParam("icon") String icon) {
//
//		Connection con = DBUtils.createConnection();
//		// Insert a new team into the DB
//
//		TeamDAO newTeam = new TeamDAO(con, 0, name, slogan, icon);
//		newTeam.insert();
//		return newTeam.toJSON();
//	}
//
//	@POST
//	@Path("addTeam")
//	@Consumes(MediaType.APPLICATION_JSON)
//	@Produces(MediaType.APPLICATION_JSON)
//	public String addTeam(String contents) {
//		Connection con = DBUtils.createConnection();
//		// Insert a new team into the DB
//
//		TeamDAO newTeam = TeamDAO.fromJSON(con, contents);
//		newTeam.insert();
//		return newTeam.toJSON();
//	}
//
//	@GET
//	@Path("addMatch")
//	@Produces(MediaType.APPLICATION_JSON)
//	public String addMatch(@QueryParam("teams") String teamsString, @QueryParam("time") long time, @QueryParam("type") String type) {
//
//		Connection con = DBUtils.createConnection();
//		// Insert a new match into the DB
//		int status = EntityManager.MATCH_STATUS_PENDING;
//		MatchDAO match = new MatchDAO(con, status, time, MatchDAO.TYPES.fromString(type));
//		match.insert();
//
//		String[] teamIds = teamsString.split(",");
//
//		Arrays.stream(teamIds).forEach( teamId -> {
//			EntityManager.MatchResultData resultData = new EntityManager.MatchResultData();
//			resultData.teamId = Integer.parseInt(teamId);
//			resultData.matchId = match.getId();
//			EntityManager.insertOrUpdateMatchResult(con, resultData);
//		});
//		return match.toJSON();
//	}
//
//	@POST
//	@Path("addMatchResult")
//	@Produces(MediaType.APPLICATION_JSON)
//	public String addMatch(String matchResultsStr) {
//		Connection con = DBUtils.createConnection();
//		try {
//			EntityManager.MatchResultData resultsData = EntityManager.MatchResultData.fromJson(matchResultsStr);
//
//			EntityManager.insertOrUpdateMatchResult(con, resultsData);
//		}
//		catch (IOException e) {
//			e.printStackTrace();
//			return "{'status':'failed', 'reason':'Parse error'}";
//		}
//		return "{'status':'success'}";
//	}
//
//	@GET
//	@Path("setAnnouncements")
//	@Produces(MediaType.APPLICATION_JSON)
//	public String setAnnouncements(@QueryParam("announcement1") String announcement1, @QueryParam("announcement2") String announcement2,
//								   @QueryParam("announcement3") String announcement3) {
//
//		Connection con = DBUtils.createConnection();
//		// Insert a new match into the DB
//		String[] announcements = new String[3];
//		announcements[0] = announcement1;
//		announcements[1] = announcement2;
//		announcements[2] = announcement3;
//		Announcements.setAnnouncements(announcements);
//		return "{status:'success'}";
//	}
//
//}
