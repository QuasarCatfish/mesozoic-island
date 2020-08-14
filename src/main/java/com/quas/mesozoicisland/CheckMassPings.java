package com.quas.mesozoicisland;

import com.quas.mesozoicisland.enums.DiscordChannel;
import com.quas.mesozoicisland.enums.DiscordRole;
import com.quas.mesozoicisland.util.Util;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class CheckMassPings extends ListenerAdapter {
	
	@Override
	public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
		if (event.getAuthor().isBot()) return;
		if (event.getAuthor().isFake()) return;
		if (event.getMember().hasPermission(Permission.ADMINISTRATOR)) return;

		String raw = event.getMessage().getContentRaw().toLowerCase();
		if (raw.contains("@everyone") || raw.contains("@here")) {
			Util.addRoleToMember(event.getMember(), DiscordRole.Muted.getIdLong());
			JDBC.setMuted(event.getAuthor().getIdLong(), true);
			DiscordChannel.Game.getChannel(MesozoicIsland.getAssistant()).sendMessageFormat("%s, you are now muted.", event.getAuthor().getAsMention()).complete();
		}
	}
}