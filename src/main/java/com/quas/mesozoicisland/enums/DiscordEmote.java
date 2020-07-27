package com.quas.mesozoicisland.enums;

import com.quas.mesozoicisland.MesozoicIsland;

import net.dv8tion.jda.api.entities.Emote;

public enum DiscordEmote {
	Fossil(735224390396149860L),
	Egg(735224390396149860L),
	Blank(730749486086881321L);
	
	private long id;
	private DiscordEmote(long id) {
		this.id = id;
	}
	
	public long getIdLong() {
		return id;
	}
	
	public Emote getEmote() {
		return MesozoicIsland.getProfessor().getGuild().getEmoteById(id);
	}
	
	@Override
	public String toString() {
		return getEmote().getAsMention();
	}
}
