package com.quas.mesozoicisland;

import com.quas.mesozoicisland.enums.DiscordChannel;
import com.quas.mesozoicisland.util.Constants;

import net.dv8tion.jda.api.entities.Message.Attachment;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.priv.PrivateMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class MonitorDMs extends ListenerAdapter {
	
	@Override
	public void onPrivateMessageReceived(PrivateMessageReceivedEvent event) {
		if (event.getAuthor().isBot()) return;
		if (event.getAuthor().isFake()) return;
		TextChannel tc = DiscordChannel.BotDMs.getChannel(MesozoicIsland.getProfessor());

		EmbedBuilder eb = new EmbedBuilder();
		eb.setColor(Constants.COLOR);
		eb.setDescription(event.getMessage().getContentRaw());
		eb.addField("User", event.getAuthor().getAsMention(), true);
		eb.addField("User ID", event.getAuthor().getId(), true);
		eb.addField("Recipient", event.getJDA().getSelfUser().getAsMention(), true);
		for (Attachment a : event.getMessage().getAttachments()) {
			eb.setImage(a.getUrl());
		}
		tc.sendMessage(eb.build()).complete();
	}
}