package org.jointheleague.iaroc.rest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.jointheleague.iaroc.db.DBUtils;
import org.jointheleague.iaroc.model.Announcements;
import org.jointheleague.iaroc.model.EntityManager;
import org.jointheleague.iaroc.model.EntityManager.MatchResultData;
import org.jointheleague.iaroc.model.MatchDAO;
import org.jointheleague.iaroc.model.TeamDAO;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
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
import java.util.List;

@Path("/")
@Component
public class RestResource {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/hello")
    public String hello() {
        return "Hello World";
    }


    @GET
    @Path("addTeam")
    @Produces(MediaType.APPLICATION_JSON)
    public String addTeam(@QueryParam("name") String name, @QueryParam("slogan") String slogan, @QueryParam("icon") String icon) {

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
        Connection con = DBUtils.createConnection();

        ObjectMapper mapper = new ObjectMapper();
        try {
            JsonNode node = mapper.readTree(contents);
            MatchDAO.TYPES type = MatchDAO.TYPES.fromString(node.get("type").asText());
            int status = EntityManager.MATCH_STATUS_PENDING;
            long time = node.get("time").asLong();

            MatchDAO match = new MatchDAO(con, status, time, type);
            match.insert();

            ArrayNode teamsArray = (ArrayNode)node.get("teams");

            for( final JsonNode innerNode : teamsArray) {
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
        }
        catch (IOException e) {
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

        matches.forEach( match -> {

            List<Integer> teams = EntityManager.getTeamsByMatch(con, match.getId());

            ObjectNode matchJson = match.toJSON();

            ArrayNode teamsJSON = matchJson.putArray("teams");

            teams.forEach( teamId -> {
                TeamDAO team = TeamDAO.loadById(teamId, con);

                if(team != null) {
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
    @Produces(MediaType.APPLICATION_JSON)
    public String setAnnouncements(@QueryParam("announcement1") String announcement1, @QueryParam("announcement2") String announcement2,
                                   @QueryParam("announcement3") String announcement3) {
        // Insert a new match into the DB
        String[] announcements = new String[3];
        announcements[0] = announcement1;
        announcements[1] = announcement2;
        announcements[2] = announcement3;
        Announcements.setAnnouncements(announcements);
        return "{status:'success'}";
    }

    //Ajax Services
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

                //Team Name
                Element teamName = doc.createElement("name");
                teamName.appendChild(doc.createTextNode(curTeam.getName()));
                team.appendChild(teamName);

                //Team Slogan
                Element teamSlogan = doc.createElement("slogan");
                teamSlogan.appendChild(doc.createTextNode(curTeam.getSlogan()));
                team.appendChild(teamSlogan);

                //Team Points
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
    @Path("live/info")
    @Produces(MediaType.TEXT_XML)
    public String liveInfo() {
        DocumentBuilderFactory icFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder icBuilder;
        try {
            icBuilder = icFactory.newDocumentBuilder();
            Document doc = icBuilder.newDocument();
            Element mainRootElement = doc.createElement("Messages");
            doc.appendChild(mainRootElement);

            // append child elements to root element
            Connection con = DBUtils.createConnection();
            Element root = doc.createElement("mssg");

            root.appendChild(doc.createTextNode(Announcements.getCurrentAnnouncement()));

            mainRootElement.appendChild(root);

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


}
