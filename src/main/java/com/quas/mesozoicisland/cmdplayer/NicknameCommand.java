package com.quas.mesozoicisland.cmdplayer;

import java.util.regex.Pattern;

import com.quas.mesozoicisland.JDBC;
import com.quas.mesozoicisland.cmdbase.ICommand;
import com.quas.mesozoicisland.enums.AccessLevel;
import com.quas.mesozoicisland.enums.DiscordChannel;
import com.quas.mesozoicisland.enums.DiscordRole;
import com.quas.mesozoicisland.objects.Dinosaur;
import com.quas.mesozoicisland.objects.Player;
import com.quas.mesozoicisland.util.Util;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class NicknameCommand implements ICommand {

	@Override
	public Pattern getCommand() {
		return pattern("nick(name)? ", DINOSAUR, " .+");
	}

	@Override
	public AccessLevel getAccessLevel() {
		return AccessLevel.Trainer;
	}

	@Override
	public String getCommandName() {
		return "nickname";
	}

	@Override
	public String getCommandSyntax() {
		return "nickname <dinosaur> <nickname>";
	}

	@Override
	public String getCommandDescription() {
		return "Gives a nickname to the chosen dinosaur.";
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
		
		String nick = Util.join(args, " ", 1, args.length);
		if (nick.toLowerCase().matches(NICKNAME)) {
			JDBC.setNickname(p.getIdLong(), d.getDex(), d.getForm(), nick);
			event.getChannel().sendMessageFormat("%s, your %s shall now be known as \"%s\".", event.getAuthor().getAsMention(), d.getDinosaurName(), nick).complete();
		} else {
			event.getChannel().sendMessageFormat("%s, that is an invalid nickname for a dinosaur.", event.getAuthor().getAsMention()).complete();
		}
	}
}
