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
import com.quas.mesozoicisland.objects.Dinosaur;
import com.quas.mesozoicisland.objects.Player;
import com.quas.mesozoicisland.util.Constants;
import com.quas.mesozoicisland.util.Util;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class WhoHasCommand implements ICommand {

	@Override
	public Pattern getCommand() {
		return pattern("who has .+");
	}

	@Override
	public AccessLevel getAccessLevel() {
		return AccessLevel.Trainer;
	}

	@Override
	public String getCommandName() {
		return "who has";
	}

	@Override
	public String getCommandSyntax() {
		return "who has <dinosaur>";
	}

	@Override
	public String getCommandDescription() {
		return "Lists the players who are able to trade the given dinosaur.";
	}

	@Override
	public DiscordChannel[] getUsableChannels() {
		return DiscordChannel.TRADE_CHANNELS_BOT_DMS;
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

		Dinosaur d = null;
		String s = Util.join(args, " ", 1, args.length);
		if (s.matches(DINOSAUR)) {
			d = Dinosaur.getDinosaur(Util.getDexForm(s));
		} else {
			try (ResultSet res = JDBC.executeQuery("select * from dinosaurs where lower(dinoname) = '%s' and rarity >= 0;", Util.cleanQuotes(s.toLowerCase()))) {
				if (res.next()) {
					d = Dinosaur.getDinosaur(res.getInt("dex"), res.getInt("form"));
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

		// Invalid Dinosaur
		if (d == null || d.getDex() < 0) {
			event.getChannel().sendMessageFormat("%s, I could not find the given dinosaur.", p.getAsMention()).complete();
			return;
		}

		try (ResultSet res = JDBC.executeQuery("select count(*) as count from captures where dex = %d and form = %d and rp > 0;", d.getDex(), d.getForm())) {
			if (res.next()) {
				if (res.getInt("count") == 0) {
					event.getChannel().sendMessageFormat("%s, no player has a tradable %s.", p.getAsMention(), d.getDinosaurName()).complete();
					return;
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		ArrayList<String> print = new ArrayList<String>();
		print.add(String.format("%s, here are the players who have a tradable %s:", p.getAsMention(), d.getDinosaurName()));

		try (ResultSet res = JDBC.executeQuery("select * from captures where dex = %d and form = %d and rp > 0 order by rp desc;", d.getDex(), d.getForm())) {
			while (res.next()) {
				Player owner = Player.getPlayer(res.getLong("player"));
				print.add(String.format("%s %s - %,d RP", Constants.BULLET_POINT, owner.getName(), res.getInt("rp")));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		for (String string : Util.bulkify(print)) {
			event.getChannel().sendMessage(string).complete();
		}
	}
}
