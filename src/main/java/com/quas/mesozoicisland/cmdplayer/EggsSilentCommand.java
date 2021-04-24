package com.quas.mesozoicisland.cmdplayer;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.StringJoiner;
import java.util.regex.Pattern;

import com.quas.mesozoicisland.JDBC;
import com.quas.mesozoicisland.cmdbase.ICommand;
import com.quas.mesozoicisland.enums.AccessLevel;
import com.quas.mesozoicisland.enums.DinosaurForm;
import com.quas.mesozoicisland.enums.DiscordChannel;
import com.quas.mesozoicisland.enums.DiscordRole;
import com.quas.mesozoicisland.enums.ItemID;
import com.quas.mesozoicisland.objects.Egg;
import com.quas.mesozoicisland.objects.Item;
import com.quas.mesozoicisland.objects.Player;

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
		
		Item standardIncubator = Item.getItem(ItemID.EggIncubator);
		long standardIncubatorCount = p.getItemCount(ItemID.EggIncubator);
		Item chaosIncubator = Item.getItem(ItemID.ChaosIncubator);
		long chaosIncubatorCount = p.getItemCount(ItemID.ChaosIncubator);
		
		ArrayList<String> eggs = new ArrayList<String>();
		int standardEggCount = 0;
		int chaosEggCount = 0;

		try (ResultSet res = JDBC.executeQuery("select * from eggs where player = %d order by incubator;", p.getIdLong())) {
			while (res.next()) {
				Egg egg = Egg.getEgg(res.getInt("eggid"));
				if (egg.getForm() == DinosaurForm.Chaos.getId()) chaosEggCount++;
				else standardEggCount++;
				if (egg.isHatchable()) eggs.add("E" + egg.getIncubatorSlot());
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		StringBuilder sb = new StringBuilder();
		sb.append(String.format("**%s's Incubators:**", p.getAsMention()));
		sb.append(String.format("\nIn Use: %,d of %,d %s", standardEggCount, standardIncubatorCount, standardIncubator.toString(standardIncubatorCount)));
		if (chaosIncubatorCount > 0 || chaosEggCount > 0) sb.append(String.format("\nIn Use: %,d of %,d %s", chaosEggCount, chaosIncubatorCount, chaosIncubator.toString(chaosIncubatorCount)));
		
		StringJoiner sj = new StringJoiner(", ");
		sj.setEmptyValue("None");
		for (String egg : eggs) sj.add(egg);
		
		sb.append("\nHatchable Eggs: ");
		sb.append(sj.toString());
		
		event.getChannel().sendMessage(sb.toString()).complete();
	}
}
