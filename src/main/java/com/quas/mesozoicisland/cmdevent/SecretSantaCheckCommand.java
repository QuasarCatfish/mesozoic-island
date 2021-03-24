package com.quas.mesozoicisland.cmdevent;

import java.util.StringJoiner;
import java.util.regex.Pattern;

import com.quas.mesozoicisland.cmdbase.ICommand;
import com.quas.mesozoicisland.enums.AccessLevel;
import com.quas.mesozoicisland.enums.DiscordChannel;
import com.quas.mesozoicisland.enums.DiscordRole;
import com.quas.mesozoicisland.enums.EventType;
import com.quas.mesozoicisland.objects.Event;
import com.quas.mesozoicisland.objects.Player;

import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.PrivateChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class SecretSantaCheckCommand implements ICommand {

	@Override
	public Pattern getCommand() {
		return pattern("santa check");
	}

	@Override
	public AccessLevel getAccessLevel() {
		return AccessLevel.Trainer;
	}

	@Override
	public String getCommandName() {
		return "santa";
	}

	@Override
	public String getCommandSyntax() {
		return "santa check";
	}

	@Override
	public String getCommandDescription() {
		return "Checks which trainer you are buying gifts for.";
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

		if (!Event.isEventActive(EventType.SecretSanta)) {
			event.getChannel().sendMessageFormat("%s, the Secret Santa event is not currently running.", p.getAsMention()).complete();
			return;
		}

		if (!p.isSecretSanta()) {
			event.getChannel().sendMessageFormat("%s, you are not participating in the Secret Santa event.", p.getAsMention()).complete();
			return;
		}

		if (DiscordChannel.getChannel(event.getChannel()) == DiscordChannel.DirectMessages) {
			send(event.getChannel(), Player.getPlayer(p.getSecretSanta()));
		} else {
			event.getChannel().sendMessageFormat("%s, please check your DMs.", p.getAsMention()).complete();
			PrivateChannel pc = event.getAuthor().openPrivateChannel().complete();
			send(pc, Player.getPlayer(p.getSecretSanta()));
		}
	}

	private void send(MessageChannel channel, Player target) {
		if (target.getEventState() == null) {
			channel.sendMessageFormat("You are buying gifts for %s.", target.getName()).complete();
		} else {
			StringJoiner sj = new StringJoiner("\n");
			int q = 1;
			for (char c : target.getEventState().toCharArray()) {
				String x = "";
				if (c == 'R') x = "A Rarity Present";
				if (c == 'B') x = "A Battling Present";
				if (c == 'H') x = "A Hatching present";
				if (c == 'D') x = "A Dungeoning Present";
				if (c == 'S') x = "A Snacking Present";
				if (c == 'X') x = "A Super Present";
				sj.add(String.format("%d. %s", q++, x));
			}

			channel.sendMessageFormat("You are buying gifts for %s. Their preference for gifts are as follows:\n%s", target.getName(), sj.toString()).complete();
		}
	}
}
