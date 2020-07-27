package com.quas.mesozoicisland;

import javax.security.auth.login.LoginException;

import com.quas.mesozoicisland.util.Constants;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.requests.GatewayIntent;

public class Bot {

	private JDA jda;
	
	public Bot(String token, MesozoicListenerAdapter mla) throws LoginException, InterruptedException {
		jda = JDABuilder.create(token, GatewayIntent.getIntents(GatewayIntent.ALL_INTENTS)).addEventListeners(mla).build().awaitReady();
		jda.awaitReady();
	}
	
	public long getIdLong() {
		return jda.getSelfUser().getIdLong();
	}
	
	public JDA getJDA() {
		return jda;
	}
	
	public Guild getGuild() {
		return jda.getGuildById(Constants.GUILD_ID);
	}
	
	public String getName() {
		return getGuild().getSelfMember().getEffectiveName();
	}
}
