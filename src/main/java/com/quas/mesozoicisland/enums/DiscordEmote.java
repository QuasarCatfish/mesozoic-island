package com.quas.mesozoicisland.enums;

import com.quas.mesozoicisland.MesozoicIsland;

import net.dv8tion.jda.api.entities.Emote;
import net.dv8tion.jda.api.entities.Guild;

public enum DiscordEmote {
	Fossil(735224390396149860L),
	Egg(735224390396149860L),
	Blank(730749486086881321L),
	
	// Form Emotes
	StandardUnowned(770493483496505375L),
	StandardOwned(770493483520884766L),
	PrismaticUnowned(770493483131076609L),
	PrismaticOwned(770493483231608833L),
	DungeonUnowned(770493483470946334L),
	DungeonOwned(770493483290198047L),
	HalloweenUnowned(770493483471208468L),
	HalloweenOwned(770493483559551016L),
	ThanksgivingUnowned(770493483680661524L),
	ThanksgivingOwned(770493483445518337L),

	// Item Emotes
	Token(774870857742286859L),
	ThanksgivingToken(774869230171586601L),
	GiftToken(774869230486552616L),
	ChickenToken(774869230486683698L),
	DungeonToken(774869230444740648L),
	QuestToken(775041150187733032L),
	TeamToken(775041150250647622L),
	JasonToken(775041615361343488L),
	CharmShard(821239325362618379L),
	;
	


	private long id;
	private DiscordEmote(long id) {
		this.id = id;
	}
	
	public long getIdLong() {
		return id;
	}
	
	public Emote getEmote() {
		for (Guild g : MesozoicIsland.getProfessor().getJDA().getGuilds()) {
			Emote e = g.getEmoteById(id);
			if (e != null) return e;
		}
		return null;
	}
	
	@Override
	public String toString() {
		return getEmote().getAsMention();
	}

	public static DiscordEmote getEmote(long id) {
		for (DiscordEmote emote : values()) {
			if (emote.getIdLong() == id) {
				return emote;
			}
		}
		return null;
	}
}
