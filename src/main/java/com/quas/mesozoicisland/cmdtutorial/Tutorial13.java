package com.quas.mesozoicisland.cmdtutorial;

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

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class Tutorial13 implements ICommand {

	@Override
	public Pattern getCommand() {
		return pattern("shop");
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
		return "Tutorial13";
	}
	
	@Override
	public synchronized void run(MessageReceivedEvent event, String... args) {
		Player p = Player.getPlayer(event.getAuthor().getIdLong());
		if (p == null) return;
		
		Util.sleep(500);
		JDBC.setState(p.getIdLong(), "TutorialXX");
		
		Util.sleep(1000);
		sendTyping(event.getChannel(), 2000);
		event.getChannel().sendMessage("This is the shop. You can see what items are available for purchase here.").complete();
		
		Util.sleep(1000);
		sendTyping(event.getChannel(), 3000);
		event.getChannel().sendMessage("Currently, there are two items that are available for purchase, but that can change from time to time.").complete();
		
		Util.sleep(1000);
		sendTyping(event.getChannel(), 2000);
		event.getChannel().sendMessage("Since there are no cookies in stock, why don't you buy an XP Potion instead?").complete();
		
		Util.sleep(1000);
		sendTyping(event.getChannel(), 2000);
		event.getChannel().sendMessage("Here, I'll even give you enough money to buy one.").complete();
		
		int count = 2000;
		Item money = Item.getItem(ItemID.TutorialDinosaurCoin);
		
		Util.sleep(1000);
		JDBC.addItem(p.getIdLong(), money.getIdDmg(), count);
		MesozoicIsland.getAssistant().getGuild().getTextChannelById(event.getChannel().getId()).sendMessageFormat("%s, you have been given %,d %s.", p.getAsMention(), count, money.toString(count)).complete();
		
		Util.sleep(1000);
		sendTyping(event.getChannel(), 2000);
		event.getChannel().sendMessage("Using `buy TutorialPotion`, you can buy the XP Potion! Go ahead and buy one.").complete();
		
		JDBC.setState(p.getIdLong(), "Tutorial14");
	}
}
