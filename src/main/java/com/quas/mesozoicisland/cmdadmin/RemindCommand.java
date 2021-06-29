package com.quas.mesozoicisland.cmdadmin;

import java.util.regex.Pattern;

import com.quas.mesozoicisland.JDBC;
import com.quas.mesozoicisland.MesozoicIsland;
import com.quas.mesozoicisland.cmdbase.ICommand;
import com.quas.mesozoicisland.enums.AccessLevel;
import com.quas.mesozoicisland.enums.ActionType;
import com.quas.mesozoicisland.enums.DiscordChannel;
import com.quas.mesozoicisland.enums.DiscordRole;
import com.quas.mesozoicisland.util.Util;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class RemindCommand implements ICommand {

	@Override
	public Pattern getCommand() {
		return pattern("admin remind ", TIME, " .*");
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
		return "admin remind <time> <msg>";
	}

	@Override
	public String getCommandDescription() {
		return "Reminds you after the given time.";
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
		long delta = Util.getTime(args[1]);
		JDBC.addAction(ActionType.SendGuildMessage, MesozoicIsland.getProfessor().getIdLong(), event.getChannel().getIdLong(), event.getAuthor().getAsMention() + ", your reminder is here!\n" + Util.cleanQuotes(Util.join(args, " ", 2, args.length)), System.currentTimeMillis() + delta);
		event.getChannel().sendMessageFormat("%s, you will be reminded in %s.", event.getAuthor().getAsMention(), Util.formatTime(delta)).complete();
	}
}
