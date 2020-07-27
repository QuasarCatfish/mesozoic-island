package com.quas.mesozoicisland.enums;

import java.util.ArrayList;

import com.quas.mesozoicisland.util.Util;

public enum ShopType {
	Standard(0, "Standard", ""),
	Tutorial(-1, Util.generateRandomString(100)),
	Debug(-2, "Debug"),
	
	Title(1, "Miscellaneous", "Misc"),
	;
	
	private int id;
	private String[] names;
	private ShopType(int id, String...names) {
		this.id = id;
		this.names = names;
	}
	
	public int getId() {
		return id;
	}
	
	public String getName() {
		return names[0];
	}
	
	
	///////////////////////////////////////////////////////////////
	
	public static ShopType of(int id) {
		for (ShopType st : values()) {
			if (st.id == id) {
				return st;
			}
		}
		
		return Standard;
	}
	
	public static ShopType of(String x) {
		for (ShopType st : values()) {
			for (String name : st.names) {
				if (name.equalsIgnoreCase(x)) {
					return st;
				}
			}
		}
		return Standard;
	}
	
	public static String listValues() {
		ArrayList<String> names = new ArrayList<String>();
		for (ShopType st : values()) {
			if (st.id < 0) continue;
			names.add(String.format("`%s`", st.names[0]));
		}
		return Util.join(names, ", ", 0, names.size());
	}
}
