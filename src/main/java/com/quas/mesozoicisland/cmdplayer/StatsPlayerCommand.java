package com.quas.mesozoicisland.cmdplayer;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.StringJoiner;
import java.util.TreeMap;
import java.util.regex.Pattern;

import com.quas.mesozoicisland.cmdbase.ICommand;
import com.quas.mesozoicisland.enums.AccessLevel;
import com.quas.mesozoicisland.enums.CustomPlayer;
import com.quas.mesozoicisland.enums.DiscordChannel;
import com.quas.mesozoicisland.enums.DiscordRole;
import com.quas.mesozoicisland.objects.Item;
import com.quas.mesozoicisland.objects.Player;
import com.quas.mesozoicisland.util.Constants;
import com.quas.mesozoicisland.util.DinoMath;
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
	public synchronized void run(MessageReceivedEvent event, String... args) {
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
		
		StringJoiner xpField = new StringJoiner("\n");
		xpField.add(String.format("Level %,d + %,d XP", p.getLevel(), p.getXpMinusLevel()));
		if (p.isOmega()) xpField.add(String.format("%s Level %,d + %,d %sXP", Constants.OMEGA, p.getOmegaLevel(), p.getOmegaXpMinusLevel(), Constants.OMEGA));
		if (!p.isMaxLevel()) xpField.add(String.format("(%,d XP until Level Up)", DinoMath.getXp(p.getLevel() + 1) - p.getXp()));
		else xpField.add(String.format("(%,d %sXP until next %s Level Up)", DinoMath.getOmegaXp(p.getOmegaLevel() + 1) - p.getOmegaXp(), Constants.OMEGA, Constants.OMEGA));
		
		eb.addField("__Join Date__", p.getJoinDate(), true);
		eb.addField("__Trainer Level and XP__", xpField.toString(), true);
		eb.addField("__Guild and Emblems__", String.format("Guild: %s\nEmblems: %s", p.getMainElement(), p.getSubElement()), true);
		
		if (event.getChannel().getIdLong() == DiscordChannel.Game.getIdLong()) {
			eb.setDescription("Use this command in " + DiscordChannel.BotCommands.toString() + " or DMs for a full list of stats.");
		} else {
			TreeMap<String, String> map = new TreeMap<String, String>();
			String[] keys = new String[] {"Dailies", "Battles", "Damage", "Dungeons", "Raids", "Dinosaurs", "Dinosaur Coins", "Eggs", "InfiniDungeon", "Chaos Dungeons", "Miscellaneous"};
			for (String key : keys) map.put(key, "");

			for (Item i : items) {
				if (Integer.parseInt(i.getData()) < 0) continue;

				for (String key : keys) {
					if (i.toString(2).startsWith(key)) {
						map.put(key, map.get(key) + String.format("%,d %s\n", bag.getOrDefault(i, 0L), i.toString(2).replace(key + " ", "")));
						break;
					} else if (key.equals(keys[keys.length - 1])) {
						map.put(key, map.get(key) + String.format("%,d %s\n", bag.getOrDefault(i, 0L), i.toString(2)));
						break;
					}
				}
			}

			map.put(keys[0], map.get(keys[0]) + String.format("%,d-Day Streak", p.getDailyStreak()));

			for (String key : keys) {
				eb.addField("__" + key + "__", map.get(key), true);
			}
		}

		long time = System.currentTimeMillis();
		StringBuilder sb = new StringBuilder();
		if (p.getFragranceXpTimer() > time) sb.append("Experience - " + Util.formatTime(p.getFragranceXpTimer() - time) + "\n");
		if (p.getFragranceBattleTimer() > time) sb.append("Battle - " + Util.formatTime(p.getFragranceBattleTimer() - time) + "\n");
		if (p.getFragranceMoneyTimer() > time) sb.append("Money - " + Util.formatTime(p.getFragranceMoneyTimer() - time) + "\n");
		if (p.getFragranceEggTimer() > time) sb.append("Egg - " + Util.formatTime(p.getFragranceEggTimer() - time) + "\n");
		if (sb.length() > 0) eb.addField("__Fragrance__", sb.toString(), true);
		
		event.getChannel().sendMessage(eb.build()).complete();
	}
}
