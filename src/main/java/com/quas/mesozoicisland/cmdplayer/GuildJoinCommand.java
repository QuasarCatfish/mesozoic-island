package com.quas.mesozoicisland.cmdplayer;

import java.util.regex.Pattern;

import com.quas.mesozoicisland.JDBC;
import com.quas.mesozoicisland.cmdbase.ICommand;
import com.quas.mesozoicisland.enums.AccessLevel;
import com.quas.mesozoicisland.enums.DiscordChannel;
import com.quas.mesozoicisland.enums.DiscordRole;
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
		return "Joins the given guild.";
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
		
		Item token = Item.getItem(new Pair<Integer, Long>(2, 0L));
		long count = p.getBag().getOrDefault(token, 0L);
		
		if (count <= 0) {
			event.getChannel().sendMessageFormat("%s, you do not have %s %s.", p.getAsMention(), Util.getArticle(token.toString()), token.toString()).complete();
		} else {
			Element e = getElement(args[1]);
			if (e == null) {
				event.getChannel().sendMessageFormat("%s, \"%s\" is an invalid guild.", p.getAsMention(), args[1]).complete();
			} else {
				event.getChannel().sendMessageFormat("%s, you have joined the %s Guild!", p.getAsMention(), e.getName()).complete();
				JDBC.executeUpdate("update players set mainelement = %d where playerid = %d;", e.getId(), p.getIdLong());
				Util.addRoleToMember(event.getMember(), e.getRole());
				JDBC.addItem(p.getIdLong(), new Pair<Integer, Long>(2, 0L), -1);
				JDBC.addItem(p.getIdLong(), new Pair<Integer, Long>(2, (long)e.getId()));
				event.getGuild().getTextChannelById(e.getGuild()).sendMessageFormat("Welcome %s to the %s Guild!", p.getAsMention(), e.getName()).complete();
			}
		}
	}
	
	private Element getElement(String element) {
		switch (element) {
		case "elementa": return Element.of(2);
		case "elementb": return Element.of(4);
		case "elementc": return Element.of(8);
		case "elementd": return Element.of(16);
		case "elemente": return Element.of(32);
		case "elementf": return Element.of(64);
		case "elementg": return Element.of(128);
		case "elementh": return Element.of(256);
		default: return null;
		}
	}
}
