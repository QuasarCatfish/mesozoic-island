package com.quas.mesozoicisland.cmdadmin;

import java.util.regex.Pattern;

import com.quas.mesozoicisland.JDBC;
import com.quas.mesozoicisland.MesozoicIsland;
import com.quas.mesozoicisland.cmdbase.ICommand;
import com.quas.mesozoicisland.enums.AccessLevel;
import com.quas.mesozoicisland.enums.DiscordChannel;
import com.quas.mesozoicisland.enums.DiscordRole;
import com.quas.mesozoicisland.objects.Player;
import com.quas.mesozoicisland.objects.Rune;
import com.quas.mesozoicisland.util.Constants;

import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class GiveRuneCommand implements ICommand {

	@Override
	public Pattern getCommand() {
		return pattern("admin give r(une)? ", PLAYER, " ", INTEGER, "( ", INTEGER, ")?");
	}

	@Override
	public AccessLevel getAccessLevel() {
		// return AccessLevel.Admin;
		return AccessLevel.Disabled;
	}

	@Override
	public String getCommandName() {
		return "admin";
	}

	@Override
	public String getCommandSyntax() {
		return "admin give rune @player <rune> [count]";
	}

	@Override
	public String getCommandDescription() {
		return "Gives a rune to a player.";
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
		long pid = Long.parseLong(args[2].replaceAll("[^\\d]", ""));
		Player p = Player.getPlayer(pid);
		if (p == null) {
			event.getChannel().sendMessageFormat("%s, this is an invalid player.", event.getAuthor().getAsMention()).complete();
			return;
		}
		
		Rune rune = Rune.getRune(Integer.parseInt(args[3]));
		int count = args.length > 4 ? Integer.parseInt(args[4]) : 1;
		if (rune == null) {
			event.getChannel().sendMessageFormat("%s, this is an invalid rune.", event.getAuthor().getAsMention()).complete();
		} else {
			MessageChannel mc = Constants.SPAWN_CHANNEL.getChannel(MesozoicIsland.getAssistant());
			mc.sendMessageFormat("<@%d>, you have been given %d %s rune%s.", pid, count, rune.getName(), count == 1 ? "" : "s").complete();
			JDBC.addRune(mc, pid, rune.getId(), count);
		}
	}
}
