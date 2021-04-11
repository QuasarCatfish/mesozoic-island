package com.quas.mesozoicisland.objects;

import java.util.HashMap;
import java.util.List;

import com.quas.mesozoicisland.JDBC;
import com.quas.mesozoicisland.battle.BattleAttack;
import com.quas.mesozoicisland.battle.BattleTeam;
import com.quas.mesozoicisland.enums.ItemID;
import com.quas.mesozoicisland.enums.Location;
import com.quas.mesozoicisland.enums.Stat;
import com.quas.mesozoicisland.util.Action;
import com.quas.mesozoicisland.util.Constants;
import com.quas.mesozoicisland.util.MesozoicRandom;

public class DarknessDescentDungeon extends BasicDungeon {

	protected int floorsCleared;

	protected DarknessDescentDungeon(HashMap<String, String> data) {
		super(data);
		floorsCleared = Integer.parseInt(JDBC.getVariable(Constants.EVENT_DARKNESS_DESCENT_FLOORS));

		if (MesozoicRandom.nextInt(4) > 0) {
			if (floorsCleared > 600 && MesozoicRandom.nextInt(3) == 0) difficulty = 6;
			else if (floorsCleared > 450) difficulty = 5;
			else if (floorsCleared > 350) difficulty = 4;
			else if (floorsCleared > 200) difficulty = 3;
			else if (floorsCleared > 50) difficulty = 2;
			else difficulty = 1;
		}

		loc = Location.MurkyLabyrinth;
		if (floors < Constants.EVENT_DARKNESS_DESCENT_MIN_DUNGEON_FLOORS) floors = Constants.EVENT_DARKNESS_DESCENT_MIN_DUNGEON_FLOORS;
		setReward(ItemID.DungeonToken, difficulty * 2);
	}
	
	@Override
	public String getEmbedTitle() {
		if (floorsCleared > 0) {
			return "A path forward has appeared!";
		} else {
			return "The entrance to the Murky Labyrinth has appeared!";
		}
	}

	@Override
	public void onEndFloor(List<BattleTeam> teams, long timer, boolean bossWin) {
		super.onEndFloor(teams, timer, bossWin);

		if (bossWin) {
			JDBC.setVariable(Constants.EVENT_DARKNESS_DESCENT_FLOORS, Integer.toString(Integer.parseInt(JDBC.getVariable(Constants.EVENT_DARKNESS_DESCENT_FLOORS)) - Constants.EVENT_DARKNESS_DESCENT_LOSS_FLOOR_COUNT));
			JDBC.setVariable(Constants.EVENT_DARKNESS_DESCENT_LOSSES, Integer.toString(Integer.parseInt(JDBC.getVariable(Constants.EVENT_DARKNESS_DESCENT_LOSSES)) + 1));
		} else {
			JDBC.setVariable(Constants.EVENT_DARKNESS_DESCENT_FLOORS, Integer.toString(Integer.parseInt(JDBC.getVariable(Constants.EVENT_DARKNESS_DESCENT_FLOORS)) + 1));
			for (BattleTeam bt : teams) {
				Action.addItemDelayed(bt.getPlayer().getIdLong(), timer, Stat.DarknessDescentFloorsCleared.getId(), 1);
			}
		}
	}

	@Override
	protected Dinosaur randDinosaur() {
		Dinosaur dino = super.randDinosaur();

		// Modify Attack
		if (floorsCleared > 100) {
			dino.removeAttack(BattleAttack.BaseAttack);
			dino.addAttack(BattleAttack.Terror);
		}

		return dino;
	}

	@Override
	protected Dinosaur randDinosaurBoss() {
		Dinosaur dino = super.randDinosaurBoss();

		// Modify Level
		if (floorsCleared > 350) boss.setLevel(100);
		else if (floorsCleared > 250) boss.setLevel(50);

		// Modify Attack
		if (floorsCleared > 250) {
			dino.removeAttack(BattleAttack.Heal10);
			dino.addAttack(BattleAttack.Heal50);
		}

		return dino;
	}
}
