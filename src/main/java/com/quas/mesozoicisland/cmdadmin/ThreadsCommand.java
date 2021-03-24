package com.quas.mesozoicisland.cmdadmin;

import java.util.TreeSet;
import java.util.regex.Pattern;

import com.quas.mesozoicisland.cmdbase.ICommand;
import com.quas.mesozoicisland.enums.AccessLevel;
import com.quas.mesozoicisland.enums.DiscordChannel;
import com.quas.mesozoicisland.enums.DiscordRole;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class ThreadsCommand implements ICommand {

	@Override
	public Pattern getCommand() {
		return pattern("admin threads");
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
		return "admin threads";
	}

	@Override
	public String getCommandDescription() {
		return "Checks the currently active threads.";
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
		TreeSet<String> threads = new TreeSet<String>();
		for (Thread t : Thread.getAllStackTraces().keySet()) {
			threads.add(t.getName());
		}
		
		StringBuilder sb = new StringBuilder();
		sb.append("**Threads:**\n");
		
		int q = 1;
		for (String str : threads) {
			sb.append(String.format("%d) %s\n", q++, str));
		}
		
		event.getChannel().sendMessage(sb.toString()).complete();
	}
}
