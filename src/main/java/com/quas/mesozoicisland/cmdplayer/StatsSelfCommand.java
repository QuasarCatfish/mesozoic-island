package com.quas.mesozoicisland.cmdplayer;

import java.util.regex.Pattern;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class StatsSelfCommand extends StatsPlayerCommand {

	@Override
	public Pattern getCommand() {
		return pattern("stats");
	}

	@Override
	public String getCommandSyntax() {
		return "stats";
	}

	@Override
	public String getCommandDescription() {
		return "Gets your stats.";
	}

	@Override
	public synchronized void run(MessageReceivedEvent event, String... args) {
		super.run(event, event.getAuthor().getId());
	}
}
