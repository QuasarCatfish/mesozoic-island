package com.quas.mesozoicisland.cmdplayer;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.regex.Pattern;

import com.quas.mesozoicisland.JDBC;
import com.quas.mesozoicisland.cmdbase.ICommand;
import com.quas.mesozoicisland.enums.AccessLevel;
import com.quas.mesozoicisland.enums.DinosaurForm;
import com.quas.mesozoicisland.enums.DiscordChannel;
import com.quas.mesozoicisland.enums.DiscordRole;
import com.quas.mesozoicisland.enums.ItemID;
import com.quas.mesozoicisland.objects.Dinosaur;
import com.quas.mesozoicisland.objects.Item;
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
		return DiscordChannel.STANDARD_CHANNELS_TRADE_DMS;
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
		String dinosaur = Util.join(args, " ", 0, args.length);
		Dinosaur d = null;
		
		if (dinosaur.toLowerCase().equals("raid")) {
			Item item = Item.getItem(new Pair<>(ItemID.RaidPass.getItemId(), Long.parseLong(JDBC.getVariable("raidpass"))));
			d = Dinosaur.getDinosaur(Integer.parseInt(item.getData().split("\\s+")[0]), DinosaurForm.Standard.getId());
		} else if (dinosaur.toLowerCase().matches(DINOSAUR)) {
			d = Dinosaur.getDinosaur(Util.getDexForm(dinosaur));
		} else {
			try (ResultSet res = JDBC.executeQuery("select * from dinosaurs where lower(dinoname) = '%s' and rarity >= 0;", Util.cleanQuotes(dinosaur.toLowerCase()))) {
				if (res.next()) {
					d = Dinosaur.getDinosaur(new Pair<Integer, Integer>(res.getInt("dex"), res.getInt("form")));
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
		if (d == null) {
			event.getChannel().sendMessageFormat("%s, this dinosaur could not be found.", event.getAuthor().getAsMention()).complete();
			return;
		}
		
		EmbedBuilder eb = new EmbedBuilder();
		eb.setColor(Constants.COLOR);
		eb.setTitle(d.getDinosaurName());
		
		if (event.getChannel().getIdLong() == DiscordChannel.Game.getIdLong()) {
			if (d.getDinosaurForm() == DinosaurForm.Standard) {
				eb.addField("Dex", "#" + d.getId(), true);
			} else if (d.getDinosaurForm().getOwnedEmote() == null) {
				eb.addField("Dex and Form", String.format("#%s\n%s", d.getId(), d.getFormName()), true);
			} else {
				eb.addField("Dex and Form", String.format("#%s\n%s %s", d.getId(), d.getFormName(), d.getDinosaurForm().getOwnedEmote().getEmote().getAsMention()), true);
			}
			eb.addField("Element and Rarity", String.format("%s\n%s", d.getElement().getAsString(), d.getRarity().getAsString()), true);
			eb.addField("Base Stats", String.format("%,d Health\n%,d Attack\n%,d Defense", d.getHealth(), d.getAttack(), d.getDefense()), true);
		} else {
			eb.addField("Dex Number", "#" + d.getId(), true);
			if (d.getDinosaurForm() != DinosaurForm.Standard) {
				if (d.getDinosaurForm().getOwnedEmote() == null) {
					eb.addField("Form", d.getFormName(), true);
				} else {
					eb.addField("Form", String.format("%s %s", d.getFormName(), d.getDinosaurForm().getOwnedEmote().getEmote().getAsMention()), true);
				}
			}
			eb.addField("Element", d.getElement().getAsString(), true);
			eb.addField("Rarity", d.getRarity().getAsString(), true);

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
		}

		event.getChannel().sendMessage(eb.build()).complete();
	}
}
