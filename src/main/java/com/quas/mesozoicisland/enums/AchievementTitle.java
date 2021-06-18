package com.quas.mesozoicisland.enums;

import com.quas.mesozoicisland.objects.Dinosaur;
import com.quas.mesozoicisland.objects.Item;
import com.quas.mesozoicisland.objects.Player;
import com.quas.mesozoicisland.util.Pair;

public enum AchievementTitle {
	
	Damager1(ItemID.NoviceDamagerTitle, 1_000_000, Stat.DamageDealt, "dealing", "damage"),
	Damager2(ItemID.AdvancedDamagerTitle, 3_000_000, Stat.DamageDealt, "dealing", "damage"),
	Damager3(ItemID.EliteDamagerTitle, 10_000_000, Stat.DamageDealt, "dealing", "damage"),
	Damager4(ItemID.MasterDamagerTitle, 40_000_000, Stat.DamageDealt, "dealing", "damage"),
	Damager5(ItemID.LegendaryDamagerTitle, 100_000_000, Stat.DamageDealt, "dealing", "damage"),
	Damager6(ItemID.TranscendantDamagerTitle, 500_000_000, Stat.DamageDealt, "dealing", "damage"),

	Survivor1(ItemID.NoviceSurvivorTitle, 1_000_000, Stat.DamageReceived, "taking", "damage"),
	Survivor2(ItemID.AdvancedSurvivorTitle, 3_000_000, Stat.DamageReceived, "taking", "damage"),
	Survivor3(ItemID.EliteSurvivorTitle, 10_000_000, Stat.DamageReceived, "taking", "damage"),
	Survivor4(ItemID.MasterSurvivorTitle, 40_000_000, Stat.DamageReceived, "taking", "damage"),
	Survivor5(ItemID.LegendarySurvivorTitle, 100_000_000, Stat.DamageReceived, "taking", "damage"),
	Survivor6(ItemID.TranscendantSurvivorTitle, 500_000_000, Stat.DamageReceived, "taking", "damage"),
	
	Battler1(ItemID.NoviceBattlerTitle, 1_000, Stat.BattlesEntered, "entering", "battles"),
	Battler2(ItemID.AdvancedBattlerTitle, 3_000, Stat.BattlesEntered, "entering", "battles"),
	Battler3(ItemID.EliteBattlerTitle, 10_000, Stat.BattlesEntered, "entering", "battles"),
	Battler4(ItemID.MasterBattlerTitle, 40_000, Stat.BattlesEntered, "entering", "battles"),
	Battler5(ItemID.LegendaryBattlerTitle, 100_000, Stat.BattlesEntered, "entering", "battles"),
	Battler6(ItemID.TranscendantBattlerTitle, 500_000, Stat.BattlesEntered, "entering", "battles"),
	
	Victor1(ItemID.NoviceVictorTitle, 1_000, Stat.BattlesWon, "winning", "battles"),
	Victor2(ItemID.AdvancedVictorTitle, 3_000, Stat.BattlesWon, "winning", "battles"),
	Victor3(ItemID.EliteVictorTitle, 10_000, Stat.BattlesWon, "winning", "battles"),
	Victor4(ItemID.MasterVictorTitle, 40_000, Stat.BattlesWon, "winning", "battles"),
	Victor5(ItemID.LegendaryVictorTitle, 100_000, Stat.BattlesWon, "winning", "battles"),
	Victor6(ItemID.TranscendantVictorTitle, 500_000, Stat.BattlesWon, "winning", "battles"),
	
	Shopper1(ItemID.NoviceShopperTitle, 50_000, Stat.DinosaurCoinsSpent, "spending", Item.getItem(ItemID.DinosaurCoin).toString(2)),
	Shopper2(ItemID.AdvancedShopperTitle, 200_000, Stat.DinosaurCoinsSpent, "spending", Item.getItem(ItemID.DinosaurCoin).toString(2)),
	Shopper3(ItemID.EliteShopperTitle, 1_000_000, Stat.DinosaurCoinsSpent, "spending", Item.getItem(ItemID.DinosaurCoin).toString(2)),
	Shopper4(ItemID.MasterShopperTitle, 5_000_000, Stat.DinosaurCoinsSpent, "spending", Item.getItem(ItemID.DinosaurCoin).toString(2)),
	Shopper5(ItemID.LegendaryShopperTitle, 10_000_000, Stat.DinosaurCoinsSpent, "spending", Item.getItem(ItemID.DinosaurCoin).toString(2)),
	Shopper6(ItemID.TranscendantShopperTitle, 50_000_000, Stat.DinosaurCoinsSpent, "spending", Item.getItem(ItemID.DinosaurCoin).toString(2)),
	
