package com.quas.mesozoicisland.enums;

import com.quas.mesozoicisland.util.Util;

public enum ItemCategory {

	KeyItems(1, "Key Items", "Key Item", "Key", "KI"),
	Money(2, "Money", "$"),
	Consumable(3, "Consumables", "Consumable", "Consume"),
	HeldItems(4, "Held Items", "Held Item", "Held", "Hold Items", "Hold Item", "Hold", "HI"),
	Tickets(5, "Tickets", "Ticket", "Tix"),
	Event(6, "Event Items", "Event", "EI"),
	
	Titles(99, "Titles", "Title"),
	
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
			if (Integer.toString(ic.category).equals(cat)) {
				return ic;
			}
			
			for (String name : ic.names) {
				if (name.equalsIgnoreCase(cat)) {
					return ic;
				}
			}
		}
		return Misc;
	}
}
