package com.quas.mesozoicisland.cmdtutorial;

import java.util.ArrayList;
import java.util.regex.Pattern;

import com.quas.mesozoicisland.JDBC;
import com.quas.mesozoicisland.cmdbase.ICommand;
import com.quas.mesozoicisland.enums.AccessLevel;
import com.quas.mesozoicisland.enums.DiscordChannel;
import com.quas.mesozoicisland.enums.DiscordRole;
import com.quas.mesozoicisland.objects.Dinosaur;
import com.quas.mesozoicisland.objects.Player;
import com.quas.mesozoicisland.util.Constants;
import com.quas.mesozoicisland.util.Pair;
import com.quas.mesozoicisland.util.Util;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class Tutorial07 implements ICommand {

	@Override
	public Pattern getCommand() {
		return pattern(".*");
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
		return "Tutorial07";
	}
	
	@Override
	public void run(MessageReceivedEvent event, String... args) {
		Player p = Player.getPlayer(event.getAuthor().getIdLong());
		if (p == null) return;
		
		Util.sleep(500);
		JDBC.setState(p.getIdLong(), "TutorialXX");
		
		String dinosaur = event.getMessage().getContentRaw();
		ArrayList<Pair<Dinosaur, String>> pairs = Constants.getStarterDinosaurs();
		Dinosaur select = null;
		
		for (Pair<Dinosaur, String> pair : pairs) {
			if (dinosaur.equalsIgnoreCase(pair.getFirstValue().getDinosaurName())) {
				select = pair.getFirstValue();
			}
		}
		
		if (select == null) {
			sendTyping(event.getChannel(), 3000);
			event.getChannel().sendMessage("That's not one of the options. Please try again.\n*If you believe this is an error, please contact an Administrator for help.*").complete();
			
			JDBC.setState(p.getIdLong(), "Tutorial07");
		} else {
			sendTyping(event.getChannel(), 3000);
			event.getChannel().sendMessageFormat("Are you sure you want the %s-type dinosaur, %s?", select.getElement().getName(), select.getDinosaurName()).complete();
			
			JDBC.setStarter(p.getIdLong(), select);
			JDBC.setState(p.getIdLong(), "Tutorial08");
		}
	}
}
