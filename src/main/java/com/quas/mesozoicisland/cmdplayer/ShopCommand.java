package com.quas.mesozoicisland.cmdplayer;

import java.util.TreeMap;
import java.util.regex.Pattern;

import com.quas.mesozoicisland.cmdbase.CommandManager;
import com.quas.mesozoicisland.cmdbase.ICommand;
import com.quas.mesozoicisland.enums.AccessLevel;
import com.quas.mesozoicisland.enums.DiscordChannel;
import com.quas.mesozoicisland.enums.DiscordRole;
import com.quas.mesozoicisland.enums.ShopType;
import com.quas.mesozoicisland.objects.Item;
import com.quas.mesozoicisland.objects.Player;
import com.quas.mesozoicisland.objects.ShopItem;
import com.quas.mesozoicisland.util.Constants;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class ShopCommand implements ICommand {

	@Override
	public Pattern getCommand() {
		return pattern("shop .*");
	}

	@Override
	public AccessLevel getAccessLevel() {
		return AccessLevel.Trainer;
	}

	@Override
	public String getCommandName() {
		return "shop";
	}

	@Override
	public String getCommandSyntax() {
		return "shop <store>";
	}

	@Override
	public String getCommandDescription() {
		return "Gets a list of items you can purchase in the given store.\nCurrent Stores: " + ShopType.listValues() + ".";
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
		
		ShopType shop = ShopType.of(String.join(" ", args));

		if (!shop.isVisible()) {
			if (shop == ShopType.Debug && p.getAccessLevel().getLevel() < AccessLevel.Admin.getLevel()) shop = ShopType.None;
			else if (shop == ShopType.Tutorial);
			else shop = ShopType.None;
		}

		if (shop == ShopType.None) {
			CommandManager.handleCommand(event, "shop");
			return;
		}
		
		EmbedBuilder eb = new EmbedBuilder();
		if (shop == ShopType.Tutorial) eb.setTitle("Mesozoic Island Shop - Tutorial");
		else eb.setTitle("Mesozoic Island Shop - " + shop.getName());
		eb.setDescription("To purchase one of these items, use `buy <package> [count]`.");
		eb.setColor(Constants.COLOR);
		
		TreeMap<Item, Long> bag = p.getBag();
		for (ShopItem si : ShopItem.values()) {
			if (!si.isVisible()) continue;
			if (si.getShopType() != shop) continue;
			
			long stock = si.getPlayerStock(p.getIdLong());
			StringBuilder fname = new StringBuilder();
			fname.append("**");
			fname.append(si.getName());
			fname.append("**");
			if (si.getRequiredLevel() > 0) fname.append(String.format(" [Requires Lv %,d]", si.getRequiredLevel()));
			if (stock > -1) fname.append(String.format(" (%,d left in stock)", stock));

			String fvalue = String.format("__Receive:__ %,d %s (You have %,d)%n__Pay:__ %,d %s (You have %,d)", si.getBuyCount(), si.getBuyItem().toString(si.getBuyCount()), bag.getOrDefault(si.getBuyItem(), 0L), si.getPayCount(), si.getPayItem().toString(si.getPayCount()), bag.getOrDefault(si.getPayItem(), 0L));
			eb.addField(fname.toString(), fvalue, false);
		}
		
		event.getChannel().sendMessage(eb.build()).complete();
	}
}
