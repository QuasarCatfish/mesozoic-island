package com.quas.mesozoicisland.cmdadmin;

import java.util.regex.Pattern;

import com.quas.mesozoicisland.JDBC;
import com.quas.mesozoicisland.battle.SpawnManager;
import com.quas.mesozoicisland.cmdbase.CommandManager;
import com.quas.mesozoicisland.cmdbase.ICommand;
import com.quas.mesozoicisland.enums.AccessLevel;
import com.quas.mesozoicisland.enums.DiscordChannel;
import com.quas.mesozoicisland.enums.DiscordRole;
import com.quas.mesozoicisland.util.Constants;
import com.quas.mesozoicisland.util.DinoMath;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class TestCommand implements ICommand {

	@Override
	public Pattern getCommand() {
		return pattern("admin test( [a-z0-9]+)+");
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
		return "admin test <test>";
	}

	@Override
	public String getCommandDescription() {
		return "Executes a test code.";
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
		try {
			switch (args[1].toLowerCase()) {
			case "level": {
				int level = args.length > 2 ? Integer.parseInt(args[2].replaceAll("\\D", "")) : Constants.MAX_LEVEL;
				event.getChannel().sendMessageFormat("%s, a Level %,d dinosaur would have %,d XP.", event.getAuthor().getAsMention(), level, DinoMath.getXp(level)).complete();
			} break;
			
			case "commands": {
				event.getChannel().sendMessageFormat("%s, there are %,d commands.", event.getAuthor().getAsMention(), CommandManager.values().size()).complete();
			} break;
			
			case "spawncheck": {
				StringBuilder sb = new StringBuilder();
				sb.append("```");
				sb.append("\nspawn time  = " + SpawnManager.spawntime);
				sb.append("\nlast spawn  = " + SpawnManager.lastspawn);
				sb.append("\ncurrent     = " + System.currentTimeMillis());
				sb.append("\nwaiting     = " + SpawnManager.waiting);
				sb.append("\nwild battle = " + SpawnManager.isWildBattleHappening());
				sb.append("\ncfg spawn   = " + Constants.SPAWN);
				sb.append("\n```");
				event.getChannel().sendMessage(sb.toString()).complete();
			} break;
			
			case "reward": {
				String reward = JDBC.getReward(args[2]);
				if (reward == null) {
					event.getChannel().sendMessageFormat("%s, there is no reward named `%s`.", event.getAuthor().getAsMention(), args[2]).complete();
				} else {
					event.getChannel().sendMessageFormat("%s, the reward for `%s` is:\n%s", event.getAuthor().getAsMention(), args[2], JDBC.getRedeemMessage(reward)).complete();
				}
			} break;

			case "givehp": {
				int x = Integer.parseInt(args[2]);
				while (x --> 0) {
					JDBC.updateEggs();
				}
			} break;

			}
		} catch (Exception e) {
			e.printStackTrace();
			event.getChannel().sendMessageFormat("%s, an error has occured.", event.getAuthor().getAsMention()).complete();
		}
	}
}
