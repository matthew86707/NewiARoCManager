//package org.jointheleague.iaroc.iaroc2;
//
//import org.jointheleague.iaroc.iaroc2.db.DBUtils;
//import org.jointheleague.iaroc.model.Announcements;
//import org.jointheleague.iaroc.model.TeamDAO;
//import org.w3c.dom.Document;
//import org.w3c.dom.Element;
//
//import javax.ws.rs.GET;
//import javax.ws.rs.Path;
//import javax.ws.rs.Produces;
//import javax.ws.rs.core.MediaType;
//import javax.xml.parsers.DocumentBuilder;
//import javax.xml.parsers.DocumentBuilderFactory;
//import javax.xml.transform.OutputKeys;
//import javax.xml.transform.Transformer;
//import javax.xml.transform.TransformerFactory;
//import javax.xml.transform.dom.DOMSource;
//import javax.xml.transform.stream.StreamResult;
//import java.io.InputStream;
//import java.io.StringWriter;
//import java.sql.Connection;
//import java.util.List;
//
//@Path("/index")
//public class EntryPoint {
//
//    //Pages
//
//    @GET
//    @Path("home")
//    @Produces(MediaType.TEXT_HTML)
//    public InputStream home() {
//        return PageLoader.getPage("index.html", true, true);
//    }
//
//    @GET
//    @Path("teams")
//    @Produces(MediaType.TEXT_HTML)
//    public InputStream teams() {
//        return PageLoader.getPage("teams.html", true, true);
//    }
//
//    @GET
//    @Path("admin")
//    @Produces(MediaType.TEXT_HTML)
//    public InputStream admin() {
//        return PageLoader.getPage("admin.html", true, true);
//    }
//
//    @GET
//    @Path("admin/home")
//    @Produces(MediaType.TEXT_HTML)
//    public InputStream adminHome() {
//        return PageLoader.getPage("/admin/home.html", true, true);
//    }
//
//    @GET
//    @Path("admin/forms/addTeam")
//    @Produces(MediaType.TEXT_HTML)
//    public InputStream adminAddTeam() {
//        return PageLoader.getPage("/admin/forms/addTeam.html", true, true);
//    }
//
//    @GET
//    @Path("admin/forms/addMatch")
//    @Produces(MediaType.TEXT_HTML)
//    public InputStream adminAddMatch() {
//        return PageLoader.getPage("/admin/forms/addMatch.html", true, true);
//    }
//
//    @GET
//    @Path("admin/forms/announcements")
//    @Produces(MediaType.TEXT_HTML)
//    public InputStream adminAnnouncements() {
//        return PageLoader.getPage("/admin/forms/announcements.html", true, true);
//    }
//
//    @GET
//    @Path("live")
//    @Produces(MediaType.TEXT_HTML)
//    public InputStream live() {
//        return PageLoader.getPage("live.html", true, true);
//    }
//
//    @GET
//    @Path("admin/forms/addMatchResult")
//    @Produces(MediaType.TEXT_HTML)
//    public InputStream addMatchResult() {
//        return PageLoader.getPage("/admin/forms/addMatchResult.html", true, true);
//    }
//
//    //Ajax Services
//    @GET
//    @Path("teams/data")
//    @Produces(MediaType.TEXT_XML)
//    public String teamData() {
//        DocumentBuilderFactory icFactory = DocumentBuilderFactory.newInstance();
//        DocumentBuilder icBuilder;
//        try {
//            icBuilder = icFactory.newDocumentBuilder();
//            Document doc = icBuilder.newDocument();
//            Element mainRootElement = doc.createElement("Teams");
//            doc.appendChild(mainRootElement);
//
//            // append child elements to root element
//            Connection con = DBUtils.createConnection();
//            List<TeamDAO> teams = TeamDAO.retrieveAllEntries(con);
//            for (TeamDAO curTeam : teams) {
//                Element team = doc.createElement("Team");
//                team.setAttribute("id", Integer.toString(curTeam.getId()));
//
//                //Team Name
//                Element teamName = doc.createElement("name");
//                teamName.appendChild(doc.createTextNode(curTeam.getName()));
//                team.appendChild(teamName);
//
//                //Team Slogan
//                Element teamSlogan = doc.createElement("slogan");
//                teamSlogan.appendChild(doc.createTextNode(curTeam.getSlogan()));
//                team.appendChild(teamSlogan);
//
//                //Team Points
//                Element teamPoints = doc.createElement("points");
//                teamPoints.appendChild(doc.createTextNode(curTeam.getPoints() + ""));
//                team.appendChild(teamPoints);
//
//                mainRootElement.appendChild(team);
//            }
//
//            // output DOM XML to console
//
//            StringWriter writer = new StringWriter();
//            StreamResult result = new StreamResult(writer);
//
//            Transformer transformer = TransformerFactory.newInstance().newTransformer();
//            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
//            DOMSource source = new DOMSource(doc);
//            transformer.transform(source, result);
//
//            writer.flush();
//            return writer.toString();
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        return "";
//    }
//
//    @GET
//    @Path("live/info")
//    @Produces(MediaType.TEXT_XML)
//    public String liveInfo() {
//        DocumentBuilderFactory icFactory = DocumentBuilderFactory.newInstance();
//        DocumentBuilder icBuilder;
//        try {
//            icBuilder = icFactory.newDocumentBuilder();
//            Document doc = icBuilder.newDocument();
//            Element mainRootElement = doc.createElement("Messages");
//            doc.appendChild(mainRootElement);
//
//            // append child elements to root element
//            Connection con = DBUtils.createConnection();
//            Element root = doc.createElement("mssg");
//
//            root.appendChild(doc.createTextNode(Announcements.getCurrentAnnouncement()));
//
//            mainRootElement.appendChild(root);
//
//            // output DOM XML to console
//
//            StringWriter writer = new StringWriter();
//            StreamResult result = new StreamResult(writer);
//
//            Transformer transformer = TransformerFactory.newInstance().newTransformer();
//            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
//            DOMSource source = new DOMSource(doc);
//            transformer.transform(source, result);
//
//            writer.flush();
//            return writer.toString();
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        return "";
//    }
//}
//
