package com.quas.mesozoicisland.enums;

public class StatusAilment {
	
	private StatusEffect effect;
	private int level;

	public StatusAilment(StatusEffect effect, int level) {
		this.effect = effect;
		this.level = level;
	}

	public StatusEffect getEffect() {
		return effect;
	}

	public int getLevel() {
		return level;
	}

	public boolean canLevelUp() {
		return level < effect.getMaxLevel();
	}

	public boolean levelUp() {
		if (!canLevelUp()) return false;

		level++;
		return true;
	}
}
