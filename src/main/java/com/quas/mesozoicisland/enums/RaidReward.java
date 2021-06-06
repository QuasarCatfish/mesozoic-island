package com.quas.mesozoicisland.enums;

import java.util.ArrayList;
import java.util.Collections;
import java.util.StringJoiner;
import java.util.TreeMap;

import com.quas.mesozoicisland.util.Constants;
import com.quas.mesozoicisland.util.MesozoicRandom;

public enum RaidReward {

	// Dinosaur Coins
	DinosaurCoin(ItemID.DinosaurCoin, 200, 1, 1),

	// Other Special Items
	CharmShard(ItemID.CharmShard, 5, 2, 4),
	DungeonToken(ItemID.DungeonToken, 5, 2, 4),
	ChickenNugget(ItemID.ChickenNugget, 5, 2, 4),
	PrismaticConverter(ItemID.PrismaticConverter, 1, 1_000, 100),

	// Dungeon Tickets
	DungeonTicket(ItemID.DungeonTicket, 1, 2, 5),
	PremiumDungeonTicket(ItemID.PremiumDungeonTicket, 1, 5, 12),
	
	// XP Potions
	ATierPotion(ItemID.ATierXPPotion, 1, 3, 5),
	STierPotion(ItemID.STierXPPotion, 1, 1_000, 40),
	
	// Dinosaur Locators
	DinosaurLocator(ItemID.DinosaurLocator, 1, 1, 3),
	
	// Dungeon Locators
	WhiteDungeonLocator(ItemID.DungeonLocator, 1, 2, 5),
	GreenDungeonLocator(ItemID.GreenDungeonLocator, 1, 5, 10),
	YellowDungeonLocator(ItemID.YellowDungeonLocator, 1, 8, 20),
	OrangeDungeonLocator(ItemID.OrangeDungeonLocator, 1, 11, 30),
	RedDungeonLocator(ItemID.RedDungeonLocator, 1, 14, 40),

	// Eau
	RandomEau(ItemID.MysteryEauPouch, 2, 3, 4),
	BattleEau(ItemID.EauDeBataille, 1, 2, 3),
	ExperienceEau(ItemID.EauDeExperience, 1, 2, 3),
	MoneyEau(ItemID.EauDeArgent, 1, 2, 3),
	EggEau(ItemID.EauDeOeuf, 1, 2, 3),
	;

	private static double weightsum = -1;

	static {
		for (RaidReward reward : values()) {
			weightsum += reward.weight;
		}
	}

	private ItemID item;
	private int count;
	private int points;
	private double weight;
	
	private RaidReward(ItemID item, int count, int points, int weight) {
		this.item = item;
		this.count = count;
		this.points = points;
		this.weight = 1d / weight;
	}

	public ItemID getItem() {
		return item;
	}

	public int getCount() {
		return count;
	}

	private static RaidReward rand() {
		double d = MesozoicRandom.nextDouble(weightsum);

		for (RaidReward reward : values()) {
			d -= reward.weight;
			if (d < 0) return reward;
		}

		return DinosaurCoin;
	}

	public static String randomReward() {
		// Generate items so that points >= RAID_MIN_POINTS
		int points = 0;
		ArrayList<RaidReward> rewards = new ArrayList<>();
		while (points < Constants.RAID_MIN_POINTS) {
			RaidReward rr = rand();
			rewards.add(rr);
			points += rr.points;
		}
		Collections.sort(rewards, (a, b) -> -Integer.compare(a.points, b.points));
		
		// Restrict items so that points <= 1.5 * RAID_MIN_POINTS
		points = 0;
		TreeMap<ItemID, Integer> reward = new TreeMap<>();
		for (RaidReward rr : rewards) {
			if (!reward.containsKey(rr.item)) reward.put(rr.item, 0);
			reward.put(rr.item, reward.get(rr.item) + rr.count);
			if (points > 1.5 * Constants.RAID_MIN_POINTS) break;
		}

		// Create redeem string
		StringJoiner sj = new StringJoiner(" ");
		for (ItemID item : reward.keySet()) {
			sj.add(String.format("item %d %d %d", item.getItemId(), item.getItemDamage(), reward.get(item)));
		}

		return sj.toString();
	}
}
