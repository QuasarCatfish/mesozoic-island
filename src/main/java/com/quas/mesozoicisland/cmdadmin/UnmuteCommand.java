package com.quas.mesozoicisland.cmdadmin;

import java.util.regex.Pattern;

import com.quas.mesozoicisland.JDBC;
import com.quas.mesozoicisland.cmdbase.ICommand;
import com.quas.mesozoicisland.enums.AccessLevel;
import com.quas.mesozoicisland.enums.DiscordChannel;
import com.quas.mesozoicisland.enums.DiscordRole;
import com.quas.mesozoicisland.objects.Player;
import com.quas.mesozoicisland.util.Util;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class UnmuteCommand implements ICommand {

	@Override
	public Pattern getCommand() {
		return pattern("admin unmute ", PLAYER);
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
		return "admin unmute <player>";
	}

	@Override
	public String getCommandDescription() {
		return "Unmutes a player.";
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
		Player p = Player.getPlayer(Long.parseLong(args[1].replaceAll("\\D", "")));
		if (p == null) {
			event.getChannel().sendMessageFormat("%s, this is an invalid player.", event.getAuthor().getAsMention());
			return;
		}
		
		Member m = event.getGuild().getMemberById(p.getIdLong());
		Util.removeRoleFromMember(m, DiscordRole.Muted.getIdLong());
		JDBC.setMuted(p.getIdLong(), false);
		event.getChannel().sendMessageFormat("%s, you are now unmuted.", p.getAsMention()).complete();
	}
}
