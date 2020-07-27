package com.quas.mesozoicisland.cmdplayer;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.regex.Pattern;

import com.quas.mesozoicisland.JDBC;
import com.quas.mesozoicisland.cmdbase.ICommand;
import com.quas.mesozoicisland.enums.AccessLevel;
import com.quas.mesozoicisland.enums.DiscordChannel;
import com.quas.mesozoicisland.enums.DiscordRole;
import com.quas.mesozoicisland.objects.Item;
import com.quas.mesozoicisland.objects.Player;
import com.quas.mesozoicisland.util.Pair;
import com.quas.mesozoicisland.util.Util;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class DeleteTeamCommand implements ICommand {

	@Override
	public Pattern getCommand() {
		return pattern("team delete ", ALPHA);
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
		return "team delete <team name>";
	}

	@Override
	public String getCommandDescription() {
		return "Deletes a team with the given name, if it exists.";
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
		
		Item i = Item.getItem(new Pair<Integer, Long>(102, 0L));
		
		try (ResultSet res = JDBC.executeQuery("select * from teams where playerid = %d and teamname = '%s';", p.getIdLong(), Util.cleanQuotes(args[1]))) {
			if (res.next()) {
				JDBC.executeUpdate("delete from teams where playerid = %d and teamname = '%s';", p.getIdLong(), Util.cleanQuotes(args[1]));
				JDBC.addItem(p.getIdLong(), i.getIdDmg());
				event.getChannel().sendMessageFormat("%s, you have deleted Team `%s`, which returned %s %s.", p.getAsMention(), args[1], Util.getArticle(i.toString()), i.toString()).complete();
			} else {
				event.getChannel().sendMessageFormat("%s, you don't have a team named `%s`.", p.getAsMention(), args[1]).complete();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
