package com.quas.mesozoicisland.cmdplayer;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.TreeSet;

import com.quas.mesozoicisland.JDBC;
import com.quas.mesozoicisland.cmdbase.ICommand;
import com.quas.mesozoicisland.enums.AccessLevel;
import com.quas.mesozoicisland.enums.CustomPlayer;
import com.quas.mesozoicisland.enums.DinosaurForm;
import com.quas.mesozoicisland.enums.DiscordChannel;
import com.quas.mesozoicisland.enums.DiscordRole;
import com.quas.mesozoicisland.enums.ItemID;
import com.quas.mesozoicisland.enums.Stat;
import com.quas.mesozoicisland.objects.Dinosaur;
import com.quas.mesozoicisland.objects.Item;
import com.quas.mesozoicisland.objects.Leaderboard;
import com.quas.mesozoicisland.objects.Player;
import com.quas.mesozoicisland.util.Constants;
import com.quas.mesozoicisland.util.Pair;
import com.quas.mesozoicisland.util.Util;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public abstract class LeaderboardCommand implements ICommand {

	private boolean displayAll;
	protected LeaderboardCommand(boolean displayAll) {
		this.displayAll = displayAll;
	}

	@Override
	public AccessLevel getAccessLevel() {
		return AccessLevel.Trainer;
	}

	@Override
	public String getCommandName() {
		return "top";
	}

	@Override
	public DiscordChannel[] getUsableChannels() {
		return DiscordChannel.STANDARD_CHANNELS_DMS;
	}

	@Override
	public DiscordRole[] getRequiredRoles() {
		return null;
	}

	@Override
	public String getRequiredState() {
		return null;
	}

	@Override
	public synchronized void run(MessageReceivedEvent event, String... args) {
		if (args.length == 0) args = Util.arr("dex");
		LeaderboardCategory lbc = LeaderboardCategory.of(Util.join(args, " ", 0, args.length));
		if (lbc == null) {
			event.getChannel().sendMessageFormat("%s, `%s` is an invalid leaderboard.", event.getAuthor().getAsMention(), Util.join(args, " ", 0, args.length)).complete();
			return;
		}
		
		Leaderboard lb = new Leaderboard(lbc.getLeaderboardType().getRegex());
		lb.setUnlimited(displayAll);
		TreeSet<Long> players = new TreeSet<Long>();

		switch (lbc.getLeaderboardType()) {
		case DinosaurLevel:
			try (ResultSet res = JDBC.executeQuery("select * from captures where %s order by xp desc limit %d;", lbc.getWhereClause(), Constants.MAX_LEADERBOARD_CHECK)) {
				while (res.next()) {
					if (res.getLong("player") < CustomPlayer.getUpperLimit()) continue;
					if (players.contains(res.getLong("player"))) continue;
					players.add(res.getLong("player"));
					Dinosaur d = Dinosaur.getDinosaur(res.getLong("player"), new Pair<Integer, Integer>(res.getInt("dex"), res.getInt("form")));
					lb.addEntry(d.getXp() + 1, d.getPlayer().getName(), d.getEffectiveName(), d.getLevel(), d.getXpMinusLevel());
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
			break;
		case DinosaurRank:
			try (ResultSet res = JDBC.executeQuery("select *, (1000000000000 * rnk + rp) as effrp from captures where %s order by effrp desc limit %d;", lbc.getWhereClause(), Constants.MAX_LEADERBOARD_CHECK)) {
				while (res.next()) {
					if (res.getLong("player") < CustomPlayer.getUpperLimit()) continue;
					if (players.contains(res.getLong("player"))) continue;
					players.add(res.getLong("player"));
					Dinosaur d = Dinosaur.getDinosaur(res.getLong("player"), new Pair<Integer, Integer>(res.getInt("dex"), res.getInt("form")));
					lb.addEntry(res.getLong("effrp"), d.getPlayer().getName(), d.getEffectiveName(), d.getRankString(), d.getRp());
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
			break;
		case DinosaurStat:
			switch (Long.toString(lbc.getItems()[0])) {
			case STAT_WINS:
				try (ResultSet res = JDBC.executeQuery("select * from captures where wins + losses >= %d order by wins desc limit %d;", Constants.LEADERBOARD_REQUIRED_BATTLES, Constants.MAX_LEADERBOARD_CHECK)) {
					while (res.next()) {
						if (res.getLong("player") < CustomPlayer.getUpperLimit()) continue;
						if (players.contains(res.getLong("player"))) continue;
						players.add(res.getLong("player"));
						Dinosaur d = Dinosaur.getDinosaur(res.getLong("player"), new Pair<Integer, Integer>(res.getInt("dex"), res.getInt("form")));
						lb.addEntry(d.getWins(), d.getPlayer().getName(), d.getEffectiveName(), d.getWins(), d.getWins() == 1 ? "Dinosaur Defeated" : "Dinosaurs Defeated");
					}
				} catch (SQLException e) {
					e.printStackTrace();
				}
				break;
				
			}
			break;
		case DinosaurTwoStat:
			switch (Long.toString(lbc.getItems()[0])) {
			case TWOSTAT_WTL:
				try (ResultSet res = JDBC.executeQuery("select *, (case when losses = 0 then 999999999 else wins / losses end) as wtl from captures where wins + losses >= %d order by wtl desc, wins desc limit %d;", Constants.LEADERBOARD_REQUIRED_BATTLES, Constants.MAX_LEADERBOARD_CHECK)) {
					while (res.next()) {
						if (res.getLong("player") < CustomPlayer.getUpperLimit()) continue;
						if (players.contains(res.getLong("player"))) continue;
						players.add(res.getLong("player"));
						Dinosaur d = Dinosaur.getDinosaur(res.getLong("player"), new Pair<Integer, Integer>(res.getInt("dex"), res.getInt("form")));
						lb.addEntry((long)(1_000_000L * res.getDouble("wtl")), d.getPlayer().getName(), d.getEffectiveName(), d.getWins(), d.getWins() == 1 ? "Win" : "Wins", d.getLosses(), d.getLosses() == 1 ? "Loss" : "Losses");
					}
				} catch (SQLException e) {
					e.printStackTrace();
				}
				break;
				
				
			}
			
			break;
		case PlayerDexCount:
			DinosaurForm form = DinosaurForm.of((int)lbc.getItems()[0]);
			int total = JDBC.getDexCount(form.getId());
			for (Player p : Player.values()) {
				if (p.getIdLong() < CustomPlayer.getUpperLimit()) continue;
				int dex = p.getDexCount(form.getId());
				lb.addEntry(dex, p.getName(), dex, total);
			}
			break;
		case PlayerRuneCount:
			int rtotal = JDBC.getRuneCount();
			for (Player p : Player.values()) {
				if (p.getIdLong() < CustomPlayer.getUpperLimit()) continue;
				int runes = p.getRuneCount();
				lb.addEntry(runes, p.getName(), runes, rtotal);
			}
			break;
		case PlayerItem:
			try (ResultSet res = JDBC.executeQuery("select * from bags where item = %d and dmg = %d order by count desc limit %d;", lbc.getItems()[0], lbc.getItems()[1], Constants.MAX_LEADERBOARD_CHECK)) {
				Item item = Item.getItem(new Pair<Integer, Long>((int)lbc.getItems()[0], (long)lbc.getItems()[1]));
				while (res.next()) {
					Player p = Player.getPlayer(res.getLong("player"));
					if (p.getIdLong() < CustomPlayer.getUpperLimit()) continue;
					lb.addEntry(res.getLong("count"), p.getName(), res.getLong("count"), item.toString(res.getLong("count")));
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
			break;
		case PlayerStat:
			break;
		case PlayerLevel:
			try (ResultSet res = JDBC.executeQuery("select * from players order by xp desc limit %d;", Constants.MAX_LEADERBOARD_CHECK)) {
				while (res.next()) {
					Player p = Player.getPlayer(res.getLong("playerid"));
					if (p.getIdLong() < CustomPlayer.getUpperLimit()) continue;

					if (p.getOmegaXp() > 0) {
						lb.addEntry(Constants.MAX_PLAYER_XP + res.getLong("omegaxp"), p.getName(), Constants.OMEGA + " ", p.getOmegaLevel(), p.getOmegaXpMinusLevel(), Constants.OMEGA);
					} else {
						lb.addEntry(res.getLong("xp"), p.getName(), "", p.getLevel(), p.getXpMinusLevel(), "");
					}
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
			break;
		}
		
		ArrayList<String> print = new ArrayList<String>();
		print.add("**" + lbc.getName() + " Leaderboard:**");
		print.addAll(lb.getLeaderboard());
		
		for (String msg : Util.bulkify(print)) {
			event.getChannel().sendMessage(msg).complete();
		}
	}
	
	protected enum LeaderboardCategory {
		// Dex
		Dex(Util.arr("dex"), "Dex Completion", LeaderboardType.PlayerDexCount, null, DinosaurForm.AnyForms.getId()),
		DexAll(Util.arr("dex all"), "Dex Completion (All)", LeaderboardType.PlayerDexCount, null, DinosaurForm.AllForms.getId()),
		DexStandard(Util.arr("dex standard", "dex std"), "Dex Completion (Standard)", LeaderboardType.PlayerDexCount, null, DinosaurForm.Standard.getId()),
		DexPrismatic(Util.arr("dex prismatic", "dex prism", "dex pris"), "Dex Completion (Prismatic)", LeaderboardType.PlayerDexCount, null, DinosaurForm.Prismatic.getId()),
		DexDungeon(Util.arr("dex dungeon"), "Dex Completion (Dungeon)", LeaderboardType.PlayerDexCount, null, DinosaurForm.Dungeon.getId()),
		DexHalloween(Util.arr("dex halloween"), "Dex Completion (Halloween)", LeaderboardType.PlayerDexCount, null, DinosaurForm.Halloween.getId()),
		DexThanksgiving(Util.arr("dex thanksgiving"), "Dex Completion (Thanksgiving)", LeaderboardType.PlayerDexCount, null, DinosaurForm.Thanksgiving.getId()),
		DexChaos(Util.arr("dex chaos"), "Dex Completion (Chaos)", LeaderboardType.PlayerDexCount, null, DinosaurForm.Chaos.getId()),
		DexMechanical(Util.arr("dex mechanical"), "Dex Completion (Mechanical)", LeaderboardType.PlayerDexCount, null, DinosaurForm.Mechanical.getId()),
		DexStatue(Util.arr("dex statue"), "Dex Completion (Statue)", LeaderboardType.PlayerDexCount, null, DinosaurForm.Statue.getId()),

		// Runes
//		Runes(Util.arr("runes", "rune"), "Rune Count", LeaderboardType.PlayerRuneCount, null),
		
		// Level
		Level(Util.arr("level", "lvl", "lv"), "Dinosaur Level", LeaderboardType.DinosaurLevel, null),
		LevelStandard(Util.arr("level standard", "level std", "lvl standard", "lvl std", "lv standard", "lv std"), "Dinosaur Level (Standard)", LeaderboardType.DinosaurLevel, "form = " + DinosaurForm.Standard.getId()),
		LevelPrismatic(Util.arr("level prismatic", "lvl prismatic", "lv prismatic", "level prism", "lvl prism", "lv prism", "level pris", "lvl pris", "lv pris"), "Dinosaur Level (Prismatic)", LeaderboardType.DinosaurLevel, "form = " + DinosaurForm.Prismatic.getId()),
		LevelDungeon(Util.arr("level dungeon", "lvl dungeon", "lv dungeon"), "Dinosaur Level (Dungeon)", LeaderboardType.DinosaurLevel, "form = " + DinosaurForm.Dungeon.getId()),
		LevelHalloween(Util.arr("level halloween", "lvl halloween", "lv halloween"), "Dinosaur Level (Halloween)", LeaderboardType.DinosaurLevel, "form = " + DinosaurForm.Halloween.getId()),
		LevelThanksgiving(Util.arr("level thanksgiving", "lvl thanksgiving", "lv thanksgiving"), "Dinosaur Level (Thanksgiving)", LeaderboardType.DinosaurLevel, "form = " + DinosaurForm.Thanksgiving.getId()),
		LevelChaos(Util.arr("level chaos", "lvl chaos", "lv chaos"), "Dinosaur Level (Chaos)", LeaderboardType.DinosaurLevel, "form = " + DinosaurForm.Chaos.getId()),
		LevelMechanical(Util.arr("level mechanical", "lvl mechanical", "lv mechanical"), "Dinosaur Level (Mechanical)", LeaderboardType.DinosaurLevel, "form = " + DinosaurForm.Mechanical.getId()),
		LevelStatue(Util.arr("level statue", "lvl statue", "lv statue"), "Dinosaur Level (Statue)", LeaderboardType.DinosaurLevel, "form = " + DinosaurForm.Statue.getId()),
		
		// Rank
		Rank(Util.arr("rank"), "Dinosaur Rank", LeaderboardType.DinosaurRank, null),
		RankStandard(Util.arr("rank standard", "rank std"), "Dinosaur Rank (Standard)", LeaderboardType.DinosaurRank, "form = " + DinosaurForm.Standard.getId()),
		RankPrismatic(Util.arr("rank prismatic", "rank prism", "rank pris"), "Dinosaur Rank (Prismatic)", LeaderboardType.DinosaurRank, "form = " + DinosaurForm.Prismatic.getId()),
		RankDungeon(Util.arr("rank dungeon"), "Dinosaur Rank (Dungeon)", LeaderboardType.DinosaurRank, "form = " + DinosaurForm.Dungeon.getId()),
		RankHalloween(Util.arr("rank halloween"), "Dinosaur Rank (Halloween)", LeaderboardType.DinosaurRank, "form = " + DinosaurForm.Halloween.getId()),
		RankThanksgiving(Util.arr("rank thanksgiving"), "Dinosaur Rank (Thanksgiving)", LeaderboardType.DinosaurRank, "form = " + DinosaurForm.Thanksgiving.getId()),
		RankChaos(Util.arr("rank chaos"), "Dinosaur Rank (Chaos)", LeaderboardType.DinosaurRank, "form = " + DinosaurForm.Chaos.getId()),
		RankMechancial(Util.arr("rank mechanical"), "Dinosaur Rank (Mechanical)", LeaderboardType.DinosaurRank, "form = " + DinosaurForm.Mechanical.getId()),
		RankStatue(Util.arr("rank statue"), "Dinosaur Rank (Statue)", LeaderboardType.DinosaurRank, "form = " + DinosaurForm.Statue.getId()),
		
		// Item
		Money(Util.arr("money", "coins"), "Player Wealth", LeaderboardType.PlayerItem, null, ItemID.DinosaurCoin),
		Cookies(Util.arr("cookies", "cookie"), "Cookie Count", LeaderboardType.PlayerItem, null, ItemID.Cookie),

		// Stats
		BattlesEntered(Util.arr("battles entered"), "Battles Entered", LeaderboardType.PlayerItem, null, Stat.BattlesEntered),
		BattlesWon(Util.arr("battles won"), "Battles Won", LeaderboardType.PlayerItem, null, Stat.BattlesWon),
		DamageDealt(Util.arr("damage dealt"), "Damage Dealt", LeaderboardType.PlayerItem, null, Stat.DamageDealt),
		DamageReceived(Util.arr("damage received", "damage taken"), "Damage Received", LeaderboardType.PlayerItem, null, Stat.DamageReceived),
		DinosaursCaught(Util.arr("dinosaurs caught", "dinos caught"), "Dinosaurs Caught", LeaderboardType.PlayerItem, null, Stat.DinosaursCaught),
		QuestsCompleted(Util.arr("quests", "quests completed"), "Quests Completed", LeaderboardType.PlayerItem, null, Stat.QuestsCompleted),
		EggsHatched(Util.arr("eggs", "eggs hatched"), "Eggs Hatched", LeaderboardType.PlayerItem, null, Stat.EggsHatched),
		SnacksFed(Util.arr("snacks", "snacks fed"), "Snacks Fed", LeaderboardType.PlayerItem, null, Stat.SnacksFed),
		
		// Dinosaur Stats
		WTL(Util.arr("wtl", "wintoloss", "winstolosses"), "Dinosaur Win-to-Loss Ratio", LeaderboardType.DinosaurTwoStat, null, Long.parseLong(TWOSTAT_WTL)),
		Wins(Util.arr("wins", "win"), "Dinosaur Wins", LeaderboardType.DinosaurStat, null, Long.parseLong(STAT_WINS)),

		// Player Level
		PlayerLevel(Util.arr("trainer level", "trainer lvl", "trainer lv", "trainer", "tlvl", "tlv"), "Trainer Level", LeaderboardType.PlayerLevel, null),

		// Contest
		Contest(Util.arr("contest"), "Contest", LeaderboardType.DinosaurLevel, "form = " + DinosaurForm.Contest.getId()),
		;
		
		private String[] names;
		private String name, where;
		private long[] items;
		private LeaderboardType lbt;

		private LeaderboardCategory(String[] names, String name, LeaderboardType lbt, String where, Stat stat) {
			this(names, name, lbt, where, stat.getId().getFirstValue(), stat.getStatId());
		}

		private LeaderboardCategory(String[] names, String name, LeaderboardType lbt, String where, ItemID item) {
			this(names, name, lbt, where, item.getItemId(), item.getItemDamage());
		}

		private LeaderboardCategory(String[] names, String name, LeaderboardType lbt, String where, long...items) {
			this.names = names;
			this.name = name;
			this.lbt = lbt;
			this.where = where == null ? "true" : where;
			this.items = items;
		}
		
		public String getName() {
			return name;
		}
		
		public LeaderboardType getLeaderboardType() {
			return lbt;
		}
		
		public long[] getItems() {
			return items;
		}
		
		public String getWhereClause() {
			return where;
		}
		
		//////////////////////////////////////////////////
		
		public static String listValues() {
			String[] vals = new String[values().length];
			for (int q = 0; q < values().length; q++) {
				if (values()[q].names.length == 0) continue;
				vals[q] = String.format("`%s`", values()[q].names[0].toLowerCase());
			}
			return String.join(", ", vals);
		}
		
		public static LeaderboardCategory of(String cat) {
			for (LeaderboardCategory lbc : values()) {
				for (String name : lbc.names) {
					if (name.equalsIgnoreCase(cat)) {
						return lbc;
					}
				}
			}
			
			return null;
		}
	}
	
	protected enum LeaderboardType {
		DinosaurLevel("%s's %s - Level %,d + %,d XP"),
		DinosaurRank("%s's %s - Rank %s + %,d RP"),
		DinosaurStat("%s's %s - %,d %s"),
		DinosaurTwoStat("%s's %s - %,d %s to %,d %s"),
		PlayerDexCount("%s - %,d/%,d Dinosaurs"),
		PlayerRuneCount("%s - %,d/%,d Runes"),
		PlayerItem("%s - %,d %s"),
		PlayerStat("%s - %,d %s"),
		PlayerLevel("%s - %sLevel %,d + %,d %sXP");
		
		private String regex;
		private LeaderboardType(String regex) {
			this.regex = regex;
		}
		
		public String getRegex() {
			return regex;
		}
	}
	
	private static final String TWOSTAT_WTL = "1";
	private static final String STAT_WINS = "2";
}
