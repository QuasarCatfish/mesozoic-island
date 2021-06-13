package com.quas.mesozoicisland.objects;

import java.util.HashMap;
import java.util.List;
import java.util.StringJoiner;
import java.util.TreeMap;

import com.quas.mesozoicisland.MesozoicIsland;
import com.quas.mesozoicisland.battle.BattleChannel;
import com.quas.mesozoicisland.battle.BattleTeam;
import com.quas.mesozoicisland.enums.DinosaurForm;
import com.quas.mesozoicisland.enums.EventType;
import com.quas.mesozoicisland.enums.ItemID;
import com.quas.mesozoicisland.enums.Location;
import com.quas.mesozoicisland.enums.Stat;
import com.quas.mesozoicisland.util.Action;
import com.quas.mesozoicisland.util.Constants;
import com.quas.mesozoicisland.util.MesozoicRandom;

import net.dv8tion.jda.api.entities.MessageEmbed;

public abstract class Dungeon {
	
	protected int difficulty;
	protected int currentFloor;
	protected Location loc;
	protected TreeMap<ItemID, Integer> reward;
	protected HashMap<String, String> data;

	protected Dungeon(HashMap<String, String> data) {
		reward = new TreeMap<>();
		this.data = data;
		loc = MesozoicRandom.nextLocation();
		currentFloor = 0;

		if (data.containsKey("difficulty")) {
			difficulty = Integer.parseInt(data.get("difficulty"));
		} else if (data.containsKey("minDifficulty")) {
			do {
				difficulty = randDifficulty();
			} while (difficulty < Integer.parseInt(data.get("minDifficulty")));
		} else {
			difficulty = randDifficulty();
		}
	}

	public int getFloor() {
		return currentFloor + 1;
	}

	public Location getLocation() {
		return loc;
	}

	public TreeMap<ItemID, Integer> getReward() {
		return reward;
	}

	public boolean hasReward() {
		for (ItemID itemid : reward.keySet()) {
			if (reward.get(itemid) > 0) return true;
		}
		return false;
	}

	public String getRewardString(int players) {
		StringJoiner sj = new StringJoiner("\n");
		for (ItemID itemid : reward.keySet()) {
			if (reward.get(itemid) <= 0) continue;

			Item item = Item.getItem(itemid);
			int total = players * reward.get(itemid);

			StringBuilder sb = new StringBuilder();
			sb.append(Constants.BULLET_POINT);
			sb.append(String.format(" %,d %s", total, item.toString(total)));
			if (players > 1) sb.append(String.format(" (%,d per trainer)", reward.get(itemid)));

			sj.add(sb.toString());
		}
		return sj.toString();
	}

	protected void setReward(ItemID item, int count) {
		reward.put(item, count);
	}

	protected void addReward(ItemID item, int count) {
		if (!reward.containsKey(item)) reward.put(item, 0);
		reward.put(item, reward.get(item) + count);
	}

	public String getFloorName(boolean plural) {
		return plural ? "Floors" : "Floor";
	}

	public abstract String getEmbedTitle();
	public abstract MessageEmbed getEmbed();
	public abstract boolean hasNextFloor();
	public abstract Dinosaur[] nextFloor();

	public void onEndFloor(List<BattleTeam> teams, long timer, boolean bossWin) {
		if (!bossWin) currentFloor++;
	}

	public void onEndDungeon(List<BattleTeam> teams, long timer, boolean bossWin) {
		if (!bossWin) {
			StringBuilder sb = new StringBuilder();

			if (teams.size() == 1) {
				sb.append(teams.get(0).getPlayer().getName());
				sb.append(" has");
			} else {
				sb.append("The players have");
			}
			sb.append(" defeated **all floors** of the dungeon!");

			if (hasReward()) {
				sb.append(" A crate was left as the dungeon disappeared. It contained:\n");
				sb.append(getRewardString(teams.size()));
				if (teams.size() > 1) sb.append(String.format("\nThe %,d players split the rewards evenly.", teams.size()));
			}

			Action.sendDelayedMessage(MesozoicIsland.getAssistant().getIdLong(), timer, BattleChannel.Dungeon.getBattleChannel(), sb.toString());
			Action.sendDelayedMessage(MesozoicIsland.getAssistant().getIdLong(), timer, Constants.SPAWN_CHANNEL, sb.toString());
			
			for (BattleTeam bt : teams) {
				Action.addItemDelayed(bt.getPlayer().getIdLong(), timer, Stat.DungeonsCleared.getId(), 1);
				if (this instanceof ChaosDungeon) Action.addItemDelayed(bt.getPlayer().getIdLong(), timer, Stat.ChaosDungeonsCleared.getId(), 1);

				for (ItemID item : reward.keySet()) {
					if (reward.get(item) <= 0) continue;
					Action.addItemDelayed(bt.getPlayer().getIdLong(), timer, item.getId(), reward.get(item));
				}
			}
		}
	}

	////////////////////////////////////////////////////////

	protected Dinosaur randDinosaur() {
		int minLevel = 5 * difficulty * (difficulty - 1);
		int maxLevel = 5 * difficulty * (difficulty + 1);
		int level = MesozoicRandom.nextInt(minLevel, maxLevel) + 1;
		Dinosaur dino = MesozoicRandom.nextDinosaur(DinosaurForm.UncapturableDungeon).setLevel(level).addBoost(Constants.DUNGEON_BOOST);
		return dino;
	}

	protected Dinosaur randDinosaurBoss() {
		int level = 5 * difficulty * (difficulty + 1);
		return MesozoicRandom.nextDinosaur(DinosaurForm.UncapturableDungeonBoss).setLevel(level).addBoost(Constants.DUNGEON_BOOST);
	}

	///////////////////////////////////////////////
	
	protected static String getStars(int count) {
		StringBuilder sb = new StringBuilder();
		for (int q = 0; q < count; q++) sb.append("\u2605");
		for (int q = count; q < Constants.MAX_DUNGEON_DIFFICULTY; q++) sb.append("\u2606");
		return sb.toString();
	}

	protected static final int randFloorCount() {
		return MesozoicRandom.nextInt(Constants.MIN_DUNGEON_FLOORS, Constants.MAX_DUNGEON_FLOORS + 1);
	}

	protected static final int randDifficulty() {
		return Constants.MAX_DUNGEON_DIFFICULTY - (int)Math.pow(MesozoicRandom.nextInt(0, (int)Math.pow(Constants.MAX_DUNGEON_DIFFICULTY, 3)), 1d / 3);
	}

	////////////////////////////////////////////////

	public static Dungeon generateRandomDungeon(String dataStr) {
		HashMap<String, String> data = new HashMap<>();
		data.put("type", "");

		if (dataStr != null) {
			for (String str : dataStr.split(";")) {
				String[] split = str.split("=");
				data.put(split[0], split[1]);
			}
		}
		
		// Specific Dungeon Type
		switch (data.get("type")) {
			case "basic": return new BasicDungeon(data);
			case "infini": return new InfiniDungeon(data);
			case "chaos": return new ChaosDungeon(data);
		}

		// Dungeon Variant
		switch (MesozoicRandom.nextInt(Constants.DUNGEON_VARIANT_CHANCE)) {
			case 0: return new InfiniDungeon(data);
			case 1: return new ChaosDungeon(data);
		}
		
		// Event Dungeons
		if (Event.isEventActive(EventType.DarknessDescent)) {
			return new DarknessDescentDungeon(data);
		} else if (Event.isEventActive(EventType.CrystalPalaceFlashback)) {
			return new CrystalPalaceDungeon(data);
		}

		// Basic Dungeon
		return new BasicDungeon(data);
	}
}
