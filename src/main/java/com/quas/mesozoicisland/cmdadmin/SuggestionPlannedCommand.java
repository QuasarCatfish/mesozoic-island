package com.quas.mesozoicisland.cmdadmin;

import java.util.regex.Pattern;

import com.quas.mesozoicisland.enums.SuggestionStatus;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class SuggestionPlannedCommand extends SuggestionUpdateStatusCommand {

	@Override
	public Pattern getCommand() {
		return pattern("suggestion planned ", INTEGER);
	}

	@Override
	public String getCommandSyntax() {
		return "suggestion planned <id>";
	}

	@Override
	public String getCommandDescription() {
		return "Sets the suggestion's status as Planned.";
	}
	
	@Override
	public synchronized void run(MessageReceivedEvent event, String... args) {
		doUpdate(event, SuggestionStatus.Planned, args);
	}
}
