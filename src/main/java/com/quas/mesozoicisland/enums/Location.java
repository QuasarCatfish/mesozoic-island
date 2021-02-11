package com.quas.mesozoicisland.enums;

import com.quas.mesozoicisland.objects.Element;
import com.quas.mesozoicisland.util.Util;

public enum Location {
	Plains("Plains", true),
	Volcano("Volcano", true, Element.of(2)),
	Mountain("Mountain", true, Element.of(4)),
	Jungle("Jungle", true, Element.of(8)),
	Plateau("Plateau", true, Element.of(16)),
	Tundra("Tundra", true, Element.of(32)),
	Cave("Cave", true, Element.of(64)),
	Beach("Beach", true, Element.of(128)),
	Cliffs("Cliffs", true, Element.of(256)),
	
	MurkyLabyrinth("Murky Labyrinth", false, Element.of(64), Element.of(128));
	
	private String name;
	private boolean selectable;
	private Element[] boost;
	private boolean inuse;
	private Location(String name, boolean selectable, Element...boost) {
		this.name = name;
		this.selectable = selectable;
		this.boost = boost;
		inuse = false;
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
	
	public void setInUse(boolean inuse) {
		this.inuse = inuse;
	}
	
	public void setInUse(boolean inuse, long delay) {
		new Thread() {
			@Override
			public void run() {
				Util.sleep(delay);
				setInUse(inuse);
			};
		}.start();
	}
	
	public boolean isInUse() {
		return inuse;
	}
}
