package com.quas.mesozoicisland.util;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

import com.quas.mesozoicisland.JDBC;
import com.quas.mesozoicisland.MesozoicIsland;
import com.quas.mesozoicisland.battle.Battle;
import com.quas.mesozoicisland.battle.BattleChannel;
import com.quas.mesozoicisland.enums.ActionType;
import com.quas.mesozoicisland.enums.DiscordChannel;

import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Message.MentionType;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.exceptions.ErrorResponseException;

public class Action {

	public static void doActions(Guild guild) {
		long self = guild.getSelfMember().getUser().getIdLong();

		try (ResultSet res = JDBC.executeQuery("select * from actions order by time, actionid;")) {
			while (res.next()) {
				if (System.currentTimeMillis() < res.getLong("time")) continue;
				if (res.getLong("bot") != 0 && res.getLong("bot") != self) continue;
				
				
				switch (ActionType.getActionType(res.getInt("actiontype"))) {
				case Error:
					System.out.println("Error executing the action: " + res.getString("msg"));
					break;
				case SendGuildMessage:
					TextChannel tc = guild.getTextChannelById(res.getLong("recipient"));
					if (tc == null) tc = DiscordChannel.Moderation.getChannel(MesozoicIsland.getBot(self));
					if (tc.getParent().getIdLong() == DiscordChannel.CATEGORY_BATTLE) {
						MessageBuilder mb = new MessageBuilder(res.getString("msg"));
						mb.denyMentions(MentionType.EVERYONE, MentionType.HERE, MentionType.USER, MentionType.ROLE);
						tc.sendMessage(mb.build()).complete();
					} else {
						tc.sendMessage(res.getString("msg")).complete();
					}
					break;
				case SendPrivateMessage:
					guild.getMemberById(res.getLong("recipient")).getUser().openPrivateChannel().complete().sendMessage(res.getString("msg")).complete();
					break;
				case GiveDinosaur:
					String[] dinosplit = res.getString("msg").split("\\s+");
					JDBC.addDinosaur(Constants.SPAWN_CHANNEL.getChannel(MesozoicIsland.getBot(self)), res.getLong("recipient"), Util.getDexForm(dinosplit[0]), dinosplit.length > 1 ? Integer.parseInt(dinosplit[1]) : 1);
					break;
				case GiveItem:
					String[] itemsplit = res.getString("msg").split("\\s+");
					JDBC.addItem(res.getLong("recipient"), new Pair<Integer, Long>(Integer.parseInt(itemsplit[0]), Long.parseLong(itemsplit[1])), itemsplit.length > 2 ? Long.parseLong(itemsplit[2]) : 1);
					break;
				case GiveRune:
					String[] runesplit = res.getString("msg").split("\\s+");
					JDBC.addRune(Constants.SPAWN_CHANNEL.getChannel(MesozoicIsland.getBot(self)), res.getLong("recipient"), Integer.parseInt(runesplit[0]), runesplit.length > 1 ? Integer.parseInt(runesplit[1]) : 1);
					break;
				case LogBattleChannel:
					BattleChannel bc = BattleChannel.of(res.getLong("recipient"));
					if (bc == null) break;
					List<Message> messages = Util.getMessages(bc.getBattleChannel().getChannel(MesozoicIsland.getBot(self)));
					Collections.reverse(messages);
					ArrayList<String> print = new ArrayList<String>();
					
					for (Message m : messages) {
						if (m.getAuthor().isBot()) print.add(m.getContentRaw());
						else print.add(String.format("**%s**: %s", m.getMember().getEffectiveName(), m.getContentRaw()));
						deleteMessage(res.getLong("bot"), m.getChannel().getIdLong(), m.getIdLong());
					}
					
					for (String s : Util.bulkify(print)) {
						Action.sendMessage(self, bc.getLogChannel(), s);
					}
					break;
				case DeleteMessage:
					try {
						guild.getTextChannelById(res.getLong("recipient")).deleteMessageById(res.getLong("msg")).complete();
					} catch (ErrorResponseException e) {
						System.out.println("Caught exception: " + e.getLocalizedMessage());
					}
					break;
				case RemovePlayerFromBattle:
					Battle.markPlayerBattling(res.getLong("recipient"), false);
					break;
				case AddXpToDinosaur:
					String[] xpsplit = res.getString("msg").split("\\s+");
					JDBC.addXp(Constants.SPAWN_CHANNEL.getChannel(MesozoicIsland.getBot(self)), res.getLong("recipient"), Util.getDexForm(xpsplit[0]), Long.parseLong(xpsplit[1]), true);
					break;
				case NewDay:
					long dtime = res.getLong("time");
					Daily.doUpdate(dtime);
					JDBC.addAction(ActionType.NewDay, self, 0, "Daily Message.", dtime + TimeUnit.DAYS.toMillis(1));
					break;
				case Redeem:
					JDBC.redeem(Constants.SPAWN_CHANNEL.getChannel(MesozoicIsland.getAssistant()), res.getLong("recipient"), res.getString("msg"));
					break;
				case AddWinToDinosaur:
					String windino = res.getString("msg");
					JDBC.addWin(res.getLong("recipient"), Util.getDexForm(windino));
					break;
				case AddLossToDinosaur:
					String lossdino = res.getString("msg");
					JDBC.addLoss(res.getLong("recipient"), Util.getDexForm(lossdino));
					break;
				case NewHour:
					long htime = res.getLong("time");
					Daily.doHourly(htime);
					JDBC.addAction(ActionType.NewHour, self, 0, "2-Hourly Message.", htime + TimeUnit.HOURS.toMillis(2));
					break;
				}
				
				JDBC.deleteAction(res.getInt("actionid"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public static void sendMessage(long from, DiscordChannel channel, String...msg) {
		for (String m : msg) {
			JDBC.addAction(ActionType.SendGuildMessage, from, channel.getIdLong(), Util.cleanQuotes(m), 0);
		}
	}
	
	public static void sendMessage(long from, MessageChannel channel, String...msg) {
		for (String m : msg) {
			JDBC.addAction(ActionType.SendGuildMessage, from, channel.getIdLong(), Util.cleanQuotes(m), 0);
		}
	}
	
	public static void sendDelayedMessage(long from, long after, DiscordChannel channel, String...msg) {
		for (String m : msg) {
			JDBC.addAction(ActionType.SendGuildMessage, from, channel.getIdLong(), Util.cleanQuotes(m), System.currentTimeMillis() + after);
		}
	}
	
	public static void sendDelayedMessage(long from, long after, MessageChannel channel, String...msg) {
		for (String m : msg) {
			JDBC.addAction(ActionType.SendGuildMessage, from, channel.getIdLong(), Util.cleanQuotes(m), System.currentTimeMillis() + after);
		}
	}
	
	public static void sendPrivateMessage(long from, long to, String...msg) {
		for (String m : msg) {
			JDBC.addAction(ActionType.SendPrivateMessage, from, to, Util.cleanQuotes(m), 0);
		}
	}
	
	public static void sendDelayedPrivateMessage(long from, long to, long after, String...msg) {
		for (String m : msg) {
			JDBC.addAction(ActionType.SendPrivateMessage, from, to, Util.cleanQuotes(m), System.currentTimeMillis() + after);
		}
	}
	
	public static void deleteMessage(long from, long channel, long messageid) {
		JDBC.addAction(ActionType.DeleteMessage, from, channel, Long.toString(messageid), 0);
	}
	
	public static void deleteMessageDelayed(long from, long channel, long messageid, long after) {
		JDBC.addAction(ActionType.DeleteMessage, from, channel, Long.toString(messageid), System.currentTimeMillis() + after);
	}
	
	public static void logBattleChannel(long from, long channel) {
		JDBC.addAction(ActionType.LogBattleChannel, from, channel, "", 0);
	}
	
	public static void logBattleChannelDelayed(long from, long channel, long after) {
		JDBC.addAction(ActionType.LogBattleChannel, from, channel, "", System.currentTimeMillis() + after);
	}
	
	public static void removePlayerFromBattle(long player) {
		JDBC.addAction(ActionType.RemovePlayerFromBattle, 0, player, "", 0);
	}
	
	public static void removePlayerFromBattleDelayed(long player, long after) {
		JDBC.addAction(ActionType.RemovePlayerFromBattle, 0, player, "", System.currentTimeMillis() + after);
	}
	
	public static void addDinosaur(long to, String dino) {
		JDBC.addAction(ActionType.GiveDinosaur, MesozoicIsland.getAssistant().getIdLong(), to, dino, 0);
	}
	
	public static void addDinosaurDelayed(long to, long after, String dino) {
		JDBC.addAction(ActionType.GiveDinosaur, MesozoicIsland.getAssistant().getIdLong(), to, dino, System.currentTimeMillis() + after);
	}
	
	public static void addRune(long to, int rune) {
		JDBC.addAction(ActionType.GiveRune, MesozoicIsland.getAssistant().getIdLong(), to, Integer.toString(rune), 0);
	}
	
	public static void addRuneDelayed(long to, long after, int rune) {
		JDBC.addAction(ActionType.GiveRune, MesozoicIsland.getAssistant().getIdLong(), to, Integer.toString(rune), System.currentTimeMillis() + after);
	}
	
	public static void addItem(long to, Pair<Integer, Long> item, long count) {
		JDBC.addAction(ActionType.GiveItem, 0, to, String.format("%d %d %d", item.getFirstValue(), item.getSecondValue(), count), 0);
	}
	
	public static void addItemDelayed(long to, long after, Pair<Integer, Long> item, long count) {
		JDBC.addAction(ActionType.GiveItem, 0, to, String.format("%d %d %d", item.getFirstValue(), item.getSecondValue(), count), System.currentTimeMillis() + after);
	}
	
	public static void addXp(long to, String dino, long xp) {
		JDBC.addAction(ActionType.AddXpToDinosaur, MesozoicIsland.getAssistant().getIdLong(), to, dino + " " + xp, 0);
	}
	
	public static void addXpDelayed(long to, long after, String dino, long xp) {
		JDBC.addAction(ActionType.AddXpToDinosaur, MesozoicIsland.getAssistant().getIdLong(), to, dino + " " + xp, System.currentTimeMillis() + after);
	}
	
	public static void addRedeemDelayed(long from, long to, long after, String redeem) {
		JDBC.addAction(ActionType.Redeem, from, to, redeem, System.currentTimeMillis() + after);
	}

	public static void addDinosaurWinDelayed(long to, long after, String dino) {
		JDBC.addAction(ActionType.AddWinToDinosaur, 0, to, dino, System.currentTimeMillis() + after);
	}

	public static void addDinosaurLossDelayed(long to, long after, String dino) {
		JDBC.addAction(ActionType.AddLossToDinosaur, 0, to, dino, System.currentTimeMillis() + after);
	}
}
