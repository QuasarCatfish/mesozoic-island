package com.quas.mesozoicisland.cmdadmin;

import java.util.regex.Pattern;

import com.quas.mesozoicisland.JDBC;
import com.quas.mesozoicisland.cmdbase.ICommand;
import com.quas.mesozoicisland.enums.AccessLevel;
import com.quas.mesozoicisland.enums.DiscordChannel;
import com.quas.mesozoicisland.enums.DiscordRole;
import com.quas.mesozoicisland.util.Util;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class GenerateRedeemCommand implements ICommand {

	@Override
	public Pattern getCommand() {
		return pattern("redeem generate .+");
	}

	@Override
	public AccessLevel getAccessLevel() {
		return AccessLevel.Admin;
	}

	@Override
	public String getCommandName() {
		return "redeem";
	}

	@Override
	public String getCommandSyntax() {
		return "redeem generate <prize>";
	}

	@Override
	public String getCommandDescription() {
		return "Generates a redeem code. Valid prizes: `item <id> <dmg> [count]`, `dino <dex> <form> [count]`, `egg <dex> <form>`, `rune <id> [count]`.";
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
		String prize = Util.join(args, " ", 1, args.length) + " ";
		
		if (prize.length() <= 100 && prize.matches(String.format("((item %s %s( %s)? )|(dino %s %s( %s)? )|(rune %s( %s)? )|(egg %s %s )|(curse )|(quest %s ))+", INTEGER, LONG, LONG, INTEGER, INTEGER, INTEGER, INTEGER, INTEGER, INTEGER, INTEGER, INTEGER, INTEGER))) {
			String code = Util.generateRandomString(16);
			JDBC.executeUpdate("insert into redeems(redeem, reward) values('%s', '%s');", Util.cleanQuotes(code), Util.cleanQuotes(prize));
			event.getChannel().sendMessageFormat("%s, the redeem code was generated successfully.", event.getAuthor().getAsMention()).complete();
			event.getAuthor().openPrivateChannel().complete().sendMessage(code).complete();
		} else {
			event.getChannel().sendMessageFormat("%s, the associated prize is invalid.", event.getAuthor().getAsMention()).complete();
		}
	}
}
