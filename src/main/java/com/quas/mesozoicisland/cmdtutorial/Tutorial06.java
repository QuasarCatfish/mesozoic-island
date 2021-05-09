package com.quas.mesozoicisland.cmdtutorial;

import java.util.ArrayList;
import java.util.regex.Pattern;

import com.quas.mesozoicisland.JDBC;
import com.quas.mesozoicisland.MesozoicIsland;
import com.quas.mesozoicisland.cmdbase.ICommand;
import com.quas.mesozoicisland.enums.AccessLevel;
import com.quas.mesozoicisland.enums.DiscordChannel;
import com.quas.mesozoicisland.enums.DiscordRole;
import com.quas.mesozoicisland.objects.Dinosaur;
import com.quas.mesozoicisland.objects.Element;
import com.quas.mesozoicisland.objects.Player;
import com.quas.mesozoicisland.util.Constants;
import com.quas.mesozoicisland.util.Pair;
import com.quas.mesozoicisland.util.Util;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.requests.restaction.MessageAction;

public class Tutorial06 implements ICommand {

	@Override
	public Pattern getCommand() {
		return pattern("use 1");
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
		return "Tutorial06";
	}
	
	@Override
	public synchronized void run(MessageReceivedEvent event, String... args) {
		Player p = Player.getPlayer(event.getAuthor().getIdLong());
		if (p == null) return;
		
		Util.sleep(500);
		JDBC.setState(p.getIdLong(), "TutorialXX");
		
		MessageChannel assistantChannel = MesozoicIsland.getAssistant().getGuild().getTextChannelById(event.getChannel().getId());
		
		Util.sleep(2000);
		sendTyping(event.getChannel(), 2000);
		event.getChannel().sendMessage("Well, now that you're officially a dinosaur trainer, let's teach you how to do things on Mesozoic Island.").complete();
		Util.sleep(1000);
		
		sendTyping(event.getChannel(), 2000);
		event.getChannel().sendMessage("Firstly, we need to get you situated with a starter dinosaur! Let's head over to the nursery so you can pick one out.").complete();
		Util.sleep(5000);
		
		sendTyping(event.getChannel(), 1000);
		event.getChannel().sendMessage("Here we are, the nursery! Now, what dinosaurs do we have for you to choose from today?").complete();
		Util.sleep(1000);
		
		sendTyping(event.getChannel(), 1000);
		event.getChannel().sendMessageFormat("%s, can you pull up the data on each of these eight dinosaurs that %s can choose from?", MesozoicIsland.getAssistant().getName(), p.getRawName()).complete();
		Util.sleep(1000);
		
		assistantChannel.sendMessage("Loading dinosaur data... Please wait...").complete();
		Util.sleep(5000);
		
		ArrayList<Pair<Dinosaur, String>> pairs = Constants.getStarterDinosaurs();
		
		for (Pair<Dinosaur, String> pair : pairs) {
			EmbedBuilder eb = new EmbedBuilder();
			eb.setColor(Constants.COLOR);
			eb.setTitle("Dinosaur Data");
			
			eb.addField("Dinosaur Name", pair.getFirstValue().getDinosaurName(), true);
			eb.addField("Element", pair.getFirstValue().getElement().getAsString(), true);
			eb.addField("Element Effectiveness", getElementEffectiveness(pair.getFirstValue().getElement()), true);
			eb.addField("Dinosaur Description", pair.getSecondValue(), false);

			MessageAction ma = assistantChannel.sendMessage(eb.build());

			if (pair.getFirstValue().getImageLink() != null) {
				eb.setThumbnail("attachment://" + pair.getFirstValue().getImageLink());
				ma.addFile(pair.getFirstValue().getImage());
			}
			
			ma.complete();
			Util.sleep(1000);
		}
		
		sendTyping(event.getChannel(), 2000);
		event.getChannel().sendMessageFormat("So, %s, take your choice. Which dinosaur do you want as your starter?", p.getRawName()).complete();
		
		JDBC.setState(p.getIdLong(), "Tutorial07");
	}

	private String getElementEffectiveness(Element e1) {
		ArrayList<String> strong = new ArrayList<String>();
		ArrayList<String> weak = new ArrayList<String>();

		for (Element e2 : Element.values()) {
			if (e2.getId() < 0) continue;
			if (Integer.bitCount(e2.getId()) > 1) continue;
			
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

		return sb.toString();
	}
}