	Snacker1(ItemID.NoviceSnackerTitle, 100, Stat.SnacksFed, "feeding", "snacks"),
	Snacker2(ItemID.AdvancedSnackerTitle, 300, Stat.SnacksFed, "feeding", "snacks"),
	Snacker3(ItemID.EliteSnackerTitle, 1_000, Stat.SnacksFed, "feeding", "snacks"),
	Snacker4(ItemID.MasterSnackerTitle, 4_000, Stat.SnacksFed, "feeding", "snacks"),
	Snacker5(ItemID.LegendarySnackerTitle, 10_000, Stat.SnacksFed, "feeding", "snacks"),
	Snacker6(ItemID.TranscendantSnackerTitle, 50_000, Stat.SnacksFed, "feeding", "snacks"),
	
	Hatcher1(ItemID.NoviceHatcherTitle, 100, Stat.EggsHatched, "hatching", "eggs"),
	Hatcher2(ItemID.AdvancedHatcherTitle, 250, Stat.EggsHatched, "hatching", "eggs"),
	Hatcher3(ItemID.EliteHatcherTitle, 1_000, Stat.EggsHatched, "hatching", "eggs"),
	Hatcher4(ItemID.MasterHatcherTitle, 3_000, Stat.EggsHatched, "hatching", "eggs"),
	Hatcher5(ItemID.LegendaryHatcherTitle, 10_000, Stat.EggsHatched, "hatching", "eggs"),
	Hatcher6(ItemID.TranscendantHatcherTitle, 50_000, Stat.EggsHatched, "hatching", "eggs"),
	
	Raider1(ItemID.NoviceRaiderTitle, 10, Stat.RaidsAttempted, "attempting", "raids"),
	Raider2(ItemID.AdvancedRaiderTitle, 50, Stat.RaidsAttempted, "attempting", "raids"),
	Raider3(ItemID.EliteRaiderTitle, 200, Stat.RaidsAttempted, "attempting", "raids"),
	Raider4(ItemID.MasterRaiderTitle, 1_000, Stat.RaidsAttempted, "attempting", "raids"),
	Raider5(ItemID.LegendaryRaiderTitle, 5_000, Stat.RaidsAttempted, "attempting", "raids"),
	Raider6(ItemID.TranscendantRaiderTitle, 20_000, Stat.RaidsAttempted, "attempting", "raids"),
	
	RaidVictor1(ItemID.NoviceRaidVictorTitle, 10, Stat.RaidsDefeated, "defeating", "raids"),
	RaidVictor2(ItemID.AdvancedRaidVictorTitle, 50, Stat.RaidsDefeated, "defeating", "raids"),
	RaidVictor3(ItemID.EliteRaidVictorTitle, 200, Stat.RaidsDefeated, "defeating", "raids"),
	RaidVictor4(ItemID.MasterRaidVictorTitle, 1_000, Stat.RaidsDefeated, "defeating", "raids"),
	RaidVictor5(ItemID.LegendaryRaidVictorTitle, 5_000, Stat.RaidsDefeated, "defeating", "raids"),
	RaidVictor6(ItemID.TranscendantRaidVictorTitle, 20_000, Stat.RaidsDefeated, "defeating", "raids"),

	Veteran(ItemID.VeteranTitle, 365, Stat.DailiesClaimed, "claiming your daily for", "days"),
	ChickenHunter(ItemID.ChickenHunterTitle, 200, Stat.ChickensDefeated, "defeating", "chickens"),
	ChickenTamer(ItemID.ChickenTamerTitle, DinoID.Chicken, DinosaurForm.Standard, 25),
	;

	private boolean isDino;
	private ItemID item;
	private long amount;
	private Stat stat;
	private String verb;
	private String statname;
	private Pair<Integer, Integer> dinoid;
	private int level;

	private AchievementTitle(ItemID item, long amount, Stat stat, String verb, String statname) {
		this.item = item;
		this.amount = amount;
		this.stat = stat;
		this.verb = verb;
		this.statname = statname;
		this.isDino = false;
	}

	private AchievementTitle(ItemID item, DinoID dino, DinosaurForm form, int level) {
		this.item = item;
		this.dinoid = dino.getId(form);
		this.level = level;
		this.isDino = true;
	}

	public String toString(Player p, Dinosaur dino) {
		if (isDino) return String.format("%s, for training your %s to Level %,d, you have earned the %s.", p.getAsMention(), dino.getEffectiveName(), level, Item.getItem(item).toString());
		return String.format("%s, for %s %,d %s, you have earned the %s.", p.getAsMention(), verb, amount, statname, Item.getItem(item).toString());
	}

	public Item getItem() {
		return Item.getItem(item);
	}

	public Pair<Integer, Integer> getDinoId() {
		return dinoid;
	}

	public Stat getStat() {
		return stat;
	}

	public long getStatAmount() {
		return amount;
	}

	public int getLevel() {
		return level;
	}

	public boolean isDino() {
		return isDino;
	}
}
