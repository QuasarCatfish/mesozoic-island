package com.quas.mesozoicisland.util;

import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

import com.quas.mesozoicisland.battle.BattleAttack;
import com.quas.mesozoicisland.enums.DinosaurForm;
import com.quas.mesozoicisland.enums.Location;
import com.quas.mesozoicisland.objects.Dinosaur;
import com.quas.mesozoicisland.objects.Rarity;

public class MesozoicRandom {

	public static int nextInt(int high) {
		return nextInt(0, high);
	}
	
	public static int nextInt(int low, int high) {
		return ThreadLocalRandom.current().nextInt(low, high);
	}
	
	public static long nextLong(long high) {
		return nextLong(0, high);
	}
	
	public static long nextLong(long low, long high) {
		return ThreadLocalRandom.current().nextLong(low, high);
	}
	
	public static boolean nextBoolean() {
		return ThreadLocalRandom.current().nextBoolean();
	}
	
	public static int nextLevel() {
		for (int q = 1;; q++) {
			if (q == Constants.MAX_LEVEL || nextInt(4) == 0) {
				return q;
			}
		}
	}
	
	public static Location nextUnusedLocation() {
		ArrayList<Location> locations = new ArrayList<Location>();
		for (Location loc : Location.values()) {
			if (loc.isInUse()) continue;
			locations.add(loc);
		}
		
		if (locations.isEmpty()) {
			return Location.values()[nextInt(Location.values().length)];
		} else {
			return locations.get(nextInt(locations.size()));
		}
	}
	
	public static Dinosaur nextDinosaur() {
		Dinosaur[] values = Dinosaur.values();
		long sum = 0;
		for (Dinosaur d : values) sum += d.getRarity().getDinoCount();
		long rand = nextLong(sum);
		for (Dinosaur d : values) {
			rand -= d.getRarity().getDinoCount();
			if (rand < 0) return d;
		}
		return values[0];
	}
	
	public static Dinosaur nextDinosaur(Rarity r) {
		Dinosaur d;
		do {
			d = nextDinosaur();
		} while (!d.getRarity().equals(r));
		return d;
	}
	
	public static Dinosaur nextDungeonDinosaur() {
		return Dinosaur.getDinosaur(nextDinosaur().getDex(), DinosaurForm.UncapturableDungeon.getId());
	}
	
	public static Dinosaur nextDungeonBossDinosaur() {
		return Dinosaur.getDinosaur(nextDinosaur().getDex(), DinosaurForm.UncapturableDungeonBoss.getId());
	}
	
	public static BattleAttack nextAttackingBattleEffect(DinosaurForm form) {
		return form.getAttacks()[nextInt(form.getAttacks().length)];
	}
	
	public static BattleAttack nextDefendingBattleEffect(DinosaurForm form) {
		return form.getDefenses()[nextInt(form.getDefenses().length)];
	}
	
	public static int nextHatchPoints(Rarity rarity, DinosaurForm form) {
		// HP Based on Rarity
		int hp = rarity.getId() % 10 == 0 ? 15_000 : 1_000 * ((rarity.getId() % 10) + 4);
		
		// Bonus HP Based on Form
		switch (form) {
		case Standard:
			break;
		case Prismatic:
			hp += 10_000;
			break;
		case Dungeon:
		case Halloween:
		case Thanksgiving:
			hp += 3_000;
			break;
		default:
			hp += 1_000_000_000;
			break;
		}
		
		hp += nextInt(1_000);
		
		return hp;
	}
}
