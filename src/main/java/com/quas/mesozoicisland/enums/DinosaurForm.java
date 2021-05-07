package com.quas.mesozoicisland.enums;

import com.quas.mesozoicisland.battle.BattleAttack;
import com.quas.mesozoicisland.util.Constants;
import com.quas.mesozoicisland.util.Util;

public enum DinosaurForm {

	Invalid(-1, "Invalid", "X", BattleAttack.STANDARD_ATTACKS, BattleAttack.STANDARD_DEFENSE, null, null),
	Standard(0, "Standard", "", BattleAttack.STANDARD_ATTACKS, BattleAttack.STANDARD_DEFENSE, DiscordEmote.StandardOwned, DiscordEmote.StandardUnowned),
	
	UncapturableDungeon(-2, "Dungeon", Util.mult(Constants.ZERO_WIDTH_SPACE, 1), BattleAttack.STANDARD_ATTACKS, BattleAttack.STANDARD_DEFENSE, null, null),
	UncapturableDungeonBoss(-3, "Dungeon Boss", Util.mult(Constants.ZERO_WIDTH_SPACE, 2), BattleAttack.DUNGEON_BOSS_ATTACKS, BattleAttack.STANDARD_DEFENSE, null, null),
	Contest(-4, "Contest", "C", BattleAttack.STANDARD_ATTACKS, BattleAttack.STANDARD_DEFENSE, null, null),
	RaidBoss(-5, "Raid Boss", Util.mult(Constants.ZERO_WIDTH_SPACE, 4), BattleAttack.RAID_BOSS_ATTACKS, BattleAttack.RAID_BOSS_DEFENSE, null, null),
	Accursed(-6, "Accursed", "A", BattleAttack.STANDARD_ATTACKS_LOW_ACCURACY, BattleAttack.STANDARD_DEFENSE, null, null),
	ChaosBoss(-7, "Chaos Boss", Util.mult(Constants.ZERO_WIDTH_SPACE, 6), BattleAttack.CHAOS_BOSS_ATTACKS, BattleAttack.RAID_BOSS_DEFENSE, null, null),
	
	Prismatic(10, "Prismatic", "P", BattleAttack.STANDARD_ATTACKS, BattleAttack.STANDARD_DEFENSE, DiscordEmote.PrismaticOwned, null),
	Dungeon(11, "Dungeon", "D", BattleAttack.STANDARD_ATTACKS, BattleAttack.STANDARD_DEFENSE, DiscordEmote.DungeonOwned, DiscordEmote.DungeonUnowned),
	Chaos(12, "Chaos", "K", BattleAttack.STANDARD_ATTACKS, BattleAttack.STANDARD_DEFENSE, DiscordEmote.ChaosOwned, null),
	Halloween(1031, "Halloween", "H", BattleAttack.STANDARD_ATTACKS_SCARE, BattleAttack.STANDARD_DEFENSE, DiscordEmote.HalloweenOwned, DiscordEmote.HalloweenUnowned),
	Thanksgiving(1100, "Thanksgiving", "T", BattleAttack.STANDARD_ATTACKS_HEAL, BattleAttack.STANDARD_DEFENSE, DiscordEmote.ThanksgivingOwned, DiscordEmote.ThanksgivingUnowned),
	Mechanical(1400, "Mechanical", "M", BattleAttack.STANDARD_ATTACKS, BattleAttack.STANDARD_DEFENSE_BLOCK, DiscordEmote.MechanicalOwned, DiscordEmote.MechanicalUnowned),

	AllForms(Integer.MIN_VALUE, "All", Util.mult(Constants.ZERO_WIDTH_SPACE, 3), null, null, null, null),
	AnyForms(Integer.MIN_VALUE + 1, "Any", Util.mult(Constants.ZERO_WIDTH_SPACE, 5), null, null, null, null);
	
	private int id;
	private String name, symbol;
	private BattleAttack[] attacks, defenses;
	private DiscordEmote owned, unowned;
	private DinosaurForm(int id, String name, String symbol, BattleAttack[] attacks, BattleAttack[] defenses, DiscordEmote owned, DiscordEmote unowned) {
		this.id = id;
		this.name = name;
		this.symbol = symbol;
		this.attacks = attacks;
		this.defenses = defenses;
		this.owned = owned;
		this.unowned = unowned;
	}
	
	public int getId() {
		return id;
	}
	
	public String getName() {
		return name;
	}
	
	public String getSymbol() {
		return symbol;
	}
	
	public BattleAttack[] getAttacks() {
		return attacks;
	}
	
	public BattleAttack[] getDefenses() {
		return defenses;
	}

	public DiscordEmote getOwnedEmote() {
		return owned;
	}

	public DiscordEmote getUnownedEmote() {
		return unowned;
	}
	
	//////////////////////////////////////////////
	
	public static DinosaurForm of(int id) {
		for (DinosaurForm f : values()) {
			if (f.id == id) {
				return f;
			}
		}
		return Invalid;
	}
	
	public static DinosaurForm of(String symbol) {
		for (DinosaurForm f : values()) {
			if (f.symbol.equalsIgnoreCase(symbol)) {
				return f;
			}
		}
		return Invalid;
	}
}
