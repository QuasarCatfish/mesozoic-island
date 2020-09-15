package com.quas.mesozoicisland.cmdplayer;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.regex.Pattern;

import com.quas.mesozoicisland.JDBC;
import com.quas.mesozoicisland.cmdbase.ICommand;
import com.quas.mesozoicisland.enums.AccessLevel;
import com.quas.mesozoicisland.enums.CustomPlayer;
import com.quas.mesozoicisland.enums.DiscordChannel;
import com.quas.mesozoicisland.enums.DiscordRole;
import com.quas.mesozoicisland.objects.Player;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class BenedictStockCommand implements ICommand {

	@Override
	public Pattern getCommand() {
		return pattern("benedict( stock)?");
	}

	@Override
	public AccessLevel getAccessLevel() {
		return AccessLevel.GuineaPig;
	}

	@Override
	public String getCommandName() {
		return "benedict";
	}

	@Override
	public String getCommandSyntax() {
		return "benedict";
	}

	@Override
	public String getCommandDescription() {
		return "Lists the status of " + CustomPlayer.EggSalesman.getPlayer().getName() + "'s stock.";
	}

	@Override
	public DiscordChannel[] getUsableChannels() {
		return DiscordChannel.STANDARD_CHANNELS;
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

		try (ResultSet res = JDBC.executeQuery("select count(*) as count from eggs where player = %d;", CustomPlayer.EggSalesman.getIdLong())) {
			if (res.next()) {
				event.getChannel().sendMessageFormat("%s, %s has %,d eggs in stock.", p.getAsMention(), CustomPlayer.EggSalesman.getPlayer().getName(), res.getInt("count")).complete();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
