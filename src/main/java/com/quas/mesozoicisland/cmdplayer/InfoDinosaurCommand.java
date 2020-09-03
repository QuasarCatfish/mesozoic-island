package com.quas.mesozoicisland.cmdplayer;

import java.util.regex.Pattern;

import com.quas.mesozoicisland.cmdbase.ICommand;
import com.quas.mesozoicisland.enums.AccessLevel;
import com.quas.mesozoicisland.enums.DiscordChannel;
import com.quas.mesozoicisland.enums.DiscordRole;
import com.quas.mesozoicisland.objects.Dinosaur;
import com.quas.mesozoicisland.objects.Player;
import com.quas.mesozoicisland.util.Constants;
import com.quas.mesozoicisland.util.Util;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class InfoDinosaurCommand implements ICommand {

	@Override
	public Pattern getCommand() {
		return pattern("info ", DINOSAUR);
	}

	@Override
	public AccessLevel getAccessLevel() {
		return AccessLevel.Trainer;
	}

	@Override
	public String getCommandName() {
		return "info";
	}

	@Override
	public String getCommandSyntax() {
		return "info <dinosaur>";
	}

	@Override
	public String getCommandDescription() {
		return "Gives information on the chosen dinosaur.";
	}

	@Override
	public DiscordChannel[] getUsableChannels() {
		return DiscordChannel.STANDARD_CHANNELS_TRADE_DMS;
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
		if (d == null) {
			event.getChannel().sendMessageFormat("%s, I could not find the given dinosaur.", event.getAuthor().getAsMention()).complete();
			return;
		}
		
		EmbedBuilder eb = new EmbedBuilder();
		eb.setTitle(d.getEffectiveName());
		eb.setColor(Constants.COLOR);
		
		eb.addField("Dex Number", d.getDex() < 0 ? "#" + Util.mult("?", Constants.MAX_DEX_DIGITS) : String.format("#%0" + Constants.MAX_DEX_DIGITS + "d", d.getDex()), true);
		if (d.getForm() != 0) eb.addField("Form", d.getFormName(), true);
		eb.addField("Owner", p.getName(), true);
		eb.addField("Element", d.getElement().getName(), true);
		eb.addField("Rarity", d.getRarity().getName(), true);
		eb.addField("Level and XP", String.format("Level %,d + %,d XP", d.getLevel(), d.getXpMinusLevel()), true);
		eb.addField("Rank and RP", String.format("Rank %s + %,d RP", d.getRankString(), d.getRp()), true);
		eb.addField("Health", String.format("%,d (+%d%%)", d.getHealth(), d.getHealthMultiplier()), true);
		eb.addField("Attack", String.format("%,d (+%d%%)", d.getAttack(), d.getAttackMultiplier()), true);
		eb.addField("Defense", String.format("%,d (+%d%%)", d.getDefense(), d.getDefenseMultiplier()), true);
		eb.addField("Dinosaurs Defeated", Util.formatNumber(d.getWins()), true);
		eb.addField("Times Defeated", Util.formatNumber(d.getLosses()), true);
		if (d.getItem() != null && d.getItem().getId() != 0) eb.addField("Held Item", d.getItem().toString(), true);
		if (d.getRune() != null && d.getRune().getId() != 0) eb.addField("Rune", d.getRune().getName(), true);
		
		event.getChannel().sendMessage(eb.build()).complete();
	}
}
