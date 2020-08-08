package com.quas.mesozoicisland.enums;

import com.quas.mesozoicisland.objects.Element;
import com.quas.mesozoicisland.util.Util;

public enum Location {
	Plains("Plains"),
	Volcano("Volcano", Element.of(2)),
	Mountain("Mountain", Element.of(4)),
	Jungle("Jungle", Element.of(8)),
	Plateau("Plateau", Element.of(16)),
	Tundra("Tundra", Element.of(32)),
	Cave("Cave", Element.of(64)),
	Beach("Beach", Element.of(128)),
	Cliffs("Cliffs", Element.of(256));
	
	private String name;
	private Element[] boost;
	private boolean inuse;
	private Location(String name, Element...boost) {
		this.name = name;
		this.boost = boost;
		inuse = false;
	}
	
	@Override
	public String toString() {
		return name;
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
