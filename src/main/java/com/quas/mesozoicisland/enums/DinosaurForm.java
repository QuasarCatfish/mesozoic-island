package com.quas.mesozoicisland.enums;

import com.quas.mesozoicisland.battle.BattleAttack;
import com.quas.mesozoicisland.util.Constants;
import com.quas.mesozoicisland.util.Util;

public enum DinosaurForm {

	Invalid(-1, "Invalid", "X", BattleAttack.STANDARD_ATTACKS, BattleAttack.STANDARD_DEFENSE),
	Standard(0, "Standard", "", BattleAttack.STANDARD_ATTACKS, BattleAttack.STANDARD_DEFENSE),
	
	UncapturableDungeon(-2, "Dungeon", Util.mult(Constants.ZERO_WIDTH_SPACE, 1), BattleAttack.STANDARD_ATTACKS, BattleAttack.STANDARD_DEFENSE),
	UncapturableDungeonBoss(-3, "Dungeon Boss", Util.mult(Constants.ZERO_WIDTH_SPACE, 2), BattleAttack.DUNGEON_BOSS_ATTACKS, BattleAttack.STANDARD_DEFENSE),
	Contest(-4, "Contest", "C", BattleAttack.STANDARD_ATTACKS, BattleAttack.STANDARD_DEFENSE),
	RaidBoss(-5, "Raid Boss", Util.mult(Constants.ZERO_WIDTH_SPACE, 4), BattleAttack.RAID_BOSS_ATTACKS, BattleAttack.RAID_BOSS_DEFENSE),
	Accursed(-6, "Accursed", "A", BattleAttack.STANDARD_ATTACKS_LOW_ACCURACY, BattleAttack.STANDARD_DEFENSE),
	
	Prismatic(10, "Prismatic", "P", BattleAttack.STANDARD_ATTACKS, BattleAttack.STANDARD_DEFENSE),
	Dungeon(11, "Dungeon", "D", BattleAttack.STANDARD_ATTACKS, BattleAttack.STANDARD_DEFENSE),
	Halloween(1031, "Halloween", "H", BattleAttack.STANDARD_ATTACKS, BattleAttack.STANDARD_DEFENSE),
	Thanksgiving(1100, "Thanksgiving", "T", BattleAttack.STANDARD_ATTACK_HEAL, BattleAttack.STANDARD_DEFENSE),
	
	AllForms(Integer.MIN_VALUE, "All", Util.mult(Constants.ZERO_WIDTH_SPACE, 3), null, null),
	AnyForms(Integer.MIN_VALUE + 1, "Any", Util.mult(Constants.ZERO_WIDTH_SPACE, 5), null, null);
	
	
	private int id;
	private String name, symbol;
	BattleAttack[] attacks, defenses;
	private DinosaurForm(int id, String name, String symbol, BattleAttack[] attacks, BattleAttack[] defenses) {
		this.id = id;
		this.name = name;
		this.symbol = symbol;
		this.attacks = attacks;
		this.defenses = defenses;
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
