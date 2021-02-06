package com.quas.mesozoicisland.cmdplayer;

import java.util.regex.Pattern;

import com.quas.mesozoicisland.JDBC;
import com.quas.mesozoicisland.cmdbase.ICommand;
import com.quas.mesozoicisland.enums.AccessLevel;
import com.quas.mesozoicisland.enums.CustomPlayer;
import com.quas.mesozoicisland.enums.DiscordChannel;
import com.quas.mesozoicisland.enums.DiscordRole;
import com.quas.mesozoicisland.enums.Stat;
import com.quas.mesozoicisland.objects.Player;
import com.quas.mesozoicisland.objects.Rune;
import com.quas.mesozoicisland.objects.TradeManager;
import com.quas.mesozoicisland.util.Pair;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class TradeRuneCommand implements ICommand {

	@Override
	public Pattern getCommand() {
		return pattern("trade ", PLAYER, " ", RUNE, " ", RUNE);
	}

	@Override
	public AccessLevel getAccessLevel() {
//		return AccessLevel.Trainer;
		return AccessLevel.Disabled;
	}

	@Override
	public String getCommandName() {
		return "trade";
	}

	@Override
	public String getCommandSyntax() {
		return "trade @player <your rune> <their rune>";
	}

	@Override
	public String getCommandDescription() {
		return "Initiates or completes a trade with another player's rune.";
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
	public void run(MessageReceivedEvent event, String... args) {
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
		
		Rune r1 = Rune.getRune(p1.getIdLong(), Integer.parseInt(args[1].replaceAll("\\D", "")));
		if (r1 == null) {
			if (Rune.getRune(Integer.parseInt(args[1].replaceAll("\\D", ""))) == null) {
				event.getChannel().sendMessageFormat("%s, the rune with ID `%s` does not exist.", p1.getAsMention(), args[1].toUpperCase()).complete();
			} else {
				event.getChannel().sendMessageFormat("%s, you do not own this rune.", p1.getAsMention()).complete();
			}
			return;
		} else if (!r1.isTradable()) {
			event.getChannel().sendMessageFormat("%s, your %s is not tradable.", p1.getAsMention(), r1.getName()).complete();
			return;
		}
		
		Rune r2 = Rune.getRune(p2.getIdLong(), Integer.parseInt(args[2].replaceAll("\\D", "")));
		if (r2 == null) {
			if (Rune.getRune(Integer.parseInt(args[2].replaceAll("\\D", ""))) == null) {
				event.getChannel().sendMessageFormat("%s, the rune with ID `%s` does not exist.", p1.getAsMention(), args[2].toUpperCase()).complete();
			} else {
				event.getChannel().sendMessageFormat("%s, %s does not own this rune.", p1.getAsMention(), p2.getName()).complete();
			}
			return;
		} else if (!r2.isTradable()) {
			event.getChannel().sendMessageFormat("%s, %s's %s is not tradable.", p1.getAsMention(), p2.getName(), r2.getName()).complete();
			return;
		}
		
		Pair<Rune, Rune> trade = new Pair<Rune, Rune>(r1, r2);
		if (TradeManager.isRuneTradeDuplicate(trade)) {
			event.getChannel().sendMessageFormat("%s, this trade already exists.", p1.getAsMention()).complete();
		} else if (TradeManager.doesRuneTradeExist(trade)) {
			StringBuilder sb = new StringBuilder();
			sb.append(p1.getAsMention());
			sb.append(" has completed the trade with ");
			sb.append(p2.getAsMention());
			sb.append(":");
			
			sb.append("\nRune A: ");
			sb.append(r2.getName());
			sb.append(", now ");
			sb.append(p1.getName());
			sb.append("'s");
			
			sb.append("\nRune B: ");
			sb.append(r1.getName());
			sb.append(", now ");
			sb.append(p2.getName());
			sb.append("'s");
			
			event.getChannel().sendMessage(sb.toString()).complete();
			TradeManager.addRuneTrade(trade);
			
			if (r1.getId() != r2.getId()) {
				JDBC.addRune(null, p1.getIdLong(), r1.getId(), -1);
				JDBC.addRune(event.getChannel(), p2.getIdLong(), r1.getId(), 1);
				JDBC.addRune(null, p2.getIdLong(), r2.getId(), -1);
				JDBC.addRune(event.getChannel(), p1.getIdLong(), r2.getId(), 1);
			}
			
			JDBC.addItem(p1.getIdLong(), Stat.TimesTraded.getId());
			JDBC.addItem(p2.getIdLong(), Stat.TimesTraded.getId());
		} else {
			StringBuilder sb = new StringBuilder();
			sb.append(p1.getAsMention());
			sb.append(" has initiated the trade with ");
			sb.append(p2.getAsMention());
			sb.append(":");
			
			sb.append("\nRune A: ");
			sb.append(p1.getName());
			sb.append("'s ");
			sb.append(r1.getName());
			
			sb.append("\nRune B: ");
			sb.append(p2.getName());
			sb.append("'s ");
			sb.append(r2.getName());
			
			sb.append("\n\n");
			sb.append(p2.getAsMention());
			sb.append(" to complete the trade, type ");
			sb.append(String.format("`trade %d R%d R%d`", p1.getIdLong(), r2.getId(), r1.getId()));
			sb.append(".");
			
			event.getChannel().sendMessage(sb.toString()).complete();
			TradeManager.addRuneTrade(trade);
		}
	}
}
