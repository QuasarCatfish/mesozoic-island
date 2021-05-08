package com.quas.mesozoicisland.enums;

public enum ActionType {

	Error(0),
	SendGuildMessage(1),
	SendPrivateMessage(2),
	LogBattleChannel(3),
	GiveDinosaur(4),
	GiveItem(5),
	GiveRune(6),
	DeleteMessage(7),
	RemovePlayerFromBattle(8),
	AddXpToDinosaur(9),
	NewDay(10),
	Redeem(11),
	AddWinToDinosaur(12),
	AddLossToDinosaur(13),
	NewHour(14),
	GiveEgg(15),
	IncrementVariable(16),
	;
	
	private int actiontype;
	private ActionType(int actiontype) {
		this.actiontype = actiontype;
	}
	
	public int getActionType() {
		return actiontype;
	}
	
	public static ActionType getActionType(int action) {
		for (ActionType at : values()) {
			if (at.actiontype == action) {
				return at;
			}
		}
		return Error;
	}
}
