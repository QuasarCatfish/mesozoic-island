package com.quas.mesozoicisland.cmdadmin;

import java.util.regex.Pattern;

import com.quas.mesozoicisland.JDBC;
import com.quas.mesozoicisland.cmdbase.ICommand;
import com.quas.mesozoicisland.enums.AccessLevel;
import com.quas.mesozoicisland.enums.DiscordChannel;
import com.quas.mesozoicisland.enums.DiscordRole;
import com.quas.mesozoicisland.objects.Player;
import com.quas.mesozoicisland.util.Util;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class RenamePlayerCommand implements ICommand {

	@Override
	public Pattern getCommand() {
		return pattern("admin rename ", PLAYER, " ", NICKNAME);
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
		return "admin rename player <new name>";
	}

	@Override
	public String getCommandDescription() {
		return "Renames a player.";
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
		if (target == null) {
			event.getChannel().sendMessageFormat("%s, that is an invalid Player ID.", event.getAuthor().getAsMention());
			return;
		}
		
		boolean isadmin = false;
		String name = Util.join(args, " ", 2, args.length);
		
		Member m = event.getGuild().getMemberById(target.getId());
		if (!m.hasPermission(Permission.ADMINISTRATOR)) m.modifyNickname(name).complete();
		else isadmin = true;
		name = Util.fixString(name);
		JDBC.executeUpdate("update players set playername = '%s' where playerid = %d;", Util.cleanQuotes(name), target.getIdLong());
		
		event.getChannel().sendMessageFormat("%s, '%s' will now be known as '%s'. %s", event.getAuthor().getAsMention(), target.getRawName(), name, isadmin ? "Could not update nickname due to the player being an Administrator." : "").complete();
	}
}
