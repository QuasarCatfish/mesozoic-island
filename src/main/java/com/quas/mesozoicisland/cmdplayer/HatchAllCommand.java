package com.quas.mesozoicisland.cmdplayer;

import java.util.regex.Pattern;

import com.quas.mesozoicisland.enums.DiscordChannel;
import com.quas.mesozoicisland.enums.DiscordRole;
import com.quas.mesozoicisland.objects.Egg;
import com.quas.mesozoicisland.objects.Player;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class HatchAllCommand extends HatchCommand {

	@Override
	public Pattern getCommand() {
		return pattern("hatch all");
	}

	@Override
	public String getCommandSyntax() {
		return "hatch all";
	}

	@Override
	public String getCommandDescription() {
		return "Hatches all eggs that are able to hatch.";
	}

	@Override
	public DiscordChannel[] getUsableChannels() {
		return DiscordChannel.STANDARD_CHANNELS_DMS;
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
		Player p = Player.getPlayer(event.getAuthor().getIdLong());
		if (p == null) return;
		
		int count = 0;
		for (Egg egg : p.getEggs()) {
			if (egg.isHatchable()) {
				count++;
				super.run(event, "E" + egg.getIncubatorSlot());
			}
		}

		if (count == 0) {
			event.getChannel().sendMessageFormat("%s, none of your eggs are able to hatch.", p.getAsMention()).complete();
		}
	}
}
