package com.quas.mesozoicisland;

import com.quas.mesozoicisland.enums.DiscordChannel;

import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class CopyMessages extends ListenerAdapter {
	
	@Override
	public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
		if (event.getAuthor().isBot()) return;
		if (event.getAuthor().isFake()) return;

		if (event.getChannel().getIdLong() == DiscordChannel.CaveOfLostHope.getIdLong()) {
			DiscordChannel.DebugCaveOfLostHope.getChannel(MesozoicIsland.getAssistant()).sendMessageFormat("**Message from %s:**\n> %s", event.getMember().getEffectiveName(), event.getMessage().getContentRaw()).complete();
		}
	}
}