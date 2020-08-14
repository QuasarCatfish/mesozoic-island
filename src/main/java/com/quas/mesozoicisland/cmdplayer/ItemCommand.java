package com.quas.mesozoicisland.cmdplayer;

import java.util.regex.Pattern;

import com.quas.mesozoicisland.cmdbase.ICommand;
import com.quas.mesozoicisland.enums.AccessLevel;
import com.quas.mesozoicisland.enums.DiscordChannel;
import com.quas.mesozoicisland.enums.DiscordRole;
import com.quas.mesozoicisland.enums.ItemCategory;
import com.quas.mesozoicisland.objects.Item;
import com.quas.mesozoicisland.util.Constants;
import com.quas.mesozoicisland.util.Util;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class ItemCommand implements ICommand {

	@Override
	public Pattern getCommand() {
		return pattern("item( .+)+");
	}

	@Override
	public AccessLevel getAccessLevel() {
		return AccessLevel.Trainer;
	}

	@Override
	public String getCommandName() {
		return "item";
	}

	@Override
	public String getCommandSyntax() {
		return "item <item>";
	}

	@Override
	public String getCommandDescription() {
		return "Gets information on the given item.";
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
	public void run(MessageReceivedEvent event, String... args) {
		String itemname = Util.join(args, " ", 0, args.length);
		while (itemname.charAt(0) == '0') itemname = itemname.substring(1);

		for (Item i : Item.values()) {
			if (i.getItemCategory() == ItemCategory.None) continue;
			
			String[] arr = Util.arr(Integer.toString(i.getId()), i.toString(1), i.toString(2));
			for (String s : arr) {
				if (s.equalsIgnoreCase(itemname)) {
					EmbedBuilder eb = new EmbedBuilder();
					eb.setColor(Constants.COLOR);
					if (!Constants.HIDE_ITEMS || i.isDiscovered()) {
						eb.setTitle(String.format("**%s** (ID %d)", i.toString(), i.getId()));
						eb.setDescription(i.getDescription());
					} else {
						eb.setTitle("**Unknown Item** (ID ???)");
						eb.setDescription("This item has not been discovered yet.");
					}
					event.getChannel().sendMessage(eb.build()).complete();
					return;
				}
			}
		}
		
		event.getChannel().sendMessageFormat("%s, this item does not exist.", event.getAuthor().getAsMention()).complete();
	}
}
