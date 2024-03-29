package com.quas.mesozoicisland.battle;

import java.util.ArrayList;

import com.quas.mesozoicisland.JDBC;
import com.quas.mesozoicisland.enums.EventType;
import com.quas.mesozoicisland.objects.Event;
import com.quas.mesozoicisland.util.MesozoicRandom;

public enum BattleTier {
	Tier1("Novice Tier", BattleChannel.Battle1, 1, 10, 0, 20),
	Tier2("Advanced Tier", BattleChannel.Battle2, 11, 30, 1, 50),
	Tier3("Elite Tier", BattleChannel.Battle3, 21, 50, 2, 100),
	Tier4("Master Tier", BattleChannel.Battle4, 31, 70, 3, 200),
	Contest("Contest Tier", BattleChannel.Contest, 1, 50, 0, 0),
	Test("Testing Tier", BattleChannel.Test, 1, 50, 0, 0);
	
	private String name;
	private int low, high, rerolls, boost;
	private BattleChannel bc;
	private BattleTier(String name, BattleChannel bc, int low, int high, int rerolls, int boost) {
		this.name = name;
		this.bc = bc;
		this.low = low;
		this.high = high;
		this.rerolls = rerolls;
		this.boost = boost;
	}
	
	public int getRandomLevel() {
		for (int q = low;; q++) {
			if (q == high || MesozoicRandom.nextInt(0, 4) == 0) {
				return q;
			}
		}
	}
	
	public BattleChannel getBattleChannel() {
		return bc;
	}
	
	public int getRerollCount() {
		return rerolls;
	}

	public int getBoost() {
		return boost;
	}

	@Override
	public String toString() {
		return name;
	}
	
	/////////////////////////////////////
	
	public static BattleTier of(int tier) {
		for (BattleTier bt : values()) {
			if (bt.ordinal() == tier) {
				return bt;
			}
		}
		
		return getBattleTiers()[0];
	}
	
	public static BattleTier[] getBattleTiers() {
		ArrayList<BattleTier> tiers = new ArrayList<BattleTier>();
		tiers.add(Tier1);
		tiers.add(Tier2);
		tiers.add(Tier3);
		if (Event.isEventActive(EventType.Contest)) tiers.add(Contest);
		if (Boolean.parseBoolean(JDBC.getVariable("testing"))) tiers.add(Test);
		return tiers.toArray(new BattleTier[0]);
	}
}
