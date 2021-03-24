package com.quas.mesozoicisland.cmdplayer;

import java.util.ArrayList;
import java.util.regex.Pattern;

import com.quas.mesozoicisland.cmdbase.ICommand;
import com.quas.mesozoicisland.enums.AccessLevel;
import com.quas.mesozoicisland.enums.DiscordChannel;
import com.quas.mesozoicisland.enums.DiscordRole;
import com.quas.mesozoicisland.enums.PingType;
import com.quas.mesozoicisland.util.Util;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class PingmeNoneCommand implements ICommand {

	@Override
	public Pattern getCommand() {
		return pattern("pingme (none|off)");
	}

	@Override
	public AccessLevel getAccessLevel() {
		return AccessLevel.Trainer;
	}

	@Override
	public String getCommandName() {
		return "pingme";
	}

	@Override
	public String getCommandSyntax() {
		return "pingme none";
	}

	@Override
	public String getCommandDescription() {
		return "Disables all ping types.";
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
	public synchronized void run(MessageReceivedEvent event, String... args) {
		ArrayList<String> messages = new ArrayList<String>();
		
		for (PingType ping : PingType.values()) {
			if (Util.doesMemberHaveRole(event.getMember(), ping.getRole().getIdLong())) {
				Util.removeRoleFromMember(event.getMember(), ping.getRole().getIdLong());
				messages.add(ping.getMessage());
			}
		}
		
		if (messages.size() == 0) {
			event.getChannel().sendMessageFormat("%s, you already have all the ping types disabled.", event.getAuthor().getAsMention()).complete();
		} else if (messages.size() == 1) {
			event.getChannel().sendMessageFormat("%s, you will no longer be pinged when %s.", event.getAuthor().getAsMention(), messages.get(0)).complete();
		} else if (messages.size() == 2) {
			event.getChannel().sendMessageFormat("%s, you will no longer be pinged when %s or %s.", event.getAuthor().getAsMention(), messages.get(0), messages.get(1)).complete();
		} else {
			event.getChannel().sendMessageFormat("%s, you will no longer be pinged when %s, or %s.", event.getAuthor().getAsMention(), Util.join(messages, ", ", 0, messages.size() - 1), messages.get(messages.size() - 1)).complete();
		}
	}
}
