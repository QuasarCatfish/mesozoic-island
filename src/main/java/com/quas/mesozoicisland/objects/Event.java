package com.quas.mesozoicisland.objects;

import java.util.ArrayList;

import com.quas.mesozoicisland.MesozoicIsland;
import com.quas.mesozoicisland.enums.DiscordChannel;
import com.quas.mesozoicisland.enums.EventType;

public class Event {
	
	private static boolean initialized = false;
	private static ArrayList<Event> events;
	
	public static void initialize() {
		if (initialized) return;
		initialized = true;
		events = new ArrayList<Event>();
		
		new Event("Test Event", "An Event to test that everything is working as intended.", EventType.TestEvent, System.currentTimeMillis() - 260_000, System.currentTimeMillis() - 180_000, "Welcome to the start of this event!", "Awe... the event ended already.");
	}
	
	public static boolean isEventActive(EventType et) {
		for (Event e : events) if (e.et == et && e.isRunning()) return true;
		return false;
	}
	
	public static Event[] values() {
		return events.toArray(new Event[0]);
	}
	
	/////////////////////////////////////////////////////
	
	private EventType et;
	private long start, end;
	private String name, desc, startstring, endstring;
	
	private Event(String name, String desc, EventType et, long start, long end, String startstring, String endstring) {
		this.name = name;
		this.desc = desc;
		this.et = et;
		this.start = start;
		this.end = end;
		this.startstring = startstring;
		this.endstring = endstring;
		
		events.add(this);
		new Thread() {
			@Override
			public void run() {
				setName("Event Thread - " + name);
				
				// Start
				if (System.currentTimeMillis() < start) {
					while (System.currentTimeMillis() < start);
					MesozoicIsland.getProfessor().getGuild().getTextChannelById((et == EventType.TestEvent ? DiscordChannel.GameTesting : DiscordChannel.Game).getId()).sendMessageFormat("[Event Squad Mention] **The %s Event has Started!**\n%s", name, startstring).complete();
				}
				
				// End
				if (System.currentTimeMillis() < end) {
					while (System.currentTimeMillis() < end);
					MesozoicIsland.getProfessor().getGuild().getTextChannelById((et == EventType.TestEvent ? DiscordChannel.GameTesting : DiscordChannel.Game).getId()).sendMessageFormat("[Event Squad Mention] **The %s Event has Ended!**\n%s", name, endstring).complete();
				}
			}
		}.start();
	}
	
	public String getName() {
		return name;
	}
	
	public String getDescription() {
		return desc;
	}
	
	public String getStartMessage() {
		return startstring;
	}
	
	public String getEndMessage() {
		return endstring;
	}
	
	public boolean isRunning() {
		long time = System.currentTimeMillis();
		return start <= time && time <= end;
	}
	
	public long getDuration() {
		return end - start;
	}
	
	public EventType getEventType() {
		return et;
	}
	
	public boolean hasStarted() {
		return System.currentTimeMillis() >= start;
	}
	
	public boolean hasEnded() {
		return System.currentTimeMillis() >= end;
	}
	
	public long getStartTime() {
		return start;
	}
	
	public long getEndTime() {
		return end;
	}
}
