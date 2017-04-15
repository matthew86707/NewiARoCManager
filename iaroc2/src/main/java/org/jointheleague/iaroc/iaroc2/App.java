package org.jointheleague.iaroc.iaroc2;

import java.sql.Connection;


import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.jointheleague.iaroc.iaroc2.db.DBUtils;
import org.jointheleague.iaroc.model.MemberDAO;
import org.jointheleague.iaroc.model.TeamDAO;

/**
 * Hello world!
 *
 */
public class App 
{
	 public static void main(String[] args) throws Exception {
		 
		 	Connection con = DBUtils.createConnection();
		 		if(args.length > 0 && args[0].equals("clear")){
		 			//Get Instances
		 			TeamDAO teams = new TeamDAO(con);
		 			MemberDAO members = new MemberDAO(con);
		 			//Drop
		 			teams.dropTable();
		 			members.dropTable();
		 			//Create
		 			teams.createTable();
		 			members.createTable();
		 			
		 		}
		 		
		 		
		 
		 
	        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
	        context.setContextPath("/");

	       Server jettyServer = new Server(8080);
	        jettyServer.setHandler(context);

	       ServletHolder jerseyServlet = context.addServlet(
	             org.glassfish.jersey.servlet.ServletContainer.class, "/*");
	        jerseyServlet.setInitOrder(0);

	       // Tells the Jersey Servlet which REST service/class to load.
	        jerseyServlet.setInitParameter(
	           "jersey.config.server.provider.classnames",
	           EntryPoint.class.getCanonicalName());

	       try {
	            jettyServer.start();
	            jettyServer.join();
	        } finally {
	            jettyServer.destroy();
	        }
	    }
	    
}
