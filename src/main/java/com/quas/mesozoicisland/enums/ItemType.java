package com.quas.mesozoicisland.enums;

public enum ItemType {
	PersistWithCustomUse(0),
	PersistCount(1),
	Consume(2),
	ConsumeDinosaur(3),
	Held(4),
	Title(5),
	
	None(-1);
	
	private int type;
	private ItemType(int type) {
		this.type = type;
	}
	
	public static ItemType of(int type) {
		for (ItemType it : values()) {
			if (it.type == type) {
				return it;
			}
		}
		return None;
	}
}
