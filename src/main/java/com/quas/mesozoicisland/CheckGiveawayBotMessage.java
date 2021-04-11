package com.quas.mesozoicisland;

import com.quas.mesozoicisland.enums.DiscordRole;
import com.quas.mesozoicisland.util.Constants;
import com.quas.mesozoicisland.util.Util;

import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class CheckGiveawayBotMessage extends ListenerAdapter {
	
	@Override
	public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
		if (event.getAuthor().getIdLong() != Constants.GIVEAWAY_BOT_ID) return;
		if (event.getMessage().getEmbeds().isEmpty()) return;

		MessageEmbed me = event.getMessage().getEmbeds().get(0);
		
		Util.setRolesMentionable(true, DiscordRole.EventPing);
		event.getChannel().sendMessageFormat("%s **%s**", DiscordRole.EventPing.toString(), me.getAuthor().getName()).complete();
		Util.setRolesMentionable(false, DiscordRole.EventPing);
	}
}