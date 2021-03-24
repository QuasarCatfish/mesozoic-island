package com.quas.mesozoicisland.cmdadmin;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Month;
import java.util.regex.Pattern;

import com.quas.mesozoicisland.JDBC;
import com.quas.mesozoicisland.cmdbase.ICommand;
import com.quas.mesozoicisland.enums.AccessLevel;
import com.quas.mesozoicisland.enums.DiscordChannel;
import com.quas.mesozoicisland.enums.DiscordRole;
import com.quas.mesozoicisland.util.Constants;
import com.quas.mesozoicisland.util.MesozoicCalendar;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class CheckBirthdaysCommand implements ICommand {

	@Override
	public Pattern getCommand() {
		return pattern("admin birthday");
	}

	@Override
	public AccessLevel getAccessLevel() {
		return AccessLevel.Admin;
	}

	@Override
	public String getCommandName() {
		return "admin";
	}

	@Override
	public String getCommandSyntax() {
		return "admin birthday";
	}

	@Override
	public String getCommandDescription() {
		return "Checks what birthdays are happening soon.";
	}

	@Override
	public DiscordChannel[] getUsableChannels() {
		return DiscordChannel.ALL_CHANNELS;
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
		
		int month = new MesozoicCalendar().get(MesozoicCalendar.MONTH);
		if (month == 0) month = 12;
		
		EmbedBuilder eb = new EmbedBuilder();
		eb.setTitle("Nearby Birthdays");
		eb.setColor(Constants.COLOR);
		
		for (int q = 0; q < 4; q++, month = month == 12 ? 1 : month + 1) {
			eb.addField(Month.of(month).toString(), getBirthdays(month), false);
		}
		
		event.getChannel().sendMessage(eb.build()).complete();
		
	}
	
	public String getBirthdays(int month) {
		StringBuilder sb = new StringBuilder();
		
		try (ResultSet res = JDBC.executeQuery("select * from players where birthday > %d and birthday < %d order by birthday;", 100 * month, 100 * (month + 1))) {
			while (res.next()) {
				sb.append(res.getInt("birthday") % 100);
				sb.append(" - ");
				sb.append(res.getString("playername"));
				sb.append(" [");
				sb.append(res.getLong("playerid"));
				sb.append("]\n");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		if (sb.length() == 0) sb.append("None");
		return sb.toString();
	}
}
