package com.quas.mesozoicisland.battle;

import com.quas.mesozoicisland.util.Util;

public enum BattleAttack {
	
	// Attack
	BaseAttack, Critical, Rune, Heal10, Heal50,
	
	// Defend
	BaseDefend, Block, Dodge;
	
	public static BattleAttack[] STANDARD_ATTACKS = Util.arr(BaseAttack, BaseAttack, BaseAttack, BaseAttack, BaseAttack, BaseAttack, BaseAttack, Critical, Critical, Rune);
	public static BattleAttack[] STANDARD_DEFENSE = Util.arr(BaseDefend, BaseDefend, BaseDefend, BaseDefend, BaseDefend, BaseDefend, BaseDefend, Block, Block, Dodge);

	public static BattleAttack[] STANDARD_DEFENSE_HEAL = Util.arr(BaseDefend, BaseDefend, BaseDefend, BaseDefend, BaseDefend, BaseDefend, Heal10, Block, Block, Dodge);
	
	public static BattleAttack[] DUNGEON_BOSS_ATTACKS = Util.arr(BaseAttack, BaseAttack, BaseAttack, Critical, Rune, Heal10);
	
	public static BattleAttack[] RAID_BOSS_ATTACKS = Util.arr(Critical, Critical, Heal50);
	public static BattleAttack[] RAID_BOSS_DEFENSE = Util.arr(BaseDefend, BaseDefend, Block);
}
