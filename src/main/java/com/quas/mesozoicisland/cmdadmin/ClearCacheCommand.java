package com.quas.mesozoicisland.cmdadmin;

import java.util.regex.Pattern;

import com.quas.mesozoicisland.cmdbase.ICommand;
import com.quas.mesozoicisland.enums.AccessLevel;
import com.quas.mesozoicisland.enums.DiscordChannel;
import com.quas.mesozoicisland.enums.DiscordRole;
import com.quas.mesozoicisland.objects.Dinosaur;
import com.quas.mesozoicisland.objects.Element;
import com.quas.mesozoicisland.objects.Item;
import com.quas.mesozoicisland.objects.Rarity;
import com.quas.mesozoicisland.objects.Rune;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class ClearCacheCommand implements ICommand {

	@Override
	public Pattern getCommand() {
		return pattern("admin clear cache");
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
		return "admin clear cache";
	}

	@Override
	public String getCommandDescription() {
		return "Clears the cache of stored values.";
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
	public synchronized void run(MessageReceivedEvent event, String... args) {
		Dinosaur.refresh();
		Element.refresh();
		Item.refresh();
		Rarity.refresh();
		Rune.refresh();
		event.getChannel().sendMessageFormat("%s, the cache has been cleared.", event.getAuthor().getAsMention()).complete();
	}
}
