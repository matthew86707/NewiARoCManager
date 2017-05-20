package org.jointheleague.iaroc.iaroc2;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.persistence.internal.expressions.SQLStatement;
import org.jointheleague.iaroc.iaroc2.db.DBUtils;
import org.jointheleague.iaroc.model.Announcements;
import org.jointheleague.iaroc.model.EntityManager;
import org.jointheleague.iaroc.model.MatchDAO;
import org.jointheleague.iaroc.model.TeamDAO;

public class FormHandler extends HttpServlet{
	
	@Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
    {
		
		String type = req.getParameter("type");
		
		 resp.setContentType("text/plain");
	     resp.setCharacterEncoding("UTF-8");
	     
	     Connection con = DBUtils.createConnection();
		
		//Check type of form
		switch(type){
		case "addTeam":
			
	        // Insert a new team into the DB
	        
	      
	        TeamDAO toInput = new TeamDAO(con, 0, req.getParameter("name"), req.getParameter("slogan"), "nothing");
	        toInput.insert();
	        
	        //HTML Response
	        
	        resp.sendRedirect("/index/teams");
			break;
		case "addMatch":
			
			// Insert a new match into the DB
			
			TeamDAO teamA = TeamDAO.loadById(Integer.parseInt(req.getParameter("teamA")), con);
			TeamDAO teamB = TeamDAO.loadById(Integer.parseInt(req.getParameter("teamB")), con);
	        
	        MatchDAO match = new MatchDAO(con, 0, 0, MatchDAO.TYPES.DRAG_RACE);
	        match.insert();
	        
	        EntityManager.insertRelationshipTeamToMatch(con, teamA.getId(), match.getId());
	        EntityManager.insertRelationshipTeamToMatch(con, teamB.getId(), match.getId());
	        
	        
	        System.out.println(EntityManager.getTeamsByMatch(con, match.getId()));
	        
	        //HTML Response
	        
	        resp.sendRedirect("/index/live");
			break;
		case "announcements":
			
			String[] announcements = new String[3];
			announcements[0] = req.getParameter("a1");
			announcements[1] = req.getParameter("a2");
			announcements[2] = req.getParameter("a3");
			Announcements.setAnnouncements(announcements);

			System.out.println("Announcements Edited...");
			
			
			break;
		default:
			
		}
		
		 resp.setHeader("Location:",  "/index/home");
       
    }

}
