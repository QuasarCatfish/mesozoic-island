package com.quas.mesozoicisland.enums;

public enum StatusEffect {
	ScareAttack(5), ScareDefense(5), Terror(1), Petrify(1), Combust(1);

	private int maxLevel;
	private StatusEffect(int maxLevel) {
		this.maxLevel = maxLevel;
	}

	public int getMaxLevel() {
		return maxLevel;
	}
}
