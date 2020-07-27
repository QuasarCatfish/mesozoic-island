package com.quas.mesozoicisland.util;

import com.quas.mesozoicisland.battle.BattleTier;
import com.quas.mesozoicisland.enums.DinosaurForm;
import com.quas.mesozoicisland.objects.Dinosaur;

public class DinoMath {

	public static int getLevel(long xp) {
		if (xp < 0) return -1;
		for (int q = 1;; q++) if (getXp(q) > xp) return q - 1;
	}
	
	public static long getXp(int level) {
		if (level < 1) return -1;
		return 30L * level * (level * level + 10L * level - 11L);
	}
	
	public static int getRank(int rp) {
		if (rp < 0) return -1;
		return (int)((-1 + Math.sqrt(9 + 8 * rp)) / 2d);
	}
	
	public static int getRp(int rank) {
		if (rank < 0) return -1;
		return (rank + 2) * (rank - 1) / 2;
	}
	
	public static int getLevelBoost(int level) {
		if (level < 1) return 0;
		return level;
	}
	
	public static int getRankBoost(int rank) {
		if (rank < 1) return 0;
		return 5 * rank;
	}
	
	public static long getXpDropped(int level) {
		return (level + 5) * (level + 5) - 25;
	}
	
	public static BattleTier getBattleTier(Dinosaur[] team) {
		long xp = 0;
		boolean contest = true;
		for (Dinosaur d : team) {
			xp += d.getXp();
			if (d.getDinosaurForm() != DinosaurForm.Contest) contest = false;
		}
		
		if (contest && Constants.CONTEST) return BattleTier.Contest;
		int level = getLevel(xp);
		if (level > 30) return BattleTier.Tier3;
		if (level > 10) return BattleTier.Tier2;
		return BattleTier.Tier1;
	}
}
