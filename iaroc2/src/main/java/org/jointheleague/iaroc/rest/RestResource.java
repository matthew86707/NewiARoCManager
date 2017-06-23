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
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
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

	public static String getSuccessStatus() {
		ObjectMapper mapper = new ObjectMapper();
		ObjectNode node = mapper.createObjectNode();
		node.put("status", "success");
		node.put("reason", "success");
		try {
			return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(node);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		return "";
	}

	private boolean isAdmin() {
		return request.getSession().getAttribute("isAdmin") != null &&  request.getSession().getAttribute("isAdmin").equals("true");
	}

	@GET
	@Path("/login")
	@Produces(MediaType.TEXT_HTML)
	public Response login(@QueryParam("token") String token) {
		if(token == null || !(token.length() > 0)){
			return Response.status(Response.Status.SEE_OTHER).header(HttpHeaders.LOCATION, "/login.html").build();
		}
		Boolean isAuth = false;
		try {
			isAuth = Authentication.authUser(token);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	if ((request.getSession().getAttribute("isAdmin") == null)) {
			request.getSession().setAttribute("isAdmin", "false");
		}
		if (!(request.getSession().getAttribute("isAdmin").equals("true"))) {
			if (isAuth) {
				request.getSession().setAttribute("isAdmin", "true");
				return Response.status(Response.Status.SEE_OTHER).header(HttpHeaders.LOCATION, "/admin/home.html")
						.build();
			} else {
				return Response.status(Response.Status.SEE_OTHER).header(HttpHeaders.LOCATION, "/logout.html").build();
			}
		} else {
			return Response.status(Response.Status.SEE_OTHER).header(HttpHeaders.LOCATION, "/admin/home.html").build();
		}
	}
//
//	@GET
//	@Path("addOrModifyTeam")
//	@Produces(MediaType.APPLICATION_JSON)
//	public Response addTeam(@QueryParam("name") String name, @QueryParam("teamToModify") int teamId,
//			@QueryParam("iconUrl") String icon) {
//
//		Connection con = DBUtils.createConnection();
//		// Insert a new team into the DB
//		if (teamId == -1) {
//			TeamDAO newTeam = new TeamDAO(con, name, icon);
//			newTeam.insert();
//		} else { // Otherwise, what we want to do is modify an existing team.
//			TeamDAO teamToModify = TeamDAO.loadById(teamId, con);
//			teamToModify.setName(name);
//			teamToModify.setIconUrl(icon);
//			teamToModify.update();
//		}
//
//		return Response.status(Response.Status.SEE_OTHER)
//				.header(HttpHeaders.LOCATION, "/admin/forms/addOrModifyTeam.html").build();
//	}

	@GET
	@Path("deleteTeam")
	@Produces(MediaType.APPLICATION_JSON)
	public String deleteTeam( @QueryParam("teamId") Integer teamId) {
		if (isAdmin()) {
			if(teamId == null) {
				return getFailStatus("Please provide teamId param");
			}
			Connection con = DBUtils.createConnection();
			TeamDAO team = TeamDAO.loadById(teamId, con);
			if(team == null) {
				return getFailStatus("Provided ID not found");
			}
			EntityManager.clearResultsForTeam(con, teamId);
			team.delete();
			return getSuccessStatus();
		}
		else {
			return getFailStatus("Insufficient permission");
		}
	}

	@GET
	@Path("deleteMatch")
	@Produces(MediaType.APPLICATION_JSON)
	public String deleteMatch( @QueryParam("matchId") Integer matchId) {
		if (isAdmin()) {
			if(matchId == null) {
				return getFailStatus("Please provide matchId param");
			}
			Connection con = DBUtils.createConnection();
			MatchDAO match = MatchDAO.loadById(matchId, con);
			if(match == null) {
				return getFailStatus("Provided ID not found");
			}
			match.delete();
			EntityManager.clearResultsForMatch(con, matchId);
			return getSuccessStatus();
		}
		else {
			return getFailStatus("Insufficient permission");
		}
	}

	@POST
	@Path("addOrModifyTeam")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public String addOrModifyTeamTeam(String contents) {
		if (isAdmin()) {
			Connection con = DBUtils.createConnection();
			// Insert a new team into the DB
			TeamDAO team = TeamDAO.fromJSON(con, contents);
			if(team == null) {
				return getFailStatus("Could not parse team");
			}
			if(team.getId() == -1) {
				team.insert();
			} else {
				//If ID is not -1 (new team specifier), get current and update.
				TeamDAO currentEntry = TeamDAO.loadById(team.getId(), con);
				if(currentEntry != null) {
					//Assuming that ID was found, update.
					team.update();
				}
				else {
					return getFailStatus("Provided team ID not in database");
				}
			}
			return getSuccessStatus();
		} else {
			return getFailStatus("Insufficient permission");
		}
	}

	@POST
	@Path("addOrModifyMatch")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public String addMatch(String contents) {
		if (isAdmin()) {
			ObjectMapper mapper = new ObjectMapper();
			Connection con = DBUtils.createConnection();

			try {
				JsonNode node = mapper.readTree(contents);
				MatchDAO.TYPES type = MatchDAO.TYPES.fromString(node.get("type").asText());
				int status = EntityManager.MATCH_STATUS_PENDING;
				String timeOfDay = node.get("time").asText();
				int dayOfMonth = node.get("date").asInt();
				// If all it lacks to be a good little time string is a colon, give
				// it one and make it a real value!
				if (!timeOfDay.contains(":") && timeOfDay.matches("[0-9]{4}")) {
					timeOfDay = timeOfDay.substring(0, 1) + ":" + timeOfDay.substring(2);
				}

				LocalTime lt;
				try {
					lt = LocalTime.parse(timeOfDay);
				} catch (DateTimeParseException e) {
					return getFailStatus("Improper time format. Needs to be HHMM or HH:MM");
				}

				LocalDateTime ldt = LocalDateTime.of(LocalDate.of(2017, 06, dayOfMonth), lt);

				int matchToModify = -1;
				if (node.has("matchToModify")) {
					matchToModify = node.get("matchToModify").asInt();
				}

				MatchDAO match = null;
				if (matchToModify != -1) {
					match = MatchDAO.loadById(matchToModify, con);
					if (match == null) {
						return getFailStatus("Could not find requested match id");
					}
					match.setStatus(status);
					match.setUnixTime(ldt.toEpochSecond(EntityManager.PST_TIME_OFFSET));
					match.setType(type);
					match.update();
				} else {
					match = new MatchDAO(con, status, ldt.toEpochSecond(EntityManager.PST_TIME_OFFSET), type);
					match.insert();
				}

				ArrayNode teamsArray = (ArrayNode) node.get("teams");

				EntityManager.clearResultsForMatch(con, match.getId());
				for (final JsonNode innerNode : teamsArray) {
					MatchResultData resultData = new MatchResultData();
					resultData.teamId = innerNode.asInt();
					resultData.matchId = match.getId();
					EntityManager.insertOrUpdateMatchResult(con, resultData);
				}
				return match.toJSONString();
			} catch (IOException e) {
				e.printStackTrace();
				return getFailStatus(e.toString());
			}
		}  else {
			return getFailStatus("Insufficient Permissions");
		}
	}

	@GET
	@Path("getMatchResultsExtended")
	@Produces(MediaType.APPLICATION_JSON)
	public String getMatchResultsExtended() {
		Connection con = DBUtils.createConnection();
		ObjectMapper mapper = new ObjectMapper();
		ObjectNode results = EntityManager.getMatchResultsExtended(con);
		try {
			return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(results);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
			return getFailStatus(e.toString());
		}
	}

	@POST
	@Path("addMatchResult")
	@Produces(MediaType.APPLICATION_JSON)
	public String addMatchResult(String matchResultsStr) {
		if ( isAdmin()) {
			Connection con = DBUtils.createConnection();
			try {
				MatchResultData resultsData = MatchResultData.fromJson(matchResultsStr);

				EntityManager.insertOrUpdateMatchResult(con, resultsData);
			} catch (IOException e) {
				e.printStackTrace();
				return getFailStatus("Parse error");
			}
			return getSuccessStatus();
		} else {
			return getFailStatus("Insufficient Permissions");
		}
	}

	@POST
	@Path("addMatchResults")
	@Produces(MediaType.APPLICATION_JSON)
	public String addMatchResults(String matchResultsStr) {
		if ( isAdmin()) {
			Connection con = DBUtils.createConnection();
			try {
				JsonNode node = new ObjectMapper().readTree(matchResultsStr);
				ArrayNode innerArray = (ArrayNode)node.get("matchResults");
				for( JsonNode curNode : innerArray) {
					MatchResultData data = MatchResultData.fromJsonObject(curNode);
					EntityManager.insertOrUpdateMatchResult(con, data);
				}
				return getSuccessStatus();
			} catch (IOException e) {
				e.printStackTrace();
				return getFailStatus("Parse error");
			}
		} else {
			return getFailStatus("Insufficient Permissions");
		}
	}

	@GET
	@Path("matches/data")
	@Produces(MediaType.APPLICATION_JSON)
	public String getMatches() {
		Connection con = DBUtils.createConnection();
		List<MatchDAO> matches = MatchDAO.retrieveAllEntriesByTime(con);
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
			return getFailStatus(e.toString());
		}
	}
	
	@GET
	@Path("matches/data/upcoming")
	@Produces(MediaType.APPLICATION_JSON)
	public String getUpcomingMatches() {
		Connection con = DBUtils.createConnection();
		List<MatchDAO> matches = MatchDAO.retrieveAllEntriesByTime(con);
		ObjectMapper mapper = new ObjectMapper();
		ObjectNode matchesResult = mapper.createObjectNode();
		ArrayNode matchesJSON = matchesResult.putArray("matches");

		matches.forEach(match -> {
			boolean shouldShow = false;
			boolean allFinal = true;
			for(EntityManager.MatchResultData matchResult : EntityManager.getMatchResults(con, match.getId())){
				if(matchResult.isFinalResult == false){
					allFinal = false;
				}
			}
			
			if(!allFinal){
				shouldShow = true;
			}
			
			if(shouldShow){
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
			}
		});

		try {
			return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(matchesResult);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
			return getFailStatus(e.toString());
		}
	}

	@GET
	@Path("setAnnouncements")
	public Response setAnnouncements(@QueryParam("inputAnnouncement1") String announcement1,
			@QueryParam("inputAnnouncement2") String announcement2,
			@QueryParam("inputAnnouncement3") String announcement3) {
		if (request.getSession().getAttribute("isAdmin") == null) {
			request.getSession().setAttribute("isAdmin", "false");
		}
		if (request.getSession().getAttribute("isAdmin").equals("true")) {
			// Insert a new match into the DB
			List<String> announcements = new ArrayList<>();
			if (announcement1 != null) {
				announcements.add(announcement1);
			}
			if (announcement2 != null) {
				announcements.add(announcement2);
			}
			if (announcement3 != null) {
				announcements.add(announcement3);
			}
			Announcements.getInstance().setAnnouncements(announcements);
			return Response.status(Response.Status.SEE_OTHER)
					.header(HttpHeaders.LOCATION, "/admin/success.html").build();
		} else {
			return Response.status(Response.Status.FORBIDDEN).build();
		}
	}

	@GET
	@Path("teams/standings")
	@Produces(MediaType.APPLICATION_JSON)
	public String getStandings(@QueryParam("division") int division) {
		Connection con = DBUtils.createConnection();

		ObjectMapper mapper = new ObjectMapper();

		Map<Integer, ObjectNode> teamScoreNodes = new HashMap<>();

		List<TeamDAO> teams = TeamDAO.retrieveAllEntriesByDivision(con, division);

		for (TeamDAO teamInfo : teams) {
			ObjectNode teamNode = mapper.createObjectNode();
			teamNode.put("name", teamInfo.getName());
			teamNode.put("id", teamInfo.getId());
			teamNode.put("icon", teamInfo.getIconUrl());
			// Set defaults to 0.
			teamNode.put("totalScore", 0);
			teamNode.put("scoreDragRace", "?");
			teamNode.put("scoreMaze", "?");
			teamNode.put("scoreRetrieval", "?");
			teamNode.put("scorePresentation", "?");
			teamNode.put("timeDragRace", "?");
			teamNode.put("timeMaze", "?");
			teamNode.put("timeRetrieval", "?");
			teamScoreNodes.put(teamInfo.getId(), teamNode);
		}

		for (MatchDAO.TYPES type : MatchDAO.TYPES.values()) {
			if (type != MatchDAO.TYPES.UNDEFINED) {
				List<MatchResultData> resultData = EntityManager.calculateEventResults(con, type, division, false);
				resultData.forEach(curResult -> {
					ObjectNode teamNode = teamScoreNodes.get(curResult.teamId);

					String time = "?";

					if (!curResult.completedObjective) {
						time = "X";
						// X as in did not finish. Time irrelevant.
					} else if (!curResult.isFinalResult) {
						time = "?";
					} else {
						// Time in seconds.
						double timeFloat = curResult.time / 1000;
						time = String.format("%.2f", timeFloat);
					}

					if (teamNode != null) {
						teamNode.put("totalScore", teamNode.get("totalScore").asInt() + curResult.totalPoints);
						switch (type) {
						// For each event, calculate scores and set the results
						// to the appropriate team.
						case MAZE:
							teamNode.put("scoreMaze", curResult.totalPoints);
							teamNode.put("timeMaze", time);
							break;
						case DRAG_RACE:
							teamNode.put("scoreDragRace", curResult.totalPoints);
							teamNode.put("timeDragRace", time);
							break;
						case GOLD_RUSH:
							teamNode.put("scoreRetrieval", curResult.totalPoints);
							teamNode.put("timeRetrieval", time);
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

		// Finally, before dumping results into the JSON array to return, sort
		// by total score, highest first.
		List<ObjectNode> sortedScores = teamScoreNodes.values().stream().sorted((node1, node2) -> {
			// Comparator is backwards because we want descending order.
			return ((Integer) (node2.get("totalScore").asInt())).compareTo(node1.get("totalScore").asInt());
		}).collect(Collectors.toList());

		ObjectNode returnVal = mapper.createObjectNode();
		ArrayNode teamsScoresJson = returnVal.putArray("teamScores");

		sortedScores.forEach(score -> teamsScoresJson.add(score));

		try {
			return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(returnVal);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
			return getFailStatus(e.toString());
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

				// Team Name
				Element teamIcon = doc.createElement("iconUrl");
				teamIcon.appendChild(doc.createTextNode(curTeam.getIconUrl()));
				team.appendChild(teamIcon);

				// Division
				Element division = doc.createElement("division");
				division.appendChild(doc.createTextNode(Integer.toString(curTeam.getDivision())));
				team.appendChild(division);

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
			return getFailStatus(e.toString());
		}
	}

}
