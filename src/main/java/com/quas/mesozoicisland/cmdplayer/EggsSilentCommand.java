package com.quas.mesozoicisland.cmdplayer;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.regex.Pattern;

import com.quas.mesozoicisland.JDBC;
import com.quas.mesozoicisland.cmdbase.ICommand;
import com.quas.mesozoicisland.enums.AccessLevel;
import com.quas.mesozoicisland.enums.DiscordChannel;
import com.quas.mesozoicisland.enums.DiscordRole;
import com.quas.mesozoicisland.enums.ItemID;
import com.quas.mesozoicisland.objects.Egg;
import com.quas.mesozoicisland.objects.Item;
import com.quas.mesozoicisland.objects.Player;
import com.quas.mesozoicisland.util.Util;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class EggsSilentCommand implements ICommand {

	@Override
	public Pattern getCommand() {
		return pattern("eggs (silent|nodm|count)");
	}

	@Override
	public AccessLevel getAccessLevel() {
		return AccessLevel.Trainer;
	}

	@Override
	public String getCommandName() {
		return "eggs";
	}

	@Override
	public String getCommandSyntax() {
		return "eggs silent";
	}

	@Override
	public String getCommandDescription() {
		return "Lists the basic information about the eggs a player owns.";
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
		
		Item inc = Item.getItem(ItemID.EggIncubator);
		long incubators = p.getBag().getOrDefault(inc, 0L);
		
		ArrayList<String> eggs = new ArrayList<String>();
		int eggcount = 0;

		try (ResultSet res = JDBC.executeQuery("select * from eggs where player = %d order by incubator;", p.getIdLong())) {
			while (res.next()) {
				Egg egg = Egg.getEgg(res.getInt("eggid"));
				eggcount++;
				if (egg.isHatchable()) eggs.add("E" + egg.getIncubatorSlot());
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		StringBuilder sb = new StringBuilder();
		sb.append("**");
		sb.append(p.getAsMention());
		sb.append("'s ");
		sb.append(inc.toString(incubators));
		sb.append(":**");
		
		sb.append("\nIn Use: ");
		sb.append(Util.formatNumber(eggcount));
		sb.append("/");
		sb.append(Util.formatNumber(incubators));
		
		sb.append("\nHatchable Eggs: ");
		if (eggs.isEmpty()) {
			sb.append("None");
		} else {
			sb.append(String.join(", ", eggs));
		}
		
		event.getChannel().sendMessage(sb.toString()).complete();
	}
}
