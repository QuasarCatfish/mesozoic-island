package com.quas.mesozoicisland.battle;

import com.quas.mesozoicisland.enums.DinosaurForm;
import com.quas.mesozoicisland.objects.Dinosaur;
import com.quas.mesozoicisland.objects.Player;
import com.quas.mesozoicisland.util.DinoMath;
import com.quas.mesozoicisland.util.Util;

public class BattleTeam {

	private Player player;
	private Dinosaur[] dinosaurs;
	private int cur = 0, max = 0;
	private BattleTier tier;
	
	public BattleTeam(long pid) {
		this(Player.getPlayer(pid));
	}
	
	public BattleTeam(Player p) {
		player = p;
		dinosaurs = p.getSelectedDinosaurs();
		if (dinosaurs != null) tier = DinoMath.getBattleTier(dinosaurs);
		max = dinosaurs.length;
	}
	
	public BattleTeam(Player p, Dinosaur[] team) {
		player = p;
		dinosaurs = team;
		if (dinosaurs != null) tier = DinoMath.getBattleTier(dinosaurs);
		max = dinosaurs.length;
	}
	
	public BattleTeam setMax(int max) {
		this.max = max;
		return this;
	}
	
	public Player getPlayer() {
		return player;
	}
	
	public boolean isInvalid() {
		return dinosaurs == null || !Util.contains(BattleTier.getBattleTiers(), tier);
	}
	
	public boolean hasDinosaur() {
		return cur < dinosaurs.length && cur < max;
	}
	
	public boolean hasNextDinosaur() {
		return cur + 1 < dinosaurs.length && cur + 1 < max;
	}
	
	public Dinosaur getDinosaur() {
		return hasDinosaur() ? dinosaurs[cur] : null;
	}
	
	public void changeToNextDinosaur() {
		cur++;
	}
	
	public Dinosaur[] getDinosaurs() {
		return dinosaurs;
	}
	
	public Dinosaur[] getDinosaursInBattle() {
		Dinosaur[] ret = new Dinosaur[Math.min(dinosaurs.length, max)];
		for (int q = 0; q < ret.length; q++) {
			ret[q] = dinosaurs[q];
		}
		return ret;
	}
	
	public BattleTier getBattleTier() {
		return tier;
	}
	
	public void heal() {
		cur = 0;
		for (Dinosaur d : dinosaurs) d.heal();
	}

	public boolean hasAccursed() {
		for (Dinosaur d : getDinosaursInBattle()) {
			if (d.getDinosaurForm() == DinosaurForm.Accursed) {
				return true;
			}
		}
		return false;
	}
}
