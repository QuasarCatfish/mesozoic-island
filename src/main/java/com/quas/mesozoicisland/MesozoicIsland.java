package com.quas.mesozoicisland;

import javax.security.auth.login.LoginException;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.GatewayIntent;

public class MesozoicIsland {

	public static JDA jda;
	
	public static void main(String[] args) throws LoginException, InterruptedException {

		for (String s : args) {
			System.out.println("argument received : " + s);
		}

		jda = JDABuilder.create("NjQ0MzQ3MTM3NzIyODEwMzY4.XrHFqw.Hdy_ftzhp-0Ju-aes2YnSMS13U8",
				GatewayIntent.getIntents(GatewayIntent.ALL_INTENTS)).addEventListeners(new ListenerAdapter() {

					@Override
					public void onReady(ReadyEvent event) {
						System.out.println("Bot Loaded: " + event.getJDA().getSelfUser().getName());
					}

					@Override
					public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
						if (event.getMessage().getContentRaw().equalsIgnoreCase("!quit")) {
							if (event.getAuthor().getIdLong() == 563661703124877322L) {
								event.getChannel().sendMessage("Shutting down.").complete();
								shutdown();
							} else {
								event.getChannel().sendMessage("You do not have permission to run this command.").complete();
							}
						} else if (event.getMessage().getContentRaw().equalsIgnoreCase("ping")) {
							event.getChannel().sendMessageFormat("Pong! (%d ms)", event.getJDA().getRestPing().complete()).complete();
						}
					}
				}).build().awaitReady();

		System.out.println("Ready for action!");
	}

	public static void shutdown() {
		jda.shutdown();
		System.exit(0);
	}
}
