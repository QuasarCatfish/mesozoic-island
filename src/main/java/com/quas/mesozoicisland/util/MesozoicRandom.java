package com.quas.mesozoicisland.util;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

import com.quas.mesozoicisland.JDBC;
import com.quas.mesozoicisland.battle.BattleAttack;
import com.quas.mesozoicisland.enums.DinosaurForm;
import com.quas.mesozoicisland.enums.EventType;
import com.quas.mesozoicisland.enums.Location;
import com.quas.mesozoicisland.objects.Dinosaur;
import com.quas.mesozoicisland.objects.Event;
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

	public static int nextSpawnCount() {
		for (int q = Constants.MIN_SPAWN_COUNT;; q++) {
			if (q == Constants.MAX_SPAWN_COUNT || nextInt(2) > 0) {
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
			Location loc = locations.get(nextInt(locations.size()));
			loc.setInUse(true);
			return loc;
		}
	}
	
	public static Dinosaur nextDinosaur() {
		return nextDinosaur(0);
	}

	public static Dinosaur nextDinosaur(int rerolls) {
		Dinosaur[] values = Dinosaur.values();
		boolean prismaticEvent = Event.isEventActive(EventType.BoostedPrismatic);
		boolean halloweenEvent = Event.isEventActive(EventType.Halloween);

		long sum = 0;
		for (Dinosaur d : values) {
			if (prismaticEvent && d.getDinosaurForm() == DinosaurForm.Prismatic) {
				sum += Constants.PRISMATIC_EVENT_MULTIPLIER * d.getRarity().getDinoCount();
			} else if (halloweenEvent && d.getDinosaurForm() == DinosaurForm.Halloween) {
				sum += d.getRarity().getSpecialcount();
			} else{
				sum += d.getRarity().getDinoCount();
			}
		}

		Dinosaur[] select = new Dinosaur[rerolls + 1];
		for (int q = 0; q < select.length; q++) {
			long rand = nextLong(sum);
			for (Dinosaur d : values) {
				if (prismaticEvent && d.getDinosaurForm() == DinosaurForm.Prismatic) {
					rand -= Constants.PRISMATIC_EVENT_MULTIPLIER * d.getRarity().getDinoCount();
				} else if (halloweenEvent && d.getDinosaurForm() == DinosaurForm.Halloween) {
					rand -= d.getRarity().getSpecialcount();
				} else {
					rand -= d.getRarity().getDinoCount();
				}

				if (rand < 0) {
					select[q] = d;
					break;
				}
			}
		}

		int maxrarity = select[0].getRarity().getId();
		for (int q = 1; q < select.length; q++) {
			int rarity = select[q].getRarity().getId();
			if (rarity > maxrarity) {
				maxrarity = rarity;
			}
		}
		
		for (int q = 0; q < select.length; q++) {
			if (select[q].getRarity().getId() == maxrarity) {
				return select[q];
			}
		}

		return select[0];
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
	
	public static long nextRaidPass() {
		try (ResultSet res = JDBC.executeQuery("select * from items where itemid = 701 and itemdmg > 0;")) {
			ArrayList<Long> values = new ArrayList<Long>();
			while (res.next()) {
				if (res.getString("data") == null) continue;
				values.add(res.getLong("itemdmg"));
			}
			return Util.getRandomElement(values.toArray(new Long[0]));
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return 1;
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
