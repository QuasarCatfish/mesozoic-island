package com.quas.mesozoicisland.enums;

import java.util.ArrayList;

import com.quas.mesozoicisland.util.Util;

public enum ShopType {
	Dungeon(0, Util.arr("Dungeons", "Dungeon"), "A shop to spend your Dungeon Tokens at.", true),
	Title(1, Util.arr("Titles", "Title"), "A shop containing Title-related items.", true),
	Eggs(2, Util.arr("Eggs", "Egg"), "A shop containing Egg-related items.", true),
	Fragrances(3, Util.arr("Fragrances", "Fragrance", "Scents", "Scent"), "A shop containing various scents and fragrances.", true),
	Events(4, Util.arr("Events", "Event"), "A shop containing items for the current event.", true),
	Quests(5, Util.arr("HeldItems", "HeldItem", "HI"), "A shop to buy held items with Quest Tokens or Charm Shards.", true),
	Locators(6, Util.arr("Locators", "Locator", "Log"), "A shop to buy locators for dinosaurs, eggs, and dungeons.", true),
	
	Tutorial(-1, Util.arr(Util.generateRandomString(100)), "Tutorial shop!", false),
	Debug(-2, Util.arr("Debug"), "Shop for debug items. Cannot be seen by users.", false),
	None(-3, Util.arr("None"), "Nothing", false),
	;
	
	private int id;
	private String[] names;
	private String desc;
	private boolean visible;
	private ShopType(int id, String[] names, String desc, boolean visible) {
		this.id = id;
		this.names = names;
		this.desc = desc;
		this.visible = visible;
	}
	
	public int getId() {
		return id;
	}
	
	public String getName() {
		return names[0];
	}
	
	public String getDescription() {
		return desc;
	}

	public boolean isVisible() {
		return visible;
	}
	
	///////////////////////////////////////////////////////////////
	
	public static ShopType of(int id) {
		for (ShopType st : values()) {
			if (st.id == id) {
				return st;
			}
		}
		
		return None;
	}
	
	public static ShopType of(String x) {
		for (ShopType st : values()) {
			for (String name : st.names) {
				if (name.equalsIgnoreCase(x)) {
					return st;
				}
			}
		}
		return None;
	}
	
	public static String listValues() {
		ArrayList<String> names = new ArrayList<String>();
		for (ShopType st : values()) {
			if (!st.visible) continue;
			names.add(String.format("`%s`", st.names[0]));
		}
		return Util.join(names, ", ", 0, names.size());
	}
}
