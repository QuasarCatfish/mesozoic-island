package com.quas.mesozoicisland.objects;

import java.util.HashMap;
import java.util.List;

import com.quas.mesozoicisland.MesozoicIsland;
import com.quas.mesozoicisland.battle.BattleAttack;
import com.quas.mesozoicisland.battle.BattleChannel;
import com.quas.mesozoicisland.battle.BattleTeam;
import com.quas.mesozoicisland.enums.DinosaurForm;
import com.quas.mesozoicisland.enums.ItemID;
import com.quas.mesozoicisland.enums.ItemTag;
import com.quas.mesozoicisland.util.Action;
import com.quas.mesozoicisland.util.Constants;
import com.quas.mesozoicisland.util.MesozoicRandom;
import com.quas.mesozoicisland.util.Util;

public class ChaosDungeon extends BasicDungeon {

	protected ChaosDungeon(HashMap<String, String> data) {
		super(data);
		setReward(ItemID.DungeonToken, difficulty * floors);
	}

	@Override
	public String getEmbedTitle() {
		return "A Chaos Dungeon has appeared!";
	}

	@Override
	protected String getDifficultyString() {
		return "C" + super.getDifficultyString();
	}

	@Override
	public Dinosaur[] nextFloor() {
		if (getFloor() == floors) {
			Dinosaur[] dinos = new Dinosaur[difficulty];
			for (int q = 0; q < dinos.length - 1; q++) {
				dinos[q] = randChaosDinosaur();
			}
			dinos[dinos.length - 1] = boss;
			return dinos;
		} else {
			return super.nextFloor();
		}
	}

	@Override
	public void onEndDungeon(List<BattleTeam> teams, long timer, boolean bossWin) {
		super.onEndDungeon(teams, timer, bossWin);

		if (!bossWin) {
			Player p = Util.getRandomElement(teams).getPlayer();		
			StringBuilder sb = new StringBuilder();
			Egg egg = getEgg();
	
			sb.append(p.getName());
			sb.append(" picks up the ");
			sb.append(egg.getEggName());
			sb.append(" at the end of the dungeon. What could be inside?");
	
			Action.sendDelayedMessage(MesozoicIsland.getAssistant().getIdLong(), timer, BattleChannel.Dungeon.getBattleChannel(), sb.toString());
			Action.sendDelayedMessage(MesozoicIsland.getAssistant().getIdLong(), timer, Constants.SPAWN_CHANNEL, sb.toString());
			Action.addEggDelayed(p.getIdLong(), timer, egg);
		}
	}

	private Egg getEgg() {
		Dinosaur dino = Dinosaur.getDinosaur(boss.getDex(), DinosaurForm.Chaos.getId());
		return Egg.getRandomEgg(dino.getIdPair());
	}

	protected Dinosaur randChaosDinosaur() {
		int minLevel = 5 * difficulty * (difficulty - 1);
		int maxLevel = 5 * difficulty * (difficulty + 1);
		int level = MesozoicRandom.nextInt(minLevel, maxLevel) + 1;
		Dinosaur dino = MesozoicRandom.nextDinosaur(DinosaurForm.Chaos).setLevel(level).addBoost(Constants.CHAOS_DUNGEON_BOOST);

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
			addReward(ItemID.CharmShard, 1);
		}

		return dino;
	}

	@Override
	protected Dinosaur randDinosaur() {
		Dinosaur dino = super.randDinosaur();
		dino.addBoost(Constants.CHAOS_DUNGEON_BOOST - Constants.DUNGEON_BOOST);
		return dino;
	}

	@Override
	protected Dinosaur randDinosaurBoss() {
		int level = 5 * difficulty * (difficulty + 1);
		return MesozoicRandom.nextDinosaur(DinosaurForm.ChaosBoss).setLevel(level).addBoost(Constants.CHAOS_DUNGEON_BOOST);
	}
}
