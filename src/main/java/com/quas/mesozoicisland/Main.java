package com.quas.mesozoicisland;

import javax.security.auth.login.LoginException;

import org.springframework.web.bind.annotation.RequestMapping;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.GatewayIntent;

public class Main {

	public static JDA jda;

	public static void main(String[] args) throws LoginException, InterruptedException {
		jda = JDABuilder.create("NjQ0MzQ3MTM3NzIyODEwMzY4.XrHFqw.Hdy_ftzhp-0Ju-aes2YnSMS13U8",
				GatewayIntent.getIntents(GatewayIntent.ALL_INTENTS)).addEventListeners(new ListenerAdapter() {

					@Override
					public void onReady(ReadyEvent event) {
						System.out.println("Bot Loaded: " + event.getJDA().getSelfUser().getName());
					}

					@Override
					public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
						if (event.getMessage().getContentRaw().equalsIgnoreCase("!quit")) {
							shutdown();
						}
					}
				}).build().awaitReady();

		System.out.println("Ready!");
	}

	public static void shutdown() {
		jda.shutdown();
		System.exit(0);
	}

	@RequestMapping("/")
	String index() {
		return "index";
	}
}
