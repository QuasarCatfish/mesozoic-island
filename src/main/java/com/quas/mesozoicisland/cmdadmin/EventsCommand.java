package com.quas.mesozoicisland.cmdadmin;

import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

import com.quas.mesozoicisland.cmdbase.ICommand;
import com.quas.mesozoicisland.enums.AccessLevel;
import com.quas.mesozoicisland.enums.DiscordChannel;
import com.quas.mesozoicisland.enums.DiscordRole;
import com.quas.mesozoicisland.objects.Event;
import com.quas.mesozoicisland.util.Util;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class EventsCommand implements ICommand {

	@Override
	public Pattern getCommand() {
		return pattern("admin events");
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
		return "admin events";
	}

	@Override
	public String getCommandDescription() {
		return "Checks the registered events.";
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
		StringBuilder sb = new StringBuilder();
		sb.append("**Events:**\n");
		
		for (Event e : Event.values()) {
			// Skip any events that are more than 14 days past
			if (e.hasEnded() && System.currentTimeMillis() - e.getEndTime() >= TimeUnit.DAYS.toMillis(14)) continue;

			sb.append(String.format("[%s] %s ", e.getEventType().toString(), e.getName()));

			if (e.hasEnded()) {
				sb.append(String.format("ended %s ago.\n", Util.formatTime(System.currentTimeMillis() - e.getEndTime())));
			} else if (e.isRunning()) {
				sb.append(String.format("ends in %s.\n", Util.formatTime(e.getEndTime() - System.currentTimeMillis())));
			} else {
				sb.append(String.format("starts in %s.\n", Util.formatTime(e.getStartTime() - System.currentTimeMillis())));
			}
		}
		
		event.getChannel().sendMessage(sb.toString()).complete();
	}
}
