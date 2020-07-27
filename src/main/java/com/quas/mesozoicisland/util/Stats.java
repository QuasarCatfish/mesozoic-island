package com.quas.mesozoicisland.util;

public class Stats {

	public static Pair<Integer, Long> of(long id) {
		return new Pair<Integer, Long>(0, id);
	}
	
	// Battles
	public static final long BATTLES_ENTERED = 1;
	public static final long BATTLES_WON = 2;
	public static final long DAMAGE_DEALT = 17;
	public static final long DAMAGE_RECEIVED = 18;
	public static final long DINOSAURS_DEFEATED = 24;
	
	// Dungeons
	public static final long DUNGEONS_ENTERED = 3;
	public static final long DUNGEONS_CLEARED = 4;
	
	// Dinosaurs
	public static final long DINOSAURS_CAUGHT = 5;
	public static final long DINOSAURS_LEVELED_UP = 6;
	public static final long DINOSAURS_RANKED_UP = 7;
	public static final long SNACKS_FED = 19;
	
	// Runes
	public static final long RUNES_OBTAINED = 8;
	public static final long RUNES_RANKED_UP = 9;
	
	// Times Traded
	public static final long TRADE_COUNT = 10;
	
	// Dinosaur Coins
	public static final long COINS_OBTAINED = 11;
	public static final long COINS_SPENT = 12;
	
	// Eggs
	public static final long EGGS_RECEIVED = 13;
	public static final long EGGS_HATCHED = 14;
	
	// Shop
	public static final long TRANSACTIONS_MADE = 15;
	
	// Daily
	public static final long DAILIES_CLAIMED = 16;
	public static final long RAFFLES_WON = 20;
	
	// Raids
	public static final long RAIDS_ATTEMPTED = 21;
	public static final long RAIDS_DEFEATED = 22;
	
	// Quests
	public static final long QUESTS_COMPLETED = 23;
}
