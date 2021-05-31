package com.quas.mesozoicisland.enums;

import com.quas.mesozoicisland.objects.Element;

public enum Location {
	Plains("Plains", true),
	Volcano("Volcano", true, Element.FIRE),
	Mountain("Mountain", true, Element.LIGHTNING),
	Jungle("Jungle", true, Element.LEAF),
	Plateau("Plateau", true, Element.EARTH),
	Tundra("Tundra", true, Element.ICE),
	Cave("Cave", true, Element.METAL),
	Beach("Beach", true, Element.WATER),
	Cliffs("Cliffs", true, Element.AIR),
	
	MurkyLabyrinth("Murky Labyrinth", false, Element.METAL, Element.WATER),
	PalacePark("Palace Park", false, Element.EARTH, Element.LEAF);
	
	private String name;
	private boolean selectable;
	private Element[] boost;
	private Location(String name, boolean selectable, Element...boost) {
		this.name = name;
		this.selectable = selectable;
		this.boost = boost;
	}
	
	@Override
	public String toString() {
		return name;
	}
	
	public boolean isSelectable() {
		return selectable;
	}

	public Element[] getBoostedElements() {
		return boost;
	}

	public int getBoostedElementsId() {
		int ret = 0;
		for (Element e : boost) ret |= e.getId();
		return ret;
	}
}
