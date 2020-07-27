package com.quas.mesozoicisland.cmdplayer;

import java.util.regex.Pattern;

import com.quas.mesozoicisland.JDBC;
import com.quas.mesozoicisland.cmdbase.ICommand;
import com.quas.mesozoicisland.enums.AccessLevel;
import com.quas.mesozoicisland.enums.CustomPlayer;
import com.quas.mesozoicisland.enums.DiscordChannel;
import com.quas.mesozoicisland.enums.DiscordRole;
import com.quas.mesozoicisland.objects.Egg;
import com.quas.mesozoicisland.objects.Player;
import com.quas.mesozoicisland.objects.TradeManager;
import com.quas.mesozoicisland.util.Pair;
import com.quas.mesozoicisland.util.Stats;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class TradeEggCommand implements ICommand {

	@Override
	public Pattern getCommand() {
		return pattern("trade ", PLAYER, " ", EGG, " ", EGG);
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
		return "trade @player <your egg> <their egg>";
	}

	@Override
	public String getCommandDescription() {
		return "Initiates or completes a trade with another player's egg.";
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
		
		Egg e1 = p1.getEgg(Integer.parseInt(args[1].substring(1)));
		if (e1 == null) {
			event.getChannel().sendMessageFormat("%s, you do not have an egg in this incubator slot.", p1.getAsMention()).complete();
			return;
		}
		
		Egg e2 = p2.getEgg(Integer.parseInt(args[2].substring(1)));
		if (e2 == null) {
			event.getChannel().sendMessageFormat("%s, %s does not have an egg in this incubator slot.", p1.getAsMention(), p2.getName()).complete();
			return;
		}
		
		Pair<Egg, Egg> trade = new Pair<Egg, Egg>(e1, e2);
		if (TradeManager.isEggTradeDuplicate(trade)) {
			event.getChannel().sendMessageFormat("%s, this trade already exists.", p1.getAsMention()).complete();
		} else if (TradeManager.doesEggTradeExist(trade)) {
			StringBuilder sb = new StringBuilder();
			sb.append(p1.getAsMention());
			sb.append(" has completed the trade with ");
			sb.append(p2.getAsMention());
			sb.append(":");
			
			sb.append("\nEgg A: ");
			sb.append(e2.getEggName());
			sb.append(", now ");
			sb.append(p1.getName());
			sb.append("'s");
			
			sb.append("\nEgg B: ");
			sb.append(e1.getEggName());
			sb.append(", now ");
			sb.append(p2.getName());
			sb.append("'s");
			
			event.getChannel().sendMessage(sb.toString()).complete();
			TradeManager.addEggTrade(trade);
			
			JDBC.executeUpdate("update eggs set player = %d, incubator = %d where eggid = %d;", p2.getIdLong(), e2.getIncubatorSlot(), e1.getId());
			JDBC.executeUpdate("update eggs set player = %d, incubator = %d where eggid = %d;", p1.getIdLong(), e1.getIncubatorSlot(), e2.getId());
			JDBC.addItem(p1.getIdLong(), Stats.of(Stats.TRADE_COUNT));
			JDBC.addItem(p2.getIdLong(), Stats.of(Stats.TRADE_COUNT));
		} else {
			StringBuilder sb = new StringBuilder();
			sb.append(p1.getAsMention());
			sb.append(" has initiated the trade with ");
			sb.append(p2.getAsMention());
			sb.append(":");
			
			sb.append("\nEgg A: ");
			sb.append(p1.getName());
			sb.append("'s ");
			sb.append(e1.getEggName());
			
			sb.append("\nEgg B: ");
			sb.append(p2.getName());
			sb.append("'s ");
			sb.append(e2.getEggName());
			
			sb.append("\n\n");
			sb.append(p2.getName());
			sb.append(", to complete the trade, type ");
			sb.append(String.format("`trade @%s#%s E%d E%d`", event.getMember().getEffectiveName(), event.getAuthor().getDiscriminator(), e2.getIncubatorSlot(), e1.getIncubatorSlot()));
			sb.append(".");
			
			event.getChannel().sendMessage(sb.toString()).complete();
			TradeManager.addEggTrade(trade);
		}
	}
}
