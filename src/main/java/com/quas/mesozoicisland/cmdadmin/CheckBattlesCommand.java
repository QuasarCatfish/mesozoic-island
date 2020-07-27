package com.quas.mesozoicisland.cmdadmin;

import java.util.regex.Pattern;

import com.quas.mesozoicisland.battle.Battle;
import com.quas.mesozoicisland.battle.SpawnManager;
import com.quas.mesozoicisland.cmdbase.ICommand;
import com.quas.mesozoicisland.enums.AccessLevel;
import com.quas.mesozoicisland.enums.CustomPlayer;
import com.quas.mesozoicisland.enums.DiscordChannel;
import com.quas.mesozoicisland.enums.DiscordRole;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class CheckBattlesCommand implements ICommand {

	@Override
	public Pattern getCommand() {
		return pattern("admin check battle");
	}

	@Override
	public AccessLevel getAccessLevel() {
		return AccessLevel.Admin;
	}

	@Override
	public String getCommandName() {
		return "admin";
	}

	@Override
	public String getCommandSyntax() {
		return "admin check battle";
	}

	@Override
	public String getCommandDescription() {
		return "Checks what players are battling.";
	}

	@Override
	public DiscordChannel[] getUsableChannels() {
		return DiscordChannel.ALL_CHANNELS;
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
	public void run(MessageReceivedEvent event, String... args) {
		StringBuilder sb = new StringBuilder();
		for (CustomPlayer cp : CustomPlayer.values()) {
			sb.append(cp.name());
			sb.append(" - ");
			sb.append(Battle.isPlayerBattling(cp.getIdLong()));
			sb.append("\n");
		}
		
		sb.append("Active Dungeon - ");
		sb.append(SpawnManager.isDungeonSpawned());
		
		event.getChannel().sendMessage(sb.toString()).complete();
	}
}
