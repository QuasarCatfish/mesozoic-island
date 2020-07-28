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

public class Action implements Comparable<Action> {

	private ActionType action;
	private long from, recipient, time;
	private String msg;
	private boolean delete;

	private Action(ActionType action, long from, long recipient, String msg, long time) {
		this.action = action;
		this.from = from;
		this.recipient = recipient;
		this.msg = msg;
		this.time = time;
		this.delete = false;
		System.out.println("Creating new action - " + this.toString());
	}

	@Override
	public int compareTo(Action that) {
		return Long.compare(this.time, that.time);
	}

	@Override
	public boolean equals(Object that) {
		return that instanceof Action && this.compareTo((Action)that) == 0;
	}

	@Override
	public String toString() {
		return String.format("(%d, %d, %d, '%s', %d)", action.getActionType(), from, recipient, msg.replaceAll("\\*\''", "\\\''"), time);
	}

	public boolean isDeleted() {
		return delete;
	}

	////////////////////////////////////////////////////////////////

	private static ArrayList<Action> actions = new ArrayList<Action>();

	public static ArrayList<Action> getActions() {
		return actions;
	}

	public static synchronized void initialize() {
		System.out.println("Initializing Actions:");
		try (ResultSet res = JDBC.executeQuery("select * from actions;")) {
			while (res.next()) {
				new Action(ActionType.getActionType(res.getInt("actiontype")), res.getLong("bot"), res.getLong("recipient"), res.getString("msg"), res.getLong("time"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public static synchronized void log() {
		for (int q = 0; q < actions.size(); q++) {
			if (actions.get(q).delete) {
				actions.remove(q);
				q--;
			}
		}

		JDBC.executeUpdate("delete from actions where actionid > 0;");
		JDBC.executeUpdate("insert into actions(actiontype, bot, recipient, msg, time) values %s;", Util.join(actions, ",", 0, actions.size()));
	}

	public static synchronized void doActions(Guild guild) {
		long self = guild.getSelfMember().getUser().getIdLong();
		MessageChannel channel = Constants.SPAWN_CHANNEL.getChannel(MesozoicIsland.getBot(self));

		for (int q = 0; q < actions.size(); q++) {
			Action a = actions.get(q);
			if (a.delete) continue;
			if (System.currentTimeMillis() < a.time) continue;
			if (a.from != 0 && a.from != self) continue;

			switch (a.action) {
				
			case Error:
				System.out.println("Error executing the action: " + a.msg);
				break;

			case SendGuildMessage:
				TextChannel tc = guild.getTextChannelById(a.recipient);
				if (tc == null) tc = DiscordChannel.BotLog.getChannel(MesozoicIsland.getBot(self));
				MessageBuilder mb = new MessageBuilder(a.msg);
				if (tc.getParent().getIdLong() == DiscordChannel.CATEGORY_BATTLE) {
					mb.denyMentions(MentionType.EVERYONE, MentionType.HERE, MentionType.USER, MentionType.ROLE);
				}
				tc.sendMessage(mb.build()).complete();
				break;

			case SendPrivateMessage:
				guild.getMemberById(a.recipient).getUser().openPrivateChannel().complete().sendMessage(a.msg).complete();
				break;
			case GiveDinosaur:
				String[] dinosplit = a.msg.split("\\s+");
				JDBC.addDinosaur(channel, a.recipient, Util.getDexForm(dinosplit[0]), dinosplit.length > 1 ? Integer.parseInt(dinosplit[1]) : 1);
				break;
			case GiveItem:
				String[] itemsplit = a.msg.split("\\s+");
				JDBC.addItem(a.recipient, new Pair<Integer, Long>(Integer.parseInt(itemsplit[0]), Long.parseLong(itemsplit[1])), itemsplit.length > 2 ? Long.parseLong(itemsplit[2]) : 1);
				break;
			case GiveRune:
				String[] runesplit = a.msg.split("\\s+");
				JDBC.addRune(channel, a.recipient, Integer.parseInt(runesplit[0]), runesplit.length > 1 ? Integer.parseInt(runesplit[1]) : 1);
				break;
			case LogBattleChannel:
				BattleChannel bc = BattleChannel.of(a.recipient);
				if (bc == null) break;
				List<Message> messages = Util.getMessages(bc.getBattleChannel().getChannel(MesozoicIsland.getBot(self)));
				Collections.reverse(messages);
				ArrayList<String> print = new ArrayList<String>();
				
				for (Message m : messages) {
					if (m.getAuthor().isBot()) print.add(m.getContentRaw());
					else print.add(String.format("**%s**: %s", m.getMember().getEffectiveName(), m.getContentRaw()));
					deleteMessage(a.from, m.getChannel().getIdLong(), m.getIdLong());
				}
				
				for (String s : Util.bulkify(print)) {
					Action.sendMessage(self, bc.getLogChannel(), s);
				}
				break;
			case DeleteMessage:
				try {
					guild.getTextChannelById(a.recipient).deleteMessageById(a.msg).complete();
				} catch (ErrorResponseException e) {
					System.out.println("Caught exception: " + e.getLocalizedMessage());
				}
				break;
			case RemovePlayerFromBattle:
				Battle.markPlayerBattling(a.recipient, false);
				break;
			case AddXpToDinosaur:
				String[] xpsplit = a.msg.split("\\s+");
				JDBC.addXp(channel, a.recipient, Util.getDexForm(xpsplit[0]), Long.parseLong(xpsplit[1]));
				break;
			case NewDay:
				long time = a.time;
				Daily.doUpdate(time);
				JDBC.addAction(ActionType.NewDay, self, 0, "Daily Message.", time + TimeUnit.DAYS.toMillis(1));
				break;
			case Redeem:
				JDBC.redeem(Constants.SPAWN_CHANNEL.getChannel(MesozoicIsland.getAssistant()), a.recipient, a.msg);
				break;

			}

			a.delete = true;
		}
	}

	public static synchronized void sendMessage(long from, DiscordChannel channel, String...msg) {
		for (String m : msg) {
			actions.add(new Action(ActionType.SendGuildMessage, from, channel.getIdLong(), Util.cleanQuotes(m), 0));
		}
	}
	
	public static synchronized void sendMessage(long from, MessageChannel channel, String...msg) {
		for (String m : msg) {
			actions.add(new Action(ActionType.SendGuildMessage, from, channel.getIdLong(), Util.cleanQuotes(m), 0));
		}
	}
	
	public static synchronized void sendDelayedMessage(long from, long after, DiscordChannel channel, String...msg) {
		for (String m : msg) {
			actions.add(new Action(ActionType.SendGuildMessage, from, channel.getIdLong(), Util.cleanQuotes(m), System.currentTimeMillis() + after));
		}
	}
	
	public static synchronized void sendDelayedMessage(long from, long after, MessageChannel channel, String...msg) {
		for (String m : msg) {
			actions.add(new Action(ActionType.SendGuildMessage, from, channel.getIdLong(), Util.cleanQuotes(m), System.currentTimeMillis() + after));
		}
	}
	
	public static synchronized void sendPrivateMessage(long from, long to, String...msg) {
		for (String m : msg) {
			actions.add(new Action(ActionType.SendPrivateMessage, from, to, Util.cleanQuotes(m), 0));
		}
	}
	
	public static synchronized void sendDelayedPrivateMessage(long from, long to, long after, String...msg) {
		for (String m : msg) {
			actions.add(new Action(ActionType.SendPrivateMessage, from, to, Util.cleanQuotes(m), System.currentTimeMillis() + after));
		}
	}
	
	public static synchronized void deleteMessage(long from, long channel, long messageid) {
		actions.add(new Action(ActionType.DeleteMessage, from, channel, Long.toString(messageid), 0));
	}
	
	public static synchronized void deleteMessageDelayed(long from, long channel, long messageid, long after) {
		actions.add(new Action(ActionType.DeleteMessage, from, channel, Long.toString(messageid), System.currentTimeMillis() + after));
	}
	
	public static synchronized void logBattleChannel(long from, long channel) {
		actions.add(new Action(ActionType.LogBattleChannel, from, channel, "", 0));
	}
	
	public static synchronized void logBattleChannelDelayed(long from, long channel, long after) {
		actions.add(new Action(ActionType.LogBattleChannel, from, channel, "", System.currentTimeMillis() + after));
	}
	
	public static synchronized void removePlayerFromBattle(long player) {
		actions.add(new Action(ActionType.RemovePlayerFromBattle, 0, player, "", 0));
	}
	
	public static synchronized void removePlayerFromBattleDelayed(long player, long after) {
		actions.add(new Action(ActionType.RemovePlayerFromBattle, 0, player, "", System.currentTimeMillis() + after));
	}
	
	public static synchronized void addDinosaur(long to, String dino) {
		actions.add(new Action(ActionType.GiveDinosaur, 0, to, dino, 0));
	}
	
	public static synchronized void addDinosaurDelayed(long to, long after, String dino) {
		actions.add(new Action(ActionType.GiveDinosaur, 0, to, dino, System.currentTimeMillis() + after));
	}
	
	public static synchronized void addRune(long to, int rune) {
		actions.add(new Action(ActionType.GiveRune, 0, to, Integer.toString(rune), 0));
	}
	
	public static synchronized void addRuneDelayed(long to, long after, int rune) {
		actions.add(new Action(ActionType.GiveRune, 0, to, Integer.toString(rune), System.currentTimeMillis() + after));
	}
	
	public static synchronized void addItem(long to, Pair<Integer, Long> item, long count) {
		actions.add(new Action(ActionType.GiveItem, 0, to, String.format("%d %d %d", item.getFirstValue(), item.getSecondValue(), count), 0));
	}
	
	public static synchronized void addItemDelayed(long to, long after, Pair<Integer, Long> item, long count) {
		actions.add(new Action(ActionType.GiveItem, 0, to, String.format("%d %d %d", item.getFirstValue(), item.getSecondValue(), count), System.currentTimeMillis() + after));
	}
	
	public static synchronized void addXp(long to, String dino, long xp) {
		actions.add(new Action(ActionType.AddXpToDinosaur, 0, to, dino + " " + xp, 0));
	}
	
	public static synchronized void addXpDelayed(long to, long after, String dino, long xp) {
		actions.add(new Action(ActionType.AddXpToDinosaur, 0, to, dino + " " + xp, System.currentTimeMillis() + after));
	}
	
	public static synchronized void addRedeemDelayed(long from, long to, long after, String redeem) {
		actions.add(new Action(ActionType.Redeem, from, to, redeem, System.currentTimeMillis() + after));
	}
}
