package com.quas.mesozoicisland.enums;

public enum EventType {
	TestEvent(0), Contest(1), ContestEntry(2),
	
	LostPages(101);

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
