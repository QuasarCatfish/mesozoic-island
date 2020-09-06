package com.quas.mesozoicisland.objects;

import java.awt.Color;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import com.quas.mesozoicisland.JDBC;
import com.quas.mesozoicisland.MesozoicIsland;
import com.quas.mesozoicisland.enums.DiscordChannel;
import com.quas.mesozoicisland.enums.DiscordRole;
import com.quas.mesozoicisland.enums.EventType;
import com.quas.mesozoicisland.util.Constants;
import com.quas.mesozoicisland.util.Util;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;

public class Event {
	
	private static boolean initialized = false;
	private static ArrayList<Event> events;
	
	public static void initialize() {
		if (initialized) return;
		initialized = true;
		events = new ArrayList<Event>();
		
		try (ResultSet res = JDBC.executeQuery("select * from gameevents;")) {
			while (res.next()) {
				int id = res.getInt("eventid");
				String name = res.getString("eventname");
				String description = res.getString("eventdescription");
				EventType type = EventType.of(res.getInt("eventtype"));
				long start = res.getLong("starttime");
				long end = res.getLong("endtime");
				String startmsg = res.getString("startmsg");
				String endmsg = res.getString("endmsg");
				boolean announce = res.getBoolean("announce");
				new Event(id, name, description, type, start, end, startmsg, endmsg, announce);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
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
	
	private Event(int id, String name, String desc, EventType et, long start, long end, String startstring, String endstring, boolean announce) {
		this.name = name;
		this.desc = desc;
		this.et = et;
		this.start = start;
		this.end = end;
		this.startstring = startstring;
		this.endstring = endstring;
		
		DiscordChannel dc = et == EventType.TestEvent ? DiscordChannel.GameTesting : DiscordChannel.Game;
		TextChannel tc = MesozoicIsland.getProfessor().getGuild().getTextChannelById(dc.getId());

		events.add(this);
		new Thread() {
			@Override
			public void run() {
				setName("Event Thread - " + name);
				
				// Start of Event
				if (System.currentTimeMillis() < start) {
					while (System.currentTimeMillis() < start);
					
					if (announce) {
						// Make embed in #events
						EmbedBuilder eb = new EmbedBuilder();
						eb.setColor(Color.GREEN);
						eb.setTitle(name + " Event");
						eb.setDescription(desc);
						eb.addField("Start Time", Util.formatDateTime(start), true);
						eb.addField("End Time", Util.formatDateTime(end), true);
						
						Message m = MesozoicIsland.getProfessor().getGuild().getTextChannelById(DiscordChannel.Events.getId()).sendMessage(eb.build()).complete();
						JDBC.executeUpdate("update gameevents set discordmessage = %d where eventid = %d;", m.getIdLong(), id);

						// Send message to #game
						if (startstring == null) {
							Message pin = tc.sendMessageFormat("%s **%s has Started!**", DiscordRole.EventPing.toString(), name).complete();
							pin.pin().complete();
						} else {
							Message pin = tc.sendMessageFormat("%s **%s has Started!**\n%s", DiscordRole.EventPing.toString(), name, startstring).complete();
							pin.pin().complete();
						}
					}

					if (et == EventType.Contest) {
						StringBuilder sb = new StringBuilder();
						sb.append("Contestants:");

						try (ResultSet res = JDBC.executeQuery("select playerid from players where !isnull(contest);")) {
							while (res.next()) {
								Player p = Player.getPlayer(res.getLong("playerid"));
								Dinosaur d = Dinosaur.getDinosaur(Util.getDexForm(p.getContest()));
								sb.append(String.format("\n%s %s - %s", Constants.BULLET_POINT, p.getName(), d.getDinosaurName()));
								JDBC.addDinosaur(tc, p.getIdLong(), d.getIdPair());
							}
						} catch (SQLException e) {
							e.printStackTrace();
						}

						MesozoicIsland.getProfessor().getGuild().getTextChannelById(DiscordChannel.Contest.getId()).sendMessage(sb.toString()).complete();
						sb.append("\n\nYou have each been given the contest dinosaur you have chosen.");
						tc.sendMessage(sb.toString()).complete();
					}
				}
				
				// End of Event
				if (System.currentTimeMillis() < end) {
					while (System.currentTimeMillis() < end);

					if (announce) {
						// get #events message id
						long msgid = -1L;
						try (ResultSet res = JDBC.executeQuery("select * from gameevents where eventid = %d;", id)) {
							if (res.next()) {
								msgid = res.getLong("discordmessage");
							}
						} catch (SQLException e) {
							e.printStackTrace();
						}
	
						// Edit embed in #events
						if (msgid > 0L) {
							Message msg = MesozoicIsland.getProfessor().getGuild().getTextChannelById(DiscordChannel.Events.getId()).retrieveMessageById(msgid).complete();
	
							if (!msg.getEmbeds().isEmpty()) {
								EmbedBuilder eb = new EmbedBuilder(msg.getEmbeds().get(0));
								eb.setColor(Color.RED);
								msg.editMessage(eb.build()).complete();
							}
						}

						// Send message to #game
						if (endstring == null) {
							Message pin = tc.sendMessageFormat("%s **%s has Ended!**", DiscordRole.EventPing.toString(), name).complete();
							pin.pin().complete();
						} else {
							Message pin = tc.sendMessageFormat("%s **%s has Ended!**\n%s", DiscordRole.EventPing.toString(), name, endstring).complete();
							pin.pin().complete();
						}
					}
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
