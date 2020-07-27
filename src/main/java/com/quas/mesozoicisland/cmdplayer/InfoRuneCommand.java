package com.quas.mesozoicisland.cmdplayer;

import java.util.regex.Pattern;

import com.quas.mesozoicisland.cmdbase.ICommand;
import com.quas.mesozoicisland.enums.AccessLevel;
import com.quas.mesozoicisland.enums.DiscordChannel;
import com.quas.mesozoicisland.enums.DiscordRole;
import com.quas.mesozoicisland.objects.Dinosaur;
import com.quas.mesozoicisland.objects.Player;
import com.quas.mesozoicisland.objects.Rune;
import com.quas.mesozoicisland.util.Constants;
import com.quas.mesozoicisland.util.Util;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class InfoRuneCommand implements ICommand {

	@Override
	public Pattern getCommand() {
		return pattern("info ", RUNE);
	}

	@Override
	public AccessLevel getAccessLevel() {
//		return AccessLevel.Trainer;
		return AccessLevel.Disabled;
	}

	@Override
	public String getCommandName() {
		return "info";
	}

	@Override
	public String getCommandSyntax() {
		return "info <rune>";
	}

	@Override
	public String getCommandDescription() {
		return "Gives information on the chosen rune.";
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
		
		Rune r = Rune.getRune(p.getIdLong(), Integer.parseInt(args[0].substring(1)));
		if (r == null) {
			event.getChannel().sendMessageFormat("%s, I could not find the given rune.", event.getAuthor().getAsMention()).complete();
			return;
		}
		
		EmbedBuilder eb = new EmbedBuilder();
		eb.setTitle(r.getName() + " Rune");
		eb.setColor(Constants.COLOR);
		
		eb.addField("Rune ID", r.getId() > 0 ? String.format("#%03d", r.getId()) : "#???", true);
		eb.addField("Owner", p.getName(), true);
		eb.addField("Element", r.getElement().getName(), true);
		eb.addField("Rarity", r.getRarity().getName(), true);
		eb.addField("Rank and RP", String.format("Rank %s + %,d RP", r.getRankString(), r.getRp()), true);
		eb.addField("Effect", r.getEffect(), true);
		if (r.isEquipped()) {
			Dinosaur d = Dinosaur.getDinosaur(p.getIdLong(), Util.getDexForm(r.getEquipped()));
			eb.addField("Equipped To", d.getEffectiveName(), true);
		}
		
		event.getChannel().sendMessage(eb.build()).complete();
	}
}
