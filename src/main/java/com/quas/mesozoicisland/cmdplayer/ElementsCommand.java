package com.quas.mesozoicisland.cmdplayer;

import java.util.ArrayList;
import java.util.regex.Pattern;

import com.quas.mesozoicisland.cmdbase.ICommand;
import com.quas.mesozoicisland.enums.AccessLevel;
import com.quas.mesozoicisland.enums.DiscordChannel;
import com.quas.mesozoicisland.enums.DiscordRole;
import com.quas.mesozoicisland.objects.Element;
import com.quas.mesozoicisland.util.Constants;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class ElementsCommand implements ICommand {

	@Override
	public Pattern getCommand() {
		return pattern("elements|types");
	}

	@Override
	public AccessLevel getAccessLevel() {
		return AccessLevel.Trainer;
	}

	@Override
	public String getCommandName() {
		return "elements";
	}

	@Override
	public String getCommandSyntax() {
		return "elements";
	}

	@Override
	public String getCommandDescription() {
		return "Lists the elements chart.";
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
		EmbedBuilder eb = new EmbedBuilder();
		eb.setTitle("Elements");
		eb.setColor(Constants.COLOR);
		
		for (Element e1 : Element.values()) {
			if (!e1.isVisible()) continue;
			
			ArrayList<String> strong = new ArrayList<String>();
			ArrayList<String> weak = new ArrayList<String>();
			
			for (Element e2 : Element.values()) {
				if (!e2.isVisible()) continue;
				
				double eff = e1.getEffectivenessAgainst(e2);
				if (eff > 1.001) {
					strong.add(e2.toString());
				} else if (eff < .999) {
					weak.add(e2.toString());
				}
			}
			
			StringBuilder sb = new StringBuilder();
			sb.append("Strong Against: ");
			if (strong.isEmpty()) sb.append("None");
			else sb.append(String.join(", ", strong));
			sb.append("\nResisted By: ");
			if (weak.isEmpty()) sb.append("None");
			else sb.append(String.join(", ", weak));
			
			eb.addField(e1.toString(), sb.toString(), true);
		}
		
		event.getChannel().sendMessage(eb.build()).complete();
	}
}
