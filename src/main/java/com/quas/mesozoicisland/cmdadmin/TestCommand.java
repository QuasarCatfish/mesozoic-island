package com.quas.mesozoicisland.cmdadmin;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.TreeMap;
import java.util.regex.Pattern;

import com.quas.mesozoicisland.JDBC;
import com.quas.mesozoicisland.MesozoicIsland;
import com.quas.mesozoicisland.battle.SpawnManager;
import com.quas.mesozoicisland.cmdbase.CommandManager;
import com.quas.mesozoicisland.cmdbase.ICommand;
import com.quas.mesozoicisland.enums.AccessLevel;
import com.quas.mesozoicisland.enums.CustomPlayer;
import com.quas.mesozoicisland.enums.DinoID;
import com.quas.mesozoicisland.enums.DinosaurForm;
import com.quas.mesozoicisland.enums.DiscordChannel;
import com.quas.mesozoicisland.enums.DiscordRole;
import com.quas.mesozoicisland.enums.EventType;
import com.quas.mesozoicisland.enums.ItemID;
import com.quas.mesozoicisland.enums.Stat;
import com.quas.mesozoicisland.objects.Dinosaur;
import com.quas.mesozoicisland.objects.Egg;
import com.quas.mesozoicisland.objects.Element;
import com.quas.mesozoicisland.objects.Event;
import com.quas.mesozoicisland.objects.Item;
import com.quas.mesozoicisland.objects.Leaderboard;
import com.quas.mesozoicisland.objects.Player;
import com.quas.mesozoicisland.objects.Rarity;
import com.quas.mesozoicisland.util.Constants;
import com.quas.mesozoicisland.util.DinoMath;
import com.quas.mesozoicisland.util.MesozoicRandom;
import com.quas.mesozoicisland.util.Pair;
import com.quas.mesozoicisland.util.Util;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
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
				int level = args.length > 2 ? Integer.parseInt(args[2].replaceAll("\\D", "")) : Constants.MAX_DINOSAUR_LEVEL;
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
					JDBC.updateEggs(false);
				}
			} break;

			case "contest": {
				Leaderboard lb = new Leaderboard("%s's %s - Level %,d + %,d XP");
				lb.setUnlimited(true);

				try (ResultSet res = JDBC.executeQuery("select * from captures where form = %d order by xp desc limit %d;", DinosaurForm.Contest.getId())) {
					while (res.next()) {
						if (res.getLong("player") < CustomPlayer.getUpperLimit()) continue;
						Dinosaur d = Dinosaur.getDinosaur(res.getLong("player"), new Pair<Integer, Integer>(res.getInt("dex"), res.getInt("form")));
						lb.addEntry(d.getXp() + 1, d.getPlayer().getName(), d.getEffectiveName(), d.getLevel(), d.getXpMinusLevel());
					}
				} catch (SQLException e) {
					e.printStackTrace();
				}

				for (String s : Util.bulkify(lb.getLeaderboard())) {
					event.getChannel().sendMessage(s).complete();
				}
			} break;

			case "suggestion": {
				int x = Integer.parseInt(args[2]);

				StringBuilder sb = new StringBuilder("__Suggestion " + x + ":__\n");
				try (ResultSet res = JDBC.executeQuery("select * from suggestions where suggestionid = %d;", x)) {
					if (res.next()) {
						sb.append("Author: ");
						sb.append("<@" + res.getLong("player") + ">\n");
					}
				} catch (SQLException e) {
					e.printStackTrace();
				}

				sb.append("\nVotes:\n");
				try (ResultSet res = JDBC.executeQuery("select * from suggestionvotes where suggestionid = %d;", x)) {
					while (res.next()) {
						sb.append("-- ");
						sb.append("<@" + res.getLong("player") + "> voted ");
						sb.append(res.getInt("vote"));
						sb.append("\n");
					}
				} catch (SQLException e) {
					e.printStackTrace();
				}

				event.getChannel().sendMessage(sb.toString()).complete();
			} break;

			case "pfp": {
				long id = Long.parseLong(args[2].replaceAll("\\D", ""));
				User u = event.getGuild().getMemberById(id).getUser();
				event.getChannel().sendMessage(u.getAvatarUrl()).complete();
			} break;

			case "eventtype": {
				StringBuilder sb = new StringBuilder("Event Type List:");
				for (EventType et : EventType.values()) {
					sb.append(String.format("\n%s - %b", et, Event.isEventActive(et)));
				}
				event.getChannel().sendMessage(sb.toString()).complete();
			} break;

			case "attack": {
				Dinosaur d = Dinosaur.getDinosaur(event.getAuthor().getIdLong(), 1, 0);
				event.getChannel().sendMessageFormat("%s", d.getAttacks()).complete();
			} break;

			case "santa": {
				event.getChannel().sendMessage("Start.").complete();
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
					if (players.get(q) != Constants.QUAS_ID) continue;
					
					Member m = g.getMemberById(players.get(q));
					if (m == null) continue;
					try {
						m.getUser().openPrivateChannel().complete().sendMessageFormat("**== Secret Santa Event ==**\nThis year, you will be collecting Gift Tokens to buy presents for %s! You can get Gift Tokens by opening Mystery Presents, which wild dinosaurs have a chance of holding. Be sure to buy all your presents before Christmas!", Player.getPlayer(santa.get(q)).getName()).complete();
					} catch (Exception e) {}
				}

				event.getChannel().sendMessage("Done.").complete();
			} break;

			case "benedict": {
				int count = -1;
				try (ResultSet res = JDBC.executeQuery("select count(*) as count from eggs where player = %d;", CustomPlayer.EggSalesman.getIdLong())) {
					if (res.next()) {
						count = res.getInt("count");
					}
				} catch (SQLException e) {
					e.printStackTrace();
				}

				if (count > -1) {
					int max = Integer.parseInt(JDBC.getVariable("benedict"));
					if (count == 0) {
						max += 1;
						JDBC.setVariable("benedict", Integer.toString(max));
					}

					for (int q = count; q < max; q++) {
						Egg egg = Egg.getRandomEgg(MesozoicRandom.nextOwnableDinosaur().getIdPair());
						JDBC.addEgg(CustomPlayer.EggSalesman.getIdLong(), egg);
						event.getChannel().sendMessage("Generated " + egg.getEggName()).complete();
					}

					event.getChannel().sendMessageFormat("\n%s %s has received more eggs. There are %,d eggs in stock today.", Constants.BULLET_POINT, CustomPlayer.EggSalesman.getPlayer().getName(), Math.max(count, max)).complete();
				} else {
					event.getChannel().sendMessage("There was an error in giving eggs.").complete();
				}
			} break;

			case "ihateturkey": {
				int count = 0;

				for (int q = 0; q < 10_000; q++) {
					Dinosaur d = MesozoicRandom.nextOwnableDinosaur();
					if (d.getDex() == DinoID.Turkey.getDex()) count++;
				}

				event.getChannel().sendMessageFormat("%s %d", event.getAuthor().getAsMention(), count).complete();
			} break;

			case "lost": {
				long sum = 0;
				Dinosaur[] values = Dinosaur.values();
				
				HashMap<Pair<Element, Rarity>, Long> chance = new HashMap<Pair<Element, Rarity>, Long>();
				HashMap<Pair<Element, Rarity>, Integer> count = new HashMap<Pair<Element, Rarity>, Integer>();
				HashMap<Element, Long> elements = new HashMap<Element, Long>();

				for (Dinosaur d : values) {
					if (d.getDinosaurForm() != DinosaurForm.Standard) continue;
					
					sum += d.getRarity().getDinoCount();
					Pair<Element, Rarity> pair = new Pair<Element, Rarity>(d.getElement(), d.getRarity());
					
					long val = chance.getOrDefault(pair, 0L);
					chance.put(pair, val + d.getRarity().getDinoCount());
					int c = count.getOrDefault(pair, 0);
					count.put(pair, c + 1);

					long val2 = elements.getOrDefault(d.getElement(), 0L);
					elements.put(d.getElement(), val2 + d.getRarity().getDinoCount());
				}

				ArrayList<String> print = new ArrayList<String>();
				for (Element element : elements.keySet()) {
					print.add(String.format("%s - %,d / %,d (%1.4f%%)", element, elements.get(element), sum, 100d * elements.get(element) / sum));
				}

				print.add("");
				for (Pair<Element, Rarity> pair : chance.keySet()) {
					print.add(String.format("%s - %,d | %,d / %,d (%1.4f%% | %1.4f%%)", pair, chance.get(pair) / count.get(pair), chance.get(pair), sum, 100d * chance.get(pair) / count.get(pair) / sum, 100d * chance.get(pair) / sum));
				}

				for (String s : Util.bulkify(print)) {
					event.getChannel().sendMessage(s).complete();
				}
			} break;

			case "questdarkness": {
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
