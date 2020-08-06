package com.quas.mesozoicisland.cmdplayer;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.regex.Pattern;

import com.quas.mesozoicisland.JDBC;
import com.quas.mesozoicisland.cmdbase.ICommand;
import com.quas.mesozoicisland.enums.AccessLevel;
import com.quas.mesozoicisland.enums.DiscordChannel;
import com.quas.mesozoicisland.enums.DiscordRole;
import com.quas.mesozoicisland.enums.ShopType;
import com.quas.mesozoicisland.enums.Stat;
import com.quas.mesozoicisland.objects.Player;
import com.quas.mesozoicisland.objects.ShopItem;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class BuyCommand implements ICommand {

	@Override
	public Pattern getCommand() {
		return pattern("buy ", ALPHANUM, "( " + INTEGER + ")?");
	}

	@Override
	public AccessLevel getAccessLevel() {
		return AccessLevel.Trainer;
	}

	@Override
	public String getCommandName() {
		return "buy";
	}

	@Override
	public String getCommandSyntax() {
		return "buy <package> [amount]";
	}

	@Override
	public String getCommandDescription() {
		return "Buys a package from the shop.";
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
		
		String buy = args[0];
		int count = args.length > 1 ? Integer.parseInt(args[1]) : 1;
		
		if (count <= 0) {
			event.getChannel().sendMessageFormat("%s, you cannot buy %,d items.", event.getAuthor().getAsMention(), count).complete();
			return;
		}
		
		// Find Shop Item
		ShopItem shopitem = null;
		for (ShopItem si : ShopItem.values()) {
			if (!si.isVisible()) continue;
			if (!si.getName().equalsIgnoreCase(buy)) continue;
			if (si.getShopType() == ShopType.Debug && p.getAccessLevel().getLevel() < AccessLevel.Admin.getLevel()) continue;
			shopitem = si;
			break;
		}
		
		// Shop Item Not Found
		if (shopitem == null) {
			event.getChannel().sendMessageFormat("%s, I can't find this item in the shop.", event.getAuthor().getAsMention()).complete();
			return;
		}
		
		// Check Stock
		long stock = shopitem.getPlayerStock(p.getIdLong());
		if (stock != -1 && stock < count) {
			event.getChannel().sendMessageFormat("%s, there is not enough \"%s\" for you to buy.", event.getAuthor().getAsMention(), shopitem.getName()).complete();
			return;
		}
		
		// Pay and Buy Count
		long pcount = count * shopitem.getPayCount();
		long bcount = count * shopitem.getBuyCount();
		
		// Check Pay
		if (p.getBag().getOrDefault(shopitem.getPayItem(), 0L) < pcount) {
			event.getChannel().sendMessageFormat("%s, you do not have enough %s to buy this.", event.getAuthor().getAsMention(), shopitem.getPayItem().toString(2)).complete();
			return;
		}
		
		// Make Purchase
		JDBC.addItem(p.getIdLong(), shopitem.getPayItem().getIdDmg(), -pcount);
		JDBC.addItem(p.getIdLong(), shopitem.getBuyItem().getIdDmg(), bcount);
		event.getChannel().sendMessageFormat("%s, you have bought %,d %s for %,d %s.", event.getAuthor().getAsMention(), bcount, shopitem.getBuyItem().toString(bcount), pcount, shopitem.getPayItem().toString(pcount)).complete();
		JDBC.addItem(p.getIdLong(), Stat.TransactionsMade.getId(), count);
		
		// Update Stock
		if (shopitem.getTotalStock() > 0) JDBC.executeUpdate("update shop set totalstock = totalstock - %d where shopid = %d;", count, shopitem.getId());
		if (shopitem.isPlayerSpecific()) {
			boolean b = false;
			try (ResultSet res = JDBC.executeQuery("select * from purchases where shopid = %d and player = %d;", shopitem.getId(), p.getIdLong())) {
				b = res.next();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			
			if (b) JDBC.executeUpdate("update purchases set count = count + %d where shopid = %d and player = %d;", count, shopitem.getId(), p.getIdLong());
			else JDBC.executeUpdate("insert into purchases(shopid, player, count) values(%d, %d, %d);", shopitem.getId(), p.getIdLong(), count);
		}
	}
}
