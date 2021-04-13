package com.quas.mesozoicisland.objects;

import java.util.ArrayList;
import java.util.StringJoiner;
import java.util.TreeMap;

import com.quas.mesozoicisland.JDBC;
import com.quas.mesozoicisland.enums.DinosaurForm;
import com.quas.mesozoicisland.enums.ItemTag;
import com.quas.mesozoicisland.enums.Stat;
import com.quas.mesozoicisland.util.Constants;
import com.quas.mesozoicisland.util.MesozoicRandom;

public class SnackModule {
	
	private final int NONE = -1;
	private final int HEALTH = 0;
	private final int ATTACK = 1;
	private final int DEFENSE = 2;
	private static volatile boolean mutex = false;

	private int hp, atk, def, snacksUsed;
	private int remhp, rematk, remdef;
	private Dinosaur dino;
	private Item lastItem;
	private String result;

	private SnackModule(Dinosaur dino) {
		hp = 0;
		atk = 0;
		def = 0;
		remhp = Constants.MAX_STAT_BOOST - dino.getHealthMultiplier();
		rematk = Constants.MAX_STAT_BOOST - dino.getAttackMultiplier();
		remdef = Constants.MAX_STAT_BOOST - dino.getDefenseMultiplier();
		
		snacksUsed = 0;
		this.dino = dino;
	}

	public SnackModule(Dinosaur dino, Item snack) {
		this(dino);
		while (mutex);
		setMutex(true);

		applySnack(snack);
		result = doResult();
		setMutex(false);
	}

	public SnackModule(Player p, Dinosaur dino, int count) {
		this(dino);
		while (mutex);
		setMutex(true);

		Item[] snacks = Item.getItemsWithTag(ItemTag.Snack);
		TreeMap<Item, Long> bag = p.getBag();

		loop:
		for (Item snack : snacks) {
			long amount = bag.getOrDefault(snack, 0L);
			for (int q = 0; q < amount; q++) {
				if (snacksUsed >= count) break loop;
				if (!applySnack(snack)) break loop;
			}
		}

		result = doResult();
		setMutex(false);
	}

	private synchronized void setMutex(boolean b) {
		mutex = b;
	}

	private boolean applySnack(Item snack) {
		if (!canDinoEatSnacks()) return false;

		int option = NONE;
		int max = 0;
		lastItem = snack;
		
		switch (MesozoicRandom.nextInt(3)) {
		// HEALTH
		case 0:
			max = remhp;
			if (max > 0) {
				option = HEALTH;
				break;
			}
		
		// ATTACK
		case 1:
			max = rematk;
			if (max > 0) {
				option = ATTACK;
				break;
			}
			
		// DEFENSE
		case 2:
			max = remdef;
			if (max > 0) {
				option = DEFENSE;
				break;
			}
			
		// DEFAULT
		default:
			max = remhp;
			if (max > 0) {
				option = HEALTH;
				break;
			}
			
			max = rematk;
			if (max > 0) {
				option = ATTACK;
				break;
			}
			
			max = remdef;
			if (max > 0) {
				option = DEFENSE;
				break;
			}
		}
		
		if (option == NONE || max <= 0) {
			return false;
		} else {
			int value = MesozoicRandom.nextInt(Math.min(max, Integer.parseInt(snack.getData()))) + 1;
			if (option == HEALTH) {
				hp += value;
				remhp -= value;
			} else if (option == ATTACK) {
				atk += value;
				rematk -= value;
			} else if (option == DEFENSE) {
				def += value;
				remdef -= value;
			}
			JDBC.addItem(dino.getPlayerId(), snack.getIdDmg(), -1);
			snacksUsed++;
			return true;
		}
	}

	private boolean canDinoEatSnacks() {
		if (dino.getDinosaurForm() == DinosaurForm.Accursed) return false;
		if (dino.getDinosaurForm() == DinosaurForm.Contest) return false;
		return true;
	}

	public boolean isSuccessful() {
		return snacksUsed > 0;
	}

	public String getResult() {
		return result;
	}
	
	private String doResult() {
		if (snacksUsed > 0) {
			JDBC.executeUpdate("update captures set modhealth = modhealth + %d, modattack = modattack + %d, moddefense = moddefense + %d where player = %d and dex = %d and form = %d;", hp, atk, def, dino.getPlayerId(), dino.getDex(), dino.getForm());
			JDBC.addItem(dino.getPlayerId(), Stat.SnacksFed.getId(), snacksUsed);
		}

		if (!canDinoEatSnacks()) {
			return String.format("%s, your %s refuses to eat the %s.", dino.getPlayer().getAsMention(), dino.getEffectiveName(), lastItem.toString());
		} else if (snacksUsed == 0) {
			return String.format("%s, your %s has maxed stats and refuses to eat the %s.", dino.getPlayer().getAsMention(), dino.getEffectiveName(), lastItem.toString());
		} else if (snacksUsed == 1) {
			return String.format("%s, you feed the %s to your %s, and it gained %s.", dino.getPlayer().getAsMention(), lastItem.toString(), dino.getEffectiveName(), getStatBoostString());
		} else {
			return String.format("%s, you feed the %,d snacks to your %s, and it gained %s.", dino.getPlayer().getAsMention(), snacksUsed, dino.getEffectiveName(), getStatBoostString());
		}
	}

	private String getStatBoostString() {
		ArrayList<String> strings = new ArrayList<>();
		if (hp > 0) strings.add(String.format("+%,d%% Health", hp));
		if (atk > 0) strings.add(String.format("+%,d%% Attack", atk));
		if (def > 0) strings.add(String.format("+%,d%% Defense", def));

		if (strings.isEmpty()) return "";
		if (strings.size() == 1) return strings.get(0);
		if (strings.size() == 2) return strings.get(0) + " and " + strings.get(1);
		
		StringJoiner sj = new StringJoiner(", ");
		for (int q = 0; q < strings.size(); q++) {
			if (q == strings.size() - 1) {
				sj.add("and " + strings.get(q));
			} else {
				sj.add(strings.get(q));
			}
		}

		return sj.toString();
	}
}
