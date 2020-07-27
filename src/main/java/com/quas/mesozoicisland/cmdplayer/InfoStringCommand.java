package com.quas.mesozoicisland.cmdplayer;

import java.util.regex.Pattern;

import com.quas.mesozoicisland.objects.Player;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class InfoStringCommand extends InfoDinosaurCommand {

	@Override
	public Pattern getCommand() {
		return pattern("info ", ALPHA);
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
	public void run(MessageReceivedEvent event, String... args) {
		Player p = Player.getPlayer(event.getAuthor().getIdLong());
		if (p == null) return;
		
		switch (args[0].toLowerCase()) {
		case "latest":
			if (p.getLatest() == null) {
				event.getChannel().sendMessageFormat("%s, you do not have a latest dinosaur.", p.getAsMention()).complete();
			} else {
				super.run(event, p.getLatest());
			}
			break;
		case "starter":
			if (p.getStarter() == null) {
				event.getChannel().sendMessageFormat("%s, you do not have a starter dinosaur.", p.getAsMention()).complete();
			} else {
				super.run(event, p.getStarter());
			}
			break;
		default:
			break;
		}
	}
}
