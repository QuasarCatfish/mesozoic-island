package com.quas.mesozoicisland.cmdplayer;

import java.util.regex.Pattern;

import com.quas.mesozoicisland.cmdbase.ICommand;
import com.quas.mesozoicisland.enums.AccessLevel;
import com.quas.mesozoicisland.enums.DiscordChannel;
import com.quas.mesozoicisland.enums.DiscordRole;
import com.quas.mesozoicisland.objects.Dinosaur;
import com.quas.mesozoicisland.objects.Player;
import com.quas.mesozoicisland.objects.SnackModule;
import com.quas.mesozoicisland.util.Constants;
import com.quas.mesozoicisland.util.Util;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class FeedCommand implements ICommand {

	@Override
	public Pattern getCommand() {
		return pattern("feed ", DINOSAUR, " (max|", INTEGER, ")");
	}

	@Override
	public AccessLevel getAccessLevel() {
		return AccessLevel.Trainer;
	}

	@Override
	public String getCommandName() {
		return "feed";
	}

	@Override
	public String getCommandSyntax() {
		return "feed <dinosaur> <amount>";
	}

	@Override
	public String getCommandDescription() {
		return "Feeds the given dinosaur the specified number of snacks, or `max`, if able.";
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

		Dinosaur d = Dinosaur.getDinosaur(p.getIdLong(), Util.getDexForm(args[0]));
		if (d == null) {
			event.getChannel().sendMessageFormat("%s, I could not find the given dinosaur.", p.getAsMention()).complete();
			return;
		}

		int amount = 0;
		if (args[1].equals("max")) {
			amount = 3 * Constants.MAX_STAT_BOOST;
		} else {
			amount = Integer.parseInt(args[1]);
		}

		if (amount <= 0) {
			event.getChannel().sendMessageFormat("%s, that is an invalid number of snacks to your dinosaur.", p.getAsMention()).complete();
			return;
		}

		SnackModule sm = new SnackModule(p, d, amount);
		event.getChannel().sendMessage(sm.getResult()).complete();
	}
}