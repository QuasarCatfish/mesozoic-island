package com.quas.mesozoicisland.objects;

import java.util.HashMap;
import java.util.List;
import java.util.StringJoiner;
import java.util.TreeMap;

import com.quas.mesozoicisland.JDBC;
import com.quas.mesozoicisland.MesozoicIsland;
import com.quas.mesozoicisland.battle.BattleChannel;
import com.quas.mesozoicisland.battle.BattleTeam;
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

	public String getTotalRewardString(int players) {
		StringJoiner sj = new StringJoiner(" ");
		for (ItemID item : reward.keySet()) {
			sj.add(String.format("item %d %d %d", item.getItemId(), item.getItemDamage(), reward.get(item) * players));
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

			sb.append(" A crate was left as the dungeon disappeared. It contained:\n");
			sb.append(JDBC.getRedeemMessage(getTotalRewardString(teams.size())));
			if (teams.size() > 1) sb.append(String.format("The %,d players split the rewards evenly.", teams.size()));

			Action.sendDelayedMessage(MesozoicIsland.getAssistant().getIdLong(), timer, BattleChannel.Dungeon.getBattleChannel(), sb.toString());
			Action.sendDelayedMessage(MesozoicIsland.getAssistant().getIdLong(), timer, Constants.SPAWN_CHANNEL, sb.toString());
			
			for (BattleTeam bt : teams) {
				Action.addItemDelayed(bt.getPlayer().getIdLong(), timer, Stat.DungeonsCleared.getId(), 1);
				for (ItemID item : reward.keySet()) {
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
		Dinosaur dino = MesozoicRandom.nextDungeonDinosaur().setLevel(level).addBoost(Constants.DUNGEON_BOOST);
		return dino;
	}

	protected Dinosaur randDinosaurBoss() {
		int level = 5 * difficulty * (difficulty + 1);
		return MesozoicRandom.nextDungeonBossDinosaur().setLevel(level).addBoost(Constants.DUNGEON_BOOST);
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
		
		if (Event.isEventActive(EventType.DarknessDescent) || data.get("type").equals("darknessDescent")) {
			return new DarknessDescentDungeon(data);
		}

		if (data.get("type").equals("infini")) {
			return new InfiniDungeon(data);
		}

		return new BasicDungeon(data);
	}
}
