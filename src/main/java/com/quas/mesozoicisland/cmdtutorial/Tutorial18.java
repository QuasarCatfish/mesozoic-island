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

public class Tutorial18 implements ICommand {

	@Override
	public Pattern getCommand() {
		return pattern("selected");
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
		return "Tutorial18";
	}
	
	@Override
	public synchronized void run(MessageReceivedEvent event, String... args) {
		Player p = Player.getPlayer(event.getAuthor().getIdLong());
		if (p == null) return;
		
		Util.sleep(500);
		JDBC.setState(p.getIdLong(), "TutorialXX");
		
		Util.sleep(1000);
		sendTyping(event.getChannel(), 4000);
		event.getChannel().sendMessage("Isn't that cool?! Now that you've got the hang of using these commands, how about the two of us have a battle right here and now?").complete();
		
		Util.sleep(1000);
		sendTyping(event.getChannel(), 2000);
		event.getChannel().sendMessage("Just let me know when you're ready.").complete();

		Util.sleep(500);
		MesozoicIsland.getAssistant().getGuild().getTextChannelById(event.getChannel().getId()).sendMessageFormat("Say `ready` to begin the battle against %s.", event.getGuild().getSelfMember().getEffectiveName()).complete();
		
		JDBC.setState(p.getIdLong(), "Tutorial19");
	}
}
