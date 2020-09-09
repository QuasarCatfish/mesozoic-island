package com.quas.mesozoicisland.cmdplayer;

import java.util.regex.Pattern;

import com.quas.mesozoicisland.JDBC;
import com.quas.mesozoicisland.cmdbase.ICommand;
import com.quas.mesozoicisland.enums.AccessLevel;
import com.quas.mesozoicisland.enums.DinosaurForm;
import com.quas.mesozoicisland.enums.DiscordChannel;
import com.quas.mesozoicisland.enums.DiscordRole;
import com.quas.mesozoicisland.objects.Dinosaur;
import com.quas.mesozoicisland.objects.Player;
import com.quas.mesozoicisland.util.Util;
import com.quas.mesozoicisland.util.Zalgo;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class UnnicknameCommand implements ICommand {

	@Override
	public Pattern getCommand() {
		return pattern("(un|de)nick(name)? ", DINOSAUR);
	}

	@Override
	public AccessLevel getAccessLevel() {
		return AccessLevel.Trainer;
	}

	@Override
	public String getCommandName() {
		return "unnickname";
	}

	@Override
	public String getCommandSyntax() {
		return "unnickname <dinosaur>";
	}

	@Override
	public String getCommandDescription() {
		return "Removes the nickname for the chosen dinosaur.";
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
		
		Dinosaur d = Dinosaur.getDinosaur(p.getIdLong(), Util.getDexForm(args[0]));
		if (d == null) {
			event.getChannel().sendMessageFormat("%s, I could not find the given dinosaur.", event.getAuthor().getAsMention()).complete();
			return;
		}
		
		JDBC.setNickname(p.getIdLong(), d.getDex(), d.getForm(), null);
		if (d.getDinosaurForm() == DinosaurForm.Accursed) {
			event.getChannel().sendMessageFormat("%s, your %s's nickname has been removed.", event.getAuthor().getAsMention(), Zalgo.field(d.getDinosaurName())).complete();
		} else {
			event.getChannel().sendMessageFormat("%s, your %s's nickname has been removed.", event.getAuthor().getAsMention(), d.getDinosaurName()).complete();
		}
	}
}
