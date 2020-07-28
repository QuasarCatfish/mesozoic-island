package com.quas.mesozoicisland.battle;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;
import java.util.TreeSet;

import com.quas.mesozoicisland.JDBC;
import com.quas.mesozoicisland.MesozoicIsland;
import com.quas.mesozoicisland.enums.CustomPlayer;
import com.quas.mesozoicisland.enums.DiscordChannel;
import com.quas.mesozoicisland.enums.DiscordEmote;
import com.quas.mesozoicisland.enums.DiscordRole;
import com.quas.mesozoicisland.enums.Location;
import com.quas.mesozoicisland.objects.Dinosaur;
import com.quas.mesozoicisland.objects.Dungeon;
import com.quas.mesozoicisland.objects.Egg;
import com.quas.mesozoicisland.objects.Item;
import com.quas.mesozoicisland.objects.Player;
import com.quas.mesozoicisland.util.Action;
import com.quas.mesozoicisland.util.Constants;
import com.quas.mesozoicisland.util.MesozoicRandom;
import com.quas.mesozoicisland.util.Pair;
import com.quas.mesozoicisland.util.Stats;
import com.quas.mesozoicisland.util.Util;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageHistory;
import net.dv8tion.jda.api.entities.User;

public class SpawnManager {

	private static long spawntime = System.currentTimeMillis() + Constants.MIN_SPAWN_TIMER;
	private static long lastspawn = 0;
	private static boolean autospawn = true;
	private static volatile boolean waiting = false;
	
	public static boolean doAutoSpawn() {
		return autospawn;
	}
	
	public static synchronized void trySpawn() {
		Util.sleep(100);
		if (MesozoicIsland.isQuitting()) return;
		if (waiting || isWildBattleHappening()) {
			spawntime = Long.MAX_VALUE;
			return;
		}
		if (spawntime == Long.MAX_VALUE && lastspawn + Constants.MAX_SPAWN_TIMER <= System.currentTimeMillis()) resetSpawnTime();
		if (spawntime == Long.MAX_VALUE) setSpawnTime();
		if (spawntime <= System.currentTimeMillis()) {
			new Thread() {
				@Override
				public void run() {
					// Spawn Dinosaur
					autospawn = true;
					if (Constants.SPAWN_DUNGEONS && MesozoicRandom.nextInt(Constants.DUNGEON_SPAWN_CHANCE) == 0 && !isDungeonSpawned()) {
						spawnDungeon();
					} else if (Constants.SPAWN_EGGS && MesozoicRandom.nextInt(Constants.EGG_SPAWN_CHANCE) == 0) {
						spawnEgg();
					} else {
						spawnWild();
					}
					lastspawn = System.currentTimeMillis();
				}
			}.start();
		}
	}
	
	public static void setSpawnTime() {
		spawntime = System.currentTimeMillis() + getRandomSpawnTime();
	}
	
	public static void setSpawnTime(long time) {
		spawntime = time;
	}
	
	public static long getRandomSpawnTime() {
		return MesozoicRandom.nextLong(Constants.MIN_SPAWN_TIMER, Constants.MAX_SPAWN_TIMER);
	}
	
	public static void resetSpawnTime() {
		spawntime = 0;
	}
	
	public static long getSpawnTime() {
		return spawntime;
	}
	
	public static boolean isWildBattleHappening() {
		for (BattleTier bc : BattleTier.getBattleTiers()) {
			MessageHistory mh = bc.getBattleChannel().getBattleChannel().getChannel(MesozoicIsland.getProfessor()).getHistory();
			mh.retrievePast(1).complete();
			if (!mh.isEmpty()) return true;
		}
		return false;
	}
	
	public static boolean isDungeonSpawned() {
		MessageHistory mh = BattleChannel.Dungeon.getBattleChannel().getChannel(MesozoicIsland.getProfessor()).getHistory();
		mh.retrievePast(1).complete();
		return !mh.isEmpty();
	}
	
	public static int getSpawnCount() {
		return 10 / MesozoicRandom.nextInt(3, 11);
	}
	
