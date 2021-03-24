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

import net.dv8tion.jda.api.entities.PrivateChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class EggsCommand implements ICommand {

	@Override
	public Pattern getCommand() {
		return pattern("eggs");
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
		return "eggs";
	}

	@Override
	public String getCommandDescription() {
		return "Lists all the eggs the player owns.";
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
		long hatch = 0;
		
		ArrayList<Egg> eggs = new ArrayList<Egg>();
		try (ResultSet res = JDBC.executeQuery("select * from eggs where player = %d order by incubator;", p.getIdLong())) {
			while (res.next()) {
				Egg egg = Egg.getEgg(res.getInt("eggid"));
				eggs.add(egg);
				if (egg.isHatchable()) hatch++;
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
		sb.append(Util.formatNumber(eggs.size()));
		sb.append("/");
		sb.append(Util.formatNumber(incubators));
		
		sb.append("\nHatchable Eggs: ");
		sb.append(Util.formatNumber(hatch));
		sb.append("/");
		sb.append(Util.formatNumber(eggs.size()));
		
		sb.append("\nYou'll find a full list of your egg incubators in your DMs.");
		event.getChannel().sendMessage(sb.toString()).complete();
		
		ArrayList<String> print = new ArrayList<String>();
		print.add("**Your " + inc.toString(incubators) + ":**");
		for (Egg egg : eggs) {
			if (egg.isHatchable()) {
				print.add(String.format("E%,d) %s (Ready to Hatch)", egg.getIncubatorSlot(), egg.toString()));
			} else {
				print.add(String.format("E%,d) %s (%,d of %,d Hatch Points)", egg.getIncubatorSlot(), egg.toString(), egg.getCurrentHatchPoints(), egg.getMaxHatchPoints()));
			}
		}

		if (incubators > eggs.size()) print.add(" + " + (incubators - eggs.size()) + " empty " + inc.toString(incubators - eggs.size()));
		
		PrivateChannel pc = event.getAuthor().openPrivateChannel().complete();
		for (String msg : Util.bulkify(print)) {
			pc.sendMessage(msg).complete();
		}
	}
}
