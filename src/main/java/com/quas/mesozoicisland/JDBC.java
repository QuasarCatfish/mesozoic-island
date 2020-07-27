package com.quas.mesozoicisland;

import java.awt.Color;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashSet;
import java.util.Scanner;
import java.util.TreeSet;

import com.quas.mesozoicisland.enums.ActionType;
import com.quas.mesozoicisland.enums.CustomPlayer;
import com.quas.mesozoicisland.enums.DinosaurForm;
import com.quas.mesozoicisland.enums.NewPlayerStatus;
import com.quas.mesozoicisland.objects.Dinosaur;
import com.quas.mesozoicisland.objects.Egg;
import com.quas.mesozoicisland.objects.Item;
import com.quas.mesozoicisland.objects.Player;
import com.quas.mesozoicisland.objects.Rune;
import com.quas.mesozoicisland.util.Constants;
import com.quas.mesozoicisland.util.MesozoicCalendar;
import com.quas.mesozoicisland.util.MesozoicDate;
import com.quas.mesozoicisland.util.MesozoicRandom;
import com.quas.mesozoicisland.util.Pair;
import com.quas.mesozoicisland.util.Stats;
import com.quas.mesozoicisland.util.Util;

import net.dv8tion.jda.api.entities.MessageChannel;

public class JDBC {

	private static JDBC jdbc = null;
	private Connection connection = null;
	private Statement update = null;
	
