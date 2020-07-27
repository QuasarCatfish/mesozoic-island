package com.quas.mesozoicisland.enums;

public enum AccessLevel {
	Disabled(6),
	Pontifex(5),
	Bot(5),
	Admin(4),
	Moderator(3),
	GuineaPig(2),
	Trainer(1),
	Nobody(0);
	
	private int level;
	private AccessLevel(int level) {
		this.level = level;
	}
	
	public int getLevel() {
		return level;
	}
	
	//////////////////////////////////////
	
	public static AccessLevel of(int level) {
		for (AccessLevel al : values()) {
			if (al.level == level) {
				return al;
			}
		}
		
		return Nobody;
	}
}
