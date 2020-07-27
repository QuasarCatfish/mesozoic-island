package com.quas.mesozoicisland.cmdplayer;

import java.util.regex.Pattern;

import com.quas.mesozoicisland.MesozoicIsland;
import com.quas.mesozoicisland.cmdbase.ICommand;
import com.quas.mesozoicisland.enums.AccessLevel;
import com.quas.mesozoicisland.enums.DiscordChannel;
import com.quas.mesozoicisland.enums.DiscordRole;
import com.quas.mesozoicisland.util.Util;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class UptimeCommand implements ICommand {

	@Override
	public Pattern getCommand() {
		return pattern("uptime");
	}

	@Override
	public AccessLevel getAccessLevel() {
		return AccessLevel.Trainer;
	}

	@Override
	public String getCommandName() {
		return "uptime";
	}

	@Override
	public String getCommandSyntax() {
		return "uptime";
	}

	@Override
	public String getCommandDescription() {
		return "Gets the uptime of the Mesozoic Island Researchers.";
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
		event.getChannel().sendMessageFormat("%s, the Mesozoic Island bots have been online for %s.", event.getAuthor().getAsMention(), Util.formatTime(MesozoicIsland.getUptime())).complete();
	}
}
