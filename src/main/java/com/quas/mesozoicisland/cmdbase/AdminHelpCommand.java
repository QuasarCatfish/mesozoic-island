package com.quas.mesozoicisland.cmdbase;

import java.util.ArrayList;
import java.util.regex.Pattern;

import com.quas.mesozoicisland.enums.AccessLevel;
import com.quas.mesozoicisland.enums.DiscordChannel;
import com.quas.mesozoicisland.enums.DiscordRole;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class AdminHelpCommand implements ICommand {

	@Override
	public Pattern getCommand() {
		return pattern("admin help " + ALPHA);
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
		return "admin help <command>";
	}

	@Override
	public String getCommandDescription() {
		return "Gets regex information on a specific command.";
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
		StringBuilder sb = new StringBuilder();
		ArrayList<ICommand> help = new ArrayList<ICommand>();
		
		loop:
		for (ICommand command : CommandManager.values()) {
			if (command.getCommandName() == null) continue;
			if (command.getCommandDescription() == null) continue;
			
			// Check if an instance of this command is already in the list
			for (ICommand c : help) {
				if (c.getCommandName() == null) continue;
				if (c.getCommandDescription() == null) continue;
				if (c.getCommandDescription().contentEquals(command.getCommandDescription())) {
					continue loop;
				}
			}
			
			// Add command to the list
			if (command.getCommandName().equalsIgnoreCase(args[1]) && command.canBeUsed(event)) {
				help.add(command);
				sb.append("� `");
				sb.append(command.getCommandSyntax());
				sb.append("`: `");
				sb.append(command.getCommand().pattern());
				sb.append("`\n");
			}
		}
		
		if (sb.length() == 0) {
			event.getChannel().sendMessageFormat("%s, I could not find the \"%s\" command.", event.getAuthor().getAsMention(), args[1]).complete();
		} else {
			event.getChannel().sendMessageFormat("__Command Information:__\n%s", sb.toString()).complete();
		}
	}
}
