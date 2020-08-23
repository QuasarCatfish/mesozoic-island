package com.quas.mesozoicisland.cmdplayer;

import java.util.TreeMap;
import java.util.regex.Pattern;

import com.quas.mesozoicisland.cmdbase.ICommand;
import com.quas.mesozoicisland.enums.AccessLevel;
import com.quas.mesozoicisland.enums.DiscordChannel;
import com.quas.mesozoicisland.enums.DiscordRole;
import com.quas.mesozoicisland.enums.ItemCategory;
import com.quas.mesozoicisland.objects.Item;
import com.quas.mesozoicisland.objects.Player;
import com.quas.mesozoicisland.util.Constants;
import com.quas.mesozoicisland.util.Util;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class BagCategoryCommand implements ICommand {

	@Override
	public Pattern getCommand() {
		return pattern("bag .+");
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
		return "bag <compartment>";
	}

	@Override
	public String getCommandDescription() {
		return "Checks the items in one of your bag's compartments.";
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
		
		ItemCategory cat = ItemCategory.of(Util.join(args, " ", 0, args.length));
		TreeMap<Item, Long> bag = p.getBag();

		if (cat == null) {
			Item item = Item.of(Util.join(args, " ", 0, args.length));
			if (!item.isDiscovered()) item = null;

			if (item != null) {
				StringBuilder sb = new StringBuilder();

				for (Item i : Item.getItems(item.getId())) {
					long count = bag.getOrDefault(i, 0L);
					if (count <= 0) continue;

					if (bag.get(i) != 1 || (cat != ItemCategory.KeyItems && cat != ItemCategory.Titles)) {
						sb.append(Util.formatNumber(bag.get(i)));
						sb.append(" ");
					}
					sb.append(i.toString(bag.get(i)));
					sb.append(" (ID ");
					sb.append(i.getId());
					sb.append(")\n");
				}

				EmbedBuilder eb = new EmbedBuilder();
				eb.setColor(Constants.COLOR);
				eb.setTitle(p.getName() + "'s Bag - " + item.toString(2));
				eb.setDescription(sb.length() == 0 ? "You do not have any of this item." : sb.toString());
				event.getChannel().sendMessage(eb.build()).complete();
				return;
			}
		}
		
		
		StringBuilder sb = new StringBuilder();
		for (Item i : bag.keySet()) {
			if (i.getItemCategory() != cat) continue;
			if (bag.get(i) != 1 || (cat != ItemCategory.KeyItems && cat != ItemCategory.Titles)) {
				sb.append(Util.formatNumber(bag.get(i)));
				sb.append(" ");
			}
			sb.append(i.toString(bag.get(i)));
			sb.append(" (ID ");
			sb.append(i.getId());
			sb.append(")\n");
		}
		
		EmbedBuilder eb = new EmbedBuilder();
		eb.setColor(Constants.COLOR);
		eb.setTitle(p.getName() + "'s Bag - " + cat + " Compartment");
		eb.setDescription(sb.length() == 0 ? "You have no items in this compartment." : sb.toString());
		event.getChannel().sendMessage(eb.build()).complete();
	}
}
