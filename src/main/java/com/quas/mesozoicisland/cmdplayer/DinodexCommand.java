package com.quas.mesozoicisland.cmdplayer;

import java.util.ArrayList;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.regex.Pattern;

import com.quas.mesozoicisland.JDBC;
import com.quas.mesozoicisland.cmdbase.ICommand;
import com.quas.mesozoicisland.enums.AccessLevel;
import com.quas.mesozoicisland.enums.DinosaurForm;
import com.quas.mesozoicisland.enums.DiscordChannel;
import com.quas.mesozoicisland.enums.DiscordRole;
import com.quas.mesozoicisland.objects.Dinosaur;
import com.quas.mesozoicisland.objects.Player;
import com.quas.mesozoicisland.util.Pair;
import com.quas.mesozoicisland.util.Util;

import net.dv8tion.jda.api.entities.PrivateChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class DinodexCommand implements ICommand {

	@Override
	public Pattern getCommand() {
		return pattern("dinodex( [a-z]+)*");
	}

	@Override
	public AccessLevel getAccessLevel() {
		return AccessLevel.Trainer;
	}

	@Override
	public String getCommandName() {
		return "dinodex";
	}

	@Override
	public String getCommandSyntax() {
		return "dinodex [form]";
	}

	@Override
	public String getCommandDescription() {
		return "Lists your dinodex. If a form is specified, only dinosaurs with that form will be listed.";
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
		
		event.getChannel().sendMessageFormat("**%s's Dinodex:**\nYou have obtained %,d of %,d dinosaurs.\nCheck your DMs for a detailed list.", event.getAuthor().getAsMention(), p.getDexCount(DinosaurForm.AllForms.getId()), JDBC.getDexCount(DinosaurForm.AllForms.getId())).complete();
		
		DinosaurForm target = DinosaurForm.AllForms;
		if (args.length > 0) {
			target = DinosaurForm.getDinosaurForm(Util.join(args, " ", 0, args.length));
			if (target == DinosaurForm.Invalid) target = DinosaurForm.AllForms;
		}

		ArrayList<String> print = new ArrayList<String>();
		print.add("__**DINODEX**__");
		
		TreeMap<Integer, ArrayList<Pair<Dinosaur, Boolean>>> dinos = new TreeMap<Integer, ArrayList<Pair<Dinosaur, Boolean>>>();
		for (Dinosaur d : Dinosaur.values()) {
			if (d.getDex() < 0) continue;
			if (d.getForm() < 0) continue;
			if (!dinos.containsKey(d.getDex())) dinos.put(d.getDex(), new ArrayList<Pair<Dinosaur, Boolean>>());
			
			Dinosaur pd = Dinosaur.getDinosaur(p.getIdLong(), d.getIdPair());
			dinos.get(d.getDex()).add(new Pair<Dinosaur, Boolean>(d, pd != null));
		}

		for (int key : dinos.keySet()) {
			Dinosaur base = Dinosaur.getDinosaur(key, DinosaurForm.Standard.getId());
			ArrayList<String> forms = new ArrayList<String>();
			TreeSet<DinosaurForm> dinosaurForms = new TreeSet<>();

			for (Pair<Dinosaur, Boolean> pair : dinos.get(key)) {
				DinosaurForm form = pair.getFirstValue().getDinosaurForm();
				if (form.getOwnedEmote() == null && form.getUnownedEmote() == null) continue;
				dinosaurForms.add(form);
				
				if (pair.getSecondValue()) {
					if (form.getOwnedEmote() == null) continue;
					forms.add(form.getOwnedEmote().toString());
				} else {
					if (form.getUnownedEmote() == null) continue;
					forms.add(form.getUnownedEmote().toString());
				}
			}
			
			if (target == DinosaurForm.AllForms || dinosaurForms.contains(target)) {
				print.add(String.format("%s %s %s %s", base, base.getElement().getAsBrackets(), base.getRarity().getAsBrackets(), String.join(" ", forms)));
			}
		}
		
		PrivateChannel pc = event.getAuthor().openPrivateChannel().complete();
		for (String s : Util.bulkify(print)) pc.sendMessage(s).complete();
	}
}
