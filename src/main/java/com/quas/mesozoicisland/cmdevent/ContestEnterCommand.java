package com.quas.mesozoicisland.cmdevent;

import java.util.regex.Pattern;

import com.quas.mesozoicisland.JDBC;
import com.quas.mesozoicisland.cmdbase.ICommand;
import com.quas.mesozoicisland.enums.AccessLevel;
import com.quas.mesozoicisland.enums.DinosaurForm;
import com.quas.mesozoicisland.enums.DiscordChannel;
import com.quas.mesozoicisland.enums.DiscordRole;
import com.quas.mesozoicisland.enums.EventType;
import com.quas.mesozoicisland.objects.Dinosaur;
import com.quas.mesozoicisland.objects.Event;
import com.quas.mesozoicisland.objects.Player;
import com.quas.mesozoicisland.util.Pair;
import com.quas.mesozoicisland.util.Util;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class ContestEnterCommand implements ICommand {

	@Override
	public Pattern getCommand() {
		return pattern("contest enter ", DINOSAUR);
	}

	@Override
	public AccessLevel getAccessLevel() {
		return AccessLevel.Trainer;
	}

	@Override
	public String getCommandName() {
		return "contest";
	}

	@Override
	public String getCommandSyntax() {
		return "contest enter <dinosaur>";
	}

	@Override
	public String getCommandDescription() {
		return "Enters the contest with the given dinosaur.";
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
	public synchronized void run(MessageReceivedEvent event, String... args) {
		Player p = Player.getPlayer(event.getAuthor().getIdLong());
		if (p == null) return;
		
		if (!Event.isEventActive(EventType.ContestEntry)) {
			event.getChannel().sendMessageFormat("%s, the entry period for the contest is closed.", event.getAuthor().getAsMention()).complete();
			return;
		}

		Dinosaur d = Dinosaur.getDinosaur(p.getIdLong(), Util.getDexForm(args[1]));
		if (d == null) {
			event.getChannel().sendMessageFormat("%s, I could not find the given dinosaur.", event.getAuthor().getAsMention()).complete();
			return;
		} else if (!d.getDiet().equals("Carnivore")) {
			event.getChannel().sendMessageFormat("%s, %s is not a Carnivore. This contest is exclusive to Carnivore Dinosaurs only.", event.getAuthor().getAsMention(), d.getDinosaurName()).complete();
			return;
		}

		Dinosaur contest = Dinosaur.getDinosaur(new Pair<Integer, Integer>(d.getDex(), DinosaurForm.Contest.getId()));
		if (contest == null) {
			event.getChannel().sendMessageFormat("%s, this dinosaur is banned from contests.", event.getAuthor().getAsMention()).complete();
		} else {
			event.getChannel().sendMessageFormat("%s, you have chosen the %s as your representative for the contest.", event.getAuthor().getAsMention(), contest.getDinosaurName()).complete();
			JDBC.setContest(p.getIdLong(), contest);
		}
	}
}