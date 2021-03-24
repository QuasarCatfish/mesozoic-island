package com.quas.mesozoicisland.cmdadmin;

import java.util.regex.Pattern;

import com.quas.mesozoicisland.JDBC;
import com.quas.mesozoicisland.MesozoicIsland;
import com.quas.mesozoicisland.cmdbase.ICommand;
import com.quas.mesozoicisland.enums.AccessLevel;
import com.quas.mesozoicisland.enums.DiscordChannel;
import com.quas.mesozoicisland.enums.DiscordRole;
import com.quas.mesozoicisland.objects.Dinosaur;
import com.quas.mesozoicisland.objects.Player;
import com.quas.mesozoicisland.util.Constants;
import com.quas.mesozoicisland.util.Util;

import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class GiveDinosaurCommand implements ICommand {

	@Override
	public Pattern getCommand() {
		return pattern("admin give d(ino(saur)?)? ", PLAYER, " ", DINOSAUR, "( ", INTEGER, ")?");
	}

	@Override
	public AccessLevel getAccessLevel() {
		return AccessLevel.Admin;
	}

	@Override
	public String getCommandName() {
		return "admin";
	}

	@Override
	public String getCommandSyntax() {
		return "admin give dinosaur @player <dinosaur> [count]";
	}

	@Override
	public String getCommandDescription() {
		return "Gives a dinosaur to a player.";
	}

	@Override
	public DiscordChannel[] getUsableChannels() {
		return DiscordChannel.ALL_CHANNELS;
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
		long pid = Long.parseLong(args[2].replaceAll("[^\\d]", ""));
		Player p = Player.getPlayer(pid);
		if (p == null) {
			event.getChannel().sendMessageFormat("%s, this is an invalid player.", event.getAuthor().getAsMention()).complete();
			return;
		}
		
		Dinosaur dino = Dinosaur.getDinosaur(Util.getDexForm(args[3]));
		int count = args.length > 4 ? Integer.parseInt(args[4]) : 1;
		if (dino == null) {
			event.getChannel().sendMessageFormat("%s, this is an invalid dinosaur.", event.getAuthor().getAsMention()).complete();
		} else {
			MessageChannel mc = Constants.SPAWN_CHANNEL.getChannel(MesozoicIsland.getAssistant());
			mc.sendMessageFormat("<@%d>, you have been given %d %s crystal%s.", pid, count, dino.getDinosaurName(), count == 1 || count == -1 ? "" : "s").complete();
			JDBC.addDinosaur(mc, pid, dino.getIdPair(), count);
		}
	}
}
