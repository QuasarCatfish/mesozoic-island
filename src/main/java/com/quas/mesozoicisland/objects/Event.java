package com.quas.mesozoicisland.objects;

import java.awt.Color;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;

import com.quas.mesozoicisland.JDBC;
import com.quas.mesozoicisland.MesozoicIsland;
import com.quas.mesozoicisland.enums.CustomPlayer;
import com.quas.mesozoicisland.enums.DiscordChannel;
import com.quas.mesozoicisland.enums.DiscordRole;
import com.quas.mesozoicisland.enums.EventType;
import com.quas.mesozoicisland.enums.ItemID;
import com.quas.mesozoicisland.enums.Stat;
import com.quas.mesozoicisland.util.Constants;
import com.quas.mesozoicisland.util.Util;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
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
	private boolean announce;
	
	private Event(int id, String name, String desc, EventType et, long start, long end, String startstring, String endstring, boolean announce) {
		this.name = name;
		this.desc = desc;
		this.et = et;
		this.start = start;
		this.end = end;
		this.startstring = startstring;
		this.endstring = endstring;
		this.announce = announce;
		
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
						eb.setTitle(name);
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
					
					if (et == EventType.SecretSanta) {
						ArrayList<Long> players = new ArrayList<Long>();
						ArrayList<Long> santa = new ArrayList<Long>();

						try (ResultSet res = JDBC.executeQuery("select * from players where santa > 0;")) {
							while (res.next()) {
								long id = res.getLong("playerid");
								players.add(id);
								santa.add(id);
							}
						} catch (SQLException e) {
							e.printStackTrace();
						}

						int count = 0;
						do {
							count = 0;
							Collections.shuffle(santa);

							for (int q = 0; q < santa.size(); q++) {
								if (players.get(q).equals(santa.get(q))) {
									count++;
								}
							}
						} while (count > 0);

						Guild g = MesozoicIsland.getAssistant().getGuild();
						for (int q = 0; q < santa.size(); q++) {
							JDBC.executeUpdate("update players set santa = %d where playerid = %d;", santa.get(q), players.get(q));
							Member m = g.getMemberById(players.get(q));
							if (m == null) continue;
							try {
								m.getUser().openPrivateChannel().complete().sendMessageFormat("**== Secret Santa Event ==**\nThis year, you will be collecting Gift Tokens to buy presents for %s! You can get Gift Tokens by opening Mystery Presents, which wild dinosaurs have a chance of holding. Be sure to buy all your presents before Christmas!", Player.getPlayer(santa.get(q)).getName()).complete();
							} catch (Exception e) {}
						}
					}

					// reset vars and give quests
					if (et == EventType.DarknessDescent) {
						JDBC.setVariable(Constants.EVENT_DARKNESS_DESCENT_FLOORS, "0");
						JDBC.setVariable(Constants.EVENT_DARKNESS_DESCENT_LOSSES, "0");
						JDBC.executeUpdate("update bags set count = 0 where item = 0 and dmg = %d;", Stat.DarknessDescentDungeonsEntered.getStatId());
						JDBC.executeUpdate("update bags set count = 0 where item = 0 and dmg = %d;", Stat.DarknessDescentDinosaursDefeated.getStatId());
						JDBC.executeUpdate("update bags set count = 0 where item = 0 and dmg = %d;", Stat.DarknessDescentFloorsCleared.getStatId());
						
						ItemID questbook = ItemID.QuestBook;
						try (ResultSet res = JDBC.executeQuery("select * from bags where item = %d and dmg = %d;", questbook.getItemId(), questbook.getItemDamage())) {
							while (res.next()) {
								long player = res.getLong("bags.player");
								if (player < CustomPlayer.getUpperLimit()) continue;

								JDBC.addQuest(player, 1101);
								JDBC.addQuest(player, 1103);
							}
						} catch (SQLException e) {
							e.printStackTrace();
						}
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

					// Event End
					if (et == EventType.SecretSanta) {
						// Finish the end of secret santa happenings.
						// If players still have presents, randomly spend them on gifts
						// Turn gifts into mail
						// Send mail to recipient
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

	public boolean isAnnounce() {
		return announce;
	}
}
