package com.quas.mesozoicisland.cmdplayer;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.regex.Pattern;

import com.quas.mesozoicisland.JDBC;
import com.quas.mesozoicisland.battle.BattleTier;
import com.quas.mesozoicisland.cmdbase.ICommand;
import com.quas.mesozoicisland.enums.AccessLevel;
import com.quas.mesozoicisland.enums.DinosaurForm;
import com.quas.mesozoicisland.enums.DiscordChannel;
import com.quas.mesozoicisland.enums.DiscordRole;
import com.quas.mesozoicisland.objects.Dinosaur;
import com.quas.mesozoicisland.objects.Player;
import com.quas.mesozoicisland.util.DinoMath;
import com.quas.mesozoicisland.util.Util;
import com.quas.mesozoicisland.util.Zalgo;

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
	public synchronized void run(MessageReceivedEvent event, String... args) {
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
					event.getChannel().sendMessageFormat("%s, you have not saved any dinosaurs to Team `%s`.", p.getAsMention(), res.getString("teamname")).complete();
				} else {
					String[] split = selected.split("\\s+");
					StringBuilder sb = new StringBuilder();
					sb.append(p.getAsMention());
					
					if (split.length == 1) sb.append(", here is the dinosaur on Team ");
					else sb.append(", here are the dinosaurs on Team ");
					sb.append(res.getString("teamname"));
					sb.append(":\n");
					
					Dinosaur[] team = new Dinosaur[split.length];
					for (int q = 0; q < split.length; q++) {
						team[q] = Dinosaur.getDinosaur(p.getIdLong(), Util.getDexForm(split[q]));
						if (team[q].getDinosaurForm() == DinosaurForm.Accursed) {
							sb.append((q + 1) + ") " + team[q] + " " + Zalgo.field("[" + team[q].getElement() + "]"));
							
							if (team[q].hasItem()) {
								if (team[q].getItem().hasIcon()) {
									sb.append(" ");
									sb.append(team[q].getItem().getIcon().toString());
								} else {
									sb.append(Zalgo.field(String.format(" [Holding: %s]", team[q].getItem().toString())));
								}
							}

							if (team[q].hasRune()) sb.append(Zalgo.field(String.format(" [Rune: %s]", team[q].getRune().toString())));
							sb.append("\n");
						} else {
							sb.append(String.format("%d) %s [%s]", q + 1, team[q], team[q].getElement()));

							if (team[q].hasItem()) {
								if (team[q].getItem().hasIcon()) {
									sb.append(" ");
									sb.append(team[q].getItem().getIcon().toString());
								} else {
									sb.append(" [Holding: ");
									sb.append(team[q].getItem().toString());
									sb.append("]");
								}
							}
							
							if (team[q].hasRune()) sb.append(String.format(" [Rune: %s]", team[q].getRune().toString()));
							sb.append("\n");
						}
					}
					
					BattleTier bt = DinoMath.getBattleTier(team);
					int percent = DinoMath.getNextBattleTierPercent(team);
					if (percent >= 0) {
						sb.append(String.format("This team is in the %s and is %d%% of the way to the next tier.", bt, percent));
					} else {
						sb.append(String.format("This team is in the %s.", bt));
					}
					
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
