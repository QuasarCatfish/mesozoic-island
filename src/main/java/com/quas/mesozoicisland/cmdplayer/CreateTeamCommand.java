package com.quas.mesozoicisland.cmdplayer;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.TreeMap;
import java.util.regex.Pattern;

import com.quas.mesozoicisland.JDBC;
import com.quas.mesozoicisland.cmdbase.ICommand;
import com.quas.mesozoicisland.enums.AccessLevel;
import com.quas.mesozoicisland.enums.DiscordChannel;
import com.quas.mesozoicisland.enums.DiscordRole;
import com.quas.mesozoicisland.enums.ItemID;
import com.quas.mesozoicisland.objects.Item;
import com.quas.mesozoicisland.objects.Player;
import com.quas.mesozoicisland.util.Util;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class CreateTeamCommand implements ICommand {

	@Override
	public Pattern getCommand() {
		return pattern("team create .+");
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
		return "team create <team name>";
	}

	@Override
	public String getCommandDescription() {
		return "Creates a team with the given name, if allowed.";
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
		
		Item i = Item.getItem(ItemID.TeamToken);
		TreeMap<Item, Long> bag = p.getBag();

		if (!args[1].toLowerCase().matches(ALPHA) || args.length > 2) {
			event.getChannel().sendMessageFormat("%s, that is an invalid name for a team.", p.getAsMention()).complete();
			return;
		}
		
		if (bag.getOrDefault(i, 0L) <= 0L) {
			event.getChannel().sendMessageFormat("%s, you do not have %s %s.", p.getAsMention(), Util.getArticle(i.toString()), i.toString()).complete();
		} else {
			try (ResultSet res = JDBC.executeQuery("select * from teams where playerid = %d and teamname = '%s';", p.getIdLong(), Util.cleanQuotes(args[1]))) {
				if (res.next()) {
					event.getChannel().sendMessageFormat("%s, you already have a team named `%s`.", p.getAsMention(), args[1]).complete();
				} else {
					JDBC.executeUpdate("insert into teams(playerid, teamname) values(%d, '%s');", p.getIdLong(), Util.cleanQuotes(args[1]));
					JDBC.addItem(p.getIdLong(), i.getIdDmg(), -1);
					event.getChannel().sendMessageFormat("%s, you have created Team `%s`, which consumed %s %s.", p.getAsMention(), args[1], Util.getArticle(i.toString()), i.toString()).complete();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
}
