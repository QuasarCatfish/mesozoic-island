package com.quas.mesozoicisland.cmdtutorial;

import com.quas.mesozoicisland.cmdplayer.SelectDinosaursCommand;
import com.quas.mesozoicisland.enums.DiscordChannel;
import com.quas.mesozoicisland.enums.DiscordRole;
import com.quas.mesozoicisland.util.Util;

public class TutorialSelectCommand extends SelectDinosaursCommand {

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
		return "Tutorial1[7-9]";
	}
}
