package com.quas.mesozoicisland.cmdplayer;

import java.util.regex.Pattern;

import com.quas.mesozoicisland.enums.DiscordChannel;
import com.quas.mesozoicisland.util.Util;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class TopAllCommand extends LeaderboardCommand {

	public TopAllCommand() {
		super(true);
	}

	@Override
	public Pattern getCommand() {
		return pattern("(top|leaderboard)all( ", ALPHA, ")*");
	}

	@Override
	public String getCommandSyntax() {
		return "topall [category]";
	}

	@Override
	public String getCommandDescription() {
		return "Lists all members of a particular category.";
	}

	@Override
	public DiscordChannel[] getUsableChannels() {
		return Util.arr(DiscordChannel.DirectMessages);
	}

	@Override
	public synchronized void run(MessageReceivedEvent event, String... args) {
		super.run(event, args);
	}
}
