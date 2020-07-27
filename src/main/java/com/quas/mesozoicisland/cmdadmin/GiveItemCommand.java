package com.quas.mesozoicisland.cmdadmin;

import java.util.regex.Pattern;

import com.quas.mesozoicisland.JDBC;
import com.quas.mesozoicisland.cmdbase.ICommand;
import com.quas.mesozoicisland.enums.AccessLevel;
import com.quas.mesozoicisland.enums.DiscordChannel;
import com.quas.mesozoicisland.enums.DiscordRole;
import com.quas.mesozoicisland.enums.ItemCategory;
import com.quas.mesozoicisland.objects.Item;
import com.quas.mesozoicisland.objects.Player;
import com.quas.mesozoicisland.util.Pair;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class GiveItemCommand implements ICommand {

	@Override
	public Pattern getCommand() {
		return pattern("admin give i(tem)? ", PLAYER, " ", INTEGER, " ", LONG, "( ", LONG, ")?");
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
		return "admin give item @player <item> <meta> [count]";
	}

	@Override
	public String getCommandDescription() {
		return "Gives an item to a player.";
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
		long pid = Long.parseLong(args[2].replaceAll("[^\\d]", ""));
		Player p = Player.getPlayer(pid);
		if (p == null) {
			event.getChannel().sendMessageFormat("%s, this is an invalid player.", event.getAuthor().getAsMention()).complete();
			return;
		}
		
		Item item = Item.getItem(new Pair<Integer, Long>(Integer.parseInt(args[3]), Long.parseLong(args[4])));
		long count = args.length > 5 ? Long.parseLong(args[5]) : 1;
		if (item == null) {
			event.getChannel().sendMessageFormat("%s, no such item exists.", event.getAuthor().getAsMention()).complete();
		} else {
			JDBC.addItem(pid, item.getIdDmg(), count);
			if (item.getItemCategory() == ItemCategory.Titles) {
				event.getChannel().sendMessageFormat("<@%d>, you have been given the %s.", pid, item.toString(count)).complete();
			} else {
				event.getChannel().sendMessageFormat("<@%d>, you have been given %,d %s.", pid, count, item.toString(count)).complete();
			}
		}
	}
}
