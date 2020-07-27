package com.quas.mesozoicisland.enums;

public enum RuneType {
	None(-1),
	DealDamage(0);
	
	private int type;
	private RuneType(int type) {
		this.type = type;
	}
	
	public static RuneType of(int type) {
		for (RuneType rt : values()) {
			if (rt.type == type) {
				return rt;
			}
		}
		return None;
	}
}
