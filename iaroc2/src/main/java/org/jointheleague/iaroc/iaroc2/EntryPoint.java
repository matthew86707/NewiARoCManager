package org.jointheleague.iaroc.iaroc2;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

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
			return null;
	    }
	}

