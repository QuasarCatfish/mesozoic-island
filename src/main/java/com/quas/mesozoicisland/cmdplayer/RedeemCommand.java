package com.quas.mesozoicisland.cmdplayer;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.regex.Pattern;

import com.quas.mesozoicisland.JDBC;
import com.quas.mesozoicisland.cmdbase.ICommand;
import com.quas.mesozoicisland.enums.AccessLevel;
import com.quas.mesozoicisland.enums.DiscordChannel;
import com.quas.mesozoicisland.enums.DiscordRole;
import com.quas.mesozoicisland.objects.Player;
import com.quas.mesozoicisland.util.Util;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class RedeemCommand implements ICommand {

	@Override
	public Pattern getCommand() {
		return pattern("redeem \\w+");
	}

	@Override
	public AccessLevel getAccessLevel() {
		return AccessLevel.Trainer;
	}

	@Override
	public String getCommandName() {
		return "redeem";
	}

	@Override
	public String getCommandSyntax() {
		return "redeem <code>";
	}

	@Override
	public String getCommandDescription() {
		return "Redeems the prize associated with the code.";
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
	public synchronized void run(MessageReceivedEvent event, String... args) {
		Player p = Player.getPlayer(event.getAuthor().getIdLong());
		if (p == null) return;
		
		try (ResultSet res = JDBC.executeQuery("select * from redeems where redeem = '%s';", Util.cleanQuotes(args[0]))) {
			if (res.next()) {
				String white = res.getString("whitelist");
				String black = res.getString("blacklist");
				boolean allowed = true;
				
				if (white != null) {
					allowed = false;
					for (String id : white.split("\\s+")) {
						if (id.equals(p.getId())) allowed = true;
					}
				}
				
				if (black != null) {
					for (String id : black.split("\\s+")) {
						if (id.equals(p.getId())) allowed = false;
					}
				}
				
				if (allowed) {
					event.getChannel().sendMessageFormat("%s, you have redeemed the following:%n%s", p.getAsMention(), JDBC.getRedeemMessage(res.getString("reward"))).complete();
					JDBC.redeem(event.getChannel(), p.getIdLong(), res.getString("reward"));
					JDBC.executeUpdate("delete from redeems where redeem = '%s';", Util.cleanQuotes(args[0]));
				} else {
					event.getChannel().sendMessageFormat("%s, there is no such redeem code.", p.getAsMention()).complete();
				}
			} else {
				event.getChannel().sendMessageFormat("%s, there is no such redeem code.", p.getAsMention()).complete();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
