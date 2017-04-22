package org.jointheleague.iaroc.iaroc2;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.StringWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.jointheleague.iaroc.iaroc2.db.DBUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

@Path("/index")
	public class EntryPoint {
	
	//Pages
	
	    @GET
	    @Path("home")
	    @Produces(MediaType.TEXT_HTML)
	    public InputStream home() {
	        return PageLoader.getPage("index.html", true, true);
	    }
	    
	    @GET
	    @Path("teams")
	    @Produces(MediaType.TEXT_HTML)
	    public InputStream teams() {
	        return PageLoader.getPage("teams.html", true, true);
	    }
	    
	    @GET
	    @Path("admin")
	    @Produces(MediaType.TEXT_HTML)
	    public InputStream admin() {
	        return PageLoader.getPage("admin.html", true, true);
	    }
	    
	    @GET
	    @Path("admin/home")
	    @Produces(MediaType.TEXT_HTML)
	    public InputStream adminHome() {
	        return PageLoader.getPage("/admin/home.html", true, true);
	    }
	    
	    @GET
	    @Path("admin/forms/addTeam")
	    @Produces(MediaType.TEXT_HTML)
	    public InputStream adminAddTeam() {
	        return PageLoader.getPage("/admin/forms/addTeam.html", true, true);
	    }
	    
	    @GET
	    @Path("live")
	    @Produces(MediaType.TEXT_HTML)
	    public InputStream live() {
	        return PageLoader.getPage("live.html", true, true);
	    }
	    
	//Ajax Services
	    
	    @GET
	    @Path("teams/data")
	    @Produces(MediaType.TEXT_XML)
	    public String teamData(){
	    	 DocumentBuilderFactory icFactory = DocumentBuilderFactory.newInstance();
	         DocumentBuilder icBuilder;
	         try {
	             icBuilder = icFactory.newDocumentBuilder();
	             Document doc = icBuilder.newDocument();
	             Element mainRootElement = doc.createElement("Teams");
	             doc.appendChild(mainRootElement);
	  
	             // append child elements to root element
	             Connection con = DBUtils.createConnection();
	             String sql = "SELECT * FROM TEAMS";
	             try {
	     			PreparedStatement stmt = con.prepareStatement(sql);
	     			ResultSet rs = stmt.executeQuery();
	     			while(rs.next()){
	     				
	     				Element team = doc.createElement("Team");
	     		        team.setAttribute("id", rs.getString("id"));
	     		        
	     		       //Team Name
	     				Element teamName = doc.createElement("name");
	     		        teamName.appendChild(doc.createTextNode(rs.getString("name")));
	     		        team.appendChild(teamName);
	     		        
	     		       //Team Slogan
	     				Element teamSlogan = doc.createElement("slogan");
	     		        teamSlogan.appendChild(doc.createTextNode(rs.getString("slogan")));
	     		        team.appendChild(teamSlogan);
	     		        
	     		        mainRootElement.appendChild(team);
	     		        
	     			}
	     		} catch (SQLException e) {
	     			// TODO Auto-generated catch block
	     			e.printStackTrace();
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
	}

