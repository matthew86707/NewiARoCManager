package org.jointheleague.iaroc.rest;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken.Payload;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;

public class Authentication {
	
	public static boolean authUser(String idTokenString) throws Exception{
		JsonFactory jsFac = new JacksonFactory();
		HttpTransport trans = new NetHttpTransport();
		GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(trans, jsFac)
	    .setAudience(Collections.singletonList("287870693888-duj18o9i2mubd2s6r6ca1a9efp7gkp9j.apps.googleusercontent.com"))
	    .build();

	// (Receive idTokenString by HTTPS POST)

	GoogleIdToken idToken = verifier.verify(idTokenString);
	if (idToken != null) {
	  Payload payload = idToken.getPayload();

	  // Print user identifier
	  String userId = payload.getSubject();
	  System.out.println("User ID: " + userId);

	  // Get profile information from payload
	  String email = payload.getEmail();
	  boolean emailVerified = Boolean.valueOf(payload.getEmailVerified());
	  String name = (String) payload.get("name");
	  String pictureUrl = (String) payload.get("picture");
	  String locale = (String) payload.get("locale");
	  String familyName = (String) payload.get("family_name");
	  String givenName = (String) payload.get("given_name");

	  System.out.println("Reading whitelist");
	  ArrayList<String> emails = new ArrayList<String>();
		InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream("authWhitelist.txt");
		BufferedReader br = new BufferedReader(new InputStreamReader(is));
	  String line = br.readLine();
	  while(line != null){
	  	System.out.println("email is " + email);
		  emails.add(line);
		  line = br.readLine();
	  }
	  br.close();
	  
	  if(emails.contains(email) && emailVerified){
		  System.out.println("Email Adress Verified");
		  return true;
	  }else{
		  System.out.println("Invalid Email Adress");
		  return false;
	  }

	  // Use or store profile information
	  // ...

	} else {
	  System.out.println("Invalid ID token.");
	  return false;
	}

	}

}