	private JDBC() {
		try {
			connection = DriverManager.getConnection("jdbc:mysql://jsk3f4rbvp8ayd7w.cbetxkdyhwsb.us-east-1.rds.amazonaws.com:3306/", "zegb93elad8rfnl0", "lq0oocxozsn8egj6");
			Statement s = connection.createStatement();
			s.execute("use nskkqzax7tfjfm66;");
			s.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	private synchronized Statement getUpdateStatement() {
		if (update == null) try {
			update = connection.createStatement();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return update;
	}
	
	private synchronized Statement getStatement() {
		try {
			Statement s = connection.createStatement();
			s.closeOnCompletion();
			return s;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	////////////////////////////////////////////
	
	private static synchronized JDBC getInstance() {
		if (jdbc == null) jdbc = new JDBC();
		return jdbc;
	}
	
	public static synchronized ResultSet executeQuery(String sql, Object...args) throws SQLException {
		return executeQuery(String.format(sql, args));
	}
	
	public static synchronized ResultSet executeQuery(String sql) throws SQLException {
		return getInstance().getStatement().executeQuery(sql);
	}
	
	public static synchronized boolean executeUpdate(String format, Object... args) {
		return executeUpdate(String.format(format, args));
	}
	
	public static synchronized boolean executeUpdate(String sql) {
		System.out.println("Executing Update: " + sql.replace("\r\n", "\\n"));
		try {
			getInstance().getUpdateStatement().executeUpdate(sql);
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	public static synchronized boolean ping() {
		return executeUpdate("update vars set value = value + 1 where var = 'pings';");
	}
	
	public static synchronized NewPlayerStatus addPlayer(long playerid) {
		try (ResultSet res = executeQuery("select * from players where playerid = %d;", playerid)) {
			if (res.next()) return NewPlayerStatus.Returning;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		MesozoicCalendar gc = new MesozoicCalendar();
		boolean b = executeUpdate("insert into players(playerid, playername, joindate) values(%d, 'New Player', '%tm/%td/%tY');", playerid, gc, gc, gc);
		if (!b) return NewPlayerStatus.Error;
		
		try (ResultSet res = JDBC.executeQuery("select * from mesozoicisland2.players where playerid = %d;", playerid)) {
			if (res.next()) return NewPlayerStatus.NewToVersion;
			else return NewPlayerStatus.New;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return NewPlayerStatus.Error;
	}
	
	public static synchronized boolean addItem(long playerid, Pair<Integer, Long> item) {
		return addItem(playerid, item, 1);
	}
	
	public static synchronized boolean addItem(long playerid, Pair<Integer, Long> item, long count) {
		if (item.getFirstValue() == 100 && item.getSecondValue() == 0L) {
			if (count > 0) addItem(playerid, Stats.of(Stats.COINS_OBTAINED), count);
			if (count < 0) addItem(playerid, Stats.of(Stats.COINS_SPENT), -count);
		}
		
		try (ResultSet res = executeQuery("select * from bags where player = %d and item = %d and dmg = %d;", playerid, item.getFirstValue(), item.getSecondValue())) {
			if (res.next()) {
				return executeUpdate("update bags set count = count + %d where player = %d and item = %d and dmg = %d;", count, playerid, item.getFirstValue(), item.getSecondValue());
			} else {
				return executeUpdate("insert into bags(player, item, dmg, count) values(%d, %d, %d, %d);", playerid, item.getFirstValue(), item.getSecondValue(), count);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return false;
	}
	
	public static synchronized boolean addMail(long playerid, String name, String from, String message, String reward) {
		message = message == null ? "null" : "'" + Util.cleanQuotes(message) + "'";
		reward = reward == null ? "null" : "'" + Util.cleanQuotes(reward) + "'";
		return executeUpdate("insert into mail(player, mail.name, mail.from, message, reward) values(%d, '%s', '%s', %s, %s);", playerid, Util.cleanQuotes(name), Util.cleanQuotes(from), message, reward);
	}
	
	public static synchronized boolean setName(long pid, String name) {
		return executeUpdate("update players set playername = '%s' where playerid = %d;", Util.cleanQuotes(name), pid);
	}
	
	public static synchronized boolean setDaily(long pid) {
		return executeUpdate("update players set daily = %d where playerid = %d;", System.currentTimeMillis(), pid);
	}
	
	public static synchronized boolean setDailyStreak(long pid, int streak) {
		return executeUpdate("update players set dailystreak = %d where playerid = %d;", streak, pid);
	}
	
	public static synchronized boolean setBirthday(long pid, int birthday) {
		return executeUpdate("update players set birthday = %d where playerid = %d;", birthday, pid);
	}
	
	public static synchronized boolean setColor(long pid, Color color) {
		return executeUpdate("update players set color = %d where playerid = %d;", color.getRGB(), pid);
	}
	
	public static synchronized boolean setStarter(long pid, Dinosaur starter) {
		return executeUpdate("update players set starter = '%s' where playerid = %d;", starter.getId(), pid);
	}
	
	public static synchronized boolean setLatest(long pid, Dinosaur latest) {
		return executeUpdate("update players set latest = '%s' where playerid = %d;", latest.getId(), pid);
	}
	
	public static synchronized boolean setState(long pid, String state) {
		return executeUpdate("update players set gamestate = '%s' where playerid = %d;", Util.cleanQuotes(state), pid);
	}
	
	public static synchronized boolean setTitle(long pid, String title, boolean invert) {
		if (title == null) return executeUpdate("update players set title = null, invertedtitle = false where playerid = %d;", pid);
		return executeUpdate("update players set title = '%s', invertedtitle = %b where playerid = %d;", Util.cleanQuotes(title), invert, pid);
	}
	
	public static synchronized boolean setMuted(long pid, boolean muted) {
		return executeUpdate("update players set muted = %b where playerid = %d;", muted, pid);
	}
	
	public static synchronized boolean addAction(ActionType action, long from, long recipient, String msg, long time) {
		msg = msg.replaceAll("\\?\'", "\\\'");
		return executeUpdate("insert into actions(actiontype, bot, recipient, msg, time) values(%d, %d, %d, '%s', %d);", action.getActionType(), from, recipient, msg, time);
	}
	
	public static synchronized boolean deleteAction(int actionid) {
		return executeUpdate("delete from actions where actionid = " + actionid + ";");
	}
	
	public static synchronized boolean setNickname(long pid, int dex, int form, String nick) {
		if (nick == null) {
			return executeUpdate("update captures set nick = null where player = " + pid + " and dex = " + dex + " and form = " + form + ";");
		} else {
			return executeUpdate("update captures set nick = '" + Util.cleanQuotes(nick) + "' where player = " + pid + " and dex = " + dex + " and form = " + form + ";");
		}
	}
	
	public static synchronized boolean setItem(long pid, Pair<Integer, Integer> dexform, Pair<Integer, Long> item) {
		return executeUpdate("update captures set item = %d and itemdmg = %d where player = %d and dex = %d and item = %d;", item.getFirstValue(), item.getSecondValue(), pid, dexform.getFirstValue(), dexform.getSecondValue());
	}
	
	public static synchronized boolean setSelected(long pid, Dinosaur[] dinos) {
		String[] dinostr = new String[dinos.length];
		for (int q = 0; q < dinos.length; q++) dinostr[q] = dinos[q].getId();
		return executeUpdate("update players set selected = '%s' where playerid = %d;", Util.cleanQuotes(Util.join(dinostr, " ", 0, dinostr.length)), pid);
	}
	
	public static String getRedeemMessage(String redeem) {
		StringBuilder ret = new StringBuilder();
		Scanner in = new Scanner(redeem.toLowerCase());
		
		while (in.hasNext()) {
			switch (in.next()) {
			case "i": case "item":
				int itemid = in.nextInt();
				long itemdmg = in.nextLong();
				long count = in.hasNextLong() ? in.nextLong() : 1L;
				ret.append(String.format("� %,d %s%n", count, Item.getItem(new Pair<Integer, Long>(itemid, itemdmg)).toString(count)));
				break;
			case "d": case "dino": case "dinosaur":
				int dinoid = in.nextInt();
				int dinoform = in.nextInt();
				int dinorp = in.hasNextInt() ? in.nextInt() : 1;
				ret.append(String.format("� %,d %s Crystal%s%n", dinorp, Dinosaur.getDinosaur(new Pair<Integer, Integer>(dinoid, dinoform)).getDinosaurName(), dinorp == 1 || dinorp == -1 ? "" : "s"));
				break;
			case "r": case "rune":
				int runeid = in.nextInt();
				int runerp = in.hasNextInt() ? in.nextInt() : 1;
				ret.append(String.format("� %,d %s Rune%s%n", runerp, Rune.getRune(runeid).getName(), runerp == 1 || runerp == -1 ? "" : "s"));
				break;
			case "e": case "egg":
				int eggdex = in.nextInt();
				int eggform = in.nextInt();
				Egg egg = Egg.getRandomEgg(new Pair<Integer, Integer>(eggdex, eggform));
				ret.append(String.format("� %s %s%n", Util.getArticle(egg.getEggName()), egg.getEggName()));
				break;
			}
		}
		
		in.close();
		return ret.toString();
	}
	
	public static synchronized void redeem(MessageChannel channel, long pid, String redeem) {
		if (redeem == null) return;
		
		Scanner in = new Scanner(redeem.toLowerCase());
		while (in.hasNext()) {
			switch (in.next()) {
			case "i": case "item":
				int itemid = in.nextInt();
				long itemdmg = in.nextLong();
				long count = in.hasNextLong() ? in.nextLong() : 1L;
				addItem(pid, new Pair<Integer, Long>(itemid, itemdmg), count);
				break;
			case "d": case "dino": case "dinosaur":
				int dinoid = in.nextInt();
				int dinoform = in.nextInt();
				int dinorp = in.hasNextInt() ? in.nextInt() : 1;
				addDinosaur(channel, pid, new Pair<Integer, Integer>(dinoid, dinoform), dinorp);
				break;
			case "r": case "rune":
				int runeid = in.nextInt();
				int runerp = in.hasNextInt() ? in.nextInt() : 1;
				addRune(channel, pid, runeid, runerp);
				break;
			case "e": case "egg":
				int eggdex = in.nextInt();
				int eggform = in.nextInt();
				Egg egg = Egg.getRandomEgg(new Pair<Integer, Integer>(eggdex, eggform));
				addEgg(pid, egg);
				break;
			}
		}
		
		in.close();
	}
	
	public static synchronized int getPlayerCount() {
		try (ResultSet res = executeQuery("select count(*) as count from players where playerid > %d;", CustomPlayer.getUpperLimit())) {
			if (res.next()) {
				return res.getInt("count");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return 0;
	}
	
	public static synchronized int getActivePlayerCount() {
		try (ResultSet res = executeQuery("select count(*) as count from players where playerid > %d and daily >= %d;", CustomPlayer.getUpperLimit(), System.currentTimeMillis() - Constants.ACTIVE_PLAYER_TIMER)) {
			if (res.next()) {
				return res.getInt("count");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return 0;
	}
	
	public static synchronized int getDexCount(int form) {
		if (form == DinosaurForm.AllForms.getId()) {
			try (ResultSet res = JDBC.executeQuery("select count(*) as count from dinosaurs where dex > 0 and form >= 0 and form != %d and rarity >= 0;", DinosaurForm.Prismatic.getId())) {
				if (res.next()) return res.getInt("count");
			} catch (SQLException e) {
				e.printStackTrace();
			}
		} else {
			try (ResultSet res = JDBC.executeQuery("select count(*) as count from dinosaurs where dex > 0 and form = " + form + " and rarity >= 0;")) {
				if (res.next()) return res.getInt("count");
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
		return 0;
	}
	
	public static synchronized int getRuneCount() {
		try (ResultSet res = JDBC.executeQuery("select count(*) as count from runes where runeid > 0;")) {
			if (res.next()) return res.getInt("count");
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return 0;
	}
	
	public static synchronized boolean addEgg(long pid, Egg egg) {
		HashSet<Integer> used = new HashSet<Integer>();
		try (ResultSet res = JDBC.executeQuery("select * from eggs where player = %d;", pid)) {
			while (res.next()) {
				used.add(res.getInt("incubator"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		int slot = -1;
		for (int q = 1;; q++) {
			if (used.contains(q)) continue;
			slot = q;
			break;
		}
		
		JDBC.addItem(pid, Stats.of(Stats.EGGS_RECEIVED));
		
		if (egg.hasCustomName()) {
			return executeUpdate("insert into eggs(dex, form, player, incubator, original, maxhp, color, patterncolor, pattern, eggname) values(%d, %d, %d, %d, %d, %d, %d, %d, %d, '%s');",
					egg.getDex(), egg.getForm(), pid, slot, pid, egg.getMaxHatchPoints(), egg.getEggColor().getId(), egg.getPatternColor().getId(), egg.getPattern().getId(), egg.getEggName());
		} else {
			return executeUpdate("insert into eggs(dex, form, player, incubator, original, maxhp, color, patterncolor, pattern) values(%d, %d, %d, %d, %d, %d, %d, %d, %d);",
					egg.getDex(), egg.getForm(), pid, slot, pid, egg.getMaxHatchPoints(), egg.getEggColor().getId(), egg.getPatternColor().getId(), egg.getPattern().getId());
		}
	}
	
	public static synchronized boolean updateEggs() {
		return executeUpdate("update eggs set hp = hp + floor(rand() * %d) + %d where player > %d;", Constants.MAX_HP_PER_MINUTE - Constants.MIN_HP_PER_MINUTE + 1, Constants.MIN_HP_PER_MINUTE, CustomPlayer.getUpperLimit());
	}
	
	public static synchronized boolean addDinosaur(MessageChannel channel, long pid, Pair<Integer, Integer> dino) {
		return addDinosaur(channel, pid, dino, 1);
	}
	
	public static synchronized boolean addDinosaur(MessageChannel channel, long pid, Pair<Integer, Integer> dino, int rp) {
		Dinosaur d = Dinosaur.getDinosaur(pid, dino);
		if (d == null) {
			boolean b = executeUpdate("insert into captures(player, dex, form) values(%d, %d, %d);", pid, dino.getFirstValue(), dino.getSecondValue());
			JDBC.addItem(pid, Stats.of(Stats.DINOSAURS_CAUGHT));
			if (rp > 1) return b && addDinosaur(channel, pid, dino, rp - 1);
			setLatest(pid, Dinosaur.getDinosaur(dino));
			return b;
		} else {
			boolean b = executeUpdate("update captures set rp = rp + %d where player = %d and dex = %d and form = %d;", rp, pid, dino.getFirstValue(), dino.getSecondValue());
			JDBC.addItem(pid, Stats.of(Stats.DINOSAURS_CAUGHT), rp);
			if (!d.canRankup() && Dinosaur.getDinosaur(pid, dino).canRankup()) {
				channel.sendMessageFormat("%s, your %s can now rankup to **Rank %s**. Use `rankup %s` to rankup this dinosaur.", d.getPlayer().getAsMention(), d.getEffectiveName(), d.getNextRankString(), d.getId()).complete();
			}
			setLatest(pid, Dinosaur.getDinosaur(dino));
			return b;
		}
	}
	
	public static synchronized boolean addXp(MessageChannel channel, long pid, Pair<Integer, Integer> dino, long xp) {
		Dinosaur d = Dinosaur.getDinosaur(pid, dino);
		if (d == null) return false;
		boolean b = executeUpdate("update captures set xp = %d where player = %d and dex = %d and form = %d;", Math.min(d.getXp() + xp, Constants.MAX_XP), pid, dino.getFirstValue(), dino.getSecondValue());
		Dinosaur d2 = Dinosaur.getDinosaur(pid, dino);
		if (d.getLevel() < d2.getLevel()) {
			JDBC.addItem(pid, Stats.of(Stats.DINOSAURS_LEVELED_UP), d2.getLevel() - d.getLevel());
			channel.sendMessageFormat("%s, your %s is now **Level %,d**.", d.getPlayer().getAsMention(), d.getEffectiveName(), d2.getLevel()).complete();
		}
		updatePlayerXp(pid);
		return b;
	}
	
	public static synchronized boolean updatePlayerXp(long playerid) {
		Player p = Player.getPlayer(playerid);
		boolean b = executeUpdate("update players set xp = %d where playerid = %d;", calculatePlayerXp(playerid), playerid);
		Player p2 = Player.getPlayer(playerid);
		if (p2.getLevel() == 2) return b;
		
		for (int level = p.getLevel() + 1; level <= p2.getLevel(); level++) {
			Constants.SPAWN_CHANNEL.getChannel(MesozoicIsland.getAssistant()).sendMessageFormat("%s, you have leveled up to **Level %,d**! Check your mailbox for your reward.", p.getAsMention(), level).complete();
			Constants.addLevelUpMail(p, level);
		}
		return b;
	}
	
	private static synchronized long calculatePlayerXp(long playerid) {
		try (ResultSet res = executeQuery("select sum(xp) as xp from captures where player = %d;", playerid)) {
			if (res.next()) {
				return res.getLong("xp");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return 0L;
	}
	
	public static synchronized boolean addRune(MessageChannel channel, long pid, int rune) {
		return addRune(channel, pid, rune, 1);
	}
	
	public static synchronized boolean addRune(MessageChannel channel, long pid, int rune, int rp) {
		Rune r = Rune.getRune(pid, rune);
		if (r == null) {
			boolean b = executeUpdate("insert into runepouches(player, rune) values(%d, %d);", pid, rune);
			if (rp > 1) return b && addRune(channel, pid, rune, rp - 1);
			return b;
		} else {
			boolean b = executeUpdate("update runepouches set rp = rp + %d where player = %d and rune = %d;", rp, pid, rune);
			if (!r.canRankup() && Rune.getRune(pid, rune).canRankup()) {
				channel.sendMessageFormat("%s, your %s can now rankup to **Rank %s**. Use `rankup R%d` to rankup this rune.", r.getPlayer().getAsMention(), r.getName(), r.getNextRankString(), r.getId()).complete();
			}
			return b;
		}
	}
	
	public static synchronized boolean addWin(long pid, Pair<Integer, Integer> dino) {
		return executeUpdate("update captures set wins = wins + 1 where player = %d and dex = %d and form = %d;", pid, dino.getFirstValue(), dino.getSecondValue());
	}
	
	public static synchronized boolean addLoss(long pid, Pair<Integer, Integer> dino) {
		return executeUpdate("update captures set losses = losses + 1 where player = %d and dex = %d and form = %d;", pid, dino.getFirstValue(), dino.getSecondValue());
	}
	
	public static synchronized boolean rankup(long pid, Pair<Integer, Integer> dino) {
		return executeUpdate("update captures set rnk = rnk + 1, rp = rp - rnk where player = %d and dex = %d and form = %d;", pid, dino.getFirstValue(), dino.getSecondValue());
	}
	
	public static synchronized boolean rankup(long pid, int rune) {
		return executeUpdate("update runepouches set rnk = rnk + 1, rp = rp - rnk where player = %d and rune = %d", pid, rune);
	}
	
	public static synchronized boolean equipRune(long pid, Pair<Integer, Integer> dino, int rune) {
		boolean a = executeUpdate("update captures set rune = %d where player = %d and dex = %d and form = %d;", rune, pid, dino.getFirstValue(), dino.getSecondValue());
		boolean b = executeUpdate("update runepouches set equipped = '%s' where player = %d and rune = %d;", Dinosaur.getDinosaur(dino).getId(), pid, rune);
		return a && b;
	}
	
	public static synchronized boolean unequipRune(long pid, Pair<Integer, Integer> dino, int rune) {
		boolean a = executeUpdate("update captures set rune = 0 where player = %d and dex = %d and form = %d;", pid, dino.getFirstValue(), dino.getSecondValue());
		boolean b = executeUpdate("update runepouches set equipped = null where player = %d and rune = %d;", pid, rune);
		return a && b;
	}
	
	public static synchronized boolean hasDungeonTickets() {
		try (ResultSet res = JDBC.executeQuery("select * from dungeontickets where date = '%s';", MesozoicDate.getToday())) {
			return res.next();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	public static synchronized boolean generateDungeonTickets() {
		if (hasDungeonTickets()) return false;
		TreeSet<String> set = new TreeSet<String>();
		while (set.size() < 3) set.add(Dinosaur.getDinosaur(MesozoicRandom.nextDinosaur().getDex(), DinosaurForm.Dungeon.getId()).getId());
		String tier1 = Util.join(set, " ", 0, set.size());
		while (set.size() < 7) set.add(Dinosaur.getDinosaur(MesozoicRandom.nextDinosaur().getDex(), DinosaurForm.Dungeon.getId()).getId());
		String tier2 = Util.join(set, " ", 0, set.size());
		return executeUpdate("insert into dungeontickets(date, dino1, dino2) values('%s', '%s', '%s');", MesozoicDate.getToday(), tier1, tier2);
	}
	
	public static synchronized String getDungeonTickets(int tier) {
		try (ResultSet res = JDBC.executeQuery("select * from dungeontickets where date = '%s';", MesozoicDate.getToday())) {
			if (res.next()) {
				return res.getString("dino" + tier);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return "";
	}
	
	public static synchronized String getVariable(String name) {
		try (ResultSet res = JDBC.executeQuery("select * from vars where var = '%s';", Util.cleanQuotes(name))) {
			if (res.next()) return res.getString("value");
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	public static synchronized String getReward(String name) {
		try (ResultSet res = JDBC.executeQuery("select * from rewards where rewardname = '%s';", Util.cleanQuotes(name))) {
			if (res.next()) return res.getString("reward");
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return null;
	}
}
