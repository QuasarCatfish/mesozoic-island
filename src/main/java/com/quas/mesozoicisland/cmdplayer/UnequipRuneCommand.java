package com.quas.mesozoicisland.cmdplayer;

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

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class UnequipRuneCommand implements ICommand {

	@Override
	public Pattern getCommand() {
		return pattern("(un|d)equip ", RUNE);
	}

	@Override
	public AccessLevel getAccessLevel() {
//		return AccessLevel.Trainer;
		return AccessLevel.Disabled;
	}

	@Override
	public String getCommandName() {
		return "unequip";
	}

	@Override
	public String getCommandSyntax() {
		return "unequip <rune>";
	}

	@Override
	public String getCommandDescription() {
		return "Unequips the rune attached to a dinosaur.";
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
		
		Rune r = Rune.getRune(p.getIdLong(), Integer.parseInt(args[0].substring(1)));
		if (r == null) {
			event.getChannel().sendMessageFormat("%s, your rune does not exists.", event.getAuthor().getAsMention()).complete();
		} else if (!r.isEquipped()) {
			event.getChannel().sendMessageFormat("%s, your %s rune is not equipped to any dinosaur.", event.getAuthor().getAsMention(), r.getName()).complete();
		} else {
			Dinosaur d = Dinosaur.getDinosaur(p.getIdLong(), Util.getDexForm(r.getEquipped()));
			event.getChannel().sendMessageFormat("%s, your %s is no longer equipped with the %s rune.", event.getAuthor().getAsMention(), d.getEffectiveName(), r.getName()).complete();
			JDBC.unequipRune(p.getIdLong(), d.getIdPair(), r.getId());
		}
	}
}
