package com.quas.mesozoicisland.cmdplayer;

import java.util.regex.Pattern;

import com.quas.mesozoicisland.JDBC;
import com.quas.mesozoicisland.cmdbase.ICommand;
import com.quas.mesozoicisland.enums.AccessLevel;
import com.quas.mesozoicisland.enums.DiscordChannel;
import com.quas.mesozoicisland.enums.DiscordRole;
import com.quas.mesozoicisland.objects.Dinosaur;
import com.quas.mesozoicisland.objects.Player;
import com.quas.mesozoicisland.util.Util;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class UnequipDinosaurCommand implements ICommand {

	@Override
	public Pattern getCommand() {
		return pattern("(un|d)equip ", DINOSAUR);
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
		return "unequip <dinosaur>";
	}

	@Override
	public String getCommandDescription() {
		return "Unequips a rune attached to the dinosaur.";
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
		
		Dinosaur d = Dinosaur.getDinosaur(p.getIdLong(), Util.getDexForm(args[0]));
		if (d == null) {
			event.getChannel().sendMessageFormat("%s, your dinosaur does not exists.", event.getAuthor().getAsMention()).complete();
		} else if (d.getRune() == null || d.getRune().getId() == 0) {
			event.getChannel().sendMessageFormat("%s, your %s is not equipped with any rune.", event.getAuthor().getAsMention(), d.getEffectiveName()).complete();
		} else {
			event.getChannel().sendMessageFormat("%s, your %s is no longer equipped with the %s rune.", event.getAuthor().getAsMention(), d.getEffectiveName(), d.getRune().getName()).complete();
			JDBC.unequipRune(p.getIdLong(), d.getIdPair(), d.getRune().getId());
		}
	}
}
