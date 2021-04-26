package com.quas.mesozoicisland.cmdplayer;

import java.util.regex.Pattern;

import com.quas.mesozoicisland.cmdbase.ICommand;
import com.quas.mesozoicisland.enums.AccessLevel;
import com.quas.mesozoicisland.enums.DiscordChannel;
import com.quas.mesozoicisland.enums.DiscordRole;
import com.quas.mesozoicisland.objects.Event;
import com.quas.mesozoicisland.util.Util;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class ListEventsCommand implements ICommand {

	@Override
	public Pattern getCommand() {
		return pattern("events?");
	}

	@Override
	public AccessLevel getAccessLevel() {
		return AccessLevel.Trainer;
	}

	@Override
	public String getCommandName() {
		return "events";
	}

	@Override
	public String getCommandSyntax() {
		return "events";
	}

	@Override
	public String getCommandDescription() {
		return "Lists the currently running events.";
	}

	@Override
	public DiscordChannel[] getUsableChannels() {
		return DiscordChannel.STANDARD_CHANNELS_DMS;
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
		Event[] events = Event.values();
		
		int count = 0;
		for (Event e : events) {
			if (e.isRunning()) {
				count++;
			}
		}
		
		if (count == 0) {
			event.getChannel().sendMessageFormat("%s, there are no events, currently.", event.getAuthor().getAsMention()).complete();
		} else {
			StringBuilder sb = new StringBuilder();
			sb.append(event.getAuthor().getAsMention());
			sb.append(", here are the events currently running:\n");
			
			for (Event e : events) {
				if (!e.isRunning()) continue;
				sb.append("**");
				sb.append(e.getName());
				sb.append("** - Ends in ");
				sb.append(Util.formatTime(e.getEndTime() - System.currentTimeMillis()));
				sb.append("\n");
			}
			
			event.getChannel().sendMessage(sb.toString()).complete();
		}
	}
}
