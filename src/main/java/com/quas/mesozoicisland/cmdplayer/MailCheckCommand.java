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

public class MailCheckCommand implements ICommand {

	@Override
	public Pattern getCommand() {
		return pattern("mail(box)?");
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
		return "mail";
	}

	@Override
	public String getCommandDescription() {
		return "Checks what mail you have pending in your mailbox.";
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
		
		ArrayList<String> print = new ArrayList<String>();
		
		DiscordChannel dc = DiscordChannel.getChannel(event.getChannel());
		int count = 0;
		
		try (ResultSet res = JDBC.executeQuery("select * from mail where player = %d;", p.getIdLong())) {
			while (res.next()) {
				if (res.getBoolean("opened")) continue;
				count++;
				if (dc == DiscordChannel.DirectMessages || count <= Constants.PUBLIC_MAIL_DISPLAY) {
					print.add(String.format("%d - **%s** (from %s)", res.getInt("id"), res.getString("name"), res.getString("from")));
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		if (dc != DiscordChannel.DirectMessages && count > Constants.PUBLIC_MAIL_DISPLAY) {
			print.add(String.format("+ %,d more messages", count - Constants.PUBLIC_MAIL_DISPLAY));
		}
		
		if (print.isEmpty()) {
			event.getChannel().sendMessageFormat("%s, you don't have pending mail. To see all your mail, use the command `mail all` in DMs with ELISE.", p.getAsMention()).complete();
		} else {
			print.add(0, String.format("%s, here is your pending mail:", p.getAsMention()));
			print.add("You can open mail with the `mail open <id>` command.");
			for (String s : Util.bulkify(print)) {
				event.getChannel().sendMessage(s).complete();
			}
		}
	}
}
