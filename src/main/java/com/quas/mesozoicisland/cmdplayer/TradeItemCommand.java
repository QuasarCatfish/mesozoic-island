package com.quas.mesozoicisland.cmdplayer;

import java.util.TreeMap;
import java.util.regex.Pattern;

import com.quas.mesozoicisland.JDBC;
import com.quas.mesozoicisland.cmdbase.ICommand;
import com.quas.mesozoicisland.enums.AccessLevel;
import com.quas.mesozoicisland.enums.CustomPlayer;
import com.quas.mesozoicisland.enums.DiscordChannel;
import com.quas.mesozoicisland.enums.DiscordRole;
import com.quas.mesozoicisland.enums.Stat;
import com.quas.mesozoicisland.objects.Item;
import com.quas.mesozoicisland.objects.Player;
import com.quas.mesozoicisland.objects.TradeManager;
import com.quas.mesozoicisland.util.Pair;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class TradeItemCommand implements ICommand {

	@Override
	public Pattern getCommand() {
		return pattern("trade ", PLAYER, " i", INTEGER, " i", INTEGER);
	}

	@Override
	public AccessLevel getAccessLevel() {
		return AccessLevel.Trainer;
	}

	@Override
	public String getCommandName() {
		return "trade";
	}

	@Override
	public String getCommandSyntax() {
		return "trade @player <your item> <their item>";
	}

	@Override
	public String getCommandDescription() {
		return "Initiates or completes a trade with another player's item.";
	}

	@Override
	public DiscordChannel[] getUsableChannels() {
		return DiscordChannel.TRADE_CHANNELS;
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
		Player p1 = Player.getPlayer(event.getAuthor().getIdLong());
		if (p1 == null) return;
		
		Player p2 = Player.getPlayer(Long.parseLong(args[0].replaceAll("\\D", "")));
		if (p2 == null || p2.getIdLong() < CustomPlayer.getUpperLimit()) {
			event.getChannel().sendMessageFormat("%s, the player you are trying to trade with does not exist.", p1.getAsMention()).complete();
			return;
		}
		
		if (p1.getIdLong() == p2.getIdLong()) {
			event.getChannel().sendMessageFormat("%s, you cannot trade with yourself.", p1.getAsMention()).complete();
			return;
		}
		
		Item[] ia1 = Item.getItems(Integer.parseInt(args[1].replaceAll("\\D", "")));
		Item i1 = null;
		TreeMap<Item, Long> bag1 = p1.getBag();
		for (Item item : ia1) {
			long count = bag1.getOrDefault(item, 0L);
			if (count > 0) {
				i1 = item;
				break;
			}
		}

		if (i1 == null) {
			if (ia1.length == 0) {
				event.getChannel().sendMessageFormat("%s, this item does not exist.", p1.getAsMention()).complete();
			} else {
				event.getChannel().sendMessageFormat("%s, you do not have any of this item.", p1.getAsMention()).complete();
			}
			return;
		} else if (!i1.isTradable()) {
			event.getChannel().sendMessageFormat("%s, a %s is not tradable.", p1.getAsMention(), i1.toString()).complete();
			return;
		}

		Item[] ia2 = Item.getItems(Integer.parseInt(args[2].replaceAll("\\D", "")));
		Item i2 = null;
		TreeMap<Item, Long> bag2 = p2.getBag();
		for (Item item : ia2) {
			long count = bag2.getOrDefault(item, 0L);
			if (count > 0) {
				i2 = item;
				break;
			}
		}

		if (i2 == null) {
			if (ia2.length == 0) {
				event.getChannel().sendMessageFormat("%s, this item does not exist.", p1.getAsMention()).complete();
			} else {
				event.getChannel().sendMessageFormat("%s, %s does not have any of this item.", p1.getAsMention(), p2.getName()).complete();
			}
			return;
		} else if (!i2.isTradable()) {
			event.getChannel().sendMessageFormat("%s, a %s is not tradable.", p1.getAsMention(), i2.toString()).complete();
			return;
		}

		Pair<Item, Item> trade = new Pair<Item, Item>(i1, i2);
		if (TradeManager.isItemTradeDuplicate(trade)) {
			event.getChannel().sendMessageFormat("%s, this trade already exists.", p1.getAsMention()).complete();
		} else if (TradeManager.doesItemTradeExist(trade)) {
			StringBuilder sb = new StringBuilder();
			sb.append(p1.getAsMention());
			sb.append(" has completed the trade with ");
			sb.append(p2.getAsMention());
			sb.append(":");
			
			sb.append("\nItem A: ");
			sb.append(i2.toString());
			sb.append(", now ");
			sb.append(p1.getName());
			sb.append("'s");
			
			sb.append("\nItem B: ");
			sb.append(i1.toString());
			sb.append(", now ");
			sb.append(p2.getName());
			sb.append("'s");
			
			event.getChannel().sendMessage(sb.toString()).complete();
			TradeManager.addItemTrade(trade);
			
			JDBC.addItem(p1.getIdLong(), i1.getIdDmg(), -1);
			JDBC.addItem(p2.getIdLong(), i1.getIdDmg(), 1);
			JDBC.addItem(p2.getIdLong(), i2.getIdDmg(), -1);
			JDBC.addItem(p1.getIdLong(), i2.getIdDmg(), 1);
			JDBC.addItem(p1.getIdLong(), Stat.TimesTraded.getId());
			JDBC.addItem(p2.getIdLong(), Stat.TimesTraded.getId());
		} else {
			StringBuilder sb = new StringBuilder();
			sb.append(p1.getAsMention());
			sb.append(" has initiated the trade with ");
			sb.append(p2.getAsMention());
			sb.append(":");
			
			sb.append("\nItem A: ");
			sb.append(p1.getName());
			sb.append("'s ");
			sb.append(i1.toString());
			
			sb.append("\nItem B: ");
			sb.append(p2.getName());
			sb.append("'s ");
			sb.append(i2.toString());
			
			sb.append("\n\n");
			sb.append(p2.getName());
			sb.append(", to complete the trade, type ");
			sb.append(String.format("`trade %d I%d I%d`", p1.getIdLong(), i2.getId(), i1.getId()));
			sb.append(".");
			
			event.getChannel().sendMessage(sb.toString()).complete();
			TradeManager.addItemTrade(trade);
		}
	}
}
