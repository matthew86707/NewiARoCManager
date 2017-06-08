package org.jointheleague.iaroc.rest;

import com.fasterxml.jackson.core.JsonProcessingException;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import org.jointheleague.iaroc.Application;
import org.jointheleague.iaroc.db.DBUtils;
import org.jointheleague.iaroc.model.Announcements;
import org.jointheleague.iaroc.model.EntityManager;
import org.jointheleague.iaroc.model.EntityManager.MatchResultData;
import org.jointheleague.iaroc.model.MatchDAO;
import org.jointheleague.iaroc.model.TeamDAO;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.IOException;
import java.io.StringWriter;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Path("/")
@Component

public class RestResource {

	@Context
	private HttpServletRequest request;

	public static String getFailStatus(String reason) {
		ObjectMapper mapper = new ObjectMapper();
		ObjectNode node = mapper.createObjectNode();
		node.put("status", "failed");
		node.put("reason", reason);
		try {
			return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(node);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		return "";
	}

	public static String getSuccessStatus(String reason) {
		ObjectMapper mapper = new ObjectMapper();
		ObjectNode node = mapper.createObjectNode();
		node.put("status", "success");
		node.put("reason", reason);
		try {
			return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(node);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		return "";
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/hello")
	public String hello() {
		return "Hello World";
	}

	@GET
	@Path("/login")
	@Produces(MediaType.TEXT_HTML)
	public String login(@QueryParam("password") String password) {
		// Insert a new match into the DB
		if (password != null && password.equals(Application.password)) {
			request.getSession().setAttribute("isAdmin", "true");
			return getSuccessStatus("Welcome, Keith. If you're not Keith, sorry.");
		} else {
			return getFailStatus("Nice Try m8");
		}
	}

	@GET
	@Path("addTeam")
	@Produces(MediaType.APPLICATION_JSON)
	public String addTeam(@QueryParam("name") String name, @QueryParam("slogan") String slogan,
			@QueryParam("icon") String icon) {

		Connection con = DBUtils.createConnection();
		// Insert a new team into the DB

		TeamDAO newTeam = new TeamDAO(con, 0, name, slogan, icon);
		newTeam.insert();
		return newTeam.toJSONString();
	}

	@POST
	@Path("addTeam")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public String addTeam(String contents) {
		Connection con = DBUtils.createConnection();
		// Insert a new team into the DB

		TeamDAO newTeam = TeamDAO.fromJSON(con, contents);
		newTeam.insert();
		return newTeam.toJSONString();
	}

	@POST
	@Path("addMatch")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public String addMatch(String contents) {
		String isAdmin = (String) request.getSession().getAttribute("isAdmin");
		ObjectMapper mapper = new ObjectMapper();
		if (isAdmin == null || !(isAdmin.equals("true"))) {
			ObjectNode node = mapper.createObjectNode();
			node.put("status", "failed");
			try {
				return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(node);
			} catch (JsonProcessingException e) {
				e.printStackTrace();
			}
		}
		Connection con = DBUtils.createConnection();

		try {
			JsonNode node = mapper.readTree(contents);
			MatchDAO.TYPES type = MatchDAO.TYPES.fromString(node.get("type").asText());
			int status = EntityManager.MATCH_STATUS_PENDING;
			long time = node.get("time").asLong();

			MatchDAO match = new MatchDAO(con, status, time, type);
			match.insert();

			ArrayNode teamsArray = (ArrayNode) node.get("teams");

			for (final JsonNode innerNode : teamsArray) {
				MatchResultData resultData = new MatchResultData();
				resultData.teamId = innerNode.asInt();
				resultData.matchId = match.getId();
				EntityManager.insertMatchResult(con, resultData);
			}
			return match.toJSONString();
		} catch (IOException e) {
			e.printStackTrace();
			return "{'status':'failed'}";
		}
	}

	@POST
	@Path("addMatchResult")
	@Produces(MediaType.APPLICATION_JSON)
	public String addMatchResult(String matchResultsStr) {
		Connection con = DBUtils.createConnection();
		try {
			MatchResultData resultsData = MatchResultData.fromJson(matchResultsStr);

			EntityManager.insertMatchResult(con, resultsData);
		} catch (IOException e) {
			e.printStackTrace();
			return "{'status':'failed', 'reason':'Parse error'}";
		}
		return "{'status':'success'}";
	}

	@GET
	@Path("matches/data")
	@Produces(MediaType.APPLICATION_JSON)
	public String getMatches() {
		Connection con = DBUtils.createConnection();
		List<MatchDAO> matches = MatchDAO.retrieveAllEntries(con);
		ObjectMapper mapper = new ObjectMapper();
		ObjectNode matchesResult = mapper.createObjectNode();
		ArrayNode matchesJSON = matchesResult.putArray("matches");

		matches.forEach(match -> {

			List<Integer> teams = EntityManager.getTeamsByMatch(con, match.getId());

			ObjectNode matchJson = match.toJSON();

			ArrayNode teamsJSON = matchJson.putArray("teams");

			teams.forEach(teamId -> {
				TeamDAO team = TeamDAO.loadById(teamId, con);

				if (team != null) {
					teamsJSON.add(team.toJSON());
				}
			});
			matchesJSON.add(matchJson);
		});

		try {
			return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(matchesResult);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
			return "{status:'failed'}";
		}
	}

	@GET
	@Path("setAnnouncements")
	public Response setAnnouncements(@QueryParam("inputAnnouncement1") String announcement1,
									 @QueryParam("inputAnnouncement2") String announcement2,
									 @QueryParam("inputAnnouncement3") String announcement3) {
		// Insert a new match into the DB
		List<String> announcements = new ArrayList<>();
		if(announcement1 != null) {
			announcements.add(announcement1);
		}
		if(announcement2 != null) {
			announcements.add(announcement2);
		}
		if(announcement3 != null) {
			announcements.add(announcement3);
		}
		Announcements.getInstance().setAnnouncements(announcements);
		return Response.status(Response.Status.SEE_OTHER)
				.header(HttpHeaders.LOCATION, "/admin/forms/announcements.html")
				.build();
	}

	@GET
	@Path("teams/standings")
	@Produces(MediaType.APPLICATION_JSON)
	public String getStandings() {
		Connection con = DBUtils.createConnection();

		ObjectMapper mapper = new ObjectMapper();

		Map<Integer, ObjectNode> teamScoreNodes = new HashMap<>();

		List<TeamDAO> teams = TeamDAO.retrieveAllEntries(con);

		for(TeamDAO teamInfo : teams) {
			ObjectNode teamNode = mapper.createObjectNode();
			teamNode.put("name", teamInfo.getName());
			teamNode.put("id", teamInfo.getId());
			teamNode.put("icon", teamInfo.getIconUrl());
			//Set defaults to 0.
			teamNode.put("totalScore", 0);
			teamNode.put("scoreDragRace", 0);
			teamNode.put("scoreMaze", 0);
			teamNode.put("scoreRetrieval", 0);
			teamNode.put("scorePresentation", 0);
			teamScoreNodes.put(teamInfo.getId(), teamNode);
		}

		for(MatchDAO.TYPES type : MatchDAO.TYPES.values()) {
			if(type != MatchDAO.TYPES.UNDEFINED) {
				List<MatchResultData> resultData = EntityManager.calculateEventResults(
						con, type, false);
				resultData.forEach( curResult -> {
					ObjectNode teamNode = teamScoreNodes.get(curResult.teamId);
					if(teamNode != null) {
						teamNode.put("totalScore", teamNode.get("totalScore").asInt() + curResult.totalPoints);
						switch(type) {
							//For each event, calculate scores and set the results to the appropriate team.
							case MAZE:
								teamNode.put("scoreMaze", curResult.totalPoints);
								break;
							case DRAG_RACE:
								teamNode.put("scoreDragRace", curResult.totalPoints);
								break;
							case GOLD_RUSH:
								teamNode.put("scoreRetrieval", curResult.totalPoints);
								break;
							case PRESENTATION:
								teamNode.put("scorePresentation", curResult.totalPoints);
								break;
							default:
								break;
						}
					}
				});
			}
		}

		//Finally, before dumping results into the JSON array to return, sort by total score, highest first.
		List<ObjectNode> sortedScores = teamScoreNodes.values().stream().sorted( (node1, node2) -> {
			//Comparator is backwards because we want descending order.
			return ((Integer)(node2.get("totalScore").asInt())).compareTo(node1.get("totalScore").asInt());
		} ).collect(Collectors.toList());

		ObjectNode returnVal = mapper.createObjectNode();
		ArrayNode teamsScoresJson = returnVal.putArray("teamScores");

		sortedScores.forEach( score -> teamsScoresJson.add(score));

		try {
			return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(returnVal);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
			return "{status:'failed'}";
		}
	}

	// Ajax Services
	@GET
	@Path("teams/data")
	@Produces(MediaType.TEXT_XML)
	public String teamData() {
		DocumentBuilderFactory icFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder icBuilder;
		try {
			icBuilder = icFactory.newDocumentBuilder();
			Document doc = icBuilder.newDocument();
			Element mainRootElement = doc.createElement("Teams");
			doc.appendChild(mainRootElement);

			// append child elements to root element
			Connection con = DBUtils.createConnection();
			List<TeamDAO> teams = TeamDAO.retrieveAllEntries(con);
			for (TeamDAO curTeam : teams) {
				Element team = doc.createElement("Team");
				team.setAttribute("id", Integer.toString(curTeam.getId()));

				// Team Name
				Element teamName = doc.createElement("name");
				teamName.appendChild(doc.createTextNode(curTeam.getName()));
				team.appendChild(teamName);

				// Team Slogan
				Element teamSlogan = doc.createElement("slogan");
				teamSlogan.appendChild(doc.createTextNode(curTeam.getSlogan()));
				team.appendChild(teamSlogan);

				// Team Points
				Element teamPoints = doc.createElement("points");
				teamPoints.appendChild(doc.createTextNode(curTeam.getPoints() + ""));
				team.appendChild(teamPoints);

				mainRootElement.appendChild(team);
			}

			// output DOM XML to console

			StringWriter writer = new StringWriter();
			StreamResult result = new StreamResult(writer);

			Transformer transformer = TransformerFactory.newInstance().newTransformer();
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			DOMSource source = new DOMSource(doc);
			transformer.transform(source, result);

			writer.flush();
			return writer.toString();

		} catch (Exception e) {
			e.printStackTrace();
		}

		return "";
	}

	@GET
	@Path("live/announcements")
	@Produces(MediaType.APPLICATION_JSON)
	public String liveInfo() {
		String currentAnnouncement = Announcements.getInstance().getCurrentAnnouncement();

		ObjectMapper mapper = new ObjectMapper();

		ObjectNode node = mapper.createObjectNode();

		node.put("announcement", currentAnnouncement);
		try {
			return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(node);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
			return "{status:'failed'}";
		}
	}

}
