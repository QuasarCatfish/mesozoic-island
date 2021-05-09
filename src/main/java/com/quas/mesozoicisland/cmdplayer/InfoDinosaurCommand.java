package com.quas.mesozoicisland.cmdplayer;

import java.util.regex.Pattern;

import com.quas.mesozoicisland.cmdbase.ICommand;
import com.quas.mesozoicisland.enums.AccessLevel;
import com.quas.mesozoicisland.enums.DinosaurForm;
import com.quas.mesozoicisland.enums.DiscordChannel;
import com.quas.mesozoicisland.enums.DiscordRole;
import com.quas.mesozoicisland.objects.Dinosaur;
import com.quas.mesozoicisland.objects.Player;
import com.quas.mesozoicisland.util.Constants;
import com.quas.mesozoicisland.util.DinoMath;
import com.quas.mesozoicisland.util.Util;
import com.quas.mesozoicisland.util.Zalgo;

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
	public synchronized void run(MessageReceivedEvent event, String... args) {
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
		
		if (event.getChannel().getIdLong() == DiscordChannel.Game.getIdLong()) {
			if (d.getDinosaurForm() == DinosaurForm.Accursed) {
				eb.addField(Zalgo.title("Dex and Form"), String.format("%s\n%s", Zalgo.field("#" + d.getId()), Zalgo.field(d.getFormName())), true);
				eb.addField(Zalgo.title("Owner"), Zalgo.field(p.getName()), true);
				eb.addField(Zalgo.title("Element"), Zalgo.field(d.getElement().toString()), true);

				if (d.getItem() != null && d.getItem().getId() != 0) eb.addField(Zalgo.title("Held Item"), Zalgo.field(d.getItem().toString()), true);
				if (d.getRune() != null && d.getRune().getId() != 0) eb.addField(Zalgo.title("Rune"), Zalgo.field(d.getRune().getName()), true);
			} else {
				if (d.getDinosaurForm() == DinosaurForm.Standard) {
					eb.addField("Dex", "#" + d.getId(), true);
				} else if (d.getDinosaurForm().getOwnedEmote() == null) {
					eb.addField("Dex and Form", String.format("#%s\n%s", d.getId(), d.getFormName()), true);
				} else {
					eb.addField("Dex and Form", String.format("#%s\n%s %s", d.getId(), d.getFormName(), d.getDinosaurForm().getOwnedEmote().getEmote().getAsMention()), true);
				}
				eb.addField("Owner", p.getName(), true);
				eb.addField("Element and Rarity", String.format("%s\n%s", d.getElement().getAsString(), d.getRarity().getAsString()), true);
				
				eb.addField("Level and Rank", String.format("Level %,d + %,d XP\nRank %s + %,d RP", d.getLevel(), d.getXpMinusLevel(), d.getRankString(), d.getRp()), true);
				eb.addField("Battle Tier", String.format("%s + %,d%%", DinoMath.getBattleTier(d).toString(), DinoMath.getNextBattleTierPercent(d)), true);

				boolean item = d.getItem() != null && d.getItem().getId() != 0;
				boolean rune = d.getRune() != null && d.getRune().getId() != 0;
				if (item && rune) {
					eb.addField("Held Item and Rune", String.format("%s\n%s", d.getItem().toString(), d.getRune().toString()), true);
				} else if (item) {
					eb.addField("Held Item", d.getItem().toString(), true);
				} else if (rune) {
					eb.addField("Rune", d.getRune().getName(), true);
				}
			}
		} else {
			if (d.getDinosaurForm() == DinosaurForm.Accursed) {
				eb.addField(Zalgo.title("Dex Number"), Zalgo.field("#" + d.getId()), true);
				eb.addField(Zalgo.title("Form"), Zalgo.field(d.getFormName()), true);
				eb.addField(Zalgo.title("Owner"), Zalgo.field(p.getName()), true);

				eb.addField(Zalgo.title("Element"), Zalgo.field(d.getElement().toString()), true);
				eb.addField(Zalgo.title("Dinosaurs Defeated"), Zalgo.field(Util.formatNumber(d.getWins())), true);
				eb.addField(Zalgo.title("Times Defeated"), Zalgo.field(Util.formatNumber(d.getLosses())), true);

				if (d.getItem() != null && d.getItem().getId() != 0) eb.addField(Zalgo.title("Held Item"), Zalgo.field(d.getItem().toString()), true);
				if (d.getRune() != null && d.getRune().getId() != 0) eb.addField(Zalgo.title("Rune"), Zalgo.field(d.getRune().getName()), true);
			} else {
				eb.addField("Dex Number", "#" + d.getId(), true);
				if (d.getDinosaurForm() != DinosaurForm.Standard) {
					if (d.getDinosaurForm().getOwnedEmote() == null) {
						eb.addField("Form", d.getFormName(), true);
					} else {
						eb.addField("Form", String.format("%s %s", d.getFormName(), d.getDinosaurForm().getOwnedEmote().getEmote().getAsMention()), true);
					}
				}
				eb.addField("Owner", p.getName(), true);
				eb.addField("Element", d.getElement().getAsString(), true);
				eb.addField("Rarity", d.getRarity().getAsString(), true);

				// level
				if (d.getXp() >= Constants.MAX_DINOSAUR_XP) {
					eb.addField("Level and XP", String.format("Level %,d (MAX)", d.getLevel()), true);
				} else {
					eb.addField("Level and XP", String.format("Level %,d + %,d XP\n(%,d XP to Level Up)", d.getLevel(), d.getXpMinusLevel(), DinoMath.getXp(d.getLevel() + 1) - d.getXp()), true);
				}
				
				// rank
				if (d.getRank() >= Constants.MAX_RANK) {
					eb.addField("Rank and RP", String.format("Rank %s + %,d RP\n(Max Rank Reached)", d.getRankString(), d.getRp()), true);
				} else if (d.canRankup()) {
					eb.addField("Rank and RP", String.format("Rank %s + %,d RP\n(Rankup Available)", d.getRankString(), d.getRp()), true);
				} else {
					eb.addField("Rank and RP", String.format("Rank %s + %,d RP\n(%,d RP to Rankup)", d.getRankString(), d.getRp(), d.getRpToRankup()), true);
				}
				
				eb.addField("Battle Tier", String.format("%s + %,d%%", DinoMath.getBattleTier(d).toString(), DinoMath.getNextBattleTierPercent(d)), true);
				eb.addField("Health", String.format("%,d (+%d%%)", d.getHealth(), d.getHealthMultiplier()), true);
				eb.addField("Attack", String.format("%,d (+%d%%)", d.getAttack(), d.getAttackMultiplier()), true);
				eb.addField("Defense", String.format("%,d (+%d%%)", d.getDefense(), d.getDefenseMultiplier()), true);
				eb.addField("Dinosaurs Defeated", Util.formatNumber(d.getWins()), true);
				eb.addField("Times Defeated", Util.formatNumber(d.getLosses()), true);
				if (d.getItem() != null && d.getItem().getId() != 0) eb.addField("Held Item", d.getItem().toString(), true);
				if (d.getRune() != null && d.getRune().getId() != 0) eb.addField("Rune", d.getRune().getName(), true);
			}
		}

		
		event.getChannel().sendMessage(eb.build()).complete();
	}
}
