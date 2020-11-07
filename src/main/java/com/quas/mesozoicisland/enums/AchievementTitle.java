package com.quas.mesozoicisland.enums;

import com.quas.mesozoicisland.objects.Item;
import com.quas.mesozoicisland.objects.Player;

public enum AchievementTitle {
	
	Damager1(ItemID.NoviceDamagerTitle, 1_000_000, Stat.DamageDealt, "dealing", "damage"),
	Damager2(ItemID.AdvancedDamagerTitle, 3_000_000, Stat.DamageDealt, "dealing", "damage"),
	Damager3(ItemID.EliteDamagerTitle, 10_000_000, Stat.DamageDealt, "dealing", "damage"),
	Damager4(ItemID.MasterDamagerTitle, 40_000_000, Stat.DamageDealt, "dealing", "damage"),
	Damager5(ItemID.LegendaryDamagerTitle, 100_000_000, Stat.DamageDealt, "dealing", "damage"),
	Survivor1(ItemID.NoviceSurvivorTitle, 1_000_000, Stat.DamageReceived, "taking", "damage"),
	Survivor2(ItemID.AdvancedSurvivorTitle, 3_000_000, Stat.DamageReceived, "taking", "damage"),
	Survivor3(ItemID.EliteSurvivorTitle, 10_000_000, Stat.DamageReceived, "taking", "damage"),
	Survivor4(ItemID.MasterSurvivorTitle, 40_000_000, Stat.DamageReceived, "taking", "damage"),
	Survivor5(ItemID.LegendarySurvivorTitle, 100_000_000, Stat.DamageReceived, "taking", "damage"),
	Battler1(ItemID.NoviceBattlerTitle, 1_000, Stat.BattlesEntered, "entering", "battles"),
	Battler2(ItemID.AdvancedBattlerTitle, 3_000, Stat.BattlesEntered, "entering", "battles"),
	Battler3(ItemID.EliteBattlerTitle, 10_000, Stat.BattlesEntered, "entering", "battles"),
	Battler4(ItemID.MasterBattlerTitle, 40_000, Stat.BattlesEntered, "entering", "battles"),
	Battler5(ItemID.LegendaryBattlerTitle, 100_000, Stat.BattlesEntered, "entering", "battles"),
	Victor1(ItemID.NoviceVictorTitle, 1_000, Stat.BattlesWon, "winning", "battles"),
	Victor2(ItemID.AdvancedVictorTitle, 3_000, Stat.BattlesWon, "winning", "battles"),
	Victor3(ItemID.EliteVictorTitle, 10_000, Stat.BattlesWon, "winning", "battles"),
	Victor4(ItemID.MasterVictorTitle, 40_000, Stat.BattlesWon, "winning", "battles"),
	Victor5(ItemID.LegendaryVictorTitle, 100_000, Stat.BattlesWon, "winning", "battles"),
	Shopper1(ItemID.NoviceShopperTitle, 50_000, Stat.DinosaurCoinsSpent, "spending", Item.getItem(ItemID.DinosaurCoin).toString(2)),
	Shopper2(ItemID.AdvancedShopperTitle, 200_000, Stat.DinosaurCoinsSpent, "spending", Item.getItem(ItemID.DinosaurCoin).toString(2)),
	Shopper3(ItemID.EliteShopperTitle, 1_000_000, Stat.DinosaurCoinsSpent, "spending", Item.getItem(ItemID.DinosaurCoin).toString(2)),
	Shopper4(ItemID.MasterShopperTitle, 5_000_000, Stat.DinosaurCoinsSpent, "spending", Item.getItem(ItemID.DinosaurCoin).toString(2)),
	Shopper5(ItemID.LegendaryShopperTitle, 10_000_000, Stat.DinosaurCoinsSpent, "spending", Item.getItem(ItemID.DinosaurCoin).toString(2)),
	Snacker1(ItemID.NoviceSnackerTitle, 100, Stat.SnacksFed, "feeding", "snacks"),
	Snacker2(ItemID.AdvancedSnackerTitle, 300, Stat.SnacksFed, "feeding", "snacks"),
	Snacker3(ItemID.EliteSnackerTitle, 1_000, Stat.SnacksFed, "feeding", "snacks"),
	Snacker4(ItemID.MasterSnackerTitle, 4_000, Stat.SnacksFed, "feeding", "snacks"),
	Snacker5(ItemID.LegendarySnackerTitle, 10_000, Stat.SnacksFed, "feeding", "snacks"),
	Hatcher1(ItemID.NoviceHatcherTitle, 100, Stat.EggsHatched, "hatching", "eggs"),
	Hatcher2(ItemID.AdvancedHatcherTitle, 250, Stat.EggsHatched, "hatching", "eggs"),
	Hatcher3(ItemID.EliteHatcherTitle, 1_000, Stat.EggsHatched, "hatching", "eggs"),
	Hatcher4(ItemID.MasterHatcherTitle, 3_000, Stat.EggsHatched, "hatching", "eggs"),
	Hatcher5(ItemID.LegendaryHatcherTitle, 10_000, Stat.EggsHatched, "hatching", "eggs"),
	Raider1(ItemID.NoviceRaiderTitle, 10, Stat.RaidsAttempted, "attempting", "raids"),
	Raider2(ItemID.AdvancedRaiderTitle, 50, Stat.RaidsAttempted, "attempting", "raids"),
	Raider3(ItemID.EliteRaiderTitle, 200, Stat.RaidsAttempted, "attempting", "raids"),
	Raider4(ItemID.MasterRaiderTitle, 1_000, Stat.RaidsAttempted, "attempting", "raids"),
	Raider5(ItemID.LegendaryRaiderTitle, 5_000, Stat.RaidsAttempted, "attempting", "raids"),
	RaidVictor1(ItemID.NoviceRaidVictorTitle, 10, Stat.RaidsDefeated, "defeating", "raids"),
	RaidVictor2(ItemID.AdvancedRaidVictorTitle, 50, Stat.RaidsDefeated, "defeating", "raids"),
	RaidVictor3(ItemID.EliteRaidVictorTitle, 200, Stat.RaidsDefeated, "defeating", "raids"),
	RaidVictor4(ItemID.MasterRaidVictorTitle, 1_000, Stat.RaidsDefeated, "defeating", "raids"),
	RaidVictor5(ItemID.LegendaryRaidVictorTitle, 5_000, Stat.RaidsDefeated, "defeating", "raids"),
	;

	private ItemID item;
	private long amount;
	private Stat stat;
	private String verb;
	private String statname;
	private AchievementTitle(ItemID item, long amount, Stat stat, String verb, String statname) {
		this.item = item;
		this.amount = amount;
		this.stat = stat;
		this.verb = verb;
		this.statname = statname;
	}

	public String toString(Player p) {
		return String.format("%s, for %s %,d %s, you have earned the %s.", p.getAsMention(), verb, amount, statname, Item.getItem(item).toString());
	}

	public Item getItem() {
		return Item.getItem(item);
	}

	public Stat getStat() {
		return stat;
	}

	public long getStatAmount() {
		return amount;
	}
}
