package com.quas.mesozoicisland.cmdtutorial;

import com.quas.mesozoicisland.cmdplayer.UseCommand;
import com.quas.mesozoicisland.enums.DiscordChannel;
import com.quas.mesozoicisland.enums.DiscordRole;
import com.quas.mesozoicisland.util.Util;

public class TutorialUseCommand extends UseCommand {

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
		return "Tutorial(0[6-9]|1[0-9])";
	}
}
