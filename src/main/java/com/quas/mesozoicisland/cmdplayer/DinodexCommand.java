package com.quas.mesozoicisland.cmdplayer;

import java.util.ArrayList;
import java.util.regex.Pattern;

import com.quas.mesozoicisland.JDBC;
import com.quas.mesozoicisland.cmdbase.ICommand;
import com.quas.mesozoicisland.enums.AccessLevel;
import com.quas.mesozoicisland.enums.DinosaurForm;
import com.quas.mesozoicisland.enums.DiscordChannel;
import com.quas.mesozoicisland.enums.DiscordRole;
import com.quas.mesozoicisland.objects.Dinosaur;
import com.quas.mesozoicisland.objects.Player;
import com.quas.mesozoicisland.util.Util;

import net.dv8tion.jda.api.entities.PrivateChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class DinodexCommand implements ICommand {

	@Override
	public Pattern getCommand() {
		return pattern("dinodex");
	}

	@Override
	public AccessLevel getAccessLevel() {
		return AccessLevel.Trainer;
	}

	@Override
	public String getCommandName() {
		return "dinodex";
	}

	@Override
	public String getCommandSyntax() {
		return "dinodex";
	}

	@Override
	public String getCommandDescription() {
		return "Lists your dinodex.";
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
	public void run(MessageReceivedEvent event, String... args) {
		Player p = Player.getPlayer(event.getAuthor().getIdLong());
		if (p == null) return;
		
		event.getChannel().sendMessageFormat("**%s's Dinodex:**\nYou have obtained %,d of %,d dinosaurs.\nCheck your DMs for a detailed list.", event.getAuthor().getAsMention(), p.getDexCount(DinosaurForm.AllForms.getId()), JDBC.getDexCount(DinosaurForm.AllForms.getId())).complete();
		
		ArrayList<String> print = new ArrayList<String>();
		print.add("__**DINODEX**__");
		
		for (Dinosaur d : Dinosaur.values()) {
			if (d.getForm() < 0) continue;
			Dinosaur pd = Dinosaur.getDinosaur(p.getIdLong(), d.getIdPair());
			if (pd == null && d.getDinosaurForm() == DinosaurForm.Prismatic) continue;
			else if (pd == null) print.add(String.format("#%s UNKNOWN DINOSAUR", d.getId()));
			else print.add(String.format("%s [%s] [%s]", d, d.getElement(), d.getRarity()));
		}
		
		PrivateChannel pc = event.getAuthor().openPrivateChannel().complete();
		for (String s : Util.bulkify(print)) pc.sendMessage(s).complete();
	}
}