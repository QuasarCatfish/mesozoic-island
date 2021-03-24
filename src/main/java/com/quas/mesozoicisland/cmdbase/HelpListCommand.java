package com.quas.mesozoicisland.cmdbase;

import java.util.TreeSet;
import java.util.regex.Pattern;

import com.quas.mesozoicisland.enums.AccessLevel;
import com.quas.mesozoicisland.enums.DiscordChannel;
import com.quas.mesozoicisland.enums.DiscordRole;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class HelpListCommand implements ICommand {

	@Override
	public Pattern getCommand() {
		return pattern("help");
	}

	@Override
	public AccessLevel getAccessLevel() {
		return AccessLevel.Trainer;
	}

	@Override
	public String getCommandName() {
		return "help";
	}

	@Override
	public String getCommandSyntax() {
		return "help";
	}

	@Override
	public String getCommandDescription() {
		return "Gets a list of all commands.";
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
		TreeSet<String> commands = new TreeSet<String>();
		for (ICommand command : CommandManager.values()) {
			if (command.getCommandName() == null || command.getCommandDescription() == null) continue;
			
			if (command.canBeUsed(event)) {
				commands.add(command.getCommandName());
			}
		}
		
		StringBuilder sb = new StringBuilder();
		sb.append("__**Available Commands:**__\n");
		for (String cmd : commands) {
			sb.append("`");
			sb.append(cmd);
			sb.append("`, ");
		}
		sb.setLength(sb.length() - 2);
		sb.append(".\nYou can use `help <command>` to learn more about a specific command.");
		
		event.getChannel().sendMessage(sb.toString()).complete();
	}
}
