package com.quas.mesozoicisland.cmdplayer;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.regex.Pattern;

import com.quas.mesozoicisland.JDBC;
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
		
		for (Item i : Item.values()) {
			if (i.getItemCategory() == ItemCategory.None) continue;
			
			String[] arr = Util.arr(Integer.toString(i.getId()), i.toString(1), i.toString(2));
			for (String s : arr) {
				if (s.equalsIgnoreCase(itemname)) {
					EmbedBuilder eb = new EmbedBuilder();
					eb.setColor(Constants.COLOR);
					if (!Constants.HIDE_ITEMS || isDiscovered(i)) {
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
	
	private static boolean isDiscovered(Item i) {
		try (ResultSet res = JDBC.executeQuery("select * from bags where item = %d and dmg = %d;", i.getId(), i.getDamage())) {
			if (res.next()) return true;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return false;
	}
}
