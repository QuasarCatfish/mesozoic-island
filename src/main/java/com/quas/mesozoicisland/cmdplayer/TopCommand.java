package com.quas.mesozoicisland.cmdplayer;

import java.util.regex.Pattern;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class TopCommand extends LeaderboardCommand {

	public TopCommand() {
		super(false);
	}

	@Override
	public Pattern getCommand() {
		return pattern("(top|leaderboard)( ", ALPHA, ")*");
	}

	@Override
	public String getCommandSyntax() {
		return "top [category]";
	}

	@Override
	public String getCommandDescription() {
		return "Lists the top members of a particular category.\nCurrent Categories: " + LeaderboardCategory.listValues();
	}

	@Override
	public synchronized void run(MessageReceivedEvent event, String... args) {
		super.run(event, args);
	}
}
