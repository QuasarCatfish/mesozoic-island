package com.quas.mesozoicisland.objects;

import java.util.HashMap;
import java.util.List;

import com.quas.mesozoicisland.MesozoicIsland;
import com.quas.mesozoicisland.battle.BattleChannel;
import com.quas.mesozoicisland.battle.BattleTeam;
import com.quas.mesozoicisland.enums.DinosaurForm;
import com.quas.mesozoicisland.enums.ItemID;
import com.quas.mesozoicisland.enums.ItemTag;
import com.quas.mesozoicisland.enums.Location;
import com.quas.mesozoicisland.util.Action;
import com.quas.mesozoicisland.util.Constants;
import com.quas.mesozoicisland.util.DinoMath;
import com.quas.mesozoicisland.util.MesozoicRandom;
import com.quas.mesozoicisland.util.Util;

public class CrystalPalaceDungeon extends BasicDungeon {

	private int clay = 0;

	protected CrystalPalaceDungeon(HashMap<String, String> data) {
		super(data);
		loc = Location.PalacePark;
	}

	@Override
	public String getFloorName() {
		return "Park Sector";
	}
	
	@Override
	public String getEmbedTitle() {
		return "The park is open! Territorial Statue Dinosaurs challenge you!";
	}

	@Override
	public Dinosaur[] nextFloor() {
		Dinosaur[] dinos = super.nextFloor();
		for (Dinosaur d : dinos) clay += DinoMath.getClayDropped(d.getRarity());
		return dinos;
	}

	@Override
	public void onEndFloor(List<BattleTeam> teams, long timer, boolean bossWin) {
		super.onEndFloor(teams, timer, bossWin);
		if (!bossWin) setReward(ItemID.EnchantedClay, (int)Math.ceil(1d * clay / teams.size()));
	}

	@Override
	public void onEndDungeon(List<BattleTeam> teams, long timer, boolean bossWin) {
		super.onEndDungeon(teams, timer, bossWin);
		
		Item armature = null;
		for (Item item : Item.getItemsWithTag(ItemTag.Armature)) {
			Dinosaur dino = Dinosaur.getDinosaur(Util.getDexForm(item.getData()));
			if (dino == null) continue;
			if (!dino.getIdPair().equals(boss.getIdPair())) continue;
			
			armature = item;
			break;
		}

		int armatureCount = 0;
		if (!bossWin) {
			for (BattleTeam bt : teams) {
				if (armature == null || bt.getPlayer().getItemCount(armature) > 0) {
					int clay = DinoMath.getClayDropped(boss.getRarity());
					Action.addItemDelayed(bt.getPlayer().getIdLong(), timer, ItemID.EnchantedClay.getId(), clay);
				} else {
					armatureCount++;
					Action.addItemDelayed(bt.getPlayer().getIdLong(), timer, armature.getIdDmg(), 1);
				}
			}
		}

		StringBuilder sb = new StringBuilder();
		Item clay = Item.getItem(ItemID.EnchantedClay);

		if (teams.size() == 1) {
			if (armature == null || armatureCount == 0) {
				sb.append(teams.get(0).getPlayer().getName());
				sb.append(" finds some ");
				sb.append(clay.toString(2));
				sb.append(" at the end of the park and takes some.");
			} else {
				sb.append(teams.get(0).getPlayer().getName());
				sb.append(" finds ");
				sb.append(Util.getArticle(armature.toString()));
				sb.append(" ");
				sb.append(armature.toString());
				sb.append(" at the end of the park and takes it.");
			}
		} else {
			if (armature == null || armatureCount == 0) {
				sb.append("The trainers find some ");
				sb.append(clay.toString(2));
				sb.append(" at the end of the park and each take some.");
			} else if (armatureCount == teams.size()) {
				sb.append("The trainers find some ");
				sb.append(armature.toString(2));
				sb.append(" at the end of the park and each take one.");
			} else if (armatureCount > 1) {
				sb.append("The trainers find some ");
				sb.append(armature.toString(2));
				sb.append(" and some ");
				sb.append(clay.toString(2));
				sb.append(" at the end of the park. Trainers without an Armature take it and the rest take some clay.");
			} else {
				sb.append("The trainers find ");
				sb.append(Util.getArticle(armature.toString()));
				sb.append(" ");
				sb.append(armature.toString());
				sb.append(" and some ");
				sb.append(clay.toString(2));
				sb.append(" at the end of the park. The trainer without an Armature takes it and the rest take some clay.");
			}
		}

		Action.sendDelayedMessage(MesozoicIsland.getAssistant().getIdLong(), timer, BattleChannel.Dungeon.getBattleChannel(), sb.toString());
		Action.sendDelayedMessage(MesozoicIsland.getAssistant().getIdLong(), timer, Constants.SPAWN_CHANNEL, sb.toString());
	}

	@Override
	protected Dinosaur randDinosaur() {
		int minLevel = 5 * difficulty * (difficulty - 1);
		int maxLevel = 5 * difficulty * (difficulty + 1);
		int level = MesozoicRandom.nextInt(minLevel, maxLevel) + 1;
		Dinosaur dino = MesozoicRandom.nextDinosaur(DinosaurForm.Statue).setLevel(level).addBoost(2 * Constants.DUNGEON_BOOST);
		return dino;
	}

	@Override
	protected Dinosaur randDinosaurBoss() {
		int level = 5 * difficulty * (difficulty + 1);
		Rarity r = difficulty == 1 ? Rarity.getRarity(21) : difficulty == 2 ? Rarity.getRarity(24) : Rarity.getRarity(25);
		return MesozoicRandom.nextDinosaur(DinosaurForm.Statue, r).setLevel(level).addBoost(2 * Constants.DUNGEON_BOOST);
	}
}
