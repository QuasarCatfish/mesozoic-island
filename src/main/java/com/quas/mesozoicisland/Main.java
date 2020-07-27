package com.quas.mesozoicisland;

import javax.security.auth.login.LoginException;

import org.springframework.web.bind.annotation.RequestMapping;

public class Main {

	// public static JDA jda;

	public static void main(String[] args) throws LoginException, InterruptedException {
		// jda = JDABuilder.create("NjQ0MzQ3MTM3NzIyODEwMzY4.XrHFqw.Hdy_ftzhp-0Ju-aes2YnSMS13U8",
		// 		GatewayIntent.getIntents(GatewayIntent.ALL_INTENTS)).addEventListeners(new ListenerAdapter() {

		// 			@Override
		// 			public void onReady(ReadyEvent event) {
		// 				System.out.println("Bot Loaded: " + event.getJDA().getSelfUser().getName());
		// 			}

		// 			@Override
		// 			public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
		// 				if (event.getMessage().getContentRaw().equalsIgnoreCase("!quit")) {
		// 					shutdown();
		// 				}
		// 			}
		// 		}).build().awaitReady();

		System.out.println("Ready!");
		for (int q = 60; q >= 0; q--) {
			System.out.println("Shutting down in " + q + " seconds.");
			Thread.sleep(q * 1000);
		}
		shutdown();
	}

	public static void shutdown() {
		// jda.shutdown();
		System.exit(0);
	}

	@RequestMapping("/")
	String index() {
		return "index";
	}
}
