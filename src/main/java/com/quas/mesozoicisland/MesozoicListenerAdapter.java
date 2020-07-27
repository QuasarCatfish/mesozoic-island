package com.quas.mesozoicisland;

import com.quas.mesozoicisland.cmdbase.CommandManager;

import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class MesozoicListenerAdapter extends ListenerAdapter {

	@Override
	public void onReady(ReadyEvent event) {
		System.out.println("Bot Loaded: " + event.getJDA().getSelfUser().getName());
	}
	
	@Override
	public void onMessageReceived(MessageReceivedEvent event) {
		if (event.getAuthor().isBot() || event.getAuthor().isFake()) return;
		if (!MesozoicIsland.isReady()) return;
		CommandManager.handleCommand(event);
	};
}
