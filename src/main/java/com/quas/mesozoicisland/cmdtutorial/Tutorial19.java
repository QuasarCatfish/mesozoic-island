package com.quas.mesozoicisland.cmdtutorial;

import java.util.ArrayList;
import java.util.regex.Pattern;

import com.quas.mesozoicisland.JDBC;
import com.quas.mesozoicisland.MesozoicIsland;
import com.quas.mesozoicisland.cmdbase.ICommand;
import com.quas.mesozoicisland.enums.AccessLevel;
import com.quas.mesozoicisland.enums.DiscordChannel;
import com.quas.mesozoicisland.enums.DiscordRole;
import com.quas.mesozoicisland.enums.Stat;
import com.quas.mesozoicisland.objects.Dinosaur;
import com.quas.mesozoicisland.objects.Element;
import com.quas.mesozoicisland.objects.Player;
import com.quas.mesozoicisland.util.Constants;
import com.quas.mesozoicisland.util.MesozoicRandom;
import com.quas.mesozoicisland.util.Pair;
import com.quas.mesozoicisland.util.Util;

import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class Tutorial19 implements ICommand {

	@Override
	public Pattern getCommand() {
		return pattern("ready");
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
		return "Tutorial19";
	}
	
	@Override
	public void run(MessageReceivedEvent event, String... args) {
		Player p = Player.getPlayer(event.getAuthor().getIdLong());
		if (p == null) return;
		
		Util.sleep(500);
		JDBC.setState(p.getIdLong(), "TutorialXX");
		
		Util.sleep(1000);
		sendTyping(event.getChannel(), 3000);
		event.getChannel().sendMessageFormat("Alright, let's get this battle started! %s, be the ref, please!", MesozoicIsland.getAssistant().getGuild().getSelfMember().getEffectiveName()).complete();
		
		MessageChannel assistantChannel = MesozoicIsland.getAssistant().getGuild().getTextChannelById(event.getChannel().getId());
		Dinosaur play = Dinosaur.getDinosaur(p.getIdLong(), Util.getDexForm(p.getStarter()));
		Dinosaur prof = getProfessorDinosaur(play).setLevel(1).setRank(1);
		
		Util.sleep(2000);
		assistantChannel.sendMessageFormat("%s is ready to referee for the battle between %s and %s.", MesozoicIsland.getAssistant().getGuild().getSelfMember().getEffectiveName(), p.getRawName(), event.getGuild().getSelfMember().getEffectiveName()).complete();
		
		Util.sleep(5000);
		assistantChannel.sendMessageFormat("**%s's Team:**\n\t%s\n\n**%s's Team:**\n\t%s", p.getRawName(), play.toString(), event.getGuild().getSelfMember().getEffectiveName(), prof.toString()).complete();

		// Battle against Professor
		String professor = event.getGuild().getSelfMember().getEffectiveName();
		while (prof.getCurrentHealth() > 0) {
			StringBuilder sb = new StringBuilder();
			if (getDamage(prof, play) > play.getCurrentHealth() || MesozoicRandom.nextBoolean()) {
				// Player attacks professor
				long damage = getDamage(play, prof);
				sb.append(String.format("%s's %s attacks %s's %s. ", p.getName(), play.getEffectiveName(), professor, prof.getEffectiveName()));
				JDBC.addItem(p.getIdLong(), Stat.DamageDealt.getId(), Math.min(damage, prof.getCurrentHealth()));
				
				prof.damage(damage);
				if (prof.getCurrentHealth() > 0) {
					sb.append(String.format("%s's %s took %,d damage and has %,d health remaining.", professor, prof.getEffectiveName(), damage, prof.getCurrentHealth()));
				} else {
					sb.append(String.format("%s's %s took %,d damage and was defeated.", professor, prof.getEffectiveName(), damage));
				}
			} else {
				// Professor attacks player
				long damage = getDamage(prof, play);
				sb.append(String.format("%s's %s attacks %s's %s. ", professor, prof.getEffectiveName(), p.getName(), play.getEffectiveName()));
				JDBC.addItem(p.getIdLong(), Stat.DamageReceived.getId(), Math.min(damage, play.getCurrentHealth()));

				play.damage(damage);
				sb.append(String.format("%s's %s took %,d damage and has %,d health remaining.", p.getName(), play.getEffectiveName(), damage, play.getCurrentHealth()));
			}
			
			Util.sleep(5000);
			assistantChannel.sendMessage(sb.toString()).complete();
		}

		Util.sleep(3000);
		sendTyping(event.getChannel(), 2000);
		event.getChannel().sendMessage("Well, darn. I thought I had you there.").complete();
		
		Util.sleep(1000);
		sendTyping(event.getChannel(), 2000);
		event.getChannel().sendMessage("Congratulations though, you deserved it!").complete();
		
		Util.sleep(1000);
		sendTyping(event.getChannel(), 2000);
		event.getChannel().sendMessage("You're certainly one promising trainer.").complete();
		
		Util.sleep(1000);
		sendTyping(event.getChannel(), 3000);
		event.getChannel().sendMessage("You might even be able to win the title of Mesozoic Island Champion one day!").complete();
		
		Util.sleep(1000);
		sendTyping(event.getChannel(), 2000);
		event.getChannel().sendMessage("Or maybe lead one of the island's guilds.").complete();
		
		Util.sleep(1000);
		sendTyping(event.getChannel(), 2000);
		event.getChannel().sendMessage("Up to you what you want to do here on Mesozoic Island.").complete();
		
		Util.sleep(1000);
		sendTyping(event.getChannel(), 2000);
		event.getChannel().sendMessage("It's your own adventure, after all.").complete();
		
		Util.sleep(1000);
		sendTyping(event.getChannel(), 3000);
		event.getChannel().sendMessage("It's time for you to set off on your own adventure as a dinosaur trainer.").complete();
		
		Util.sleep(1000);
		sendTyping(event.getChannel(), 2000);
		event.getChannel().sendMessage("Good luck out there!").complete();
		
		Util.sleep(2000);
		assistantChannel.sendMessageFormat("Congratulations, %s, on completing the tutorial.", p.getRawName()).complete();
		
		Util.sleep(2000);
		assistantChannel.sendMessage("The rest of the island will now be unlocked for you.").complete();
		
		Util.sleep(2000);
		assistantChannel.sendMessageFormat("%s, you have been promoted from 'New Player' to 'Dinosaur Trainer'.", p.getAsMention()).complete();
		Util.addRoleToMember(event.getMember(), DiscordRole.DinosaurTrainer.getIdLong());
		
		Util.sleep(2000);
		assistantChannel.sendMessage("When playing Mesozoic Island, other players will be more than willing to help you with any questions you might have. Don't be afraid to ask.").complete();
		
		Util.sleep(2000);
		assistantChannel.sendMessage("I'll do you a favor and introduce you to everyone.").complete();
		
		Util.sleep(2000);
		MesozoicIsland.getAssistant().getGuild().getTextChannelById(Constants.SPAWN_CHANNEL.getIdLong()).sendMessageFormat("Please welcome %s and their %s to Mesozoic Island.", p.getAsMention(), Dinosaur.getDinosaur(p.getIdLong(), Util.getDexForm(p.getStarter())).getDinosaurName()).complete();
		Constants.addStarterMail(p);
		
		Util.sleep(2000);
		assistantChannel.sendMessageFormat("In two minutes, you will lose access to this channel and the %s channel.", DiscordChannel.Welcome.toString()).complete();
		JDBC.setState(p.getIdLong(), "0");
		
		Util.sleep(120_000);
		Util.removeRoleFromMember(event.getMember(), DiscordRole.NewPlayer.getIdLong());
		if (event.getChannel().getIdLong() != DiscordChannel.CloneMe.getIdLong()) event.getTextChannel().delete().complete();
		else assistantChannel.sendMessage("Unable to delete tutorial channel.").complete();
	}
	
	private Dinosaur getProfessorDinosaur(Dinosaur player) {
		ArrayList<Pair<Dinosaur, String>> starters = Constants.getStarterDinosaurs();
		for (int q = 0; q < starters.size(); q++) {
			if (starters.get(q).getFirstValue().getIdPair().equals(player.getIdPair())) {
				return starters.get((q + 1) % starters.size()).getFirstValue();
			}
		}
		return starters.get(0).getFirstValue();
	}

	private long getDamage(Dinosaur attack, Dinosaur defend) {
		long damage = Math.round(1d * attack.getAttack() * attack.getAttack() / defend.getDefense() / 4d);
		double effectiveness = Element.getEffectiveness(attack.getElement(), defend.getElement());
		damage *= effectiveness;
		return damage;
	}
}
