package com.quas.mesozoicisland.cmdplayer;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.regex.Pattern;

import javax.imageio.ImageIO;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class ColorHexCommand extends ColorCommand {

	@Override
	public Pattern getCommand() {
		return pattern("color [0-9a-f]{6}") ;
	}

	@Override
	public String getCommandSyntax() {
		return "color <hex>";
	}

	@Override
	public String getCommandDescription() {
		return "Gets a sample of the color with the given hexadecimal code.";
	}

	@Override
	public void run(MessageReceivedEvent event, String... args) {
		Color color = new Color(Integer.parseInt(args[0], 16));
		
		try {
			BufferedImage img = new BufferedImage(128, 128, BufferedImage.TYPE_INT_ARGB_PRE);
			Graphics g = img.getGraphics();
			g.setColor(color);
			g.fillRect(0, 0, img.getWidth(), img.getHeight());
			
			File file = new File("color.png");
			ImageIO.write(img, "PNG", file);
			
			EmbedBuilder eb = new EmbedBuilder();
			eb.setColor(color);
			eb.setTitle("Color");
			eb.setDescription("Hex: #" + args[0].toUpperCase());
			eb.setImage("attachment://color.png");
			event.getChannel().sendMessage(eb.build()).addFile(file).complete();
			file.delete();
		} catch (IOException e) {
			event.getChannel().sendMessageFormat("%s, an error occured.", event.getAuthor().getAsMention()).complete();
		}
	}
}
