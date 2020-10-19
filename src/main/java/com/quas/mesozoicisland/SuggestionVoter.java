package com.quas.mesozoicisland;

import com.quas.mesozoicisland.enums.DiscordChannel;

import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class SuggestionVoter extends ListenerAdapter {

	@Override
	public void onGuildMessageReactionAdd(GuildMessageReactionAddEvent event) {
		if (event.getChannel().getIdLong() == DiscordChannel.GameSuggestions.getIdLong()) {
			
		}
	}
}