	public static void spawnEgg() {
		waiting = true;
		
		// Generate Egg
		Egg[] eggs = new Egg[MesozoicRandom.nextInt(Constants.MAX_EGG_SPAWN) + 1];
		for (int q = 0; q < eggs.length; q++) eggs[q] = Egg.getRandomEgg(MesozoicRandom.nextDinosaur().getIdPair());
		
		// Build Spawn Message
		EmbedBuilder eb = new EmbedBuilder();
		eb.setTitle(eggs.length == 1 ? "An Egg has been found!" : eggs.length + " Eggs have been found!");
		eb.setColor(Constants.COLOR);
		for (int q = 0; q < eggs.length; q++) eb.addField("Egg", eggs[q].getEggName(), false);
		
		String msg = String.format("React with %s to have a chance to win %s egg.", DiscordEmote.Fossil.toString(), eggs.length == 1 ? "the" : "an");
		String time = " You have %s remaining.";
		
		// Send Spawn Message
		Util.setRolesMentionable(true, DiscordRole.SpawnPing, DiscordRole.EggPing);
		Util.complete(Constants.SPAWN_CHANNEL.getChannel(MesozoicIsland.getAssistant()).sendMessageFormat("%s %s", DiscordRole.SpawnPing.toString(), DiscordRole.EggPing.toString()).embed(eb.build()));
		Util.setRolesMentionable(false, DiscordRole.SpawnPing, DiscordRole.EggPing);
		
		// Send Timer Message
		Message m = Util.complete(Constants.SPAWN_CHANNEL.getChannel(MesozoicIsland.getAssistant()).sendMessageFormat(msg + time, Util.formatTime(Constants.EGG_WAIT)));
		Util.complete(m.addReaction(DiscordEmote.Fossil.getEmote()));
		
		// Edit with time remaining
		for (long q = Constants.EGG_WAIT;; q -= Math.min(q, 5_000)) {
			if (q <= 0) break;
			try {
				m.editMessageFormat(msg + time, Util.formatTime(q)).complete();
			} catch (Exception e) {
				System.out.println("Caught: " + e.toString());
			}
			Util.sleep(Math.min(q, 5_000));
		}
		
		// Get Battle Teams
		List<User> users = Util.complete(m.retrieveReactionUsers(DiscordEmote.Fossil.getEmote()));
		List<Player> players = new ArrayList<Player>();
		Util.complete(m.delete());
		
		Item incubator = Item.getItem(new Pair<Integer, Long>(91, 0L));
		for (User u : users) {
			if (u.isBot()) continue;
			if (u.isFake()) continue;
			Player p = Player.getPlayer(u.getIdLong());
			if (p == null) continue;
			long inc = p.getBag().getOrDefault(incubator, 0L);
			if (inc <= p.getEggCount()) continue;
			players.add(p);
		}
		
		// Egg Recipients Embed
		eb = new EmbedBuilder();
		eb.setTitle(eggs.length == 1 ? "Egg Recipient" : "Egg Recipients");
		eb.setColor(Constants.COLOR);
		for (Egg egg : eggs) {
			Player p = players.isEmpty() ? Player.getPlayer(CustomPlayer.EggSalesman.getIdLong()) : players.get(MesozoicRandom.nextInt(players.size()));
			eb.addField(egg.getEggName(), p.getName(), true);
			JDBC.addEgg(p.getIdLong(), egg);
		}
		
		Util.complete(Constants.SPAWN_CHANNEL.getChannel(MesozoicIsland.getAssistant()).sendMessage(eb.build()));
		if (users.isEmpty()) autospawn = false;
		setSpawnTime();
		waiting = false;
	}
	
