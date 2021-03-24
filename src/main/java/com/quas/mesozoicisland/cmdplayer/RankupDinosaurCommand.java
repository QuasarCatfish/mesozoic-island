package com.quas.mesozoicisland.cmdplayer;

import java.util.regex.Pattern;

import com.quas.mesozoicisland.JDBC;
import com.quas.mesozoicisland.cmdbase.ICommand;
import com.quas.mesozoicisland.enums.AccessLevel;
import com.quas.mesozoicisland.enums.DiscordChannel;
import com.quas.mesozoicisland.enums.DiscordRole;
import com.quas.mesozoicisland.enums.Stat;
import com.quas.mesozoicisland.objects.Dinosaur;
import com.quas.mesozoicisland.objects.Player;
import com.quas.mesozoicisland.util.Constants;
import com.quas.mesozoicisland.util.Util;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class RankupDinosaurCommand implements ICommand {

	@Override
	public Pattern getCommand() {
		return pattern("rankup ", DINOSAUR);
	}

	@Override
	public AccessLevel getAccessLevel() {
		return AccessLevel.Trainer;
	}

	@Override
	public String getCommandName() {
		return "rankup";
	}

	@Override
	public String getCommandSyntax() {
		return "rankup <dinosaur>";
	}

	@Override
	public String getCommandDescription() {
		return "Ranks up the given dinosaur, if able.";
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
		
		Dinosaur d = Dinosaur.getDinosaur(p.getIdLong(), Util.getDexForm(args[0]));
		if (d == null) {
			event.getChannel().sendMessageFormat("%s, this dinosaur does not exist.", event.getAuthor().getAsMention()).complete();
		} else if (d.getRank() == Constants.MAX_RANK) {
			event.getChannel().sendMessageFormat("%s, your %s is at the max rank and cannot rankup any further.", event.getAuthor().getAsMention(), d.getEffectiveName()).complete();
		} else if (!d.canRankup()) {
			event.getChannel().sendMessageFormat("%s, your %s needs an additional %,d RP to rankup.", event.getAuthor().getAsMention(), d.getEffectiveName(), d.getRpToRankup()).complete();
		} else {
			JDBC.rankup(p.getIdLong(), d.getIdPair());
			JDBC.addItem(p.getIdLong(), Stat.DinosaursRankedUp.getId());
			
			Dinosaur d2 = Dinosaur.getDinosaur(p.getIdLong(), d.getIdPair());
			if (d2.canRankup() && d2.getRank() < Constants.MAX_RANK) {
				event.getChannel().sendMessageFormat("%s, your %s has reached **Rank %s**! It can now rankup to **Rank %s**. Use `rankup %s` to rankup this dinosaur.", event.getAuthor().getAsMention(), d.getEffectiveName(), d.getNextRankString(), d2.getNextRankString(), d2.getId()).complete();
			} else {
				event.getChannel().sendMessageFormat("%s, your %s has reached **Rank %s**!", event.getAuthor().getAsMention(), d.getEffectiveName(), d.getNextRankString()).complete();
			}
		}
	}
}
