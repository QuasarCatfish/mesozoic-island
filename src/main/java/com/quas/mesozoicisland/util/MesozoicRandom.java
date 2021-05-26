package com.quas.mesozoicisland.util;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

import com.quas.mesozoicisland.JDBC;
import com.quas.mesozoicisland.battle.BattleAttack;
import com.quas.mesozoicisland.enums.DinoID;
import com.quas.mesozoicisland.enums.DinosaurForm;
import com.quas.mesozoicisland.enums.EventType;
import com.quas.mesozoicisland.enums.ItemID;
import com.quas.mesozoicisland.enums.ItemTag;
import com.quas.mesozoicisland.enums.Location;
import com.quas.mesozoicisland.objects.Dinosaur;
import com.quas.mesozoicisland.objects.Event;
import com.quas.mesozoicisland.objects.Item;
import com.quas.mesozoicisland.objects.Rarity;

public class MesozoicRandom {

	public static int nextInt(int high) {
		return nextInt(0, high);
	}
	
	public static int nextInt(int low, int high) {
		if (low >= high) return low;
		return ThreadLocalRandom.current().nextInt(low, high);
	}
	
	public static long nextLong(long high) {
		return nextLong(0, high);
	}
	
	public static long nextLong(long low, long high) {
		if (low >= high) return low;
		return ThreadLocalRandom.current().nextLong(low, high);
	}
	
	public static boolean nextBoolean() {
		return ThreadLocalRandom.current().nextBoolean();
	}

	public static double nextDouble(double bound) {
		return ThreadLocalRandom.current().nextDouble(bound);
	}
	
	public static int nextCoinAmount() {
		return nextInt(20, 51);
	}

