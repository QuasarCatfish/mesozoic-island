package com.quas.mesozoicisland.cmdplayer;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import com.quas.mesozoicisland.JDBC;
import com.quas.mesozoicisland.cmdbase.ICommand;
import com.quas.mesozoicisland.enums.AccessLevel;
import com.quas.mesozoicisland.enums.DiscordChannel;
import com.quas.mesozoicisland.enums.DiscordRole;
import com.quas.mesozoicisland.objects.Dinosaur;
import com.quas.mesozoicisland.objects.Player;
import com.quas.mesozoicisland.objects.Rune;
import com.quas.mesozoicisland.util.Util;

import net.dv8tion.jda.api.entities.PrivateChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class RunesCommand implements ICommand {

	@Override
	public Pattern getCommand() {
		return pattern("runes");
	}

	@Override
	public AccessLevel getAccessLevel() {
//		return AccessLevel.Trainer;
		return AccessLevel.Disabled;
	}

	@Override
	public String getCommandName() {
		return "runes";
	}

	@Override
	public String getCommandSyntax() {
		return "runes";
	}

	@Override
	public String getCommandDescription() {
		return "Lists the runes that you own.";
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
		
		StringBuilder sb = new StringBuilder();
		sb.append("**");
		sb.append(event.getAuthor().getAsMention());
		sb.append("'s Runes:**\n");
		
		sb.append(String.format("**Total:** %,d/%,d\n", p.getRuneCount(), JDBC.getRuneCount()));
		
		sb.append("You'll find a full list of your runes in your DMs.");
		event.getChannel().sendMessage(sb.toString()).complete();
		
		List<String> print = new ArrayList<String>();
		print.add("**Your Runes:**");
		
		try (ResultSet res = JDBC.executeQuery("select * from runepouches where player = %s;", p.getId())) {
			while (res.next()) {
				Rune r = Rune.getRune(p.getIdLong(), res.getInt("rune"));
				sb = new StringBuilder();
				sb.append(String.format("**%s**", r.toString()));
				sb.append(String.format(" [%s]", r.getElement().getName()));
				sb.append(String.format(" [%s]", r.getRarity().getName()));
				sb.append(String.format(" [Effect: %s]", r.getEffect()));
				if (r.isEquipped()) sb.append(String.format(" [Equipped: %s]", Dinosaur.getDinosaur(r.getPlayerId(), Util.getDexForm(r.getEquipped())).getEffectiveName()));
				print.add(sb.toString());
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		PrivateChannel pc = event.getAuthor().openPrivateChannel().complete();
		for (String msg : Util.bulkify(print)) {
			pc.sendMessage(msg).complete();
		}
	}
}
