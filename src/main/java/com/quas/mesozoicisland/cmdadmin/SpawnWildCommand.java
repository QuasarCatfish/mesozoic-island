package com.quas.mesozoicisland.cmdadmin;

import java.util.regex.Pattern;

import com.quas.mesozoicisland.battle.SpawnManager;
import com.quas.mesozoicisland.cmdbase.ICommand;
import com.quas.mesozoicisland.enums.AccessLevel;
import com.quas.mesozoicisland.enums.DiscordChannel;
import com.quas.mesozoicisland.enums.DiscordRole;
import com.quas.mesozoicisland.enums.SpawnType;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class SpawnWildCommand implements ICommand {

	@Override
	public Pattern getCommand() {
		return pattern("admin spawn wild");
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
		return "admin spawn wild";
	}

	@Override
	public String getCommandDescription() {
		return "Summons a wild dinosaur battle.";
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
		if (SpawnManager.trySpawn(SpawnType.Wild)) {
			event.getChannel().sendMessageFormat("%s is spawning a wild dinosaur battle.", event.getAuthor().getAsMention()).complete();
		} else {
			event.getChannel().sendMessageFormat("%s, failed to a wild dinosaur battle.", event.getAuthor().getAsMention()).complete();
		}
	}
}
