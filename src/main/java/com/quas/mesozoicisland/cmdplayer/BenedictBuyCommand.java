package com.quas.mesozoicisland.cmdplayer;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.regex.Pattern;

import com.quas.mesozoicisland.JDBC;
import com.quas.mesozoicisland.cmdbase.ICommand;
import com.quas.mesozoicisland.enums.AccessLevel;
import com.quas.mesozoicisland.enums.CustomPlayer;
import com.quas.mesozoicisland.enums.DiscordChannel;
import com.quas.mesozoicisland.enums.DiscordRole;
import com.quas.mesozoicisland.enums.ItemID;
import com.quas.mesozoicisland.enums.Stat;
import com.quas.mesozoicisland.objects.Egg;
import com.quas.mesozoicisland.objects.Item;
import com.quas.mesozoicisland.objects.Player;
import com.quas.mesozoicisland.util.Constants;
import com.quas.mesozoicisland.util.Util;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class BenedictBuyCommand implements ICommand {

	@Override
	public Pattern getCommand() {
		return pattern("benedict buy");
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
		return "benedict buy";
	}

	@Override
	public String getCommandDescription() {
		return "Purchases an egg, if available, from " + CustomPlayer.EggSalesman.getPlayer().getName() + ".";
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

		int forsale = 0;
		try (ResultSet res = JDBC.executeQuery("select count(*) as count from eggs where player = %d;", CustomPlayer.EggSalesman.getIdLong())) {
			if (res.next()) {
				forsale = res.getInt("count");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		long money = p.getItemCount(ItemID.DinosaurCoin);
		long incubator = p.getItemCount(ItemID.EggIncubator);
		int eggs = p.getEggCount();

		if (forsale <= 0) {
			event.getChannel().sendMessageFormat("%s, there are no eggs left in stock.", p.getAsMention()).complete();
		} else if (eggs >= incubator) {
			event.getChannel().sendMessageFormat("%s, you don't have any open incubator slots.", p.getAsMention()).complete();
		} else if (money < Constants.EGG_PRICE) {
			event.getChannel().sendMessageFormat("%s, you don't have enough %s to buy an egg.", p.getAsMention(), Item.getItem(ItemID.DinosaurCoin).toString(2)).complete();
		} else {
			try (ResultSet res = JDBC.executeQuery("select * from eggs where player = %d limit 1;", CustomPlayer.EggSalesman.getIdLong())) {
				if (res.next()) {
					Egg egg = Egg.getEgg(res.getInt("eggid"));
					event.getChannel().sendMessageFormat("%s, you have bought %s %s for %,d %s.", p.getAsMention(), Util.getArticle(egg.getEggName()), egg.getEggName(), Constants.EGG_PRICE, Item.getItem(ItemID.DinosaurCoin).toString(Constants.EGG_PRICE)).complete();
					JDBC.executeUpdate("update eggs set player = %d, original = %d, incubator = %d where eggid = %d;", p.getIdLong(), p.getIdLong(), Util.getFirstOpenIncubator(p.getIdLong()), res.getInt("eggid"));
					JDBC.addItem(p.getIdLong(), Stat.EggsReceived.getId());
					JDBC.addItem(p.getIdLong(), ItemID.DinosaurCoin.getId(), -Constants.EGG_PRICE);
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

	}
}
