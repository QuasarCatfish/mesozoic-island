package com.quas.mesozoicisland.enums;

import com.quas.mesozoicisland.objects.Element;
import com.quas.mesozoicisland.util.Util;

public enum Location {
	Beach1("Beach #1"),
	Beach2("Beach #2"),
	Beach3("Beach #3");
	
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
