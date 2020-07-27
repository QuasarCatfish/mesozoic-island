package com.quas.mesozoicisland.cmdadmin;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.regex.Pattern;

import com.quas.mesozoicisland.JDBC;
import com.quas.mesozoicisland.cmdbase.ICommand;
import com.quas.mesozoicisland.enums.AccessLevel;
import com.quas.mesozoicisland.enums.CustomPlayer;
import com.quas.mesozoicisland.enums.DiscordChannel;
import com.quas.mesozoicisland.enums.DiscordRole;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class UpdatePlayerLevelsCommand implements ICommand {

	@Override
	public Pattern getCommand() {
		return pattern("admin update level");
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
		return "admin update level";
	}

	@Override
	public String getCommandDescription() {
		return "Updates all players' level.";
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
		try (ResultSet res = JDBC.executeQuery("select * from players;")) {
			while (res.next()) {
				if (res.getLong("playerid") < CustomPlayer.getUpperLimit()) continue;
				JDBC.updatePlayerXp(res.getLong("playerid"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
