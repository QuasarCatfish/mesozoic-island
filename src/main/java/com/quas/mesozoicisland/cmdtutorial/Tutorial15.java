package com.quas.mesozoicisland.cmdtutorial;

import java.util.regex.Pattern;

import com.quas.mesozoicisland.JDBC;
import com.quas.mesozoicisland.cmdbase.ICommand;
import com.quas.mesozoicisland.enums.AccessLevel;
import com.quas.mesozoicisland.enums.DiscordChannel;
import com.quas.mesozoicisland.enums.DiscordRole;
import com.quas.mesozoicisland.objects.Dinosaur;
import com.quas.mesozoicisland.objects.Player;
import com.quas.mesozoicisland.util.Util;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class Tutorial15 implements ICommand {

	@Override
	public Pattern getCommand() {
		return pattern("use 204 ", DINOSAUR);
	}

	@Override
	public AccessLevel getAccessLevel() {
		return AccessLevel.Trainer;
	}

	@Override
	public String getCommandName() {
		return null;
	}

	@Override
	public String getCommandSyntax() {
		return null;
	}

	@Override
	public String getCommandDescription() {
		return null;
	}

	@Override
	public DiscordChannel[] getUsableChannels() {
		return Util.arr(DiscordChannel.CloneMe, DiscordChannel.ClonedChannel);
	}

	@Override
	public DiscordRole[] getRequiredRoles() {
		return null;
	}

	@Override
	public String getRequiredState() {
		return "Tutorial15";
	}
	
	@Override
	public synchronized void run(MessageReceivedEvent event, String... args) {
		Player p = Player.getPlayer(event.getAuthor().getIdLong());
		if (p == null) return;
		
		Dinosaur starter = Dinosaur.getDinosaur(p.getIdLong(), Util.getDexForm(p.getStarter()));
		if (!Util.getDexForm(args[1]).equals(starter.getIdPair())) return;
		
		Util.sleep(500);
		JDBC.setState(p.getIdLong(), "TutorialXX");
		
		Util.sleep(1000);
		sendTyping(event.getChannel(), 2000);
		event.getChannel().sendMessageFormat("Nice! Your %s leveled up! It's even stronger now!", starter.getDinosaurName()).complete();
		
		Util.sleep(1000);
		sendTyping(event.getChannel(), 3000);
		event.getChannel().sendMessage("In order to see how strong a dinosaur is, we can use the `info` command. Try doing `info <dinoid>` now!").complete();
		
		JDBC.setState(p.getIdLong(), "Tutorial16");
	}
}
