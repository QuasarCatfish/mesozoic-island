package com.quas.mesozoicisland.cmdtutorial;

import java.util.regex.Pattern;

import com.quas.mesozoicisland.JDBC;
import com.quas.mesozoicisland.MesozoicIsland;
import com.quas.mesozoicisland.cmdbase.ICommand;
import com.quas.mesozoicisland.enums.AccessLevel;
import com.quas.mesozoicisland.enums.DiscordChannel;
import com.quas.mesozoicisland.enums.DiscordRole;
import com.quas.mesozoicisland.objects.Player;
import com.quas.mesozoicisland.util.Util;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class Tutorial00 implements ICommand {

	@Override
	public Pattern getCommand() {
		return pattern("start");
	}

	@Override
	public AccessLevel getAccessLevel() {
		return AccessLevel.Trainer;
	}

	@Override
	public String getCommandName() {
		return "start";
	}

	@Override
	public String getCommandSyntax() {
		return "start";
	}

	@Override
	public String getCommandDescription() {
		return "Starts the tutorial.";
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
		return "Tutorial00";
	}
	
	@Override
	public void run(MessageReceivedEvent event, String... args) {
		Player p = Player.getPlayer(event.getAuthor().getIdLong());
		if (p == null) return;
		
		Util.sleep(500);
		JDBC.setState(p.getIdLong(), "TutorialXX");
		
		sendTyping(event.getChannel(), 2000);
		event.getChannel().sendMessageFormat("Welcome, %s, to Mesozoic Island!", event.getAuthor().getAsMention()).complete();
		Util.sleep(1000);
		
		sendTyping(event.getChannel(), 4000);
		event.getChannel().sendMessage("It seems like you're new here. Let's get you registered as a dinosaur trainer!").complete();
		Util.sleep(1000);
		
		sendTyping(event.getChannel(), 2000);
		event.getChannel().sendMessage("First Question. What is your name?").complete();
		Util.sleep(1000);
		
		MesozoicIsland.getAssistant().getGuild().getTextChannelById(event.getChannel().getIdLong()).sendMessage("Please choose a name that is no longer than 25 characters. It is not expected that you use your real name, just one that will represent you on Mesozoic Island.").complete();
		
		JDBC.setState(p.getIdLong(), "Tutorial01");
	}
}
