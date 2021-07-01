package com.quas.mesozoicisland.cmdplayer;

import java.util.StringJoiner;
import java.util.regex.Pattern;

import com.quas.mesozoicisland.JDBC;
import com.quas.mesozoicisland.cmdbase.ICommand;
import com.quas.mesozoicisland.enums.AccessLevel;
import com.quas.mesozoicisland.enums.DinosaurForm;
import com.quas.mesozoicisland.enums.DiscordChannel;
import com.quas.mesozoicisland.enums.DiscordRole;
import com.quas.mesozoicisland.enums.ItemID;
import com.quas.mesozoicisland.enums.Stat;
import com.quas.mesozoicisland.objects.Dinosaur;
import com.quas.mesozoicisland.objects.Item;
import com.quas.mesozoicisland.objects.Player;
import com.quas.mesozoicisland.objects.Rarity;
import com.quas.mesozoicisland.util.Constants;
import com.quas.mesozoicisland.util.DinoMath;
import com.quas.mesozoicisland.util.Util;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class RankupDinosaurCommand implements ICommand {

	@Override
	public Pattern getCommand() {
		return pattern("rankup ", DINOSAUR);
	}

	@Override
	public AccessLevel getAccessLevel() {
		return AccessLevel.Trainer;
	}

	@Override
	public String getCommandName() {
		return "rankup";
	}

	@Override
	public String getCommandSyntax() {
		return "rankup <dinosaur>";
	}

	@Override
	public String getCommandDescription() {
		return "Ranks up the given dinosaur, if able.";
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
		
		Dinosaur consume = getRankupConsume(d);
		Dinosaur pConsume = consume == null ? null : Dinosaur.getDinosaur(d.getPlayerId(), consume.getIdPair());
		int dinoReq = d == null ? 0 : d.getRpToRankup();
		int dinoCount = pConsume == null ? 0 : pConsume.getRp();
		long deltaDino = consume == null ? 0 : dinoReq - dinoCount;

		Item item = getRankupItem(d);
		long itemReq = getRankupItemCount(d) * dinoReq;
		long itemCount = p.getItemCount(item);
		long deltaItem = itemReq - itemCount;

		if (d == null) {
			event.getChannel().sendMessageFormat("%s, this dinosaur does not exist.", event.getAuthor().getAsMention()).complete();
		} else if (d.getRank() >= Constants.MAX_RANK) {
			event.getChannel().sendMessageFormat("%s, your %s is at the max rank and cannot rankup any further.", event.getAuthor().getAsMention(), d.getEffectiveName()).complete();
		} else if (!d.canRankup() && (item == null || deltaItem > 0 || deltaDino > 0)) {
			StringJoiner sj = new StringJoiner("\n");
			sj.add(String.format("%s an additional %,d RP", Constants.BULLET_POINT, d.getRpToRankup()));

			if (item != null) {
				if (consume == null) {
					sj.add(String.format("%s an additional %,d %s", Constants.BULLET_POINT, deltaItem, item.toString(deltaItem)));
				} else if (deltaItem > 0 && deltaDino > 0) {
					sj.add(String.format("%s an additional %,d %s and %,d %s RP", Constants.BULLET_POINT, deltaItem, item.toString(deltaItem), deltaDino, consume.getDinosaurName()));
				} else if (deltaItem > 0) {
					sj.add(String.format("%s an additional %,d %s", Constants.BULLET_POINT, deltaItem, item.toString(deltaItem)));
				} else if (deltaDino > 0) {
					sj.add(String.format("%s an additional %,d %s RP", Constants.BULLET_POINT, deltaDino, consume.getDinosaurName()));
				}
			}

			event.getChannel().sendMessageFormat("%s, you need to meet one of the following to rank up your %s:\n%s", p.getAsMention(), d.getEffectiveName(), sj.toString()).complete();
		} else if (!d.canRankup() && item != null) {
			// rankup by item
			JDBC.addDinosaur(null, p.getIdLong(), d.getIdPair(), dinoReq);
			if (consume != null) JDBC.addDinosaur(null, p.getIdLong(), consume.getIdPair(), -dinoReq);
			JDBC.addItem(p.getIdLong(), item.getIdDmg(), -itemReq);

			JDBC.rankup(p.getIdLong(), d.getIdPair());
			JDBC.addItem(p.getIdLong(), Stat.DinosaursRankedUp.getId());
			
			Dinosaur d2 = Dinosaur.getDinosaur(p.getIdLong(), d.getIdPair());
			Dinosaur pConsume2 = pConsume == null ? null : Dinosaur.getDinosaur(p.getIdLong(), pConsume.getIdPair());

			int dinoReq2 = d2.getRpToRankup();
			int dinoCount2 = pConsume2 == null ? 0 : pConsume2.getRp();
			long deltaDino2 = consume == null ? 0 : dinoReq2 - dinoCount2;

			long itemReq2 = getRankupItemCount(d2) * dinoReq2;
			long itemCount2 = p.getItemCount(item);
			long deltaItem2 = itemReq2 - itemCount2;

			if (deltaItem2 <= 0 && deltaDino2 <= 0 && d2.getRank() < Constants.MAX_RANK) {
				event.getChannel().sendMessageFormat("%s, your %s has reached **Rank %s**! It can now rankup to **Rank %s**. Use `rankup %s` to rankup this dinosaur.", event.getAuthor().getAsMention(), d.getEffectiveName(), d.getNextRankString(), d2.getNextRankString(), d2.getId()).complete();
			} else {
				event.getChannel().sendMessageFormat("%s, your %s has reached **Rank %s**!", event.getAuthor().getAsMention(), d.getEffectiveName(), d.getNextRankString()).complete();
			}
		} else {
			// rankup by rp
			JDBC.rankup(p.getIdLong(), d.getIdPair());
			JDBC.addItem(p.getIdLong(), Stat.DinosaursRankedUp.getId());
			
			Dinosaur d2 = Dinosaur.getDinosaur(p.getIdLong(), d.getIdPair());
			if ((d2.canRankup() || (item != null && deltaItem <= 0 && deltaDino <= 0)) && d2.getRank() < Constants.MAX_RANK) {
				event.getChannel().sendMessageFormat("%s, your %s has reached **Rank %s**! It can now rankup to **Rank %s**. Use `rankup %s` to rankup this dinosaur.", event.getAuthor().getAsMention(), d.getEffectiveName(), d.getNextRankString(), d2.getNextRankString(), d2.getId()).complete();
			} else {
				event.getChannel().sendMessageFormat("%s, your %s has reached **Rank %s**!", event.getAuthor().getAsMention(), d.getEffectiveName(), d.getNextRankString()).complete();
			}
		}
	}

	private static Dinosaur getRankupConsume(Dinosaur dino) {
		if (dino == null) return null;

		DinosaurForm form = dino.getDinosaurForm();
		if (form == DinosaurForm.Mechanical) return Dinosaur.getDinosaur(dino.getDex(), DinosaurForm.Standard.getId());
		return null;
	}

	private static Item getRankupItem(Dinosaur dino) {
		if (dino == null) return null;

		DinosaurForm form = dino.getDinosaurForm();
		if (form == DinosaurForm.Mechanical) return Item.getItem(ItemID.MechanicalComponent);
		if (form == DinosaurForm.Statue) return Item.getItem(ItemID.EnchantedClay);
		return null;
	}

	private static int getRankupItemCount(Dinosaur dino) {
		if (dino == null) return 0;

		DinosaurForm form = dino.getDinosaurForm();
		Rarity r = dino.getRarity();

		if (form == DinosaurForm.Mechanical) return DinoMath.getMechanicalComponentRequired(r);
		if (form == DinosaurForm.Statue) return DinoMath.getClayRequired(r);
		return 0;
	}
}
