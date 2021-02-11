package com.quas.mesozoicisland.objects;

import com.quas.mesozoicisland.JDBC;
import com.quas.mesozoicisland.battle.BattleAttack;
import com.quas.mesozoicisland.enums.EventType;
import com.quas.mesozoicisland.enums.Location;
import com.quas.mesozoicisland.util.Constants;
import com.quas.mesozoicisland.util.MesozoicRandom;

public class Dungeon {

	private Dinosaur[][] floors;
	private int difficulty;
	private Location loc;
	
	private Dungeon() {
		
	}

	public String getTitle() {
		if (Event.isEventActive(EventType.DarknessDescent)) {
			if (Integer.parseInt(JDBC.getVariable(Constants.EVENT_DARKNESS_DESCENT_FLOORS)) > 0) {
				return "A path forward has appeared!";
			} else {
				return "The entrance to the Murky Labyrinth has appeared!";
			}
		}

		return "A Dungeon has appeared!";
	}
	
	public int getFloorCount() {
		return floors.length;
	}
	
	public int getDifficulty() {
		return difficulty;
	}
	
	public Dinosaur[] getDinosaursOnFloor(int floor) {
		return floors[floor];
	}
	
	public String getDifficultyString() {
		StringBuilder sb = new StringBuilder();
		
		// Colored Stars
		for (int q = 0; q < difficulty; q++) {
			sb.append("\u2605");
		}
		
		// Outline Stars
		for (int q = difficulty; q < Constants.MAX_DUNGEON_DIFFICULTY; q++) {
			sb.append("\u2606");
		}
		
		return sb.toString();
	}
	
	public Location getLocation() {
		return loc;
	}
	
	public int getTokenCount() {
		if (Event.isEventActive(EventType.DarknessDescent)) return 2 * difficulty;
		return Math.round((float)Math.ceil(difficulty * floors.length / 2f));
	}
	
	public Dinosaur getBoss() {
		return floors[floors.length - 1][0];
	}
	
	//////////////////////////////////////////////////
	
	public static Dungeon generateRandomDungeon() {
		Dungeon d = new Dungeon();

		if (Event.isEventActive(EventType.DarknessDescent)) {
			// set basic stats
			int floorsCleared = Integer.parseInt(JDBC.getVariable(Constants.EVENT_DARKNESS_DESCENT_FLOORS));
			d.floors = new Dinosaur[MesozoicRandom.nextInt(Constants.EVENT_DARKNESS_DESCENT_MIN_DUNGEON_FLOORS, Constants.MAX_DUNGEON_FLOORS + 1)][];
			d.loc = Location.MurkyLabyrinth;

			// set dungeon difficulty
			if (MesozoicRandom.nextInt(4) == 0) {
				d.difficulty = Constants.MAX_DUNGEON_DIFFICULTY - (int)Math.pow(MesozoicRandom.nextInt(0, (int)Math.pow(Constants.MAX_DUNGEON_DIFFICULTY, 3)), 1d / 3);
			} else {
				if (floorsCleared > 600 && MesozoicRandom.nextInt(3) == 0) d.difficulty = 6;
				else if (floorsCleared > 450) d.difficulty = 5;
				else if (floorsCleared > 350) d.difficulty = 4;
				else if (floorsCleared > 200) d.difficulty = 3;
				else if (floorsCleared > 50) d.difficulty = 2;
				else d.difficulty = 1;
			}

			// fill floors
			for (int q = 0; q < d.floors.length - 1; q++) {
				d.floors[q] = new Dinosaur[d.difficulty];
				for (int w = 0; w < d.floors[q].length; w++) {
					int level = MesozoicRandom.nextInt(10 * (d.difficulty - 1), 10 * d.difficulty) + 1;
					d.floors[q][w] = MesozoicRandom.nextDungeonDinosaur().setLevel(level).addBoost(Constants.DUNGEON_BOOST);

					// add terror
					if (floorsCleared > 100) {
						d.floors[q][w].removeAttack(BattleAttack.BaseAttack);
						d.floors[q][w].addAttack(BattleAttack.Terror);
					}
				}
			}
			
			// fill dungeon boss
			Dinosaur boss = MesozoicRandom.nextDungeonBossDinosaur().setLevel(10 * d.difficulty).addBoost(Constants.DUNGEON_BOOST);
			d.floors[d.floors.length - 1] = new Dinosaur[] {boss};

			// modify boss
			if (floorsCleared > 350) boss.setLevel(100);
			else if (floorsCleared > 250) boss.setLevel(50);
			
			if (floorsCleared > 250) {
				boss.removeAttack(BattleAttack.Heal10);
				boss.addAttack(BattleAttack.Heal50);
			}

		} else {
			// Set basic stats
			d.floors = new Dinosaur[MesozoicRandom.nextInt(Constants.MIN_DUNGEON_FLOORS, Constants.MAX_DUNGEON_FLOORS + 1)][];
			d.difficulty = Constants.MAX_DUNGEON_DIFFICULTY - (int)Math.pow(MesozoicRandom.nextInt(0, (int)Math.pow(Constants.MAX_DUNGEON_DIFFICULTY, 3)), 1d / 3);
			d.loc = MesozoicRandom.nextUnusedLocation();

			// fill floors
			for (int q = 0; q < d.floors.length - 1; q++) {
				d.floors[q] = new Dinosaur[d.difficulty];
				for (int w = 0; w < d.floors[q].length; w++) {
					int level = MesozoicRandom.nextInt(10 * (d.difficulty - 1), 10 * d.difficulty) + 1;
					d.floors[q][w] = MesozoicRandom.nextDungeonDinosaur().setLevel(level).addBoost(Constants.DUNGEON_BOOST);
				}
			}
			
			// fill dungeon boss
			d.floors[d.floors.length - 1] = new Dinosaur[] {
				MesozoicRandom.nextDungeonBossDinosaur().setLevel(10 * d.difficulty).addBoost(Constants.DUNGEON_BOOST)
			};
		}
		
		return d;
	}
}
