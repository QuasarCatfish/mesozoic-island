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
import com.quas.mesozoicisland.objects.Player;
import com.quas.mesozoicisland.util.Constants;
import com.quas.mesozoicisland.util.Util;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class MailCheckAllCommand implements ICommand {

	@Override
	public Pattern getCommand() {
		return pattern("mail(box)? all");
	}

	@Override
	public AccessLevel getAccessLevel() {
		return AccessLevel.Trainer;
	}

	@Override
	public String getCommandName() {
		return "mail";
	}

	@Override
	public String getCommandSyntax() {
		return "mail all";
	}

	@Override
	public String getCommandDescription() {
		return "Checks all mail you have in your mailbox.";
	}

	@Override
	public DiscordChannel[] getUsableChannels() {
		return Util.arr(DiscordChannel.DirectMessages);
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
		
		ArrayList<String> print = new ArrayList<String>();
		
		try (ResultSet res = JDBC.executeQuery("select * from mail where player = %d;", p.getIdLong())) {
			while (res.next()) {
				if (res.getBoolean("opened")) {
					print.add(String.format("%d - %s (from %s)", res.getInt("id"), res.getString("name"), res.getString("from")));
				} else {
					print.add(String.format("%d - **%s** (from %s)", res.getInt("id"), res.getString("name"), res.getString("from")));
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		if (print.isEmpty()) {
			event.getChannel().sendMessageFormat("%s, you don't have any mail.", p.getAsMention()).complete();
		} else {
			print.add(0, String.format("%s, here is all your mail:", p.getAsMention()));
			for (String s : Util.bulkify(print)) {
				event.getChannel().sendMessage(s).complete();
			}
		}
	}
}
