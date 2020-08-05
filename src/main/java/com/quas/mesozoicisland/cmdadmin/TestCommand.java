package com.quas.mesozoicisland.cmdadmin;

import java.io.File;
import java.util.regex.Pattern;

import com.quas.mesozoicisland.cmdbase.CommandManager;
import com.quas.mesozoicisland.cmdbase.ICommand;
import com.quas.mesozoicisland.enums.AccessLevel;
import com.quas.mesozoicisland.enums.DiscordChannel;
import com.quas.mesozoicisland.enums.DiscordRole;
import com.quas.mesozoicisland.enums.EggColor;
import com.quas.mesozoicisland.enums.ShopType;
import com.quas.mesozoicisland.objects.Egg;
import com.quas.mesozoicisland.util.Constants;
import com.quas.mesozoicisland.util.Daily;
import com.quas.mesozoicisland.util.DinoMath;

import net.dv8tion.jda.api.EmbedBuilder;
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
			
			case "daily": {
				Daily.doUpdate(System.currentTimeMillis());
			} break;
			
			case "commands": {
				event.getChannel().sendMessageFormat("%s, there are %,d commands.", event.getAuthor().getAsMention(), CommandManager.values().size()).complete();
			} break;
			
			case "shop": {
				event.getChannel().sendMessageFormat("%s, `%s`.", event.getAuthor().getAsMention(), ShopType.Tutorial.getName()).complete();
			} break;

			case "egggen": {
				for (EggColor ec : EggColor.values()) {
					Egg egg = Egg.getEgg(ec);
					File f = egg.getImage();
					
					EmbedBuilder eb = new EmbedBuilder();
					eb.setTitle(ec.toString());
					eb.setImage("attachment://" + f.getName());
					event.getChannel().sendMessage(eb.build()).addFile(f).complete();
				}
				event.getChannel().sendMessage("Done.").complete();
			} break;
			
			}
		} catch (Exception e) {
			e.printStackTrace();
			event.getChannel().sendMessageFormat("%s, an error has occured.", event.getAuthor().getAsMention()).complete();
		}
	}
}
