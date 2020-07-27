package com.quas.mesozoicisland.objects;

import java.util.concurrent.ThreadLocalRandom;

import com.quas.mesozoicisland.enums.Location;
import com.quas.mesozoicisland.util.Constants;
import com.quas.mesozoicisland.util.MesozoicRandom;

public class Dungeon {

	private Dinosaur[][] floors;
	private int difficulty;
	private Location loc;
	
	private Dungeon() {
		
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
		return Math.round((float)Math.ceil(difficulty * floors.length / 2f));
	}
	
	public Dinosaur getBoss() {
		return floors[floors.length - 1][0];
	}
	
	//////////////////////////////////////////////////
	
	public static Dungeon generateRandomDungeon() {
		Dungeon d = new Dungeon();
		d.floors = new Dinosaur[ThreadLocalRandom.current().nextInt(Constants.MIN_DUNGEON_FLOORS, Constants.MAX_DUNGEON_FLOORS + 1)][];
		d.difficulty = Constants.MAX_DUNGEON_DIFFICULTY - (int)Math.sqrt(ThreadLocalRandom.current().nextInt(0, Constants.MAX_DUNGEON_DIFFICULTY * Constants.MAX_DUNGEON_DIFFICULTY));
		d.loc = MesozoicRandom.nextUnusedLocation();
		
		for (int q = 0; q < d.floors.length - 1; q++) {
			d.floors[q] = new Dinosaur[d.difficulty];
			for (int w = 0; w < d.floors[q].length; w++) {
				int level = ThreadLocalRandom.current().nextInt(10 * (d.difficulty - 1), 10 * d.difficulty) + 1;
				d.floors[q][w] = MesozoicRandom.nextDungeonDinosaur().setLevel(level);
			}
		}
		
		d.floors[d.floors.length - 1] = new Dinosaur[] {
			MesozoicRandom.nextDungeonBossDinosaur().setLevel(10 * d.difficulty)
		};
		
		return d;
	}
}
