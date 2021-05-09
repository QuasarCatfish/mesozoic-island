package com.quas.mesozoicisland.enums;

import java.util.HashMap;

import com.quas.mesozoicisland.MesozoicIsland;

import net.dv8tion.jda.api.entities.Emote;
import net.dv8tion.jda.api.entities.Guild;

public class DiscordEmote {

	// Basic Emotes
	public static final DiscordEmote FOSSIL = getEmote(735224390396149860L);
	public static final DiscordEmote BLANK = getEmote(730749486086881321L);
	
	// Form Emotes
	public static final DiscordEmote STANDARD_UNOWNED = getEmote(840325163664408576L);
	public static final DiscordEmote STANDARD_OWNED = getEmote(840325164109791242L);
	public static final DiscordEmote PRISMATIC_OWNED = getEmote(839725928707915778L);
	public static final DiscordEmote DUNGEON_UNOWNED = getEmote(840324048814342194L);
	public static final DiscordEmote DUNGEON_OWNED = getEmote(840324050278416394L);
	public static final DiscordEmote HALLOWEEN_UNOWNED = getEmote(840027534237761597L);
	public static final DiscordEmote HALLOWEEN_OWNED = getEmote(840027534715912202L);
	public static final DiscordEmote THANKSGIVING_UNOWNED = getEmote(840030074261471243L);
	public static final DiscordEmote THANKSGIVING_OWNED = getEmote(840030074363052032L);
	public static final DiscordEmote CHAOS_OWNED = getEmote(840027534204338246L);
	public static final DiscordEmote MECHANICAL_UNOWNED = getEmote(839428662511009833L);
	public static final DiscordEmote MECHANICAL_OWNED = getEmote(839428662893477909L);

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

	//////////////////////////////////////////////////////////////////////////

	private static HashMap<Long, DiscordEmote> emotes = new HashMap<>();

	public static DiscordEmote getEmote(long id) {
		if (emotes.containsKey(id)) return emotes.get(id);
		initialize();
		if (!emotes.containsKey(id)) emotes.put(id, null);
		return emotes.get(id);
	}

	public static void initialize() {
		for (Guild g : MesozoicIsland.getProfessor().getJDA().getGuilds()) {
			for (Emote e : g.getEmotes()) {
				if (e == null) continue;
				if (emotes.containsKey(e.getIdLong())) continue;

				DiscordEmote de = new DiscordEmote(e.getIdLong());
				de.emote = e;
				de.gotEmote = true;
				emotes.put(e.getIdLong(), de);
			}
		}
	}

	public static void refresh() {
		emotes.clear();
		initialize();
	}
}
