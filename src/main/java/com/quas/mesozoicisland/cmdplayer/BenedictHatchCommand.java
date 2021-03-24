package com.quas.mesozoicisland.cmdplayer;

import java.util.regex.Pattern;

import com.quas.mesozoicisland.JDBC;
import com.quas.mesozoicisland.cmdbase.ICommand;
import com.quas.mesozoicisland.enums.AccessLevel;
import com.quas.mesozoicisland.enums.DiscordChannel;
import com.quas.mesozoicisland.enums.DiscordRole;
import com.quas.mesozoicisland.enums.ItemID;
import com.quas.mesozoicisland.enums.Stat;
import com.quas.mesozoicisland.objects.Dinosaur;
import com.quas.mesozoicisland.objects.Egg;
import com.quas.mesozoicisland.objects.Item;
import com.quas.mesozoicisland.objects.Player;
import com.quas.mesozoicisland.util.Util;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class BenedictHatchCommand implements ICommand {

	@Override
	public Pattern getCommand() {
		return pattern("benedict hatch ", EGG);
	}

	@Override
	public AccessLevel getAccessLevel() {
		return AccessLevel.Trainer;
	}

	@Override
	public String getCommandName() {
		return "benedict";
	}

	@Override
	public String getCommandSyntax() {
		return "benedict hatch <egg>";
	}

	@Override
	public String getCommandDescription() {
		return "Immediately hatches an egg at the cost of 1 Dinosaur Coin per 5 Hatch Points.";
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
		
		Egg egg = p.getEgg(Integer.parseInt(args[1].substring(1)));
		if (egg == null) {
			event.getChannel().sendMessageFormat("%s, you don't have an egg in this incubator.", p.getAsMention()).complete();
			return;
		}

		Item coin = Item.getItem(ItemID.DinosaurCoin);
		long money = p.getItemCount(coin);
		int hp = Math.max(0, egg.getMaxHatchPoints() - egg.getCurrentHatchPoints());
		long cost = hp / 5;
		if (money < cost) {
			event.getChannel().sendMessageFormat("%s, you do not have enough %s to hatch this egg.", p.getAsMention(), coin.toString(2)).complete();
			return;
		} else if (cost <= 0) {
			event.getChannel().sendMessageFormat("%s, this egg is already ready to hatch. Hatch it with the `hatch` command.", p.getAsMention()).complete();
			return;
		} else if (cost <= 10) {
			event.getChannel().sendMessageFormat("%s, this egg is too close to hatching.", p.getAsMention()).complete();
			return;
		}

		Dinosaur d = Dinosaur.getDinosaur(egg.getDex(), egg.getForm());
		event.getChannel().sendMessageFormat("%s, for a cost of %,d %s, your %s hatched into %s %s (#%s)!", p.getAsMention(), cost, coin.toString(cost), egg.getEggName(), Util.getArticle(d.getDinosaurName()), d.getDinosaurName(), d.getId()).complete();
		JDBC.executeUpdate("update eggs set player = 1 where eggid = %d;", egg.getId());
		JDBC.addDinosaur(event.getChannel(), p.getIdLong(), d.getIdPair());
		JDBC.addItem(p.getIdLong(), Stat.EggsHatched.getId());
		JDBC.addItem(p.getIdLong(), Stat.TransactionsMade.getId());
		JDBC.addPlayerXp(p.getIdLong(), egg.getMaxHatchPoints() / 10);
		JDBC.addItem(p.getIdLong(), coin.getIdDmg(), -cost);
	}
}
