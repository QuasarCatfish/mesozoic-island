package com.quas.mesozoicisland.cmdtutorial;

import java.awt.Color;
import java.util.regex.Pattern;

import com.quas.mesozoicisland.JDBC;
import com.quas.mesozoicisland.MesozoicIsland;
import com.quas.mesozoicisland.cmdbase.ICommand;
import com.quas.mesozoicisland.enums.AccessLevel;
import com.quas.mesozoicisland.enums.DiscordChannel;
import com.quas.mesozoicisland.enums.DiscordRole;
import com.quas.mesozoicisland.enums.ItemID;
import com.quas.mesozoicisland.objects.Item;
import com.quas.mesozoicisland.objects.Player;
import com.quas.mesozoicisland.util.Util;

import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class Tutorial05 implements ICommand {

	@Override
	public Pattern getCommand() {
		return pattern(".*");
	}

	@Override
	public AccessLevel getAccessLevel() {
		return AccessLevel.Trainer;
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
	public DiscordChannel[] getUsableChannels() {
		return Util.arr(DiscordChannel.CloneMe, DiscordChannel.ClonedChannel);
	}

	@Override
	public DiscordRole[] getRequiredRoles() {
		return null;
	}

	@Override
	public String getRequiredState() {
		return "Tutorial05";
	}
	
	@Override
	public void run(MessageReceivedEvent event, String... args) {
		Player p = Player.getPlayer(event.getAuthor().getIdLong());
		if (p == null) return;
		
		Util.sleep(500);
		JDBC.setState(p.getIdLong(), "TutorialXX");
		
		String color = event.getMessage().getContentRaw().toLowerCase();
		
		if (color.matches("red|orange|yellow|green|blue|purple")) {
			String cstr = null;
			Color c = null;
			
			switch (color) {
			case "red":
				cstr = "Red";
				c = new Color(255, 100, 100);
				break;
			case "orange":
				cstr = "Orange";
				c = new Color(255, 175, 100);
				break;
			case "yellow":
				cstr = "Yellow";
				c = new Color(255, 255, 125);
				break;
			case "green":
				cstr = "Green";
				c = new Color(150, 255, 150);
				break;
			case "blue":
				cstr = "Blue";
				c = new Color(100, 150, 255);
				break;
			case "purple":
				cstr = "Purple";
				c = new Color(200, 125, 255);
				break;
			}
			
			sendTyping(event.getChannel(), 2000);
			event.getChannel().sendMessageFormat("%s it is!", cstr).complete();
			Util.sleep(1000);
			
			sendTyping(event.getChannel(), 3000);
			event.getChannel().sendMessage("Theres one last thing we need to do before you get your license. We need to take your picture!").complete();
			Util.sleep(1000);
			
			sendTyping(event.getChannel(), 1000);
			event.getChannel().sendMessage("Say \"Cheese\"!").complete();
			Util.sleep(500);
			
			MessageChannel assistantChannel = MesozoicIsland.getAssistant().getGuild().getTextChannelById(event.getChannel().getIdLong());
			
			assistantChannel.sendMessageFormat("*Snaps a picture of %s.*", p.getRawName()).complete();
			Util.sleep(1500);
			
			Item license = Item.getItem(ItemID.MesozoicIslandTrainerLicense);
			
			sendTyping(event.getChannel(), 2000);
			event.getChannel().sendMessage("Here we have it, your official " + license.toString() + "!").complete();
			Util.sleep(1000);
			
			JDBC.addItem(p.getIdLong(), license.getIdDmg());
			assistantChannel.sendMessageFormat("%s, you have been given a %s.", p.getAsMention(), license.toString()).complete();
			Util.sleep(1000);
			
			sendTyping(event.getChannel(), 2000);
			event.getChannel().sendMessageFormat("To check it out, you can use the command `use %d`.", license.getId()).complete();
			
			JDBC.setColor(p.getIdLong(), c);
			JDBC.setState(p.getIdLong(), "Tutorial06");	
		} else {
			sendTyping(event.getChannel(), 3000);
			event.getChannel().sendMessage("Unfortunately, there is something wrong with the color you provided. Please try again.\n*If you believe this is an error, please contact an Administrator for help.*").complete();
			
			JDBC.setState(p.getIdLong(), "Tutorial05");
		}
	}
}
