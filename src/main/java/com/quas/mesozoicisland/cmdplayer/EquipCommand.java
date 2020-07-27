package com.quas.mesozoicisland.cmdplayer;

import java.util.regex.Pattern;

import com.quas.mesozoicisland.JDBC;
import com.quas.mesozoicisland.cmdbase.CommandManager;
import com.quas.mesozoicisland.cmdbase.ICommand;
import com.quas.mesozoicisland.enums.AccessLevel;
import com.quas.mesozoicisland.enums.DiscordChannel;
import com.quas.mesozoicisland.enums.DiscordRole;
import com.quas.mesozoicisland.objects.Dinosaur;
import com.quas.mesozoicisland.objects.Player;
import com.quas.mesozoicisland.objects.Rune;
import com.quas.mesozoicisland.util.Util;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class EquipCommand implements ICommand {

	@Override
	public Pattern getCommand() {
		return pattern("equip ", DINOSAUR, " ", RUNE);
	}

	@Override
	public AccessLevel getAccessLevel() {
//		return AccessLevel.Trainer;
		return AccessLevel.Disabled;
	}

	@Override
	public String getCommandName() {
		return "equip";
	}

	@Override
	public String getCommandSyntax() {
		return "equip <dinosaur> <rune>";
	}

	@Override
	public String getCommandDescription() {
		return "Equips a rune to a dinosaur.";
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
		
		Dinosaur d = Dinosaur.getDinosaur(p.getIdLong(), Util.getDexForm(args[0]));
		Rune r = Rune.getRune(p.getIdLong(), Integer.parseInt(args[1].substring(1)));
		if (d == null && r == null) {
			event.getChannel().sendMessageFormat("%s, neither your dinosaur nor rune exists.", event.getAuthor().getAsMention()).complete();
		} else if (d == null) {
			event.getChannel().sendMessageFormat("%s, your dinosaur does not exists.", event.getAuthor().getAsMention()).complete();
		} else if (r == null) {
			event.getChannel().sendMessageFormat("%s, your rune does not exists.", event.getAuthor().getAsMention()).complete();
		} else if (d.getRune() != null && d.getRune().getId() == r.getId()) {
			event.getChannel().sendMessageFormat("%s, your %s is already equipped with the %s rune.", event.getAuthor().getAsMention(), d.getEffectiveName(), r.getName()).complete();
		} else if (d.getRune() != null && d.getRune().getId() != 0) {
			CommandManager.handleCommand(event, "unequip", d.getId());
			Util.sleep(100);
			event.getChannel().sendMessageFormat("%s, your %s is now equipped with the %s rune.", event.getAuthor().getAsMention(), d.getEffectiveName(), r.getName()).complete();
			JDBC.equipRune(p.getIdLong(), d.getIdPair(), r.getId());
		} else if (r.isEquipped()) {
			CommandManager.handleCommand(event, "unequip", d.getId());
			Util.sleep(100);
			event.getChannel().sendMessageFormat("%s, your %s is now equipped with the %s rune.", event.getAuthor().getAsMention(), d.getEffectiveName(), r.getName()).complete();
			JDBC.equipRune(p.getIdLong(), d.getIdPair(), r.getId());
		} else {
			event.getChannel().sendMessageFormat("%s, your %s is now equipped with the %s rune.", event.getAuthor().getAsMention(), d.getEffectiveName(), r.getName()).complete();
			JDBC.equipRune(p.getIdLong(), d.getIdPair(), r.getId());
		}
	}
}
