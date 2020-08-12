package com.quas.mesozoicisland;

import com.quas.mesozoicisland.battle.SpawnManager;
import com.quas.mesozoicisland.enums.DiscordChannel;

import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class CheckBattleMessage extends ListenerAdapter {
	
	@Override
	public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
		if (event.getAuthor().getIdLong() == MesozoicIsland.getAssistant().getIdLong()) {
			if (event.getChannel().getParent().getIdLong() == DiscordChannel.CATEGORY_BATTLE) {
				SpawnManager.lastupdate = System.currentTimeMillis();
			}
		}
	}
}