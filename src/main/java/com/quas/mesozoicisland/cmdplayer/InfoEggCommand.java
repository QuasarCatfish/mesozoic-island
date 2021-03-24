package com.quas.mesozoicisland.cmdplayer;

import java.io.File;
import java.util.regex.Pattern;

import com.quas.mesozoicisland.cmdbase.ICommand;
import com.quas.mesozoicisland.enums.AccessLevel;
import com.quas.mesozoicisland.enums.DiscordChannel;
import com.quas.mesozoicisland.enums.DiscordRole;
import com.quas.mesozoicisland.enums.EggPattern;
import com.quas.mesozoicisland.objects.Egg;
import com.quas.mesozoicisland.objects.Player;
import com.quas.mesozoicisland.util.Constants;
import com.quas.mesozoicisland.util.Util;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class InfoEggCommand implements ICommand {

	@Override
	public Pattern getCommand() {
		return pattern("info e", INTEGER);
	}

	@Override
	public AccessLevel getAccessLevel() {
		return AccessLevel.Trainer;
	}

	@Override
	public String getCommandName() {
		return "info";
	}

	@Override
	public String getCommandSyntax() {
		return "info <incubator>";
	}

	@Override
	public String getCommandDescription() {
		return "Gives information on the chosen egg.";
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
		Player p = Player.getPlayer(event.getAuthor().getIdLong());
		if (p == null) return;
		
		Egg egg = p.getEgg(Integer.parseInt(args[0].substring(1)));
		if (egg == null) {
			event.getChannel().sendMessageFormat("%s, you don't have an egg in this incubator.", p.getAsMention()).complete();
			return;
		}
		
		EmbedBuilder eb = new EmbedBuilder();
		eb.setColor(Constants.COLOR);
		eb.setTitle(egg.getEggName());
		eb.addField("Incubator Slot", Util.formatNumber(egg.getIncubatorSlot()), true);
		eb.addField("Owner", egg.getPlayer().getName(), true);
		eb.addField("Egg Color", egg.getColorString(), true);
		if (egg.getPattern() == EggPattern.None) eb.addField("Egg Pattern", "None", true);
		else eb.addField("Egg Pattern", egg.getPatternString(), true);
		if (egg.isHatchable()) eb.addField("Hatch Points", "Ready to Hatch", true);
		else eb.addField("Hatch Points", String.format("%,d of %,d", egg.getCurrentHatchPoints(), egg.getMaxHatchPoints()), true);
		
		File image = egg.getImage();
		if (image == null) {
			event.getChannel().sendMessage(eb.build()).complete();
		} else {
			eb.setThumbnail("attachment://" + image.getName());
			event.getChannel().sendMessage(eb.build()).addFile(image).complete();
		}
	}
}
