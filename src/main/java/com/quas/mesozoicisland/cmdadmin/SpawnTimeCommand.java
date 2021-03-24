package com.quas.mesozoicisland.cmdadmin;

import java.util.regex.Pattern;

import com.quas.mesozoicisland.battle.SpawnManager;
import com.quas.mesozoicisland.cmdbase.ICommand;
import com.quas.mesozoicisland.enums.AccessLevel;
import com.quas.mesozoicisland.enums.DiscordChannel;
import com.quas.mesozoicisland.enums.DiscordRole;
import com.quas.mesozoicisland.util.Util;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class SpawnTimeCommand implements ICommand {

	@Override
	public Pattern getCommand() {
		return pattern("admin time");
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
		return "admin time";
	}

	@Override
	public String getCommandDescription() {
		return "Checks how much time until the next spawn.";
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
		long time = SpawnManager.getSpawnTime();
		
		if (time <= System.currentTimeMillis()) {
			event.getChannel().sendMessageFormat("%s, the spawn will happen now.", event.getAuthor().getAsMention()).complete();
		} else if (time == Long.MAX_VALUE) {
			event.getChannel().sendMessageFormat("%s, there is currently a spawn.", event.getAuthor().getAsMention()).complete();
		} else {
			event.getChannel().sendMessageFormat("%s, the spawn will happen in %s.", event.getAuthor().getAsMention(), Util.formatTime(time - System.currentTimeMillis())).complete();
		}
	}
}
