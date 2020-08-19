package com.quas.mesozoicisland.cmdplayer;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.regex.Pattern;

import com.quas.mesozoicisland.JDBC;
import com.quas.mesozoicisland.cmdbase.ICommand;
import com.quas.mesozoicisland.enums.AccessLevel;
import com.quas.mesozoicisland.enums.DiscordChannel;
import com.quas.mesozoicisland.enums.DiscordRole;
import com.quas.mesozoicisland.objects.Dinosaur;
import com.quas.mesozoicisland.util.Constants;
import com.quas.mesozoicisland.util.Pair;
import com.quas.mesozoicisland.util.Util;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class DataCommand implements ICommand {

	@Override
	public Pattern getCommand() {
		return pattern("data .+");
	}

	@Override
	public AccessLevel getAccessLevel() {
		return AccessLevel.Trainer;
	}

	@Override
	public String getCommandName() {
		return "data";
	}

	@Override
	public String getCommandSyntax() {
		return "data <dinosaur>";
	}

	@Override
	public String getCommandDescription() {
		return "Retrieves data on the given dinosaur.";
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
		String dinosaur = Util.join(args, " ", 0, args.length);
		
		if (dinosaur.toLowerCase().matches(DINOSAUR)) {
			Dinosaur dino = Dinosaur.getDinosaur(Util.getDexForm(dinosaur));
			if (dino != null) dinosaur = dino.getDinosaurName();
		}
		
		try (ResultSet res = JDBC.executeQuery("select * from dinosaurs where lower(dinoname) = '%s' and rarity >= 0;", Util.cleanQuotes(dinosaur.toLowerCase()))) {
			if (res.next()) {
				Dinosaur d = Dinosaur.getDinosaur(new Pair<Integer, Integer>(res.getInt("dex"), res.getInt("form")));
				
				EmbedBuilder eb = new EmbedBuilder();
				eb.setColor(Constants.COLOR);
				eb.setTitle(d.getDinosaurName());
				
				eb.addField("Dex Number", "#" + d.getId(), true);
				eb.addField("Element", d.getElement().toString(), true);
				eb.addField("Rarity", d.getRarity().toString(), true);
				eb.addField("Base Health", Util.formatNumber(d.getHealth()), true);
				eb.addField("Base Attack", Util.formatNumber(d.getAttack()), true);
				eb.addField("Base Defense", Util.formatNumber(d.getDefense()), true);
				eb.addField("Classification", d.getCreatureType(), true);
				if (d.getEpoch() != null) eb.addField("Geological Period", d.getEpoch(), true);
				if (d.getLocation() != null) eb.addField("Location", d.getLocation(), true);
				if (d.getDiet() != null) eb.addField("Diet", d.getDiet(), true);
				if (d.getDiscoveryYear() > 0) eb.addField("Discovery Year", Integer.toString(d.getDiscoveryYear()), true);
				if (d.getAuthors() != null) eb.addField("Author(s)", d.getAuthors(), false);
				eb.addField("Wikipedia Link", d.getWikiLink(), false);
				
				event.getChannel().sendMessage(eb.build()).complete();
			} else {
				event.getChannel().sendMessageFormat("%s, this dinosaur could not be found.", event.getAuthor().getAsMention()).complete();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
