package com.quas.mesozoicisland.cmdplayer;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.regex.Pattern;

import javax.imageio.ImageIO;

import com.quas.mesozoicisland.cmdbase.ICommand;
import com.quas.mesozoicisland.enums.AccessLevel;
import com.quas.mesozoicisland.enums.DiscordChannel;
import com.quas.mesozoicisland.enums.DiscordRole;
import com.quas.mesozoicisland.enums.EggColor;
import com.quas.mesozoicisland.util.Util;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class ColorCommand implements ICommand {

	@Override
	public Pattern getCommand() {
		return pattern("color .+");
	}

	@Override
	public AccessLevel getAccessLevel() {
		return AccessLevel.Trainer;
	}

	@Override
	public String getCommandName() {
		return "color";
	}

	@Override
	public String getCommandSyntax() {
		return "color <name>";
	}

	@Override
	public String getCommandDescription() {
		return "Gets a sample of the color with the given name.";
	}

	@Override
	public DiscordChannel[] getUsableChannels() {
		return Util.arr(DiscordChannel.BotCommands, DiscordChannel.GameTesting, DiscordChannel.DirectMessages);
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
		String name = Util.join(args, " ", 0, args.length);
		EggColor color = null;
		for (EggColor ec : EggColor.values()) {
			if (ec.name().equalsIgnoreCase(name) || ec.toString().equalsIgnoreCase(name)) {
				color = ec;
				break;
			}
		}
		
		if (color == null) {
			event.getChannel().sendMessageFormat("%s, this color does not exist.", event.getAuthor().getAsMention()).complete();
		} else {
			try {
				BufferedImage img = new BufferedImage(128, 128, BufferedImage.TYPE_INT_ARGB_PRE);
				Graphics g = img.getGraphics();
				g.setColor(color.toColor());
				g.fillRect(0, 0, img.getWidth(), img.getHeight());
				
				File file = new File("color.png");
				ImageIO.write(img, "PNG", file);
				
				EmbedBuilder eb = new EmbedBuilder();
				eb.setColor(color.toColor());
				eb.setTitle(color.toString());
				eb.setDescription("Hex: #" + color.getHex());
				eb.setImage("attachment://color.png");
				event.getChannel().sendMessage(eb.build()).addFile(file).complete();
				file.delete();
			} catch (IOException e) {
				event.getChannel().sendMessageFormat("%s, an error occured.", event.getAuthor().getAsMention()).complete();
			}
		}
	}
}
