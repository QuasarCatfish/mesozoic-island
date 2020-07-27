package com.quas.mesozoicisland.cmdplayer;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.regex.Pattern;

import com.quas.mesozoicisland.JDBC;
import com.quas.mesozoicisland.cmdbase.ICommand;
import com.quas.mesozoicisland.enums.AccessLevel;
import com.quas.mesozoicisland.enums.DiscordChannel;
import com.quas.mesozoicisland.enums.DiscordRole;
import com.quas.mesozoicisland.objects.Player;
import com.quas.mesozoicisland.util.Util;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class SaveTeamCommand implements ICommand {

	@Override
	public Pattern getCommand() {
		return pattern("team save ", ALPHA);
	}

	@Override
	public AccessLevel getAccessLevel() {
		return AccessLevel.Trainer;
	}

	@Override
	public String getCommandName() {
		return "team";
	}

	@Override
	public String getCommandSyntax() {
		return "team save <team name>";
	}

	@Override
	public String getCommandDescription() {
		return "Saves your currently selected dinosaurs to this team, if it exists.";
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
		
		try (ResultSet res = JDBC.executeQuery("select * from teams where playerid = %d and teamname = '%s';", p.getIdLong(), Util.cleanQuotes(args[1]))) {
			if (res.next()) {
				JDBC.executeUpdate("update teams set selected = '%s' where playerid = %d and teamname = '%s';", p.getSelected(), p.getIdLong(), Util.cleanQuotes(args[1]));
				event.getChannel().sendMessageFormat("%s, you have saved your selected dinosaurs onto Team `%s`.", p.getAsMention(), args[1]).complete();
			} else {
				event.getChannel().sendMessageFormat("%s, you don't have a team named `%s`.", p.getAsMention(), args[1]).complete();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
