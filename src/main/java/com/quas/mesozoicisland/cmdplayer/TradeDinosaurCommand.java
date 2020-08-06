package com.quas.mesozoicisland.cmdplayer;

import java.util.regex.Pattern;

import com.quas.mesozoicisland.JDBC;
import com.quas.mesozoicisland.cmdbase.ICommand;
import com.quas.mesozoicisland.enums.AccessLevel;
import com.quas.mesozoicisland.enums.CustomPlayer;
import com.quas.mesozoicisland.enums.DiscordChannel;
import com.quas.mesozoicisland.enums.DiscordRole;
import com.quas.mesozoicisland.enums.Stat;
import com.quas.mesozoicisland.objects.Dinosaur;
import com.quas.mesozoicisland.objects.Player;
import com.quas.mesozoicisland.objects.TradeManager;
import com.quas.mesozoicisland.util.Pair;
import com.quas.mesozoicisland.util.Util;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class TradeDinosaurCommand implements ICommand {

	@Override
	public Pattern getCommand() {
		return pattern("trade ", PLAYER, " ", DINOSAUR, " ", DINOSAUR);
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
		return "trade @player <your dinosaur> <their dinosaur>";
	}

	@Override
	public String getCommandDescription() {
		return "Initiates or completes a trade with another player's dinosaur.";
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
		
		Dinosaur d1 = Dinosaur.getDinosaur(p1.getIdLong(), Util.getDexForm(args[1]));
		if (d1 == null) {
			if (Dinosaur.getDinosaur(Util.getDexForm(args[1])) == null) {
				event.getChannel().sendMessageFormat("%s, the dinosaur with ID `%s` does not exist.", p1.getAsMention(), args[1].toUpperCase()).complete();
			} else {
				event.getChannel().sendMessageFormat("%s, you do not own this dinosaur.", p1.getAsMention()).complete();
			}
			return;
		} else if (!d1.isTradeable()) {
			event.getChannel().sendMessageFormat("%s, your %s is not tradeable.", p1.getAsMention(), d1.getDinosaurName()).complete();
			return;
		}
		
		Dinosaur d2 = Dinosaur.getDinosaur(p2.getIdLong(), Util.getDexForm(args[2]));
		if (d2 == null) {
			if (Dinosaur.getDinosaur(Util.getDexForm(args[2])) == null) {
				event.getChannel().sendMessageFormat("%s, the dinosaur with ID `%s` does not exist.", p1.getAsMention(), args[2].toUpperCase()).complete();
			} else {
				event.getChannel().sendMessageFormat("%s, %s does not own this dinosaur.", p1.getAsMention(), p2.getName()).complete();
			}
			return;
		} else if (!d2.isTradeable()) {
			event.getChannel().sendMessageFormat("%s, %s's %s is not tradeable.", p1.getAsMention(), p2.getName(), d2.getDinosaurName()).complete();
			return;
		}
		
		Pair<Dinosaur, Dinosaur> trade = new Pair<Dinosaur, Dinosaur>(d1, d2);
		if (TradeManager.isDinosaurTradeDuplicate(trade)) {
			event.getChannel().sendMessageFormat("%s, this trade already exists.", p1.getAsMention()).complete();
		} else if (TradeManager.doesDinosaurTradeExist(trade)) {
			StringBuilder sb = new StringBuilder();
			sb.append(p1.getAsMention());
			sb.append(" has completed the trade with ");
			sb.append(p2.getAsMention());
			sb.append(":");
			
			sb.append("\nDinosaur A: ");
			sb.append(d2.getDinosaurName());
			sb.append(", now ");
			sb.append(p1.getName());
			sb.append("'s");
			
			sb.append("\nDinosaur B: ");
			sb.append(d1.getDinosaurName());
			sb.append(", now ");
			sb.append(p2.getName());
			sb.append("'s");
			
			event.getChannel().sendMessage(sb.toString()).complete();
			TradeManager.addDinosaurTrade(trade);
			
			JDBC.addDinosaur(null, p1.getIdLong(), d1.getIdPair(), -1);
			JDBC.addDinosaur(event.getChannel(), p2.getIdLong(), d1.getIdPair(), 1);
			JDBC.addDinosaur(null, p2.getIdLong(), d2.getIdPair(), -1);
			JDBC.addDinosaur(event.getChannel(), p1.getIdLong(), d2.getIdPair(), 1);
			JDBC.addItem(p1.getIdLong(), Stat.TimesTraded.getId());
			JDBC.addItem(p2.getIdLong(), Stat.TimesTraded.getId());
		} else {
			StringBuilder sb = new StringBuilder();
			sb.append(p1.getAsMention());
			sb.append(" has initiated the trade with ");
			sb.append(p2.getAsMention());
			sb.append(":");
			
			sb.append("\nDinosaur A: ");
			sb.append(p1.getName());
			sb.append("'s ");
			sb.append(d1.getDinosaurName());
			
			sb.append("\nDinosaur B: ");
			sb.append(p2.getName());
			sb.append("'s ");
			sb.append(d2.getDinosaurName());
			
			sb.append("\n\n");
			sb.append(p2.getName());
			sb.append(", to complete the trade, type ");
			sb.append(String.format("`trade @%s#%s %s %s`", event.getMember().getEffectiveName(), event.getAuthor().getDiscriminator(), d2.getId(), d1.getId()));
			sb.append(".");
			
			event.getChannel().sendMessage(sb.toString()).complete();
			TradeManager.addDinosaurTrade(trade);
		}
	}
}
