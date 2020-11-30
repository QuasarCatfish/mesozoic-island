package com.quas.mesozoicisland.cmdplayer;

import java.util.TreeMap;
import java.util.regex.Pattern;

import com.quas.mesozoicisland.cmdbase.ICommand;
import com.quas.mesozoicisland.enums.AccessLevel;
import com.quas.mesozoicisland.enums.DiscordChannel;
import com.quas.mesozoicisland.enums.DiscordRole;
import com.quas.mesozoicisland.enums.ItemCategory;
import com.quas.mesozoicisland.enums.ItemID;
import com.quas.mesozoicisland.objects.Item;
import com.quas.mesozoicisland.objects.Player;
import com.quas.mesozoicisland.util.Constants;
import com.quas.mesozoicisland.util.Util;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class BagCommand implements ICommand {

	@Override
	public Pattern getCommand() {
		return pattern("bag");
	}

	@Override
	public AccessLevel getAccessLevel() {
		return AccessLevel.Trainer;
	}

	@Override
	public String getCommandName() {
		return "bag";
	}

	@Override
	public String getCommandSyntax() {
		return "bag";
	}

	@Override
	public String getCommandDescription() {
		return "Checks what items you have in your bag.";
	}

	@Override
	public DiscordChannel[] getUsableChannels() {
		return DiscordChannel.STANDARD_CHANNELS_TRADE_DMS;
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
		
		TreeMap<ItemCategory, Integer> map = new TreeMap<ItemCategory, Integer>();
		TreeMap<Item, Long> bag = p.getBag();
		
		for (Item i : bag.keySet()) {
			if (!map.containsKey(i.getItemCategory())) map.put(i.getItemCategory(), 0);
			map.put(i.getItemCategory(), map.get(i.getItemCategory()) + 1);
		}
		
		StringBuilder sb = new StringBuilder();
		Item money = Item.getItem(ItemID.DinosaurCoin);
		sb.append(String.format("%,d %s\n\n**Compartments:**\n", bag.getOrDefault(money, 0L), money.toString(bag.getOrDefault(money, 0L))));
		
		for (ItemCategory ic : map.keySet()) {
			if (ic == ItemCategory.None) continue;
			sb.append(ic);
			sb.append(" - ");
			sb.append(Util.formatNumber(map.get(ic)));
			sb.append(" Item");
			if (map.get(ic) != 1) sb.append("s");
			sb.append("\n");
		}
		
		EmbedBuilder eb = new EmbedBuilder();
		eb.setColor(Constants.COLOR);
		eb.setTitle(p.getName() + "'s Bag");
		eb.setDescription(sb.length() > 0 ? sb.toString() : "You have no items in your bag.");
		event.getChannel().sendMessage(eb.build()).complete();
	}
}
