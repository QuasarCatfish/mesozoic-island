package com.quas.mesozoicisland.battle;

import com.quas.mesozoicisland.enums.DiscordChannel;

public enum BattleChannel {

	Battle1(DiscordChannel.Battle1, DiscordChannel.BattleLog),
	Battle2(DiscordChannel.Battle2, DiscordChannel.BattleLog),
	Battle3(DiscordChannel.Battle3, DiscordChannel.BattleLog),
	PVP(DiscordChannel.BattlePVP, DiscordChannel.BattleLog),
	Contest(DiscordChannel.BattleContest, DiscordChannel.BattleLog),
	Dungeon(DiscordChannel.BattleDungeon, DiscordChannel.BattleDungeonLog),
	Test(DiscordChannel.BattleTest, DiscordChannel.BattleTestLog),
	Special(DiscordChannel.BattleSpecial, DiscordChannel.BattleSpecialLog);
	
	private DiscordChannel battle, log;
	private BattleChannel(DiscordChannel battle, DiscordChannel log) {
		this.battle = battle;
		this.log = log;
	}
	
	public DiscordChannel getBattleChannel() {
		return battle;
	}
	
	public DiscordChannel getLogChannel() {
		return log;
	}
	
	///////////////////////////////////////////
	
	public static BattleChannel of(long channel) {
		for (BattleChannel bc : values()) {
			if (bc.battle.getIdLong() == channel) {
				return bc;
			}
		}
		return null;
	}
}
