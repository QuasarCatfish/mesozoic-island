package com.quas.mesozoicisland.cmdplayer;

import java.util.regex.Pattern;

import com.quas.mesozoicisland.JDBC;
import com.quas.mesozoicisland.cmdbase.ICommand;
import com.quas.mesozoicisland.enums.AccessLevel;
import com.quas.mesozoicisland.enums.DinosaurForm;
import com.quas.mesozoicisland.enums.DiscordChannel;
import com.quas.mesozoicisland.enums.DiscordRole;
import com.quas.mesozoicisland.util.Constants;
import com.quas.mesozoicisland.util.MesozoicCalendar;
import com.quas.mesozoicisland.util.RomanNumeral;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class StatsServerCommand implements ICommand {

	@Override
	public Pattern getCommand() {
		return pattern("stats server");
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
		return "stats server";
	}

	@Override
	public String getCommandDescription() {
		return "Gives stats about the Mesozoic Island server and game.";
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
		eb.setTitle("Mesozoic Island Statistics");
		eb.setColor(Constants.COLOR);
		eb.addField("Total Players", String.format("%,d Players", JDBC.getPlayerCount()), true);
		eb.addField("Active Players", String.format("%,d Players", JDBC.getActivePlayerCount()), true);
		eb.addField("Collectable Dinosaurs", String.format("%,d Dinosaurs", JDBC.getDexCount(DinosaurForm.AllForms.getId())), true);
		eb.addField("Bot Line Count", String.format("%,d Lines", Constants.CODE_LINES), true);
		eb.addField("Team Size", String.format("%,d Dinosaurs", Constants.DINOS_PER_TEAM), true);
		eb.addField("Player Level Limit", String.format("Level %,d", Constants.MAX_PLAYER_LEVEL), true);
		eb.addField("Dinosaur Level Limit", String.format("Level %,d", Constants.MAX_DINOSAUR_LEVEL), true);
		eb.addField("Rank Limit", String.format("Rank %s", RomanNumeral.of(Constants.MAX_RANK)), true);
		eb.addField("Stat Boost Limit", String.format("+%,d%% per Stat", Constants.MAX_STAT_BOOST), true);
		eb.addField("Current Time", String.format("%tR UTC", new MesozoicCalendar()), true);
//		eb.addField("", String.format(""), true);
//		eb.addField("", String.format(""), true);
		
		event.getChannel().sendMessage(eb.build()).complete();
	}
}
