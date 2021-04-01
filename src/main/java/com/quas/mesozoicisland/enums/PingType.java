package com.quas.mesozoicisland.enums;

public enum PingType {
	Announcement(DiscordRole.AnnouncementPing, "an announcement is made"),
	MiniAnnouncement(DiscordRole.MiniAnnouncementPing, "a small annoucnement is made"),
	Event(DiscordRole.EventPing, "event information is announced"),
	Daily(DiscordRole.DailyPing, "a new day begins"),
	Spawn(DiscordRole.SpawnPing, "a dinosaur spawns"),
	Dungeon(DiscordRole.DungeonPing, "a dungeon has been found"),
	Egg(DiscordRole.EggPing, "an egg is found"),
//	Rune(DiscordRole.RunePing, "a dinosaur with a rune appears"),
	Suggestion(DiscordRole.SuggestionPing, "a new suggestion is made"),
	;
	
	private DiscordRole role;
	private String message;
	private PingType(DiscordRole role, String message) {
		this.role = role;
		this.message = message;
	}
	
	public DiscordRole getRole() {
		return role;
	}
	
	public String getMessage() {
		return message;
	}
	
	//////////////////////////////////
	
	public static PingType of(String name) {
		for (PingType ping : values()) {
			if (ping.name().equalsIgnoreCase(name)) {
				return ping;
			}
		}
		return null;
	}
	
	public static String listValues() {
		String[] strings = new String[values().length];
		for (int q = 0; q < strings.length; q++) {
			strings[q] = "`" + values()[q].name().toLowerCase() + "`";
		}
		return String.join(", ", strings);
	}
}