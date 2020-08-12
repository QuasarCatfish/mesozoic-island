package com.quas.mesozoicisland.cmdplayer;

import java.util.regex.Pattern;

import com.quas.mesozoicisland.cmdbase.ICommand;
import com.quas.mesozoicisland.enums.AccessLevel;
import com.quas.mesozoicisland.enums.DiscordChannel;
import com.quas.mesozoicisland.enums.DiscordRole;
import com.quas.mesozoicisland.enums.ShopType;
import com.quas.mesozoicisland.objects.Player;
import com.quas.mesozoicisland.util.Constants;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class ShopListCommand implements ICommand {

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
		return "shop";
	}

	@Override
	public String getCommandSyntax() {
		return "shop";
	}

	@Override
	public String getCommandDescription() {
		return "Gets a list of the stores and what type of items they sell.";
	}

	@Override
	public DiscordChannel[] getUsableChannels() {
		return DiscordChannel.STANDARD_CHANNELS;
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
		Player p = Player.getPlayer(event.getAuthor().getIdLong());
		if (p == null) return;
		
		EmbedBuilder eb = new EmbedBuilder();
		eb.setTitle("Mesozoic Island Shop");
		eb.setDescription("To see the packages sold in each of these stores, use `shop <store>`.");
		eb.setColor(Constants.COLOR);

		for (ShopType st : ShopType.values()) {
			if (!st.isVisible()) continue;
			eb.addField(st.getName(), st.getDescription(), false);
		}

		event.getChannel().sendMessage(eb.build()).complete();
	}
}
