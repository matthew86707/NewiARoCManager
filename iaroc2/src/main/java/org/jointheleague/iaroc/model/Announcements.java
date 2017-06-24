package org.jointheleague.iaroc.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Announcements {

	private static Announcements instance;
	private static Object lock = new Object();

	//We expect the announcements to change every 20 seconds or so.
	public Integer INTERVAL = 21;

	public static Announcements getInstance() {
		synchronized (lock) {
			if(instance == null) {
				instance = new Announcements();
			}
		}
		return instance;
	}

	private Announcements() {
	}

	private List<String> allAnnouncements = new ArrayList<>();

	public String getCurrentAnnouncement(){
		//We want to rotate between messages on a regular timer. We can, however, emulate that quite fine
		//by taking the current time and figuring out which slice it falls within.

		long currentTimeSeconds = System.currentTimeMillis() / 1000;

		synchronized (lock) {
			if(allAnnouncements.isEmpty()) {
				return "";
			}
			long fullPeriod = allAnnouncements.size() * INTERVAL;

			//Find out how far into the current period we are.
			long timeIntoCurrentPeriod = currentTimeSeconds % fullPeriod;

			long currentIndex = Math.floorDiv(timeIntoCurrentPeriod, INTERVAL);

			return allAnnouncements.get((int)currentIndex);
		}
	}

	public void setAnnouncements(Collection<String> announcements){
		synchronized (lock) {
			this.allAnnouncements.clear();
			allAnnouncements.addAll(announcements);
		}
	}

    public List<String> getAnnouncements() {
		return this.allAnnouncements;
    }
}
