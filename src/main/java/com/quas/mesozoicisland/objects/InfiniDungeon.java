package com.quas.mesozoicisland.objects;

import java.util.HashMap;
import java.util.List;

import com.quas.mesozoicisland.MesozoicIsland;
import com.quas.mesozoicisland.battle.BattleAttack;
import com.quas.mesozoicisland.battle.BattleChannel;
import com.quas.mesozoicisland.battle.BattleTeam;
import com.quas.mesozoicisland.enums.ItemID;
import com.quas.mesozoicisland.enums.ItemTag;
import com.quas.mesozoicisland.enums.Stat;
import com.quas.mesozoicisland.util.Action;
import com.quas.mesozoicisland.util.Constants;
import com.quas.mesozoicisland.util.MesozoicRandom;
import com.quas.mesozoicisland.util.Util;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;

public class InfiniDungeon extends Dungeon {
	
	protected int floorCharm;

	protected InfiniDungeon(HashMap<String, String> data) {
		super(data);
	}

	@Override
	public String getEmbedTitle() {
		return "An InfiniDungeon has appeared!";
	}

	@Override
	public MessageEmbed getEmbed() {
		EmbedBuilder eb = new EmbedBuilder();
		eb.setColor(Constants.COLOR);

		eb.setTitle(getEmbedTitle());
		eb.addField("Dungeon Size", "??? Floors", true);
		eb.addField("Difficulty", getStars(difficulty), true);
		eb.addField("Location", loc.toString(), true);

		return eb.build();
	}

	@Override
	public boolean hasNextFloor() {
		return true;
	}

	@Override
	public Dinosaur[] nextFloor() {
		floorCharm = 0;

		if (getFloor() % 5 == 0) {
			return new Dinosaur[] { randDinosaurBoss() };
		} else {
			Dinosaur[] dinos = new Dinosaur[difficulty + 2];
			for (int q = 0; q < dinos.length; q++) {
				dinos[q] = randDinosaur();
			}
			return dinos;
		}
	}

	@Override
	public void onEndFloor(List<BattleTeam> teams, long timer, boolean bossWin) {
		super.onEndFloor(teams, timer, bossWin);

		if (!bossWin) {
			addReward(ItemID.DungeonToken, 2 * difficulty);
			addReward(ItemID.CharmShard, floorCharm);
		}
	}

	@Override
	public void onEndDungeon(List<BattleTeam> teams, long timer, boolean bossWin) {
		StringBuilder sb = new StringBuilder();
		int floorsCleared = getFloor() - 1;

		if (teams.size() == 1) {
			sb.append(teams.get(0).getPlayer().getName());
			sb.append(" has");
		} else {
			sb.append("The players have");
		}
		sb.append(String.format(" successfully defeated **%,d Floor%s** of the dungeon!", floorsCleared, floorsCleared == 1 ? "" : "s"));

		sb.append(" A crate was left as the dungeon disappeared. It contained:\n");
		sb.append(getRewardString(teams.size()));
		if (teams.size() > 1) sb.append(String.format("\nThe %,d players split the rewards evenly.", teams.size()));

		Action.sendDelayedMessage(MesozoicIsland.getAssistant().getIdLong(), timer, BattleChannel.Dungeon.getBattleChannel(), sb.toString());
		Action.sendDelayedMessage(MesozoicIsland.getAssistant().getIdLong(), timer, Constants.SPAWN_CHANNEL, sb.toString());
		
		for (BattleTeam bt : teams) {
			Action.addItemDelayed(bt.getPlayer().getIdLong(), timer, Stat.DungeonsCleared.getId(), 1);
			Action.addItemDelayed(bt.getPlayer().getIdLong(), timer, Stat.InfiniDungeonFloorsCleared.getId(), floorsCleared);

			long deepest = bt.getPlayer().getItemCount(Stat.InfiniDungeonDeepestFloor);
			if (floorsCleared > deepest) {
				Action.addItemDelayed(bt.getPlayer().getIdLong(), timer, Stat.InfiniDungeonDeepestFloor.getId(), floorsCleared - deepest);
			}
			
			for (ItemID item : reward.keySet()) {
				Action.addItemDelayed(bt.getPlayer().getIdLong(), timer, item.getId(), reward.get(item));
			}
		}
	}

	private int randLevel() {
		int minLevel = 100 * (difficulty - 1) + 25 * (getFloor() - 1);
		int maxLevel = 100 * (difficulty - 1) + 25 * getFloor();
		return MesozoicRandom.nextInt(minLevel, maxLevel) + 1;
	}

	@Override
	protected Dinosaur randDinosaur() {
		Dinosaur dino = super.randDinosaur();
		dino.setLevel(randLevel());

		// Chance to add Scare attack
		if (MesozoicRandom.nextInt(Constants.MAX_DUNGEON_FLOORS) <= currentFloor) {
			dino.removeAttack(BattleAttack.BaseAttack);
			dino.addAttack(BattleAttack.Scare);
		}

		// Chance to add Heal10 attack
		if (MesozoicRandom.nextInt(Constants.MAX_DUNGEON_FLOORS) <= currentFloor) {
			dino.removeAttack(BattleAttack.BaseAttack);
			dino.addAttack(BattleAttack.Heal10);
		}
		
		// Chance to have a random dinosaur charm
		if (MesozoicRandom.nextInt(Constants.DUNGEON_CHARM_CHANCE) == 0) {
			Item charm = Util.getRandomElement(Item.getItemsWithTag(ItemTag.DungeonDinoCharm));
			dino.setItem(charm);
			floorCharm++;
		}

		dino.addBoost(dino.getLevel() / 2);
		return dino;
	}

	@Override
	protected Dinosaur randDinosaurBoss() {
		Dinosaur dino = super.randDinosaurBoss();
		dino.setLevel(randLevel());
		dino.addBoost(dino.getLevel() / 2);
		return dino;
	}
}
