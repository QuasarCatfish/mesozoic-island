package com.quas.mesozoicisland.enums;

import com.quas.mesozoicisland.util.Util;

public enum ItemCategory {

	// Important Items
	KeyItems(1, "Key Items", "Key Item", "Key", "KI"),
	Money(2, "Money", "$"),

	// Consumable Items
	Consumable(3, "Consumables", "Consumable", "Consume"),
	Snack(7, "Snacks", "Snack", "Food"),
	Fragrances(9, "Fragrances", "Fragrance", "Frags", "Frag", "Scents", "Scent", "Eaux", "Eau"),
	Gacha(10, "Gacha"),

	// Held Items
	HeldItems(4, "Held Items", "Held Item", "Held", "Hold Items", "Hold Item", "Hold", "HI", "Equip", "Equipable"),
	Charms(8, "Charms", "Charm"),

	// Other
	Tickets(5, "Tickets", "Ticket", "Tix"),
	Event(6, "Event Items", "Event", "Events", "EI"),

	// Titles
	Titles(99, "Titles", "Title"),
	AchievementTitles(100, "Achievement Titles", "Achievement", "Achieve"),
	
	None(-1, "None"),
	Misc(0, "Miscellaneous");
	
	private int category;
	private String[] names;
	private ItemCategory(int cat, String... name) {
		this.category = cat;
		this.names = name;
	}
	
	public int getCategory() {
		return category;
	}
	
	@Override
	public String toString() {
		return names[0];
	}
	
	public String toRegex() {
		return "(" + Util.join(names, "|", 0, names.length).toLowerCase() + ")";
	}
	
	public static ItemCategory of(int i) {
		for (ItemCategory ic : values()) {
			if (ic.category == i) {
				return ic;
			}
		}
		return Misc;
	}
	
	public static ItemCategory of(String cat) {
		for (ItemCategory ic : values()) {
			if (ic == None) continue;
			
			for (String name : ic.names) {
				if (name.equalsIgnoreCase(cat)) {
					return ic;
				}
			}
		}
		return null;
	}
}
