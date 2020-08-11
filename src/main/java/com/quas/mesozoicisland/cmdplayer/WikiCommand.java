package com.quas.mesozoicisland.cmdplayer;

import java.util.regex.Pattern;

import com.quas.mesozoicisland.cmdbase.ICommand;
import com.quas.mesozoicisland.enums.AccessLevel;
import com.quas.mesozoicisland.enums.DiscordChannel;
import com.quas.mesozoicisland.enums.DiscordRole;
import com.quas.mesozoicisland.util.Util;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class WikiCommand implements ICommand {

	@Override
	public Pattern getCommand() {
		return pattern("wiki( .+)?");
	}

	@Override
	public AccessLevel getAccessLevel() {
		return AccessLevel.Trainer;
	}

	@Override
	public String getCommandName() {
		return "wiki";
	}

	@Override
	public String getCommandSyntax() {
		return "wiki <search>";
	}

	@Override
	public String getCommandDescription() {
		return "Searches for the given page on the Mesozoic Island wiki.";
	}

	@Override
	public DiscordChannel[] getUsableChannels() {
		return Util.arr(DiscordChannel.Wiki, DiscordChannel.GameTesting, DiscordChannel.DirectMessages);
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
		if (args.length == 0) {
			event.getChannel().sendMessageFormat("%s, here's your link to the wiki!\n<https://mesozoic-island.amazingwikis.org/wiki/Main_Page>", event.getAuthor().getAsMention()).complete();
		} else {
			event.getChannel().sendMessageFormat("%s, here's your link to the wiki!\n<https://mesozoic-island.amazingwikis.org/wiki/index.php?search=%s>", event.getAuthor().getAsMention(), Util.join(args, "+", 0, args.length)).complete();
		}
	}
}