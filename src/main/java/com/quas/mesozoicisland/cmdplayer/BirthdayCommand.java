package com.quas.mesozoicisland.cmdplayer;

import java.util.regex.Pattern;

import com.quas.mesozoicisland.cmdbase.ICommand;
import com.quas.mesozoicisland.enums.AccessLevel;
import com.quas.mesozoicisland.enums.DiscordChannel;
import com.quas.mesozoicisland.enums.DiscordRole;
import com.quas.mesozoicisland.objects.Player;
import com.quas.mesozoicisland.util.Util;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class BirthdayCommand implements ICommand {

	@Override
	public Pattern getCommand() {
		return pattern("b(irth)?day");
	}

	@Override
	public AccessLevel getAccessLevel() {
		return AccessLevel.Trainer;
	}

	@Override
	public String getCommandName() {
		return "birthday";
	}

	@Override
	public String getCommandSyntax() {
		return "birthday";
	}

	@Override
	public String getCommandDescription() {
		return "Checks what your birthday is set to.";
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
		
		if (p.getBirthday() == -1) {
			event.getChannel().sendMessageFormat("%s, your birthday is not set.", event.getAuthor().getAsMention()).complete();
		} else {
			event.getChannel().sendMessageFormat("%s, your birthday is set as %s.", event.getAuthor().getAsMention(), Util.getBirthday(p.getBirthday())).complete();
		}
	}
}
