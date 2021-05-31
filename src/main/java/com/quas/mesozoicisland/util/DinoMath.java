package com.quas.mesozoicisland.util;

import java.util.Arrays;
import java.util.List;

import com.quas.mesozoicisland.battle.BattleTier;
import com.quas.mesozoicisland.enums.DinosaurForm;
import com.quas.mesozoicisland.objects.Dinosaur;
import com.quas.mesozoicisland.objects.Rarity;

public class DinoMath {

	public static int getLevel(long xp) {
		if (xp < 0) return -1;
		for (int q = 1;; q++) if (getXp(q) > xp) return q - 1;
	}
	
	public static long getXp(int level) {
		if (level < 1) return -1;
		return 30L * level * (level * level + 10L * level - 11L);
	}

	public static int getOmegaLevel(long xp) {
		if (xp < 0) return -1;
		for (int q = 1;; q++) if (getOmegaXp(q) > xp) return q - 1;
	}

	public static long getOmegaXp(int level) {
		return 500_000 * level * (level + 1);
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
	
	public static long getXpDropped(int attack, int defend) {
		long xp = (defend + 5) * (defend + 5) - 25;
		if (attack > defend) xp *= Math.max(1 - (attack - defend) / 100f, 0f);
		return xp;
	}

	public static int getClayDropped(Rarity r) {
		switch (r.getId() % 20) {
			case 1: return MesozoicRandom.nextInt(5, 8);
			case 4: return MesozoicRandom.nextInt(6, 10);
			case 5: return MesozoicRandom.nextInt(7, 11);
		}
		return 0;
	}

	public static int getClayRequired(Rarity r) {
		switch (r.getId() % 20) {
			case 1: return 5;
			case 4: return 10;
			case 5: return 20;
		}
		return 1_000;
	}

	public static int getMechanicalComponentRequired(Rarity r) {
		return 5 * (r.getId() % 20);
	}
	
	public static BattleTier getBattleTier(Dinosaur... team) {
		if (team.length == 0) return BattleTier.Tier1;
		
		List<BattleTier> tiers = Arrays.asList(BattleTier.getBattleTiers());
		if (team[0].getPlayerId() == Constants.QUAS_ID && tiers.contains(BattleTier.Test)) return BattleTier.Test;
		
		// Get stats
		long xp = 0;
		long bst = 0;
		boolean contest = true;
		for (Dinosaur d : team) {
			if (d.getDinosaurForm() == DinosaurForm.Accursed) {
				xp += d.getPlayer().getXp();
			} else {
				xp += d.getXp();
				long stats = d.getHealth() + d.getAttack() + d.getDefense();
				if (stats > bst) bst = stats;
			}
			if (d.getDinosaurForm() != DinosaurForm.Contest) contest = false;
		}
		
		// Contest running
		if (contest) return BattleTier.Contest;

		// Calculate Level
		int level = getLevel(xp);

		// Calculate Tier
		if ((level >= 50 || bst >= 10_000) && tiers.contains(BattleTier.Tier4)) return BattleTier.Tier4;
		if ((level >= 30 || bst >= 7_500) && tiers.contains(BattleTier.Tier3)) return BattleTier.Tier3;
		if ((level >= 10 || bst >= 5_000) && tiers.contains(BattleTier.Tier2)) return BattleTier.Tier2;
		return BattleTier.Tier1;
	}

	public static long getNextBattleTierPercent(Dinosaur... team) {
		if (team.length == 0) return -1;
		
		List<BattleTier> tiers = Arrays.asList(BattleTier.getBattleTiers());
		if (team[0].getPlayerId() == Constants.QUAS_ID && tiers.contains(BattleTier.Test)) return -1;
		
		// Get stats
		long xp = 0;
		long bst = 0;
		long maxbst = 0;
		boolean contest = true;
		for (Dinosaur d : team) {
			if (d.getDinosaurForm() == DinosaurForm.Accursed) {
				xp += d.getPlayer().getXp();
			} else {
				xp += d.getXp();
				long stats = d.getHealth() + d.getAttack() + d.getDefense();
				if (stats > bst) {
					bst = stats;
					Dinosaur base = Dinosaur.getDinosaur(d.getIdPair());
					base.setLevel(1).setRank(1);
					maxbst = base.getHealth() + base.getAttack() + base.getDefense();
				}
			}
			if (d.getDinosaurForm() != DinosaurForm.Contest) contest = false;
		}
		
		// Contest running
		if (contest) return -1;

		// Calculate Level
		int level = getLevel(xp);

		// Calculate Percent
		if (level < 10 && bst < 5_000) return floor(Math.max(100d * xp / getXp(10), 100d * (bst - maxbst) / (5_000 - maxbst)));
		if (level < 30 && bst < 7_500) return floor(Math.max(100d * (xp - getXp(10)) / (getXp(30) - getXp(10)), 100d * (bst - 5_000) / 2_500));
		// if (level < 50 && bst < 10_000)
		return floor(Math.max(100d * (xp - getXp(30)) / (getXp(50) - getXp(30)), 100d * (bst - 7_500) / 2_500));
	}

	private static long floor(double f) {
		return (long)f;
	}
}
