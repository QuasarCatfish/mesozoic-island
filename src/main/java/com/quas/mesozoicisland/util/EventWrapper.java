package com.quas.mesozoicisland.util;

import java.util.HashMap;

import com.quas.mesozoicisland.enums.EventType;
import com.quas.mesozoicisland.objects.Event;

public class EventWrapper {
	
	private HashMap<EventType, Boolean> events;
	public EventWrapper() {
		events = new HashMap<EventType, Boolean>();

		for (EventType type : EventType.values()) {
			events.put(type, Event.isEventActive(type));
		}
	}

	public boolean isEventActive(EventType type) {
		return events.getOrDefault(type, false);
	}
}
