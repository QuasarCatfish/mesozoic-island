package com.quas.mesozoicisland.cmdadmin;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.TreeMap;
import java.util.regex.Pattern;

import com.quas.mesozoicisland.JDBC;
import com.quas.mesozoicisland.battle.SpawnManager;
import com.quas.mesozoicisland.cmdbase.CommandManager;
import com.quas.mesozoicisland.cmdbase.ICommand;
import com.quas.mesozoicisland.enums.AccessLevel;
import com.quas.mesozoicisland.enums.CustomPlayer;
import com.quas.mesozoicisland.enums.DiscordChannel;
import com.quas.mesozoicisland.enums.DiscordRole;
import com.quas.mesozoicisland.enums.EventType;
import com.quas.mesozoicisland.enums.Stat;
import com.quas.mesozoicisland.objects.Dinosaur;
import com.quas.mesozoicisland.objects.Event;
import com.quas.mesozoicisland.objects.Item;
import com.quas.mesozoicisland.objects.Player;
import com.quas.mesozoicisland.util.Constants;
import com.quas.mesozoicisland.util.DinoMath;
import com.quas.mesozoicisland.util.Util;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class TestCommand implements ICommand {

	@Override
	public Pattern getCommand() {
		return pattern("admin test( [a-z0-9]+)+");
	}

	@Override
	public AccessLevel getAccessLevel() {
		return AccessLevel.Admin;
	}

	@Override
	public String getCommandName() {
		return "admin";
	}

	@Override
	public String getCommandSyntax() {
		return "admin test <test>";
	}

	@Override
	public String getCommandDescription() {
		return "Executes a test code.";
	}

	@Override
	public DiscordChannel[] getUsableChannels() {
		return DiscordChannel.ALL_CHANNELS;
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
	public void run(MessageReceivedEvent event, String... args) {
		try {
			switch (args[1].toLowerCase()) {
			case "level": {
				int level = args.length > 2 ? Integer.parseInt(args[2].replaceAll("\\D", "")) : Constants.MAX_LEVEL;
				event.getChannel().sendMessageFormat("%s, a Level %,d dinosaur would have %,d XP.", event.getAuthor().getAsMention(), level, DinoMath.getXp(level)).complete();
			} break;
			
			case "commands": {
				event.getChannel().sendMessageFormat("%s, there are %,d commands.", event.getAuthor().getAsMention(), CommandManager.values().size()).complete();
			} break;
			
			case "spawncheck": {
				StringBuilder sb = new StringBuilder();
				sb.append("```");
				sb.append("\nspawn time  = " + SpawnManager.spawntime);
				sb.append("\nlast spawn  = " + SpawnManager.lastspawn);
				sb.append("\ncurrent     = " + System.currentTimeMillis());
				sb.append("\nwaiting     = " + SpawnManager.waiting);
				sb.append("\nwild battle = " + SpawnManager.isWildBattleHappening());
				sb.append("\ncfg spawn   = " + Constants.SPAWN);
				sb.append("\n```");
				event.getChannel().sendMessage(sb.toString()).complete();
			} break;
			
			case "reward": {
				String reward = JDBC.getReward(args[2]);
				if (reward == null) {
					event.getChannel().sendMessageFormat("%s, there is no reward named `%s`.", event.getAuthor().getAsMention(), args[2]).complete();
				} else {
					event.getChannel().sendMessageFormat("%s, the reward for `%s` is:\n%s", event.getAuthor().getAsMention(), args[2], JDBC.getRedeemMessage(reward)).complete();
				}
			} break;

			case "givehp": {
				int x = Integer.parseInt(args[2]);
				while (x --> 0) {
					JDBC.updateEggs();
				}
			} break;

			case "checktier": {
				long player = Long.parseLong(args[2]);
				Player p = Player.getPlayer(player);
				Dinosaur[] team = p.getSelectedDinosaurs();
				
				long xp = 0;
				long bst = 0;
				for (Dinosaur d : team) {
					xp += d.getXp();
					long stats = d.getHealth() + d.getAttack() + d.getDefense();
					if (stats > bst) bst = stats;
				}
				
				// Calculate Level
				int level = DinoMath.getLevel(xp);
				event.getChannel().sendMessageFormat("%s's team has the following stats: [%,d %,d %,d].", p.getRawName(), level, bst, xp).complete();
			} break;

			case "eventtype": {
				StringBuilder sb = new StringBuilder("Event Type List:");
				for (EventType et : EventType.values()) {
					sb.append(String.format("\n%s - %b", et, Event.isEventActive(et)));
				}
				event.getChannel().sendMessage(sb.toString()).complete();
			} break;

			case "givequest": {
				int questid = Integer.parseInt(args[2]);

				TreeMap<Long, Integer> valid = new TreeMap<Long, Integer>();
				try (ResultSet res = JDBC.executeQuery("select players.playerid, x.quests, y.books from players left join (select playerid, count(*) as quests from quests where special = 0 and completed = false group by playerid) as x on players.playerid = x.playerid left join (select player, count as books from bags where item = 5 and dmg = 0) as y on players.playerid = y.player where players.playerid > %d;", CustomPlayer.getUpperLimit())) {
					while (res.next()) {
						if (res.getInt("x.quests") >= Constants.MAX_QUESTS) continue;
						if (res.getInt("y.books") <= 0) continue;
						valid.put(res.getLong("players.playerid"), Constants.MAX_QUESTS - res.getInt("x.quests"));
					}
				} catch (SQLException e) {
					e.printStackTrace();
				}

				try (ResultSet res = JDBC.executeQuery("select * from questlist where questid = %d;", questid)) {
					if (res.next()) {
						String name = res.getString("questname");
						long type = res.getLong("questtype");
						long goal = res.getLong("goal");
						String reward = res.getString("reward");
						int special = res.getInt("special");
						Item item = Item.getItem(Stat.of(type));
						
						for (long player : valid.keySet()) {
							if (special != 0 || valid.get(player) > 0) {
								valid.put(player, valid.get(player) - 1);
								JDBC.executeUpdate("insert into quests(playerid, questname, questtype, start, goal, reward, special) values(%d, '%s', %d, %d, %d, '%s', %d);", player, Util.cleanQuotes(name), type, Player.getPlayer(player).getBag().getOrDefault(item, 0L), goal, Util.cleanQuotes(reward), special);
							}
						}

						if (special != 0) {
							event.getChannel().sendMessageFormat("%s, all players have been given the '%s' Quest.", event.getAuthor().getAsMention(), name).complete();
						} else {
							event.getChannel().sendMessageFormat("%s, all players with space in their quest book have been given the '%s' Quest.", event.getAuthor().getAsMention(), name).complete();
						}
					} else {
						event.getChannel().sendMessageFormat("%s, there is no quest with ID %d.", event.getAuthor().getAsMention(), questid).complete();
					}
				} catch (SQLException e) {
					e.printStackTrace();
				}
			} break;

			}
		} catch (Exception e) {
			e.printStackTrace();
			event.getChannel().sendMessageFormat("%s, an error has occured.", event.getAuthor().getAsMention()).complete();
		}
	}
}
