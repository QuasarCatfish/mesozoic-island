package com.quas.mesozoicisland.enums;

public enum EventType {
	TestEvent(0), Contest(1), ContestEntry(2), DoubleContestXP(3),
	BoostedPrismatic(10), BoostedCharmShardChance(11),
	
	Halloween(20), Thanksgiving(21), Valentines(22),

	SecretSantaSignup(30), SecretSanta(31),

	DarknessDescent(100), LostPages(101);

	private int type;
	private EventType(int type)	 {
		this.type = type;
	}

	public static EventType of(int type) {
		for (EventType et : values()) {
			if (et.type == type) {
				return et;
			}
		}

		return TestEvent;
	}
}
