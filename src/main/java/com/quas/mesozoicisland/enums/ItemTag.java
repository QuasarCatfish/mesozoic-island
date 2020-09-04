package com.quas.mesozoicisland.enums;

public enum ItemTag {
	Pendant("#pendant"), Bracer("#bracer"), Gauntlet("#gauntlet");

	private String tag;
	private ItemTag(String tag) {
		this.tag = tag;
	}

	public String getTag() {
		return tag;
	}
}