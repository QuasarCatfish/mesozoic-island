package com.quas.mesozoicisland.cmdadmin;

import java.util.List;
import java.util.regex.Pattern;

import com.quas.mesozoicisland.cmdbase.ICommand;
import com.quas.mesozoicisland.enums.AccessLevel;
import com.quas.mesozoicisland.enums.DiscordChannel;
import com.quas.mesozoicisland.enums.DiscordRole;
import com.quas.mesozoicisland.util.Util;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class ClearChannelCommand implements ICommand {

	@Override
	public Pattern getCommand() {
		return pattern("admin clear channel");
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
		return "admin clear channel";
	}

	@Override
	public String getCommandDescription() {
		return "Clears the previous 100 messages in the current channel.";
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
		List<Message> messages = Util.getMessages(event.getChannel());
		for (Message msg : messages) {
			if (msg.isPinned()) continue;
			msg.delete().complete();
			Util.sleep(2500);
		}
	}
}
