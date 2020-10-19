package com.quas.mesozoicisland.cmdplayer;

import java.util.regex.Pattern;

import com.quas.mesozoicisland.JDBC;
import com.quas.mesozoicisland.battle.BattleTier;
import com.quas.mesozoicisland.cmdbase.ICommand;
import com.quas.mesozoicisland.enums.AccessLevel;
import com.quas.mesozoicisland.enums.DinosaurForm;
import com.quas.mesozoicisland.enums.DiscordChannel;
import com.quas.mesozoicisland.enums.DiscordRole;
import com.quas.mesozoicisland.objects.Dinosaur;
import com.quas.mesozoicisland.objects.Player;
import com.quas.mesozoicisland.util.Constants;
import com.quas.mesozoicisland.util.DinoMath;
import com.quas.mesozoicisland.util.Util;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class SelectDinosaursCommand implements ICommand {

	@Override
	public Pattern getCommand() {
		return pattern("select( ", DINOSAUR, "){1,", Integer.toString(Constants.DINOS_PER_TEAM), "}");
	}

	@Override
	public AccessLevel getAccessLevel() {
		return AccessLevel.Trainer;
	}

	@Override
	public String getCommandName() {
		return "select";
	}

	@Override
	public String getCommandSyntax() {
		return "select <dinosaur> ...";
	}

	@Override
	public String getCommandDescription() {
		return "Selects the given dinosaurs to be on your team.";
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
		
		Dinosaur[] dinos = new Dinosaur[args.length];
		int contest = 0;
		int accursed = 0;
		for (int q = 0; q < args.length; q++) {
			dinos[q] = Dinosaur.getDinosaur(p.getIdLong(), Util.getDexForm(args[q]));
			if (dinos[q] == null) continue;
			if (dinos[q].getDinosaurForm() == DinosaurForm.Contest) contest++;
			if (dinos[q].getDinosaurForm() == DinosaurForm.Accursed) accursed++;
		}
		
		int olen = dinos.length;
		dinos = Util.removeDuplciates(dinos);
		BattleTier bt = DinoMath.getBattleTier(dinos);
		
		if (dinos.length == 0) {
			event.getChannel().sendMessageFormat("%s, none of these dinosaurs are valid to select.", event.getAuthor().getAsMention()).complete();
			return;
		} else if (contest > 0 && contest < dinos.length) {
			event.getChannel().sendMessageFormat("%s, you cannot select a Contest dinosaur and a non-Contest dinosaur.", event.getAuthor().getAsMention()).complete();
			return;
		} else if (accursed > 0 && accursed < dinos.length) {
			event.getChannel().sendMessageFormat("%s, you cannot select an Accursed dinosaur and a non-Accursed dinosaur.", event.getAuthor().getAsMention()).complete();
			return;
		} else if (olen - dinos.length == 0) {
			event.getChannel().sendMessageFormat("%s, your selected dinosaur%s been updated. Your team is in the %s.", event.getAuthor().getAsMention(), dinos.length == 1 ? " has" : "s have", bt.toString()).complete();
		} else if (olen - dinos.length == 1) {
			event.getChannel().sendMessageFormat("%s, your selected dinosaur%s been updated. Your team is in the %s. 1 was not selected due to being a duplicate or non-existent.", event.getAuthor().getAsMention(), dinos.length == 1 ? " has" : "s have", bt.toString()).complete();
		} else {
			event.getChannel().sendMessageFormat("%s, your selected dinosaur%s been updated. Your team is in the %s. %d were not selected due to being duplicates or non-existent.", event.getAuthor().getAsMention(), dinos.length == 1 ? " has" : "s have", bt.toString(), olen - dinos.length).complete();
		}

		JDBC.setSelected(p.getIdLong(), dinos);
	}
}
