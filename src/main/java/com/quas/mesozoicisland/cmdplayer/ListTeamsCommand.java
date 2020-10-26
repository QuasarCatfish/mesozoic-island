package com.quas.mesozoicisland.cmdplayer;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.regex.Pattern;

import com.quas.mesozoicisland.JDBC;
import com.quas.mesozoicisland.cmdbase.ICommand;
import com.quas.mesozoicisland.enums.AccessLevel;
import com.quas.mesozoicisland.enums.DiscordChannel;
import com.quas.mesozoicisland.enums.DiscordRole;
import com.quas.mesozoicisland.objects.Dinosaur;
import com.quas.mesozoicisland.objects.Player;
import com.quas.mesozoicisland.util.DinoMath;
import com.quas.mesozoicisland.util.Util;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class ListTeamsCommand implements ICommand {

	@Override
	public Pattern getCommand() {
		return pattern("team list|teams");
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
		return "team list";
	}

	@Override
	public String getCommandDescription() {
		return "Lists your current teams.";
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
		
		try (ResultSet res = JDBC.executeQuery("select * from teams where playerid = %d;", p.getIdLong())) {
			if (res.next()) {
				StringBuilder sb = new StringBuilder();
				
				int index = 1;
				do {
					String selected = res.getString("selected");
					Dinosaur[] team = null;

					if (selected == null) {
						team = new Dinosaur[0];
					} else {
						String[] split = selected.split("\\s+");
						team = new Dinosaur[split.length];

						for (int q = 0; q < split.length; q++) {
							team[q] = Dinosaur.getDinosaur(p.getIdLong(), Util.getDexForm(split[q]));
						}
					}

					sb.append(String.format("%d) Team `%s` (%d Dinosaur%s) [%s]\n", index, res.getString("teamname"), team.length, team.length== 1 ? "" : "s", DinoMath.getBattleTier(team)));
					index++;
				} while (res.next());
				
				event.getChannel().sendMessageFormat("%s, here are your teams:\n%s", p.getAsMention(), sb.toString()).complete();
			} else {
				event.getChannel().sendMessageFormat("%s, you don't have any teams.", p.getAsMention()).complete();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