	public static void spawnWild() {
		waiting = true;
		
		// Generate Wild Dinosaurs
		TreeMap<BattleTier, Dinosaur[]> wilds = new TreeMap<BattleTier, Dinosaur[]>();
		TreeMap<BattleTier, Location> locations = new TreeMap<BattleTier, Location>();
		for (BattleTier tier : BattleTier.getBattleTiers()) {
			Dinosaur[] wild = new Dinosaur[getSpawnCount()];
			for (int q = 0; q < wild.length; q++) {
				wild[q] = MesozoicRandom.nextDinosaur().setLevel(tier.getRandomLevel());
			}
			wilds.put(tier, wild);
			locations.put(tier, MesozoicRandom.nextUnusedLocation());
		}
		
		// Build Spawn Message
		EmbedBuilder eb = new EmbedBuilder();
		eb.setTitle("Wild Dinosaurs have appeared!");
		eb.setColor(Constants.COLOR);
		for (BattleTier tier : wilds.keySet()) {
			StringBuilder sb = new StringBuilder();
			sb.append("Location: ");
			sb.append(locations.get(tier).toString());
			
			sb.append("\nDinosaurs:");
			for (Dinosaur d : wilds.get(tier)) {
				sb.append("\n• ");
				sb.append(d.toString());
				sb.append(" [");
				sb.append(d.getElement().toString());
				sb.append("]");
				sb.append(" [");
				sb.append(d.getRarity().toString());
				sb.append("]");
			}
			
			eb.addField(tier.toString(), sb.toString(), false);
		}
		String msg = String.format("React with %s to join the battle.", DiscordEmote.Fossil.toString());
		String time = " You have %s remaining.";
		
		// Send Spawn Message
		Util.setRolesMentionable(true, DiscordRole.SpawnPing);
		Util.complete(Constants.SPAWN_CHANNEL.getChannel(MesozoicIsland.getAssistant()).sendMessageFormat("%s", DiscordRole.SpawnPing).embed(eb.build()));
		Util.setRolesMentionable(false, DiscordRole.SpawnPing);
		
		// Send Timer Message
		Message m = Util.complete(Constants.SPAWN_CHANNEL.getChannel(MesozoicIsland.getAssistant()).sendMessageFormat(msg + time, Util.formatTime(Constants.BATTLE_WAIT)));
		Util.complete(m.addReaction(DiscordEmote.Fossil.getEmote()));
		
		// Edit with time remaining
		for (long q = Constants.BATTLE_WAIT;; q -= Math.min(q, 5_000)) {
			if (q <= 0) break;
			try {
				m.editMessageFormat(msg + time, Util.formatTime(q)).complete();
			} catch (Exception e) {
				System.out.println("Caught: " + e.toString());
			}
			Util.sleep(Math.min(q, 5_000));
		}
		
		// Get Battle Teams
		List<User> users = Util.complete(m.retrieveReactionUsers(DiscordEmote.Fossil.getEmote()));
		Util.complete(m.delete());
		TreeMap<BattleTier, List<BattleTeam>> teams = new TreeMap<BattleTier, List<BattleTeam>>();
		
		boolean players = false;
		for (User u : users) {
			if (u.isBot()) continue;
			if (u.isFake()) continue;
			players = true;
			if (Battle.isPlayerBattling(u.getIdLong())) continue;
			BattleTeam bt = new BattleTeam(u.getIdLong());
			if (bt.isInvalid()) continue;
			if (!teams.containsKey(bt.getBattleTier())) teams.put(bt.getBattleTier(), new ArrayList<BattleTeam>());
			teams.get(bt.getBattleTier()).add(bt.setMax(wilds.get(bt.getBattleTier()).length));
			JDBC.addItem(bt.getPlayer().getIdLong(), Stats.of(Stats.BATTLES_ENTERED));
		}
		
		// Create Joined Players Embed
		eb = new EmbedBuilder();
		eb.setColor(Constants.COLOR);
		eb.setDescription("Channel Links: ");
		
		long maxtime = 0;
		int sum = 0;
		for (BattleTier tier : wilds.keySet()) {
			if (teams.containsKey(tier)) {
				Battle b = new Battle(tier.getBattleChannel(), BattleType.FFA, locations.get(tier));
				b.addBoss(new BattleTeam(CustomPlayer.Wild.getPlayer(), wilds.get(tier)));
				Battle.markPlayerBattling(CustomPlayer.Wild.getIdLong(), true);
				
				TreeSet<String> names = new TreeSet<String>();
				for (BattleTeam bt : teams.get(tier)) {
					b.addTeam(bt);
					names.add(bt.getPlayer().getName());
					Battle.markPlayerBattling(bt.getPlayer().getIdLong(), true);
				}
				eb.addField(String.format("**%s** (%,d Player%s)", tier.toString(), names.size(), names.size() == 1 ? "" : "s"), Util.join(names.toArray(new String[0]), ", ", 0, names.size()), true);
				sum += names.size();
				
				eb.appendDescription(tier.getBattleChannel().getBattleChannel().toString());
				eb.appendDescription(" ");
				
				long timer = b.start(tier.ordinal());
				if (timer > maxtime) maxtime = timer;
				for (BattleTeam bt : teams.get(tier)) {
					Action.removePlayerFromBattleDelayed(bt.getPlayer().getIdLong(), timer);
				}
			} else {
				eb.addField(tier.toString(), "None", true);
			}
		}
		
		eb.setTitle(String.format("**Battle** (%,d Player%s)", sum, sum == 1 ? "" : "s"));
		Action.removePlayerFromBattleDelayed(CustomPlayer.Wild.getIdLong(), maxtime);
		Util.complete(Constants.SPAWN_CHANNEL.getChannel(MesozoicIsland.getAssistant()).sendMessage(eb.build()));
		if (!players) {
			autospawn = false;
			setSpawnTime();
		}
		waiting = false;
	}
	