	public static int nextLevel() {
		for (int q = 1;; q++) {
			if (q == Constants.MAX_DINOSAUR_LEVEL || nextInt(4) == 0) {
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
	
	public static Location nextLocation() {
		ArrayList<Location> locations = new ArrayList<Location>();
		for (Location loc : Location.values()) {
			if (!loc.isSelectable()) continue;
			locations.add(loc);
		}
		
		return locations.isEmpty() ? Location.Plains : Util.getRandomElement(locations);
	}
	
	public static Dinosaur nextEggDinosaur() {
		while (true) {
			Dinosaur d = nextOwnableDinosaur();
			if (nextHatchPoints(d.getRarity(), d.getDinosaurForm()) >= Constants.ILLEGAL_EGG_HP) continue;
			return d;
		}
	}

	public static Dinosaur nextOwnableDinosaur() {
		return nextOwnableDinosaur(0);
	}
	
	public static Dinosaur nextDinosaur() {
		return nextDinosaur(0);
	}

	public static Dinosaur nextOwnableDinosaur(int rerolls) {
		while (true) {
			Dinosaur d = nextDinosaur(rerolls);
			if (d.getDex() < 0) continue;
			return d;
		}
	}

	public static Dinosaur nextDinosaur(int rerolls) {
		Dinosaur[] values = Dinosaur.values();
		EventWrapper wrapper = new EventWrapper();

		long sum = 0;
		for (Dinosaur d : values) {
			sum += getDinosaurSpawnValue(d, wrapper);
		}

		Dinosaur[] select = new Dinosaur[rerolls + 1];
		for (int q = 0; q < select.length; q++) {
			long rand = nextLong(sum);
			for (Dinosaur d : values) {
				rand -= getDinosaurSpawnValue(d, wrapper);

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

	private static long getDinosaurSpawnValue(Dinosaur d, EventWrapper e) {
		if (e.isEventActive(EventType.BoostedPrismatic) && d.getDinosaurForm() == DinosaurForm.Prismatic) {
			return (long)(Constants.PRISMATIC_EVENT_MULTIPLIER * d.getRarity().getDinoCount());
		} else if (e.isEventActive(EventType.Halloween) && d.getDinosaurForm() == DinosaurForm.Halloween) {
			return (long)(Constants.HALLOWEEN_DINOSAUR_MULTIPLIER * d.getRarity().getSpecialCount());
		} else if (e.isEventActive(EventType.Thanksgiving) && d.getDinosaurForm() == DinosaurForm.Thanksgiving) {
			return (long)(Constants.THANKSGIVING_DINOSAUR_MULTIPLIER * d.getRarity().getSpecialCount());
		} else if (e.isEventActive(EventType.Thanksgiving) && d.getDex() == DinoID.Turkey.getDex()) {
			return d.getRarity().getSpecialCount();
		} else if (e.isEventActive(EventType.MechanicalMayhem) && d.getDinosaurForm() == DinosaurForm.Mechanical) {
			return (long)(Constants.MECHANICAL_DINOSAUR_MULTIPLIER * d.getRarity().getSpecialCount());
		} else{
			return d.getRarity().getDinoCount();
		}
	}
	
	public static Dinosaur nextDinosaur(Rarity r) {
		while (true) {
			Dinosaur d = nextDinosaur();
			if (!d.getRarity().equals(r)) continue;
			return d;
		}
	}
	
	public static Dinosaur nextDinosaur(DinosaurForm form) {
		while (true) {
			Dinosaur base = MesozoicRandom.nextDinosaur();
			if (base.getDex() < 0) continue;
			if (base.getDinosaurForm() != DinosaurForm.Standard) continue;
			Dinosaur dino = Dinosaur.getDinosaur(base.getDex(), form.getId());
			if (dino == null) continue;
			return dino;
		}
	}
	
	public static long nextRaidPass() {
		try (ResultSet res = JDBC.executeQuery("select * from items where itemid = 701 and itemdmg > 0;")) {
			ArrayList<Long> values = new ArrayList<Long>();
			while (res.next()) {
				if (res.getString("data") == null) continue;
				values.add(res.getLong("itemdmg"));
			}
			return Util.getRandomElement(values);
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return 1;
	}

	public static BattleAttack nextAttackingBattleEffect(Dinosaur dino) {
		return Util.getRandomElement(dino.getAttacks());
	}
	
	public static BattleAttack nextDefendingBattleEffect(Dinosaur dino) {
		return Util.getRandomElement(dino.getDefenses());
	}
	
	public static int nextHatchPoints(Rarity rarity, DinosaurForm form) {
		// HP Based on Rarity
		int hp = rarity.getId() % 10 == 0 ? nextInt(13_000, 17_000) : nextInt(1_000 * ((rarity.getId() % 10) + 2), 1_000 * ((rarity.getId() % 10) + 6));
		
		// Bonus HP Based on Form
		switch (form) {
		case Standard:
			break;
		case Prismatic:
			hp += nextInt(8_000, 12_000);
			break;
		case Dungeon:
		case Halloween:
		case Thanksgiving:
			hp += nextInt(1_500, 4_500);
			break;
		case Chaos:
			hp += 10_000 + nextInt(2_500, 7_500);
			break;
		default:
			hp += Constants.ILLEGAL_EGG_HP;
			break;
		}
		
		hp += nextInt(1_000);
		return hp;
	}

	public static Item nextWildHeldItem(int dex) {
		
		// Event Held Items
		if (Event.isEventActive(EventType.BoostedCharmShardChance) && MesozoicRandom.nextInt(Constants.CHARM_SHARD_SPAWN_CHANCE) == 0) {
			return Item.getItem(ItemID.CharmShard);
		}

		if (Event.isEventActive(EventType.Thanksgiving) && dex != DinoID.Turkey.getDex()) {
			return Item.getItem(ItemID.ThanksgivingToken);
		}

		if (Event.isEventActive(EventType.SecretSanta)) {
			if (MesozoicRandom.nextInt(5) == 0) {
				return Item.getItem(ItemID.MysteryPresent);
			} else if (MesozoicRandom.nextInt(4) == 0) {
				return Item.getItem(ItemID.DinoCane);
			}
		}

		if (Event.isEventActive(EventType.Valentines)) {
			if (MesozoicRandom.nextInt(2) == 0) {
				return Item.getItem(ItemID.CandyHeart);
			}
		}

		if (Event.isEventActive(EventType.Easter)) {
			if (MesozoicRandom.nextInt(4) == 0) {
				return Item.getItem(ItemID.ChocolateEgg);
			}
		}

		if (Event.isEventActive(EventType.EarthDay)) {
			// 60% chance for recycle item
			if (MesozoicRandom.nextInt(5) < 3) {
				return Util.getRandomElement(Item.getItemsWithTag(ItemTag.RecycleItem));
			}
		}

		// Normal Held Items
		if (MesozoicRandom.nextInt(Constants.CHARM_SHARD_SPAWN_CHANCE) == 0) {
			return Item.getItem(ItemID.CharmShard);
		}

		return null;
	}
}
