package com.quas.mesozoicisland.cmdplayer;

import java.util.regex.Pattern;

import com.quas.mesozoicisland.JDBC;
import com.quas.mesozoicisland.cmdbase.ICommand;
import com.quas.mesozoicisland.enums.AccessLevel;
import com.quas.mesozoicisland.enums.DiscordChannel;
import com.quas.mesozoicisland.enums.DiscordRole;
import com.quas.mesozoicisland.objects.Player;
import com.quas.mesozoicisland.objects.Rune;
import com.quas.mesozoicisland.util.Constants;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class RankupRuneCommand implements ICommand {

	@Override
	public Pattern getCommand() {
		return pattern("rankup ", RUNE);
	}

	@Override
	public AccessLevel getAccessLevel() {
//		return AccessLevel.Trainer;
		return AccessLevel.Disabled;
	}

	@Override
	public String getCommandName() {
		return "rankup";
	}

	@Override
	public String getCommandSyntax() {
		return "rankup <rune>";
	}

	@Override
	public String getCommandDescription() {
		return "Ranks up the given rune, if able.";
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
			event.getChannel().sendMessageFormat("%s, this rune does not exist.", event.getAuthor().getAsMention()).complete();
		} else if (!r.canRankup()) {
			event.getChannel().sendMessageFormat("%s, your %s rune needs an additional %,d RP to rankup.", event.getAuthor().getAsMention(), r.getName(), r.getRpToRankup()).complete();
		} else if (r.getRank() == Constants.MAX_RANK) {
			event.getChannel().sendMessageFormat("%s, your %s rune is at the max rank and cannot rankup any further.", event.getAuthor().getAsMention(), r.getName()).complete();
		} else {
			JDBC.rankup(p.getIdLong(), r.getId());
			Rune r2 = Rune.getRune(p.getIdLong(), r.getId());
			if (r2.canRankup() && r2.getRank() < Constants.MAX_RANK) {
				event.getChannel().sendMessageFormat("%s, your %s rune has reached **Rank %s**! It can now rankup to **Rank %s**. Use `rankup R%d` to rankup this rune.", event.getAuthor().getAsMention(), r.getName(), r.getNextRankString(), r2.getNextRankString(), r.getId()).complete();
			} else {
				event.getChannel().sendMessageFormat("%s, your %s rune has reached **Rank %s**!", event.getAuthor().getAsMention(), r.getName(), r.getNextRankString()).complete();
			}
		}
	}
}
