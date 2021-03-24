package com.quas.mesozoicisland.cmdtutorial;

import java.util.regex.Pattern;

import com.quas.mesozoicisland.JDBC;
import com.quas.mesozoicisland.MesozoicIsland;
import com.quas.mesozoicisland.cmdbase.ICommand;
import com.quas.mesozoicisland.enums.AccessLevel;
import com.quas.mesozoicisland.enums.DiscordChannel;
import com.quas.mesozoicisland.enums.DiscordRole;
import com.quas.mesozoicisland.enums.ItemID;
import com.quas.mesozoicisland.objects.Dinosaur;
import com.quas.mesozoicisland.objects.Item;
import com.quas.mesozoicisland.objects.Player;
import com.quas.mesozoicisland.util.Constants;
import com.quas.mesozoicisland.util.Util;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class Tutorial09 implements ICommand {

	@Override
	public Pattern getCommand() {
		return pattern("dinos");
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
		return "Tutorial09";
	}
	
	@Override
	public synchronized void run(MessageReceivedEvent event, String... args) {
		Player p = Player.getPlayer(event.getAuthor().getIdLong());
		if (p == null) return;
		
		Util.sleep(500);
		JDBC.setState(p.getIdLong(), "TutorialXX");
		
		Dinosaur starter = Dinosaur.getDinosaur(p.getIdLong(), Util.getDexForm(p.getStarter()));
		
		Util.sleep(1000);
		sendTyping(event.getChannel(), 3000);
		event.getChannel().sendMessageFormat("You'll have gotten a private message from %s containing a list of all your dinosaurs and a few stats about each of them.", MesozoicIsland.getAssistant().getGuild().getSelfMember().getEffectiveName()).complete();
		Util.sleep(3000);
		
		sendTyping(event.getChannel(), 1000);
		event.getChannel().sendMessage("Let's go over each of the stats you can see:").complete();
		Util.sleep(1000);
		
		sendTyping(event.getChannel(), 3000);
		event.getChannel().sendMessageFormat("The **%s** is your dinosaur's ID number. You use this number in all the commands that require a dinosaur's ID.", starter.getId()).complete();
		Util.sleep(2000);
		
		sendTyping(event.getChannel(), 3000);
		event.getChannel().sendMessageFormat("**Level %d** is your dinosaur's current level. By defeating other dinosaurs in battle, your dinosaurs will gain XP and level up.", starter.getLevel()).complete();
		Util.sleep(2000);
		
		sendTyping(event.getChannel(), 3000);
		event.getChannel().sendMessageFormat("**Rank %s** is your dinosaur's current rank. By collecting enough of a certain species of dinosaur, you can rank it up.", starter.getRankString()).complete();
		Util.sleep(2000);
		
		sendTyping(event.getChannel(), 3000);
		event.getChannel().sendMessageFormat("**%s** is your dinosaur's element. This determines what attacks are strong and weak against your dinosaur.", starter.getElement().getName()).complete();
		Util.sleep(2000);
		
		sendTyping(event.getChannel(), 3000);
		event.getChannel().sendMessageFormat("**%s** is your dinosaur's rarity. The higher the rarity, the less it spawns in the wild. Rarer dinosaurs also tend to be more valuable.", starter.getRarity().getName()).complete();
		Util.sleep(2000);
		
		sendTyping(event.getChannel(), 2000);
		event.getChannel().sendMessage("And lastly, your three stats: health, attack, and defense.").complete();
		Util.sleep(2000);
		
		sendTyping(event.getChannel(), 2000);
		event.getChannel().sendMessage("The more health your dinosaur has, the longer it can survive in battles.").complete();
		Util.sleep(2000);
		
		sendTyping(event.getChannel(), 2000);
		event.getChannel().sendMessage("The more attack your dinosaur has, the more damage it'll deal every attack.").complete();
		Util.sleep(2000);
		
		sendTyping(event.getChannel(), 2000);
		event.getChannel().sendMessage("The more defense your dinosaur has, the less damage it'll take from opponents' attacks.").complete();
		Util.sleep(5000);
		
		sendTyping(event.getChannel(), 3000);
		event.getChannel().sendMessage("In battles against wild dinosaurs, defeating them will yield their crystal. Collecting the crystals of various dinosaurs helps me with my research.").complete();
		Util.sleep(1000);
		
		sendTyping(event.getChannel(), 1000);
		event.getChannel().sendMessageFormat("As thanks for helping me with my research, I pay a salary of %,d %s every day to each dinosaur trainer, provided that they claim it.", Constants.DAILY_MONEY, Item.getItem(ItemID.DinosaurCoin).toString(Constants.DAILY_MONEY)).complete();
		Util.sleep(1000);
		
		sendTyping(event.getChannel(), 2000);
		event.getChannel().sendMessage("Try claiming your money for today with the `daily` command.").complete();
		
		JDBC.setState(p.getIdLong(), "Tutorial10");
	}
}
