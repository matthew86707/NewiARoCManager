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
import org.jointheleague.iaroc.model.TeamDAO;

public class FormHandler extends HttpServlet{
	
	@Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
    {

        // Insert a new team into the DB
        
        Connection con = DBUtils.createConnection();
        TeamDAO toInput = new TeamDAO(con, 0, req.getParameter("name"), req.getParameter("slogan"), "nothing");
        toInput.insert();
        
        //HTML Response
        
        resp.sendRedirect("/index/teams");
        
        resp.setContentType("text/plain");
        resp.setCharacterEncoding("UTF-8");
        
        resp.setHeader("Location:",  "/index/home");
        
       
    }

}
