package com.quas.mesozoicisland.cmdplayer;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.regex.Pattern;

import com.quas.mesozoicisland.JDBC;
import com.quas.mesozoicisland.battle.BattleTier;
import com.quas.mesozoicisland.cmdbase.ICommand;
import com.quas.mesozoicisland.enums.AccessLevel;
import com.quas.mesozoicisland.enums.DiscordChannel;
import com.quas.mesozoicisland.enums.DiscordRole;
import com.quas.mesozoicisland.objects.Dinosaur;
import com.quas.mesozoicisland.objects.Player;
import com.quas.mesozoicisland.util.DinoMath;
import com.quas.mesozoicisland.util.Util;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class InfoTeamCommand implements ICommand {

	@Override
	public Pattern getCommand() {
		return pattern("team info .+");
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
		return "team info <team name>";
	}

	@Override
	public String getCommandDescription() {
		return "Lists the dinosaurs on the given team.";
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
		
		if (!args[1].toLowerCase().matches(TEAM_NAME) || args.length > 2) {
			event.getChannel().sendMessageFormat("%s, you don't have a team with this name.", p.getAsMention()).complete();
			return;
		}

		try (ResultSet res = JDBC.executeQuery("select * from teams where playerid = %d and teamname = '%s';", p.getIdLong(), Util.cleanQuotes(args[1]))) {
			if (res.next()) {
				String selected = res.getString("selected");
				if (selected == null) {
					event.getChannel().sendMessageFormat("%s, you have not saved any dinosaurs to Team `%s`.", p.getAsMention(), args[1]).complete();
				} else {
					String[] split = selected.split("\\s+");
					StringBuilder sb = new StringBuilder();
					sb.append(p.getAsMention());
					
					if (split.length == 1) sb.append(", here is the dinosaur on Team ");
					else sb.append(", here are the dinosaurs on Team ");
					sb.append(args[1]);
					sb.append(":\n");
					
					Dinosaur[] team = new Dinosaur[split.length];
					for (int q = 0; q < split.length; q++) {
						team[q] = Dinosaur.getDinosaur(p.getIdLong(), Util.getDexForm(split[q]));
						sb.append(String.format("%d) %s\n", q + 1, team[q]));
					}
					
					BattleTier bt = DinoMath.getBattleTier(team);
					sb.append("This team is in the ");
					sb.append(bt.toString());
					sb.append(".");
					
					event.getChannel().sendMessage(sb.toString()).complete();
				}
			} else {
				event.getChannel().sendMessageFormat("%s, you don't have a team named `%s`.", p.getAsMention(), args[1]).complete();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
