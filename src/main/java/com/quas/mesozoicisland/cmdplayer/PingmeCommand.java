package com.quas.mesozoicisland.cmdplayer;

import java.util.regex.Pattern;

import com.quas.mesozoicisland.cmdbase.ICommand;
import com.quas.mesozoicisland.enums.AccessLevel;
import com.quas.mesozoicisland.enums.DiscordChannel;
import com.quas.mesozoicisland.enums.DiscordRole;
import com.quas.mesozoicisland.enums.PingType;
import com.quas.mesozoicisland.util.Util;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class PingmeCommand implements ICommand {

	@Override
	public Pattern getCommand() {
		return pattern("pingme ", ALPHA);
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
		return "pingme <ping type>";
	}

	@Override
	public String getCommandDescription() {
		return "Toggles whether or not you receive a specific ping.\nCurrent ping types: " + PingType.listValues() + ".";
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
		PingType ping = PingType.of(args[0]);
		
		if (ping == null) {
			event.getChannel().sendMessageFormat("%s, the ping type \"%s\" does not exist.", event.getAuthor().getAsMention(), args[0].toLowerCase()).complete();
		} else if (Util.doesMemberHaveRole(event.getMember(), ping.getRole().getIdLong())) {
			Util.removeRoleFromMember(event.getMember(), ping.getRole().getIdLong());
			event.getChannel().sendMessageFormat("%s, you will no longer be pinged when %s.", event.getAuthor().getAsMention(), ping.getMessage()).complete();
		} else {
			Util.addRoleToMember(event.getMember(), ping.getRole().getIdLong());
			event.getChannel().sendMessageFormat("%s, you will now be pinged when %s.", event.getAuthor().getAsMention(), ping.getMessage()).complete();
		}
	}
}
