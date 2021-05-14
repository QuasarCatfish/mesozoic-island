package com.quas.mesozoicisland.cmdplayer;

import java.util.regex.Pattern;

import com.quas.mesozoicisland.battle.BattleTier;
import com.quas.mesozoicisland.cmdbase.ICommand;
import com.quas.mesozoicisland.enums.AccessLevel;
import com.quas.mesozoicisland.enums.DiscordChannel;
import com.quas.mesozoicisland.enums.DiscordRole;
import com.quas.mesozoicisland.objects.Dinosaur;
import com.quas.mesozoicisland.objects.Player;
import com.quas.mesozoicisland.util.DinoMath;
import com.quas.mesozoicisland.util.Util;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class SelectedCommand implements ICommand {

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
		return "selected";
	}

	@Override
	public String getCommandSyntax() {
		return "selected";
	}

	@Override
	public String getCommandDescription() {
		return "Lists what dinosaurs are on your team.";
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
		
		if (p.getSelected() == null) {
			event.getChannel().sendMessageFormat("%s, you have no dinosaurs selected.", event.getAuthor().getAsMention()).complete();
			return;
		}
		
		String[] select = p.getSelected().split("\\s+");
		
		if (select == null || select.length == 0) {
			event.getChannel().sendMessageFormat("%s, you have no dinosaurs selected.", event.getAuthor().getAsMention()).complete();
		} else {
			StringBuilder sb = new StringBuilder();
			sb.append(p.getAsMention());
			
			if (select.length == 1) sb.append(", here is your selected dinosaur:\n");
			else sb.append(", here are your selected dinosaurs:\n");
			
			Dinosaur[] team = new Dinosaur[select.length];
			for (int q = 0; q < select.length; q++) {
				team[q] = Dinosaur.getDinosaur(p.getIdLong(), Util.getDexForm(select[q]));
			}
			sb.append(Util.formatTeam(team));
			
			BattleTier bt = DinoMath.getBattleTier(team);
			long percent = DinoMath.getNextBattleTierPercent(team);
			if (percent >= 0) {
				sb.append(String.format("Your team is in the %s and is %,d%% of the way to the next tier.", bt, percent));
			} else {
				sb.append(String.format("Your team is in the %s.", bt));
			}
			
			event.getChannel().sendMessage(sb.toString()).complete();
		}
	}
}
