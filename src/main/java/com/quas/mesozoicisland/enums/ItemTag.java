package com.quas.mesozoicisland.enums;

public enum ItemTag {

	XpPotion("#xppotion"),
	
	BattleFragrance("#battlefragrance"),
	ExperienceFragrance("#experiencefragrance"),
	MoneyFragrance("#moneyfragrance"),
	EggFragrance("#eggfragrance"),
	
	Scent("#scent"),
	Fragrance("#fragrance"),
	Eau("#eau"),

	Pendant("#pendant"),
	Bracer("#bracer"),
	Gauntlet("#gauntlet"),
	Charm("#charm"),
	DinosaurCharm("#dinocharm"),
	BattlefieldCharm("#fieldcharm"),
	DungeonDinoCharm("#ddcharm"),

	Snack("#snack"),
	HalloweenCandy("#halloweencandy"),

	DungeonLocator("#dungeonlocator"),
	DungeonTicket("#dungeonticket"),
	DinosaurGacha("#dinosaurgacha"),

	RecycleItem("#recycleitem"),
	LostPage("#lostpage"),
	Armature("#armature"),

	AchievementTitle("#achievementtitle"),
	;

	private String tag;
	private ItemTag(String tag) {
		this.tag = tag;
	}

	public String getTag() {
		return tag;
	}
}