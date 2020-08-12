package com.quas.mesozoicisland.cmdplayer;

import java.util.regex.Pattern;

import com.quas.mesozoicisland.JDBC;
import com.quas.mesozoicisland.cmdbase.ICommand;
import com.quas.mesozoicisland.enums.AccessLevel;
import com.quas.mesozoicisland.enums.DiscordChannel;
import com.quas.mesozoicisland.enums.DiscordRole;
import com.quas.mesozoicisland.enums.Stat;
import com.quas.mesozoicisland.objects.Dinosaur;
import com.quas.mesozoicisland.objects.Egg;
import com.quas.mesozoicisland.objects.Player;
import com.quas.mesozoicisland.util.Util;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class HatchCommand implements ICommand {

	@Override
	public Pattern getCommand() {
		return pattern("hatch e", INTEGER);
	}

	@Override
	public AccessLevel getAccessLevel() {
		return AccessLevel.Trainer;
	}

	@Override
	public String getCommandName() {
		return "hatch";
	}

	@Override
	public String getCommandSyntax() {
		return "hatch <incubator>";
	}

	@Override
	public String getCommandDescription() {
		return "Hatches the given egg.";
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
		
		Egg egg = p.getEgg(Integer.parseInt(args[0].substring(1)));
		if (egg == null) {
			event.getChannel().sendMessageFormat("%s, you don't have an egg in this incubator.", p.getAsMention()).complete();
			return;
		}
		
		if (!egg.isHatchable()) {
			int delta = egg.getMaxHatchPoints() - egg.getCurrentHatchPoints();
			event.getChannel().sendMessageFormat("%s, your %s needs %,d more Hatch Point%s before it's ready to hatch.", p.getAsMention(), egg.getEggName(), delta, delta == 1 ? "" : "s").complete();
			return;
		}
		
		Dinosaur d = Dinosaur.getDinosaur(egg.getDex(), egg.getForm());
		event.getChannel().sendMessageFormat("%s, your %s hatched into %s %s!", p.getAsMention(), egg.getEggName(), Util.getArticle(d.getDinosaurName()), d.getDinosaurName()).complete();
		JDBC.executeUpdate("update eggs set player = 1 where eggid = %d;", egg.getId());
		JDBC.addDinosaur(event.getChannel(), p.getIdLong(), d.getIdPair());
		JDBC.addItem(p.getIdLong(), Stat.EggsHatched.getId());
	}
}
