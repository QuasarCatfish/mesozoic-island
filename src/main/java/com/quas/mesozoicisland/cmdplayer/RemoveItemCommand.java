package com.quas.mesozoicisland.cmdplayer;

import java.util.regex.Pattern;

import com.quas.mesozoicisland.JDBC;
import com.quas.mesozoicisland.cmdbase.ICommand;
import com.quas.mesozoicisland.enums.AccessLevel;
import com.quas.mesozoicisland.enums.DiscordChannel;
import com.quas.mesozoicisland.enums.DiscordRole;
import com.quas.mesozoicisland.enums.ItemID;
import com.quas.mesozoicisland.objects.Dinosaur;
import com.quas.mesozoicisland.objects.Player;
import com.quas.mesozoicisland.util.Util;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class RemoveItemCommand implements ICommand {

	@Override
	public Pattern getCommand() {
		return pattern("(remove item|removeitem|ri) ", DINOSAUR);
	}

	@Override
	public AccessLevel getAccessLevel() {
		return AccessLevel.Trainer;
	}

	@Override
	public String getCommandName() {
		return "removeitem";
	}

	@Override
	public String getCommandSyntax() {
		return "removeitem <dinosaur>";
	}

	@Override
	public String getCommandDescription() {
		return "Returns the given dinosaur's held item to your bag.";
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
	public void run(MessageReceivedEvent event, String... args) {
		Player p = Player.getPlayer(event.getAuthor().getIdLong());
		if (p == null) return;

		Dinosaur d = Dinosaur.getDinosaur(p.getIdLong(), Util.getDexForm(args[args.length - 1]));
		if (d == null) {
			event.getChannel().sendMessageFormat("%s, I could not find the given dinosaur.", p.getAsMention()).complete();
			return;
		}

		if (d.hasItem()) {
			event.getChannel().sendMessageFormat("%s, the %s has been removed from your %s.", p.getAsMention(), d.getItem().toString(), d.getEffectiveName()).complete();
			JDBC.addItem(p.getIdLong(), d.getItem().getIdDmg());
			JDBC.setItem(p.getIdLong(), d.getIdPair(), ItemID.Nothing.getId());
		} else {
			event.getChannel().sendMessageFormat("%s, your %s is not holding an item.", p.getAsMention(), d.getEffectiveName()).complete();
		}
	}
}