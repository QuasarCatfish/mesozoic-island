package com.quas.mesozoicisland.battle;

public enum BattlefieldEffect {
	Fog("A fog rolls over the battlefield."),
	Enchanted("The battlefield becomes enchanted."),
	Lush("A lush forest fills the battlefield."),
	Inhabited("Prey fill the battlefield."),
	ImpendingDoom("A meteor hangs over the battlefield, ready to crash."),
	Plagued("A plague is unleashed across the battlefield."),
	Glistening("The battlefield begins to gleam and glisten."),
	Dank("The battlefield becomes dank and moist.");

	private String message;
	private BattlefieldEffect(String message) {
		this.message = message;
	}

	public String getMessage() {
		return message;
	}
}
