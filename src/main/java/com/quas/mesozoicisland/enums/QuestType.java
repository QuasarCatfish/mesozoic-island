package com.quas.mesozoicisland.enums;

public enum QuestType {
	Test("Test", -1),
	Standard("", 0),
	Event("Event", 1),
	Curse("Jason", 2);

	private String name;
	private int id;
	private QuestType(String name, int id) {
		this.name = name;
		this.id = id;
	}

	@Override
	public String toString() {
		return name;
	}

	public int getId() {
		return id;
	}

	public static QuestType of(int id) {
		for (QuestType qt : values()) {
			if (qt.id == id) {
				return qt;
			}
		}

		return Test;
	}
}
