package com.quas.mesozoicisland.enums;

import com.quas.mesozoicisland.util.Pair;

public enum Stat {

	// Error
	Error(0),
	
	// Battles
	BattlesEntered(1),
	BattlesWon(2),
	DamageDealt(17),
	DamageReceived(18),
	DinosaursDefeated(24),
	
	// Dungeons
	DungeonsEntered(3),
	DungeonsCleared(4),
	InfiniDungeonFloorsCleared(34),
	InfiniDungeonDeepestFloor(35),
	ChaosDungeonsEntered(36),
	ChaosDungeonsCleared(37),
	
	// Dinosaurs
	DinosaursCaught(5),
	DinosaursLeveledUp(6),
	DinosaursRankedUp(7),
	SnacksFed(19),
	
	// Runes
	RunesObtained(8),
	RunesRankedUp(9),
	
	// Trades
	TimesTraded(10),

	// Dinosaur Coins
	DinosaurCoinsCollected(11),
	DinosaurCoinsSpent(12),
	
	// Eggs
	EggsReceived(13),
	EggsHatched(14),
	ChaosEggsReceived(38),

	// Shop
	TransactionsMade(15),
	
	// Daily
	DailiesClaimed(16),
	RafflesWon(20),
	
	// Raids
	RaidsAttempted(21),
	RaidsDefeated(22),
	
	// Quests
	QuestsCompleted(23),
	
	// Accursed Dinosaurs
	DamageDealtWithAccursed(25),
	BattlesEnteredWithAccursed(26),
	BattlesWonWithAccursed(27),
	DungeonsEnteredWithAccursed(28),
	DinosaursDefeatedWithAccursed(29),
	
	// Events
	GiftPointsReceived(30),
	DarknessDescentDungeonsEntered(31),
	DarknessDescentDinosaursDefeated(32),
	DarknessDescentFloorsCleared(33),
	ChickensDefeated(39),

	// Fossil Fuel Fighters Event
	DefeatFuelPolyptychodon(40),
	DefeatFuelCimoliasaurus(41),
	DefeatFuelThespesius(42),
	DefeatFuelHadrosaurus(43),
	DefeatFuelDorygnathus(44),
	DefeatFuelHypsilophodon(45),
	DefeatFuelPlatecarpus(46),
	DefeatFuelRhabdodon(47),
	DefeatFuelOrnithostoma(48),
	DefeatFuelDiopecephalus(49),
	DefeatFuelRhomaleosaurus(50),
	DefeatFuelColoborhynchus(51),
	;
	
	private long id;
	private Stat(long id) {
		this.id = id;
	}

	public Pair<Integer, Long> getId() {
		return new Pair<Integer, Long>(0, id);
	}

	public long getStatId() {
		return id;
	}

	////////////////////////////////////

	public static Stat of(long id) {
		for (Stat stat : values()) {
			if (stat.id == id) {
				return stat;
			}
		}

		return Error;
	}
}