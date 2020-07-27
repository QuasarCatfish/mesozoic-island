package com.quas.mesozoicisland.cmdtutorial;

import com.quas.mesozoicisland.cmdplayer.ShopCommand;
import com.quas.mesozoicisland.enums.DiscordChannel;
import com.quas.mesozoicisland.enums.DiscordRole;
import com.quas.mesozoicisland.enums.ShopType;
import com.quas.mesozoicisland.util.Util;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class TutorialShopCommand extends ShopCommand {

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
		return "Tutorial1[3-9]";
	}
	
	@Override
	public synchronized void run(MessageReceivedEvent event, String... args) {
		super.run(event, ShopType.Tutorial.getName());
	}
}
