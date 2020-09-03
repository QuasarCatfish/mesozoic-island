package com.quas.mesozoicisland.cmdplayer;

import java.util.regex.Pattern;

import com.quas.mesozoicisland.JDBC;
import com.quas.mesozoicisland.cmdbase.ICommand;
import com.quas.mesozoicisland.enums.AccessLevel;
import com.quas.mesozoicisland.enums.DiscordChannel;
import com.quas.mesozoicisland.enums.DiscordRole;
import com.quas.mesozoicisland.enums.ItemID;
import com.quas.mesozoicisland.objects.Element;
import com.quas.mesozoicisland.objects.Item;
import com.quas.mesozoicisland.objects.Player;
import com.quas.mesozoicisland.util.Pair;
import com.quas.mesozoicisland.util.Util;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class GuildJoinCommand implements ICommand {

	@Override
	public Pattern getCommand() {
		return pattern("guild join [a-z]+");
	}

	@Override
	public AccessLevel getAccessLevel() {
		return AccessLevel.Trainer;
	}

	@Override
	public String getCommandName() {
		return "guild";
	}

	@Override
	public String getCommandSyntax() {
		return "guild join <guild>";
	}

	@Override
	public String getCommandDescription() {
		return "Joins the given guild.\nGuilds: `Fire`, `Lightning`, `Leaf`, `Earth`, `Ice`, `Metal`, `Water`, `Air`.";
	}

	@Override
	public DiscordChannel[] getUsableChannels() {
		return DiscordChannel.STANDARD_CHANNELS;
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
		
		if (p.getMainElement() != null && p.getMainElement().getId() > 0) {
			event.getChannel().sendMessageFormat("%s, you are already in the %s Guild.", p.getAsMention(), p.getMainElement().getName()).complete();
			return;
		}
		
		Item badge = Item.getItem(ItemID.GuildBadge);
		long count = p.getBag().getOrDefault(badge, 0L);
		
		if (count <= 0) {
			event.getChannel().sendMessageFormat("%s, you do not have %s %s.", p.getAsMention(), Util.getArticle(badge.toString()), badge.toString()).complete();
		} else {
			Element e = getElement(args[1]);
			if (e == null) {
				event.getChannel().sendMessageFormat("%s, \"%s\" is an invalid guild.", p.getAsMention(), args[1]).complete();
			} else {
				event.getChannel().sendMessageFormat("%s, you have joined the %s Guild!", p.getAsMention(), e.getName()).complete();
				JDBC.executeUpdate("update players set mainelement = %d where playerid = %d;", e.getId(), p.getIdLong());
				Util.addRoleToMember(event.getMember(), e.getRole());
				JDBC.addItem(p.getIdLong(), ItemID.GuildBadge.getId(), -1);
				JDBC.addItem(p.getIdLong(), new Pair<Integer, Long>(ItemID.GuildBadge.getItemId(), (long)e.getId()));
				event.getGuild().getTextChannelById(e.getGuild()).sendMessageFormat("Welcome %s to the %s Guild!", p.getAsMention(), e.getName()).complete();
			}
		}
	}
	
	private Element getElement(String element) {
		switch (element.toLowerCase()) {
		case "fire": return Element.of(2);
		case "lightning": return Element.of(4);
		case "leaf": return Element.of(8);
		case "earth": return Element.of(16);
		case "ice": return Element.of(32);
		case "metal": return Element.of(64);
		case "water": return Element.of(128);
		case "air": return Element.of(256);
		default: return null;
		}
	}
}
