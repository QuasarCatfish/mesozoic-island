package com.quas.mesozoicisland.enums;

public enum ItemTag {

	XpPotion("#xppotion"),
	
	BattleFragrance("#battlefragrance"),
	ExperienceFragrance("#experiencefragrance"),
	MoneyFragrance("#moneyfragrance"),
	EggFragrance("#eggfragrance"),
	
	Pendant("#pendant"),
	Bracer("#bracer"),
	Gauntlet("#gauntlet"),

	DungeonTicket("#dungeonticket"),

	LostPage("#lostpage"),
	;

	private String tag;
	private ItemTag(String tag) {
		this.tag = tag;
	}

	public String getTag() {
		return tag;
	}
}