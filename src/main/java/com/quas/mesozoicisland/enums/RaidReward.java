package com.quas.mesozoicisland.enums;

import com.quas.mesozoicisland.util.MesozoicRandom;

public enum RaidReward {

	// Dinosaur Coins
	DinosaurCoin100(ItemID.DinosaurCoin, 100, 1),
	DinosaurCoin250(ItemID.DinosaurCoin, 250, 3),
	DinosaurCoin500(ItemID.DinosaurCoin, 500, 5),
	DinosaurCoin1000(ItemID.DinosaurCoin, 1000, 10),

	// Other Special Items
	CharmShard5(ItemID.CharmShard, 5, 2),
	DungeonToken5(ItemID.DungeonToken, 5, 2),
	ChickenNugget5(ItemID.ChickenNugget, 5, 2),
	ChickenNugget10(ItemID.ChickenNugget, 10, 6),
	PrismaticConverter1(ItemID.PrismaticConverter, 1, 50),

	// Dungeon Tickets
	DungeonTicket1(ItemID.DungeonTicket, 1, 1),
	DungeonTicket3(ItemID.DungeonTicket, 3, 5),
	PremiumDungeonTicket1(ItemID.PremiumDungeonTicket, 1, 5),
	
	// XP Potions
	ATierPotion1(ItemID.ATierXPPotion, 1, 1),
	ATierPotion2(ItemID.ATierXPPotion, 2, 4),
	ATierPotion3(ItemID.ATierXPPotion, 3, 7),
	ATierPotion4(ItemID.ATierXPPotion, 4, 10),
	ATierPotion5(ItemID.ATierXPPotion, 5, 13),
	STierPotion1(ItemID.STierXPPotion, 1, 16),
	
	// Dinosaur Locators
	DinosaurLocator1(ItemID.DinosaurLocator, 1, 1),
	DinosaurLocator2(ItemID.DinosaurLocator, 2, 2),
	DinosaurLocator3(ItemID.DinosaurLocator, 3, 2),
	
	// Dungeon Locators
	WhiteDungeonLocator1(ItemID.DungeonLocator, 1, 3),
	WhiteDungeonLocator2(ItemID.DungeonLocator, 2, 7),
	GreenDungeonLocator1(ItemID.GreenDungeonLocator, 1, 5),
	YellowDungeonLocator1(ItemID.YellowDungeonLocator, 1, 10),
	OrangeDungeonLocator1(ItemID.OrangeDungeonLocator, 1, 15),
	RedDungeonLocator1(ItemID.RedDungeonLocator, 1, 20),

	// Eau
	RandomEau2(ItemID.MysteryEauPouch, 2, 2),
	BattleEau1(ItemID.EauDeBataille, 1, 2),
	ExperienceEau1(ItemID.EauDeExperience, 1, 2),
	MoneyEau1(ItemID.EauDeArgent, 1, 2),
	EggEau1(ItemID.EauDeOeuf, 1, 2),
	;

	private static double weightsum = -1;

	static {
		for (RaidReward reward : values()) {
			weightsum += reward.weight;
		}
	}

	private ItemID item;
	private int count;
	private double weight;
	
	private RaidReward(ItemID item, int count, int weight) {
		this.item = item;
		this.count = count;
		this.weight = 1d / weight;
	}

	public ItemID getItem() {
		return item;
	}

	public int getCount() {
		return count;
	}

	public String asRedeem() {
		return String.format("item %d %d %d", item.getItemId(), item.getItemDamage(), count);
	}

	public static RaidReward randomReward() {
		double d = MesozoicRandom.nextDouble(weightsum);

		for (RaidReward reward : values()) {
			d -= reward.weight;
			if (d < 0) return reward;
		}

		return DinosaurCoin100;
	}
}
