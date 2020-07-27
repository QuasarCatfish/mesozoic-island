package com.quas.mesozoicisland.cmdtutorial;

import com.quas.mesozoicisland.cmdplayer.BagCategoryCommand;
import com.quas.mesozoicisland.enums.DiscordChannel;
import com.quas.mesozoicisland.enums.DiscordRole;
import com.quas.mesozoicisland.util.Util;

public class TutorialBagCategoryCommand extends BagCategoryCommand {

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
		return "Tutorial1[2-9]";
	}
}
