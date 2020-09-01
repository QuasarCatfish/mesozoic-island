package com.quas.mesozoicisland.cmdplayer;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.TreeMap;
import java.util.regex.Pattern;

import com.quas.mesozoicisland.cmdbase.ICommand;
import com.quas.mesozoicisland.enums.AccessLevel;
import com.quas.mesozoicisland.enums.CustomPlayer;
import com.quas.mesozoicisland.enums.DiscordChannel;
import com.quas.mesozoicisland.enums.DiscordRole;
import com.quas.mesozoicisland.enums.Stat;
import com.quas.mesozoicisland.objects.Item;
import com.quas.mesozoicisland.objects.Player;
import com.quas.mesozoicisland.util.Util;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class StatsPlayerCommand implements ICommand {

	@Override
	public Pattern getCommand() {
		return pattern("stats ", PLAYER);
	}

	@Override
	public AccessLevel getAccessLevel() {
		return AccessLevel.Trainer;
	}

	@Override
	public String getCommandName() {
		return "stats";
	}

	@Override
	public String getCommandSyntax() {
		return "stats @player";
	}

	@Override
	public String getCommandDescription() {
		return "Gets the stats of a particular player.";
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
		Player p = Player.getPlayer(Long.parseLong(args[0].replaceAll("\\D", "")));
		if (p == null || p.getIdLong() < CustomPlayer.getUpperLimit()) {
			event.getChannel().sendMessageFormat("%s, this player does not exist", event.getAuthor().getAsMention()).complete();
			return;
		}
		
		TreeMap<Item, Long> bag = p.getBag();
		ArrayList<Item> items = new ArrayList<Item>();
		for (Item i : Item.getItems(0)) items.add(i);
		items.sort(new Comparator<Item>() {
			@Override
			public int compare(Item first, Item second) {
				return Integer.compare(Integer.parseInt(first.getData()), Integer.parseInt(second.getData()));
			}
		});
		
		EmbedBuilder eb = new EmbedBuilder();
		eb.setColor(p.getColor());
		eb.setTitle(p.getName() + "'s Stats");
		
		eb.addField("Join Date", p.getJoinDate(), true);
		eb.addField("Trainer Level and XP", String.format("Level %,d + %,d XP", p.getLevel(), p.getXpMinusLevel()), true);
		
		if (event.getChannel().getIdLong() == DiscordChannel.Game.getIdLong()) {
			eb.setDescription("For a full list of stats, please use the command in " + DiscordChannel.BotCommands.toString() + " or Direct Messages.");
		} else {
			{
				Item i = Item.getItem(Stat.DailiesClaimed.getId());
				eb.addField(i.toString(2), String.format("%,d", bag.getOrDefault(i, 0L)), true);
				eb.addField("Daily Streak", String.format("%,d Day%s", p.getDailyStreak(), p.getDailyStreak() == 1 ? "" : "s"), true);
			}
			
			for (Item i : items) {
				if (Integer.parseInt(i.getData()) < 0) continue;
				eb.addField(i.toString(2), String.format("%,d", bag.getOrDefault(i, 0L)), true);
			}
		}

		long time = System.currentTimeMillis();
		if (p.getFragranceXpTimer() > time) eb.addField("Experience Fragrance", Util.formatTime(p.getFragranceXpTimer() - time), true);
		if (p.getFragranceBattleTimer() > time) eb.addField("Battle Fragrance", Util.formatTime(p.getFragranceBattleTimer() - time), true);
		if (p.getFragranceMoneyTimer() > time) eb.addField("Money Fragrance", Util.formatTime(p.getFragranceMoneyTimer() - time), true);
		if (p.getFragranceEggTimer() > time) eb.addField("Egg Fragrance", Util.formatTime(p.getFragranceEggTimer() - time), true);
		
		event.getChannel().sendMessage(eb.build()).complete();
	}
}
