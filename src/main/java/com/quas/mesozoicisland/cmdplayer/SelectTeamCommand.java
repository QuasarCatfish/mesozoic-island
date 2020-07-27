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

public class SelectTeamCommand implements ICommand {

	@Override
	public Pattern getCommand() {
		return pattern("select ", ALPHA);
	}

	@Override
	public AccessLevel getAccessLevel() {
		return AccessLevel.Trainer;
	}

	@Override
	public String getCommandName() {
		return "select";
	}

	@Override
	public String getCommandSyntax() {
		return "select <team name>";
	}

	@Override
	public String getCommandDescription() {
		return "Selects the given team.";
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
		
		String[] select = null;
		
		try (ResultSet res = JDBC.executeQuery("select * from teams where playerid = %d and teamname = '%s';", p.getIdLong(), Util.cleanQuotes(args[0]))) {
			if (res.next()) {
				select = res.getString("selected").split("\\s+");
			} else {
				event.getChannel().sendMessageFormat("%s, you don't have a team named `%s`.", p.getAsMention(), args[0]).complete();
				return;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		if (select == null) {
			event.getChannel().sendMessageFormat("%s, you have not saved any dinosaurs to Team `%s`.", args[0]).complete();
			return;
		}
		
		Dinosaur[] dinos = new Dinosaur[select.length];
		for (int q = 0; q < select.length; q++) {
			dinos[q] = Dinosaur.getDinosaur(p.getIdLong(), Util.getDexForm(select[q]));
		}
		
		JDBC.setSelected(p.getIdLong(), dinos);
		BattleTier bt = DinoMath.getBattleTier(dinos);
		
		event.getChannel().sendMessageFormat("%s, your selected dinosaur%s been updated. Your team is in the %s.", event.getAuthor().getAsMention(), dinos.length == 1 ? " has" : "s have", bt.toString()).complete();
	}
}
