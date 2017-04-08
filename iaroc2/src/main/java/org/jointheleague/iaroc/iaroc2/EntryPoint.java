package org.jointheleague.iaroc.iaroc2;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/entry-point")
	public class EntryPoint {
	    @GET
	    @Path("test")
	    @Produces(MediaType.TEXT_HTML)
	    public InputStream test() {
	        return PageLoader.getPage("index.html");
	    }
	}

