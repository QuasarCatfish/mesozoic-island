package com.quas.mesozoicisland.cmdadmin;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.regex.Pattern;

import javax.imageio.ImageIO;

import com.quas.mesozoicisland.cmdbase.CommandManager;
import com.quas.mesozoicisland.cmdbase.ICommand;
import com.quas.mesozoicisland.enums.AccessLevel;
import com.quas.mesozoicisland.enums.DiscordChannel;
import com.quas.mesozoicisland.enums.DiscordRole;
import com.quas.mesozoicisland.enums.ShopType;
import com.quas.mesozoicisland.objects.Egg;
import com.quas.mesozoicisland.util.Action;
import com.quas.mesozoicisland.util.Constants;
import com.quas.mesozoicisland.util.Daily;
import com.quas.mesozoicisland.util.DinoMath;
import com.quas.mesozoicisland.util.Pair;
import com.quas.mesozoicisland.util.Util;

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
				
			case "egg": {
				BufferedImage img = new BufferedImage(512, 512, BufferedImage.TYPE_INT_ARGB_PRE);
				Graphics g = img.getGraphics();
				
//				final int r = 256;
				for (int x = 0; x < img.getWidth(); x++) {
					for (int y = 0; y < img.getHeight(); y++) {
//						int dx = x - img.getWidth() / 2;
//						int dy = y - img.getWidth() / 2;
						
						g.setColor(x > 0 ? Color.WHITE : Color.BLACK);
						g.drawLine(x, y, x, y);
					}
				}
				
				File out = new File("Eggs/twotone8.png");
				ImageIO.write(img, "PNG", out);
				event.getChannel().sendMessageFormat("%s, successfully created the `%s` pattern.", event.getAuthor().getAsMention(), out.getName()).complete();
			} break;
			
			case "embed": {
				EmbedBuilder eb = new EmbedBuilder();
				eb.setColor(Constants.COLOR);
				String mention = "<@" + event.getAuthor().getId() + ">";
				eb.setTitle("Title: " + mention);
				eb.setDescription("Description: " + mention);
				eb.setAuthor("Author: " + mention);
				eb.setFooter("Footer: " + mention);
				eb.addField("Field: " + mention, "Field Desc: " + mention, false);
				event.getChannel().sendMessage(eb.build()).complete();
			} break;
			
			case "randomegg": {
				for (int q = 0; q < 10; q++) {
					Egg egg = Egg.getRandomEgg(new Pair<Integer, Integer>(1, 0));
					File f = egg.getImage();
					EmbedBuilder eb = new EmbedBuilder();
					eb.setTitle("Egg #" + (q + 1) + ": " + egg.getEggName());
					eb.setColor(Constants.COLOR);
					eb.setImage("attachment://" + f.getName());
					event.getChannel().sendMessage(eb.build()).addFile(f).complete();
					f.delete();
				}
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
			
			case "actions": {
				ArrayList<String> print = new ArrayList<String>();
				for (Action a : Action.getActions()) print.add(a.toString());
				for (String s : Util.bulkify(print)) event.getChannel().sendMessage(s).complete();
			} break;

			}
		} catch (Exception e) {
			e.printStackTrace();
			event.getChannel().sendMessageFormat("%s, an error has occured.", event.getAuthor().getAsMention()).complete();
		}
	}
}
