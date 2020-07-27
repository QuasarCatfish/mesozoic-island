package com.quas.mesozoicisland.enums;

import com.quas.mesozoicisland.objects.Player;

public enum CustomPlayer {
	Clyde(1), Wild(2), Dungeon(3), EggSalesman(4), RaidChallenge(5);
	
	long id;
	private CustomPlayer(long id) {
		this.id = id;
	}
	
	public long getIdLong() {
		return id;
	}
	
	public Player getPlayer() {
		return Player.getPlayer(id);
	}
	
	///////////////////////////////////
	
	private static long max = 0;
	static {
		for (CustomPlayer cp : values()) {
			if (cp.id > max) {
				max = cp.id;
			}
		}
	}
	
	public static long getUpperLimit() {
		return max + 1;
	}
}
