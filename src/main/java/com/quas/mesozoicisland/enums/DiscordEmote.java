package com.quas.mesozoicisland.enums;

import com.quas.mesozoicisland.MesozoicIsland;

import net.dv8tion.jda.api.entities.Emote;
import net.dv8tion.jda.api.entities.Guild;

public enum DiscordEmote {
	Fossil(735224390396149860L),
	Egg(735224390396149860L),
	Blank(730749486086881321L),
	
	// Rarities
	RarityCopper(839689038055473192L),
	RarityBronze(839689037224476683L),
	RarityIron(839689037648363570L),
	RaritySilver(839689038021132299L),
	RarityGold(839689037971456051L),
	RarityPlatinum(839689037324484619L),

	// Form Emotes
	StandardUnowned(840325163664408576L),
	StandardOwned(840325164109791242L),
	PrismaticUnowned(839725929056043009L),
	PrismaticOwned(839725928707915778L),
	DungeonUnowned(840324048814342194L),
	DungeonOwned(840324050278416394L),
	HalloweenUnowned(840027534237761597L),
	HalloweenOwned(840027534715912202L),
	ThanksgivingUnowned(840030074261471243L),
	ThanksgivingOwned(840030074363052032L),
	ChaosUnowned(840027534200799272L),
	ChaosOwned(840027534204338246L),
	MechanicalUnowned(839428662511009833L),
	MechanicalOwned(839428662893477909L),

	// Token Emotes
	Token(774870857742286859L),
	ThanksgivingToken(774869230171586601L),
	GiftToken(774869230486552616L),
	ChickenToken(774869230486683698L),
	RecycleToken(833843113717399563L),
	DungeonToken(774869230444740648L),
	QuestToken(775041150187733032L),
	TeamToken(775041150250647622L),
	JasonToken(775041615361343488L),

	// Other Item Emotes
	CharmShard(821239325362618379L),
	ChocolateEgg(827280867553968159L),
	;
	


	private long id;
	private DiscordEmote(long id) {
		this.id = id;
	}
	
	public long getIdLong() {
		return id;
	}
	
	private Emote emote = null;
	private boolean gotEmote = false;
	public Emote getEmote() {
		if (gotEmote) return emote;

		for (Guild g : MesozoicIsland.getProfessor().getJDA().getGuilds()) {
			Emote e = g.getEmoteById(id);
			if (e != null) {
				gotEmote = true;
				return emote = e;
			}
		}

		gotEmote = true;
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
