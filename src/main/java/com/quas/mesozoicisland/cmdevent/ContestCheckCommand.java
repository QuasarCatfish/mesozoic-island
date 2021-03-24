package com.quas.mesozoicisland.cmdevent;

import java.util.regex.Pattern;

import com.quas.mesozoicisland.cmdbase.ICommand;
import com.quas.mesozoicisland.enums.AccessLevel;
import com.quas.mesozoicisland.enums.DiscordChannel;
import com.quas.mesozoicisland.enums.DiscordRole;
import com.quas.mesozoicisland.enums.EventType;
import com.quas.mesozoicisland.objects.Dinosaur;
import com.quas.mesozoicisland.objects.Event;
import com.quas.mesozoicisland.objects.Player;
import com.quas.mesozoicisland.util.Util;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class ContestCheckCommand implements ICommand {

	@Override
	public Pattern getCommand() {
		return pattern("contest check");
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
		return "contest check";
	}

	@Override
	public String getCommandDescription() {
		return "Checks the dinosaur you have selected for the contest.";
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
		
		if (Event.isEventActive(EventType.ContestEntry) || Event.isEventActive(EventType.Contest)) {
			if (p.getContest() == null) {
				event.getChannel().sendMessageFormat("%s, you have not selected a dinosaur for the contest.", event.getAuthor().getAsMention()).complete();	
			} else {
				Dinosaur d = Dinosaur.getDinosaur(p.getIdLong(), Util.getDexForm(p.getContest()));
				if (d == null) d = Dinosaur.getDinosaur(Util.getDexForm(p.getContest()));
				event.getChannel().sendMessageFormat("%s, %s %s is your representative for the contest.", event.getAuthor().getAsMention(), d.getId(), d.getDinosaurName()).complete();
			}
		} else {
			event.getChannel().sendMessageFormat("%s, there is not a contest running.", event.getAuthor().getAsMention()).complete();
		}
	}
}