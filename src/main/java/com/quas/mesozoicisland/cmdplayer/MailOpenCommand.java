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

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class MailOpenCommand implements ICommand {

	@Override
	public Pattern getCommand() {
		return pattern("mail(box)? open ", INTEGER);
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
		return "mail open <id>";
	}

	@Override
	public String getCommandDescription() {
		return "Opens the mail with the given ID.";
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
		
		try (ResultSet res = JDBC.executeQuery("select * from mail where id = %s;", args[1])) {
			if (res.next()) {
				if (res.getLong("player") == p.getIdLong()) {
					String msg = String.format("\n\n**%s** (from %s)\n> %s", res.getString("name"), res.getString("from"), res.getString("message"));
					boolean hasPrize = res.getString("reward") != null && !res.getBoolean("opened");
					String prize = hasPrize ? "\n\nInside you also find:\n" + JDBC.getRedeemMessage(res.getString("reward")) : "";
					event.getChannel().sendMessageFormat("%s opens the piece of mail:%s%s", p.getAsMention(), msg, prize).complete();
					if (hasPrize) JDBC.redeem(event.getChannel(), p.getIdLong(), res.getString("reward"));
					JDBC.executeUpdate("update mail set opened = true where id = %d;", res.getInt("id"));
				} else {
					event.getChannel().sendMessageFormat("%s, this piece of mail isn't yours.", p.getAsMention()).complete();
				}
			} else {
				event.getChannel().sendMessageFormat("%s, this piece of mail does not exist.", p.getAsMention()).complete();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
