package com.quas.mesozoicisland.battle;

import com.quas.mesozoicisland.util.Util;

public enum BattleAttack {
	
	// Attack
	BaseAttack, AlwaysHitAttack, Critical, Rune, Heal10, Heal50, Miss, Scare, Terror, CoinGrab,
	
	// Battlefields
	BattlefieldFog, BattlefieldEnchanted, BattlefieldLush, BattlefieldInhabited, BattlefieldImpendingDoom, BattlefieldPlagued, BattlefieldGlistening, BattlefieldDank,

	// Defend
	BaseDefend, Block, Dodge, Counter, Vulnerable;
	
	public static BattleAttack[] STANDARD_ATTACKS = Util.arr(BaseAttack, BaseAttack, BaseAttack, BaseAttack, BaseAttack, BaseAttack, BaseAttack, Critical, Critical, Rune);
	public static BattleAttack[] STANDARD_DEFENSE = Util.arr(BaseDefend, BaseDefend, BaseDefend, BaseDefend, BaseDefend, BaseDefend, BaseDefend, Block, Block, Dodge);

	public static BattleAttack[] STANDARD_ATTACKS_HEAL = Util.arr(BaseAttack, BaseAttack, BaseAttack, BaseAttack, BaseAttack, BaseAttack, Heal10, Critical, Critical, Rune);
	public static BattleAttack[] STANDARD_ATTACKS_SCARE = Util.arr(BaseAttack, BaseAttack, BaseAttack, BaseAttack, BaseAttack, BaseAttack, Scare, Critical, Critical, Rune);
	public static BattleAttack[] STANDARD_ATTACKS_LOW_ACCURACY = Util.arr(Miss, Miss, Miss, Miss, BaseAttack, BaseAttack, BaseAttack, BaseAttack, Critical, Rune);
	
	public static BattleAttack[] DUNGEON_BOSS_ATTACKS = Util.arr(BaseAttack, BaseAttack, BaseAttack, Critical, Rune, Heal10);
	
	public static BattleAttack[] RAID_BOSS_ATTACKS = Util.arr(Critical, Critical, Heal50);
	public static BattleAttack[] RAID_BOSS_DEFENSE = Util.arr(BaseDefend, BaseDefend, Block);
}
