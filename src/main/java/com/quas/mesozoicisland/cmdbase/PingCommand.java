package com.quas.mesozoicisland.cmdbase;

import java.util.regex.Pattern;

import com.quas.mesozoicisland.enums.AccessLevel;
import com.quas.mesozoicisland.enums.DiscordChannel;
import com.quas.mesozoicisland.enums.DiscordRole;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class PingCommand implements ICommand {

	@Override
	public Pattern getCommand() {
		return pattern("ping");
	}

	@Override
	public AccessLevel getAccessLevel() {
		return AccessLevel.Moderator;
	}

	@Override
	public String getCommandName() {
		return "ping";
	}

	@Override
	public String getCommandSyntax() {
		return "ping";
	}

	@Override
	public String getCommandDescription() {
		return "Checks the current ping.";
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
		event.getChannel().sendMessageFormat("Pong, %s! (%,d ms)", event.getAuthor().getAsMention(), event.getJDA().getRestPing().complete()).complete();
	}
}