	public static void spawnDungeon() {
		waiting = true;
		
		// Generate Dungeon
		Dungeon d = Dungeon.generateRandomDungeon();
		
		// Build Spawn Message
		EmbedBuilder eb = new EmbedBuilder();
		eb.setTitle("A Dungeon has appeared!");
		eb.setColor(Constants.COLOR);
		eb.addField("Dungeon Size", String.format("%,d Floors", d.getFloorCount()), true);
		eb.addField("Difficulty", d.getDifficultyString(), true);
		eb.addField("Location", d.getLocation().toString(), true);
		eb.addField("Boss", d.getBoss().toString() + " [" + d.getBoss().getElement().toString() + "]", true);
		
		String msg = String.format("React with %s to join the dungeon exploration team.", DiscordEmote.Fossil.toString());
		String time = " You have %s remaining.";
		
		// Send Initial Message
		Util.setRolesMentionable(true, DiscordRole.SpawnPing, DiscordRole.DungeonPing);
		Util.complete(Constants.SPAWN_CHANNEL.getChannel(MesozoicIsland.getAssistant()).sendMessageFormat("%s %s", DiscordRole.SpawnPing, DiscordRole.DungeonPing).embed(eb.build()));
		Util.setRolesMentionable(false, DiscordRole.SpawnPing, DiscordRole.DungeonPing);
		
		// Send Timer Mesage
		Message m = Util.complete(Constants.SPAWN_CHANNEL.getChannel(MesozoicIsland.getAssistant()).sendMessageFormat(msg + time, Util.formatTime(Constants.DUNGEON_WAIT)));
		Util.complete(m.addReaction(DiscordEmote.Fossil.getEmote()));
		
		// Edit with time remaining
		for (long q = Constants.DUNGEON_WAIT;; q -= Math.min(q, 5_000)) {
			if (q <= 0) break;
			try {
				m.editMessageFormat(msg + time, Util.formatTime(q)).complete();
			} catch (Exception e) {
				System.out.println("Caught: " + e.toString());
			}
			Util.sleep(Math.min(q, 5_000));
		}
		
		// Get Battle Teams
		List<User> users = Util.complete(m.retrieveReactionUsers(DiscordEmote.Fossil.getEmote()));
		Util.complete(m.delete());
		List<BattleTeam> teams = new ArrayList<BattleTeam>();
		
		int players = 0;
		for (User u : users) {
			if (u.isBot()) continue;
			if (u.isFake()) continue;
			if (Battle.isPlayerBattling(u.getIdLong())) continue;
			BattleTeam bt = new BattleTeam(u.getIdLong());
			if (bt.isInvalid()) continue;
			teams.add(bt);
			JDBC.addItem(bt.getPlayer().getIdLong(), Stats.of(Stats.DUNGEONS_ENTERED));
			players++;
		}
		
		// Create Joined Players Embed
		eb = new EmbedBuilder();
		eb.setTitle(String.format("**Dungeon** (%,d Player%s)", players, players == 1 ? "" : "s"));
		eb.setDescription("Channel Link: ");
		eb.appendDescription(DiscordChannel.BattleDungeon.toString());
		eb.setColor(Constants.COLOR);
		
		if (!teams.isEmpty()) {
			long timer = 10_000;
			TreeSet<String> names = new TreeSet<String>();
			for (BattleTeam bt : teams) {
				names.add(bt.getPlayer().getName());
			}
			
			for (int q = 0; q < d.getFloorCount(); q++) {
				Dinosaur[] boss = d.getDinosaursOnFloor(q);
				Battle b = new Battle(BattleChannel.Dungeon, BattleType.Boss, d.getLocation());
				b.addBoss(new BattleTeam(CustomPlayer.Dungeon.getPlayer(), boss));
				b.setDelayTime(timer);
				
				Battle.markPlayerBattling(CustomPlayer.Dungeon.getIdLong(), true);
				for (BattleTeam bt : teams) {
					bt.heal();
					b.addTeam(bt);
					Battle.markPlayerBattling(bt.getPlayer().getIdLong(), true);
				}
				
				Action.sendDelayedMessage(MesozoicIsland.getAssistant().getIdLong(), timer, BattleChannel.Dungeon.getBattleChannel(), String.format("The dungeon exploration team has reached Floor " + (q + 1) + " of the dungeon."));
				timer = b.start(q + 1);
				if (b.didBossWin()) break;
				else if (q == d.getFloorCount() - 1) {
					Item token = Item.getItem(new Pair<Integer, Long>(101, 0L));
					String winmsg = "";
					if (teams.size() == 1) winmsg = String.format("The player has defeated **all floors** of the dungeon! A crate of %,d %s were left as the dungeon disappeared.", d.getTokenCount(), token.toString(d.getTokenCount()));
					else winmsg = String.format("The players have defeated **all floors** of the dungeon! A crate of %,d %s were left as the dungeon disappeared. The players each get %,d %s.", d.getTokenCount() * teams.size(), token.toString(d.getTokenCount() * teams.size()), d.getTokenCount(), token.toString(d.getTokenCount()));
					
					Action.sendDelayedMessage(MesozoicIsland.getAssistant().getIdLong(), timer, BattleChannel.Dungeon.getBattleChannel(), winmsg);
					Action.sendDelayedMessage(MesozoicIsland.getAssistant().getIdLong(), timer, Constants.SPAWN_CHANNEL, winmsg);
					
					for (BattleTeam bt : teams) {
						Action.addItemDelayed(bt.getPlayer().getIdLong(), timer, Stats.of(Stats.DUNGEONS_CLEARED), 1);
						Action.addItemDelayed(bt.getPlayer().getIdLong(), timer, token.getIdDmg(), d.getTokenCount());
					}
				}
			}
			
			Action.removePlayerFromBattleDelayed(CustomPlayer.Dungeon.getIdLong(), timer);
			for (BattleTeam bt : teams) {
				Action.removePlayerFromBattleDelayed(bt.getPlayer().getIdLong(), timer);
			}
			
			Action.logBattleChannelDelayed(MesozoicIsland.getAssistant().getIdLong(), BattleChannel.Dungeon.getBattleChannel().getIdLong(), timer + 30_000);
			eb.addField("Joined", Util.join(names.toArray(new String[0]), ", ", 0, names.size()), true);
		} else {
			eb.addField("Joined", "None", true);
		}
		
		Util.complete(Constants.SPAWN_CHANNEL.getChannel(MesozoicIsland.getAssistant()).sendMessage(eb.build()));
		if (players == 0) autospawn = false;
		setSpawnTime();
		waiting = false;
	}
	
	public static void spawnPVP() {
		waiting = true;
		
		waiting = false;
	}
	
	public static void spawnCustom(long player, Dinosaur[] dinos) {
		waiting = true;
		
		waiting = false;
	}
}