package com.quas.mesozoicisland.cmdbase;

import java.util.regex.Pattern;

import com.quas.mesozoicisland.MesozoicIsland;
import com.quas.mesozoicisland.enums.AccessLevel;
import com.quas.mesozoicisland.enums.DiscordChannel;
import com.quas.mesozoicisland.enums.DiscordRole;
import com.quas.mesozoicisland.util.Constants;
import com.quas.mesozoicisland.util.Util;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class QuitCommand implements ICommand {

	@Override
	public Pattern getCommand() {
		return pattern("quit");
	}

	@Override
	public AccessLevel getAccessLevel() {
		return AccessLevel.Admin;
	}

	@Override
	public String getCommandName() {
		return "quit";
	}

	@Override
	public String getCommandSyntax() {
		return "quit";
	}

	@Override
	public String getCommandDescription() {
		return "Tells the bots to go to sleep.";
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
		MesozoicIsland.setQuit(true);
		event.getChannel().sendMessage(Util.getRandomElement(Constants.GOODBYE_MESSAGES)).complete();
		System.exit(0);
	}
}
