package com.quas.mesozoicisland.cmdplayer;

import java.util.regex.Pattern;

import com.quas.mesozoicisland.JDBC;
import com.quas.mesozoicisland.battle.Battle;
import com.quas.mesozoicisland.cmdbase.ICommand;
import com.quas.mesozoicisland.enums.AccessLevel;
import com.quas.mesozoicisland.enums.CustomPlayer;
import com.quas.mesozoicisland.enums.DiscordChannel;
import com.quas.mesozoicisland.enums.DiscordRole;
import com.quas.mesozoicisland.enums.EventType;
import com.quas.mesozoicisland.objects.Event;
import com.quas.mesozoicisland.util.Constants;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class StatsEventCommand implements ICommand {

	@Override
	public Pattern getCommand() {
		return pattern("stats events?");
	}

	@Override
	public AccessLevel getAccessLevel() {
		return AccessLevel.Trainer;
	}

	@Override
	public String getCommandName() {
		return "stats";
	}

	@Override
	public String getCommandSyntax() {
		return "stats event";
	}

	@Override
	public String getCommandDescription() {
		return "Gives stats about the currently active events.";
	}

	@Override
	public DiscordChannel[] getUsableChannels() {
		return DiscordChannel.STANDARD_CHANNELS;
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
		EmbedBuilder eb = new EmbedBuilder();
		eb.setTitle("Event Statistics");
		eb.setColor(Constants.COLOR);

		if (Event.isEventActive(EventType.DarknessDescent)) {
			if (Battle.isPlayerBattling(CustomPlayer.Dungeon.getIdLong())) {
				eb.addField("Floors Cleared", "???", true);
				eb.addField("Expeditions Failed", "???", true);
			} else {
				eb.addField("Floors Cleared", String.format("%,d", Integer.parseInt(JDBC.getVariable(Constants.EVENT_DARKNESS_DESCENT_FLOORS))), true);
				eb.addField("Expeditions Failed", String.format("%,d", Integer.parseInt(JDBC.getVariable(Constants.EVENT_DARKNESS_DESCENT_LOSSES))), true);
			}
		}
		
		event.getChannel().sendMessage(eb.build()).complete();
	}
}
