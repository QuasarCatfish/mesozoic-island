package com.quas.mesozoicisland.cmdplayer;

import java.util.regex.Pattern;

import com.quas.mesozoicisland.JDBC;
import com.quas.mesozoicisland.cmdbase.ICommand;
import com.quas.mesozoicisland.enums.AccessLevel;
import com.quas.mesozoicisland.enums.DiscordChannel;
import com.quas.mesozoicisland.enums.DiscordRole;
import com.quas.mesozoicisland.enums.EventType;
import com.quas.mesozoicisland.objects.Event;
import com.quas.mesozoicisland.objects.Player;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class SecretSantaEnterCommand implements ICommand {

	@Override
	public Pattern getCommand() {
		return pattern("santa enter");
	}

	@Override
	public AccessLevel getAccessLevel() {
		return AccessLevel.Trainer;
	}

	@Override
	public String getCommandName() {
		return "santa";
	}

	@Override
	public String getCommandSyntax() {
		return "santa enter";
	}

	@Override
	public String getCommandDescription() {
		return "Enters the Secret Santa event.";
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
		
		if (!Event.isEventActive(EventType.SecretSantaSignup)) {
			event.getChannel().sendMessageFormat("%s, the entry period for the Secret Santa is closed.", event.getAuthor().getAsMention()).complete();
			return;
		}

		event.getChannel().sendMessageFormat("%s, you have successfully signed up for the Secret Santa event.", event.getAuthor().getAsMention()).complete();
		JDBC.executeUpdate("update players set santa = 1 where playerid = %d;", p.getIdLong());
	}
}