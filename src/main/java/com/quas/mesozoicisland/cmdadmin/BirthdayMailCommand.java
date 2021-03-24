package com.quas.mesozoicisland.cmdadmin;

import java.util.regex.Pattern;

import com.quas.mesozoicisland.MesozoicIsland;
import com.quas.mesozoicisland.cmdbase.ICommand;
import com.quas.mesozoicisland.enums.AccessLevel;
import com.quas.mesozoicisland.enums.CustomPlayer;
import com.quas.mesozoicisland.enums.DiscordChannel;
import com.quas.mesozoicisland.enums.DiscordRole;
import com.quas.mesozoicisland.objects.Player;
import com.quas.mesozoicisland.util.Constants;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class BirthdayMailCommand implements ICommand {

	@Override
	public Pattern getCommand() {
		return pattern("admin birthday ", PLAYER);
	}

	@Override
	public AccessLevel getAccessLevel() {
		return AccessLevel.Admin;
	}

	@Override
	public String getCommandName() {
		return "admin";
	}

	@Override
	public String getCommandSyntax() {
		return "admin birthdy @player";
	}

	@Override
	public String getCommandDescription() {
		return "Gives a birthday mail to the specified player.";
	}

	@Override
	public DiscordChannel[] getUsableChannels() {
		return DiscordChannel.ALL_CHANNELS;
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
		Player target = Player.getPlayer(Long.parseLong(args[1].replaceAll("\\D", "")));
		if (target == null || target.getIdLong() < CustomPlayer.getUpperLimit()) {
			event.getChannel().sendMessageFormat("%s, this player does not exist.", event.getAuthor().getAsMention()).complete();
			return;
		}
		
		Constants.addBirthdayMail(target);
		event.getChannel().sendMessageFormat("%s, %s has been given their birthday mail.", event.getAuthor().getAsMention(), target.getName()).complete();
		Constants.SPAWN_CHANNEL.getChannel(MesozoicIsland.getAssistant()).sendMessageFormat("%s, you have received a birthday message. Check your mailbox for your rewards.", target.getAsMention()).complete();
	}
}
