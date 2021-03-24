package com.quas.mesozoicisland.cmdtutorial;

import java.util.regex.Pattern;

import com.quas.mesozoicisland.JDBC;
import com.quas.mesozoicisland.MesozoicIsland;
import com.quas.mesozoicisland.cmdbase.ICommand;
import com.quas.mesozoicisland.enums.AccessLevel;
import com.quas.mesozoicisland.enums.DiscordChannel;
import com.quas.mesozoicisland.enums.DiscordRole;
import com.quas.mesozoicisland.objects.Dinosaur;
import com.quas.mesozoicisland.objects.Player;
import com.quas.mesozoicisland.util.Util;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class Tutorial08Y implements ICommand {

	@Override
	public Pattern getCommand() {
		return pattern(YES);
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
		return "Tutorial08";
	}
	
	@Override
	public synchronized void run(MessageReceivedEvent event, String... args) {
		Player p = Player.getPlayer(event.getAuthor().getIdLong());
		if (p == null) return;
		
		Util.sleep(500);
		JDBC.setState(p.getIdLong(), "TutorialXX");
		
		Dinosaur dino = Dinosaur.getDinosaur(Util.getDexForm(p.getStarter()));
		
		sendTyping(event.getChannel(), 2000);
		event.getChannel().sendMessageFormat("The %s is a great choice for your starter dinosaur. Here's its crystal.", dino.getDinosaurName()).complete();
		Util.sleep(500);
		
		JDBC.addDinosaur(null, p.getIdLong(), dino.getIdPair());
		MesozoicIsland.getAssistant().getGuild().getTextChannelById(event.getChannel().getIdLong()).sendMessageFormat("%s, you have been given %s %s crystal.", p.getAsMention(), Util.getArticle(dino.getDinosaurName()), dino.getDinosaurName()).complete();
		Util.sleep(1500);
		
		sendTyping(event.getChannel(), 2000);
		event.getChannel().sendMessage("Now that you have a dinosaur, we can go over the other things you can do as a dinosaur trainer.").complete();
		Util.sleep(1000);
		
		sendTyping(event.getChannel(), 2000);
		event.getChannel().sendMessage("One of the most important commands is the `dinos` command. It lets you see which dinosaurs are in your possession. Try it out now.").complete();
		
		JDBC.setState(p.getIdLong(), "Tutorial09");
	}
}
