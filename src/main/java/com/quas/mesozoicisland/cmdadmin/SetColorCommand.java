package com.quas.mesozoicisland.cmdadmin;

import java.awt.Color;
import java.util.regex.Pattern;

import com.quas.mesozoicisland.JDBC;
import com.quas.mesozoicisland.cmdbase.ICommand;
import com.quas.mesozoicisland.enums.AccessLevel;
import com.quas.mesozoicisland.enums.DiscordChannel;
import com.quas.mesozoicisland.enums.DiscordRole;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class SetColorCommand implements ICommand {

	@Override
	public Pattern getCommand() {
		return pattern("setcolor ", INTEGER, " ", INTEGER, " ", INTEGER);
	}

	@Override
	public AccessLevel getAccessLevel() {
		return AccessLevel.Admin;
	}

	@Override
	public String getCommandName() {
		return "setcolor";
	}

	@Override
	public String getCommandSyntax() {
		return "setcolor <red> <green> <blue>";
	}

	@Override
	public String getCommandDescription() {
		return "Sets the color of your Trainer License.";
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
	public void run(MessageReceivedEvent event, String... args) {
		int red = Integer.parseInt(args[0]);
		int green = Integer.parseInt(args[1]);
		int blue = Integer.parseInt(args[2]);
		Color c = new Color(red, green, blue);
		JDBC.executeUpdate("update players set color = %d where playerid = %d;", c.getRGB(), event.getAuthor().getIdLong());
		event.getChannel().sendMessage("Updated.").complete();
	}
}
