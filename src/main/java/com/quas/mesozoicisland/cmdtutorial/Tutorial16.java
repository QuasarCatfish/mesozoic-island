package com.quas.mesozoicisland.cmdtutorial;

import java.util.regex.Pattern;

import com.quas.mesozoicisland.JDBC;
import com.quas.mesozoicisland.cmdbase.ICommand;
import com.quas.mesozoicisland.enums.AccessLevel;
import com.quas.mesozoicisland.enums.DiscordChannel;
import com.quas.mesozoicisland.enums.DiscordRole;
import com.quas.mesozoicisland.objects.Dinosaur;
import com.quas.mesozoicisland.objects.Player;
import com.quas.mesozoicisland.util.Constants;
import com.quas.mesozoicisland.util.Util;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class Tutorial16 implements ICommand {

	@Override
	public Pattern getCommand() {
		return pattern("info ", DINOSAUR);
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
		return "Tutorial16";
	}
	
	@Override
	public synchronized void run(MessageReceivedEvent event, String... args) {
		Player p = Player.getPlayer(event.getAuthor().getIdLong());
		if (p == null) return;
		
		Dinosaur starter = Dinosaur.getDinosaur(p.getIdLong(), Util.getDexForm(p.getStarter()));
		if (!Util.getDexForm(args[0]).equals(starter.getIdPair())) return;
		
		Util.sleep(500);
		JDBC.setState(p.getIdLong(), "TutorialXX");
		
		Util.sleep(1000);
		sendTyping(event.getChannel(), 3000);
		event.getChannel().sendMessageFormat("Woah! Your %s has some pretty good stats. It could probably hold its own in a battle!", starter.getDinosaurName()).complete();
		
		Util.sleep(1000);
		sendTyping(event.getChannel(), 2000);
		event.getChannel().sendMessage("Speaking of battles, we should put this dinosaur on your team!").complete();

		Util.sleep(1000);
		sendTyping(event.getChannel(), 3000);
		event.getChannel().sendMessage("The `select` command is what trainers use to put select the dinosaurs they'd like to take into battle.").complete();

		Util.sleep(1000);
		sendTyping(event.getChannel(), 3000);
		event.getChannel().sendMessageFormat("You can select up to %d dinosaurs to take into battle, but since you only have one, use `select <dinoid>` to put it on your team.", Constants.DINOS_PER_TEAM).complete();
		
		JDBC.setState(p.getIdLong(), "Tutorial17");
	}
}
