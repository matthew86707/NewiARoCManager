package org.jointheleague.iaroc.iaroc2;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.jointheleague.iaroc.iaroc2.db.DBWrapper;

/**
 * Hello world!
 *
 */
public class App 
{
	 public static void main(String[] args) throws Exception {
		 
		 	DBWrapper wr = new DBWrapper();
		 		if(args.length > 0 && args[0].equals("clear")){
		 			wr.dropTables();
		 			wr.createTables();
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
