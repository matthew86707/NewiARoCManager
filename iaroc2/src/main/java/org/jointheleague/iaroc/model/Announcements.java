package org.jointheleague.iaroc.model;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Announcements implements Runnable{
	
	public static final int DELAY = 6;
	
	private static String current = "";
	private static int currentIndex = 0;
	private static String[] allAnnouncements = new String[3];
	
	public static void init(){
		ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
		 scheduler.scheduleAtFixedRate(new Announcements(), 1, DELAY, TimeUnit.SECONDS);
	}
	
	public static String getCurrentAnnouncement(){
		return current;
	}
	
	public static void nextAnnouncement(){
		currentIndex++;
		if(currentIndex > allAnnouncements.length - 1){
			currentIndex = 0;
		}
		if(allAnnouncements[currentIndex].equals("auto")){
			current = getAutoAnnouncement();
		}else{
		current = allAnnouncements[currentIndex];
		}
	}
	
	public static void setAnnouncements(String[] announcements){
		allAnnouncements = announcements;
	}
	
	public static String getAutoAnnouncement(){
		//TODO : Add code that generates announcement strings based on the current matches / upcoming matches...
		return "Next up...Blue Team battles it out versus the Red Team!";
	}

	@Override
	public void run() {
		try{
		nextAnnouncement();
		}catch(Exception e){
			
		}
	}
}
