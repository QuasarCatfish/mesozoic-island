package com.quas.mesozoicisland.objects;

import java.util.HashMap;

import com.quas.mesozoicisland.battle.BattleAttack;
import com.quas.mesozoicisland.enums.ItemID;
import com.quas.mesozoicisland.enums.ItemTag;
import com.quas.mesozoicisland.util.Constants;
import com.quas.mesozoicisland.util.MesozoicRandom;
import com.quas.mesozoicisland.util.Util;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;

public class BasicDungeon extends Dungeon {

	protected int floors;
	protected Dinosaur boss;

	protected BasicDungeon(HashMap<String, String> data) {
		super(data);
		
		if (data.containsKey("floors")) {
			floors = Integer.parseInt(data.get("floors"));
		} else if (data.containsKey("minFloors")) {
			do {
				floors = randFloorCount();
			} while (floors < Integer.parseInt(data.get("minFloors")));
		} else {
			floors = randFloorCount();
		}

		boss = randDinosaurBoss();
		setReward(ItemID.DungeonToken, (difficulty * floors + 1) / 2);
	}

	@Override
	public String getEmbedTitle() {
		return "A Dungeon has appeared!";
	}

	@Override
	public MessageEmbed getEmbed() {
		EmbedBuilder eb = new EmbedBuilder();
		eb.setColor(Constants.COLOR);

		eb.setTitle(getEmbedTitle());
		eb.addField("Dungeon Size", String.format("%,d %s", floors, getFloorName()), true);
		eb.addField("Difficulty", getDifficultyString(), true);
		eb.addField("Location", loc.toString(), true);
		eb.addField("Boss", String.format("%s %s", boss.toString(true), boss.getElement().getAsBrackets()), false);

		return eb.build();
	}

	protected String getDifficultyString() {
		return getStars(difficulty);
	}

	@Override
	public boolean hasNextFloor() {
		return currentFloor < floors;
	}

	@Override
	public Dinosaur[] nextFloor() {
		if (getFloor() == floors) {
			return new Dinosaur[] { boss };
		} else {
			Dinosaur[] dinos = new Dinosaur[2 * difficulty];
			for (int q = 0; q < dinos.length; q++) {
				dinos[q] = randDinosaur();
			}
			return dinos;
		}
	}

	@Override
	protected Dinosaur randDinosaur() {
		Dinosaur dino = super.randDinosaur();

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
}
