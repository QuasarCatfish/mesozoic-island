package com.quas.mesozoicisland.cmdplayer;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.TreeMap;
import java.util.regex.Pattern;

import com.quas.mesozoicisland.JDBC;
import com.quas.mesozoicisland.MesozoicIsland;
import com.quas.mesozoicisland.cmdbase.ICommand;
import com.quas.mesozoicisland.enums.AccessLevel;
import com.quas.mesozoicisland.enums.DiscordChannel;
import com.quas.mesozoicisland.enums.DiscordRole;
import com.quas.mesozoicisland.objects.Item;
import com.quas.mesozoicisland.objects.Player;
import com.quas.mesozoicisland.util.Constants;
import com.quas.mesozoicisland.util.Pair;
import com.quas.mesozoicisland.util.Stats;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class QuestsCommand implements ICommand {

	@Override
	public Pattern getCommand() {
		return pattern("quests?");
	}

	@Override
	public AccessLevel getAccessLevel() {
		return AccessLevel.Trainer;
	}

	@Override
	public String getCommandName() {
		return "quests";
	}

	@Override
	public String getCommandSyntax() {
		return "quests";
	}

	@Override
	public String getCommandDescription() {
		return "Lists the quests the player has in progress.";
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
	public void run(MessageReceivedEvent event, String... args) {
		Player p = Player.getPlayer(event.getAuthor().getIdLong());
		if (p == null) return;
		
		TreeMap<Item, Long> bag = p.getBag();
		Item questBook = Item.getItem(new Pair<Integer, Long>(5, 0L));
		if (bag.getOrDefault(questBook, 0L) <= 0) {
			event.getChannel().sendMessageFormat("%s, you don't have a %s.", p.getAsMention(), questBook.toString()).complete();
			return;
		}
		
		
		StringBuilder sb = new StringBuilder();
		sb.append(String.format("**%s's Quests:**", p.getAsMention()));
		ArrayList<Pair<String, String>> rewards = new ArrayList<Pair<String, String>>();
		
		int questcount = 0;
		try (ResultSet res = JDBC.executeQuery("select * from quests where playerid = %d and completed = false;", p.getIdLong())) {
			while (res.next()) {
				String name = res.getString("questname");
				long quest = res.getLong("questtype");
				Item item = Item.getItem(new Pair<Integer, Long>(0, quest));
				if (item == null) continue;
				
				long start = res.getLong("start");
				long end = res.getLong("goal");
				if (end <= 0) continue;
				
				long progress = bag.getOrDefault(item, 0L) - start;
				
				sb.append("\n• ");
				if (res.getInt("special") == 1) sb.append("[EVENT] ");
				sb.append("**");
				sb.append(name);
				sb.append("** - ");
				
				if (progress >= end) {
					sb.append("COMPLETED");
					JDBC.executeUpdate("update quests set completed = true where questid = %d;", res.getInt("questid"));
					rewards.add(new Pair<String, String>(name, res.getString("reward")));
					JDBC.addItem(p.getIdLong(), Stats.of(Stats.QUESTS_COMPLETED), 1);
				} else {
					sb.append(String.format("%,d of %,d (%1.0f%% Complete)", progress, end, 100d * progress / end));
				}
				
				questcount++;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		if (questcount == 0) {
			event.getChannel().sendMessageFormat("%s, you do not have any quests.", p.getAsMention()).complete();
		} else {
			event.getChannel().sendMessage(sb.toString()).complete();
		}
		
		for (Pair<String, String> reward : rewards) {
			event.getChannel().sendMessageFormat("%s, for completing the \"%s\" quest, you have received the following rewards:\n%s", p.getAsMention(), reward.getFirstValue(), JDBC.getRedeemMessage(reward.getSecondValue())).complete();
			JDBC.redeem(Constants.SPAWN_CHANNEL.getChannel(MesozoicIsland.getAssistant()), p.getIdLong(), reward.getSecondValue());
		}
	}
}
