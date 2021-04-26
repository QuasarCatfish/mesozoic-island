package com.quas.mesozoicisland.enums;

import com.quas.mesozoicisland.util.Util;

public enum ItemCategory {

	// Important Items
	KeyItems(1, false, "Key Items", "Key Item", "Key", "KI"),
	Money(2, true, "Money", "$"),

	// Consumable Items
	Consumable(3, true, "Consumables", "Consumable", "Consume"),
	Snack(7, true, "Snacks", "Snack", "Food"),
	Fragrances(9, true, "Fragrances", "Fragrance", "Frags", "Frag", "Scents", "Scent", "Eaux", "Eau"),
	Gacha(10, true, "Gacha"),

	// Held Items
	HeldItems(4, true, "Held Items", "Held Item", "Held", "Hold Items", "Hold Item", "Hold", "HI", "Equip", "Equipable"),
	Charms(8, true, "Charms", "Charm"),

	// Other
	Tickets(5, true, "Tickets", "Ticket", "Tix"),
	Event(6, true, "Event Items", "Event", "Events", "EI"),

	// Titles
	Titles(99, false, "Titles", "Title"),
	AchievementTitles(100, false, "Achievement Titles", "Achievement", "Achieve"),
	
	None(-1, false, "None"),
	Misc(0, true, "Miscellaneous", "Misc");
	
	private int category;
	private boolean showCount;
	private String[] names;
	private ItemCategory(int cat, boolean showCount, String... name) {
		this.category = cat;
		this.showCount = showCount;
		this.names = name;
	}
	
	public int getCategory() {
		return category;
	}

	public boolean doesShowCount() {
		return showCount;
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
