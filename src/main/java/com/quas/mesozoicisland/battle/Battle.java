package com.quas.mesozoicisland.battle;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import com.quas.mesozoicisland.JDBC;
import com.quas.mesozoicisland.MesozoicIsland;
import com.quas.mesozoicisland.enums.CustomPlayer;
import com.quas.mesozoicisland.enums.DiscordEmote;
import com.quas.mesozoicisland.enums.ItemID;
import com.quas.mesozoicisland.enums.Location;
import com.quas.mesozoicisland.enums.Stat;
import com.quas.mesozoicisland.objects.Dinosaur;
import com.quas.mesozoicisland.objects.Element;
import com.quas.mesozoicisland.objects.Item;
import com.quas.mesozoicisland.util.Action;
import com.quas.mesozoicisland.util.Constants;
import com.quas.mesozoicisland.util.DinoMath;
import com.quas.mesozoicisland.util.MesozoicRandom;
import com.quas.mesozoicisland.util.Pair;
import com.quas.mesozoicisland.util.Util;

public class Battle {

	private BattleTeam boss;
	private ArrayList<BattleTeam> teams;
	private BattleChannel channel;
	private BattleType type;
	private Location loc;
	private long time;
	private boolean bosswin = false;
	
	public Battle(BattleChannel channel, BattleType type, Location loc) {
		teams = new ArrayList<BattleTeam>();
		this.channel = channel;
		this.type = type;
		this.loc = loc;
	}
	
	public Battle addTeam(BattleTeam bt) {
		if (bt.isInvalid()) return this;
		if (!bt.hasDinosaur()) return this;
		teams.add(bt);
		
		for (Dinosaur d : bt.getDinosaurs()) {
			if (d.getPlayer() != null && d.getPlayer().getFragranceBattleTimer() > System.currentTimeMillis())
				d.addBoost(Math.round(100 * Constants.BATTLE_FRAGRANCE_BONUS));
			
			for (Element e : loc.getBoostedElements()) {
				if (d.getElement().getId() == e.getId()) {
					d.addBoost(Constants.LOCATION_BOOST);
				}
			}
		}
		
		return this;
	}
	
	public Battle addBoss(BattleTeam bt) {
		if (bt.isInvalid()) return this;
		if (!bt.hasDinosaur()) return this;
		boss = bt;
		
		for (Dinosaur d : bt.getDinosaurs()) {
			if (d.getPlayer() != null && d.getPlayer().getFragranceBattleTimer() > System.currentTimeMillis())
				d.addBoost(Math.round(100 * Constants.BATTLE_FRAGRANCE_BONUS));
			
			for (Element e : loc.getBoostedElements()) {
				if (d.getElement().getId() == e.getId()) {
					d.addBoost(Constants.LOCATION_BOOST);
				}
			}
		}
		
		return this;
	}
	
	public Battle setDelayTime(long time) {
		this.time = time;
		return this;
	}
	
	public ArrayList<BattleTeam> getAliveTeams() {
		ArrayList<BattleTeam> alive = new ArrayList<BattleTeam>();
		for (BattleTeam team : teams) {
			if (team.hasDinosaur()) {
				alive.add(team);
			}
		}
		return alive;
	}
	
	public int getAliveTeamCount() {
		return getAliveTeams().size();
	}
	
	public boolean didBossWin() {
		return bosswin;
	}
	
	public long start(int data) {
		switch (type) {
		case Boss:
			return doBossBattle(data);
		case FFA:
			return doFFABattle(data);
		}
		
		return 0L;
	}
	
	private long doFFABattle(int itier) {
		if (boss != null) teams.add(boss);
		BattleTier tier = BattleTier.of(itier);
		
		ArrayList<String> print = new ArrayList<String>();
		for (BattleTeam bt : teams) {
			print.add("**" + bt.getPlayer().getName() + "'s Team:**");
			for (Dinosaur d : bt.getDinosaursInBattle()) {
				print.add(String.format("%s %s (%,d Health)", Constants.BULLET_POINT, d.toString(), d.getHealth()));
			}
			print.add(DiscordEmote.Blank.toString());
		}
		
		time += 1_000;
		for (String s : Util.bulkify(print)) {
			Action.sendDelayedMessage(MesozoicIsland.getAssistant().getIdLong(), time, channel.getBattleChannel(), s);
		}
		
		time += 10_000;
		long wait = 5_000;
		
		while (getAliveTeamCount() > 1) {
			// Get attack and defend
			ArrayList<BattleTeam> alive = getAliveTeams();
			int atk = MesozoicRandom.nextInt(alive.size());
			int def = atk;
			while (def == atk) def = MesozoicRandom.nextInt(alive.size());
			BattleTeam attack = alive.get(atk);
			BattleTeam defend = alive.get(def);
			
			// Calculate Damage
			String atkstr = doAttack(attack, defend, time);
			Action.sendDelayedMessage(MesozoicIsland.getAssistant().getIdLong(), time, channel.getBattleChannel(), atkstr);
			
			// Defending dinosaur was defeated 
			if (!defend.getDinosaur().isAlive()) {
				
				// Add Win and Loss
				if (attack.getPlayer().getIdLong() > CustomPlayer.getUpperLimit()) {
					Action.addDinosaurWinDelayed(attack.getPlayer().getIdLong(), time, attack.getDinosaur().getId());
					Action.addItemDelayed(attack.getPlayer().getIdLong(), time, Stat.DinosaursDefeated.getId(), 1);
				}
				if (defend.getPlayer().getIdLong() > CustomPlayer.getUpperLimit()) {
					Action.addDinosaurLossDelayed(defend.getPlayer().getIdLong(), time, defend.getDinosaur().getId());
				}
				
				// Pick Up Dinosaur
				if (defend.getPlayer().getIdLong() == CustomPlayer.Wild.getIdLong()) {
					String get = String.format("%s picks up the %s crystal", attack.getPlayer().getName(), defend.getDinosaur().getDinosaurName());
					if (defend.getDinosaur().hasRune()) get += String.format(" and the %s rune", defend.getDinosaur().getRune().getName());
					if (defend.getDinosaur().hasItem()) get += String.format(" and %s %s", Util.getArticle(defend.getDinosaur().getItem().toString()), defend.getDinosaur().getItem().toString());
					
					Action.sendDelayedMessage(MesozoicIsland.getAssistant().getIdLong(), time, channel.getBattleChannel(), get + ".");
					Action.sendDelayedMessage(MesozoicIsland.getAssistant().getIdLong(), time, Constants.SPAWN_CHANNEL, "**" + tier.toString() + ":** " + get + ".");
					Action.addDinosaurDelayed(attack.getPlayer().getIdLong(), time + 1500, defend.getDinosaur().getId());
					if (defend.getDinosaur().hasRune()) Action.addRuneDelayed(attack.getPlayer().getIdLong(), time + 1500, defend.getDinosaur().getRune().getId());
					if (defend.getDinosaur().hasItem()) Action.addItemDelayed(attack.getPlayer().getIdLong(), time + 1500, defend.getDinosaur().getItem().getIdDmg(), 1);
				}
				
				// Dinosaur gains XP
				if (attack.getPlayer().getIdLong() > CustomPlayer.getUpperLimit()) {
					double mult = Constants.XP_MULTIPLIER;
					if (attack.getPlayer().getFragranceXpTimer() > System.currentTimeMillis()) mult += Constants.XP_FRAGRANCE_BONUS;
					Action.addXpDelayed(attack.getPlayer().getIdLong(), time, attack.getDinosaur().getId(), Math.round(mult * DinoMath.getXpDropped(attack.getDinosaur().getLevel(), defend.getDinosaur().getLevel())));
				}
				
				// Switch to next Dinosaur
				defend.changeToNextDinosaur();
				if (defend.hasDinosaur()) {
					Action.sendDelayedMessage(MesozoicIsland.getAssistant().getIdLong(), time, channel.getBattleChannel(), String.format("**%s switches dinosaurs to their %s.**", defend.getPlayer().getName(), defend.getDinosaur().getEffectiveName()));
				} else {
					Action.sendDelayedMessage(MesozoicIsland.getAssistant().getIdLong(), time, channel.getBattleChannel(), String.format("**%s has been defeated.**", defend.getPlayer().getName()));
				}
			}
			
			time += wait;
		}
		
		loc.setInUse(false, time);
		
		BattleTeam winner = getAliveTeams().get(0);
		if (winner.getPlayer().getIdLong() == CustomPlayer.Wild.getIdLong()) {
			String msg = "";
			if (winner.hasNextDinosaur()) msg = "The wild dinosaurs win the battle. The survivors run away.";
			else msg = "The wild dinosaur wins the battle. The survivor runs away.";

			Action.sendDelayedMessage(MesozoicIsland.getAssistant().getIdLong(), time, channel.getBattleChannel(), msg);
			Action.sendDelayedMessage(MesozoicIsland.getAssistant().getIdLong(), time, Constants.SPAWN_CHANNEL, "**" + tier.toString() + ":** " + msg);
		} else if (winner.getPlayer().getIdLong() < CustomPlayer.getUpperLimit()) {
			Action.sendDelayedMessage(MesozoicIsland.getAssistant().getIdLong(), time, channel.getBattleChannel(), String.format("%s wins the battle.", winner.getPlayer().getName()));
			Action.sendDelayedMessage(MesozoicIsland.getAssistant().getIdLong(), time, Constants.SPAWN_CHANNEL, String.format("**%s:** %s wins the battle.", tier.toString(), winner.getPlayer().getName()));
		} else {
			Action.sendDelayedMessage(MesozoicIsland.getAssistant().getIdLong(), time, channel.getBattleChannel(), String.format("%s wins the battle.", winner.getPlayer().getName()));
			Action.sendDelayedMessage(MesozoicIsland.getAssistant().getIdLong(), time, Constants.SPAWN_CHANNEL, String.format("**%s:** %s wins the battle.", tier.toString(), winner.getPlayer().getName()));
			
			// Stat - Battles Won
			Action.addItemDelayed(winner.getPlayer().getIdLong(), time, Stat.BattlesWon.getId(), 1);
			
			// Random Item Dropped at the end of the battle.
			switch (MesozoicRandom.nextInt(5)) {
			
			// Money - 20% Chance
			case 0: {
				int amount = MesozoicRandom.nextInt(20, 51);
				if (winner.getPlayer().getFragranceMoneyTimer() > System.currentTimeMillis())
					amount *= 1 + Constants.MONEY_FRAGRANCE_BONUS;
				Pair<Integer, Long> money = ItemID.DinosaurCoin.getId();
				Action.sendDelayedMessage(MesozoicIsland.getAssistant().getIdLong(), time, Constants.SPAWN_CHANNEL, String.format("%s, you found %,d %s on the battlefield.", winner.getPlayer().getAsMention(), amount, Item.getItem(money).toString(amount)));
				Action.addItemDelayed(winner.getPlayer().getIdLong(), time, money, amount);
			} break;
			
			
			
			}
		}
		
		Action.logBattleChannelDelayed(MesozoicIsland.getAssistant().getIdLong(), channel.getBattleChannel().getIdLong(), time + 20_000);
		return time + 20_000;
	}
	
	private long doBossBattle(int floor) {
		ArrayList<String> print = new ArrayList<String>();
		print.add("**" + boss.getPlayer().getName() + "'s Team:**");
		for (Dinosaur d : boss.getDinosaursInBattle()) {
			print.add(String.format("%s %s (%,d Health)", Constants.BULLET_POINT, d.toString(), d.getHealth()));
		}
		print.add(DiscordEmote.Blank.toString());
		for (BattleTeam bt : teams) {
			print.add("**" + bt.getPlayer().getName() + "'s Team:**");
			for (Dinosaur d : bt.getDinosaursInBattle()) {
				print.add(String.format("%s %s (%,d Health)", Constants.BULLET_POINT, d.toString(), d.getHealth()));
			}
			print.add(DiscordEmote.Blank.toString());
		}
		
		time += 1_000;
		for (String s : Util.bulkify(print)) {
			Action.sendDelayedMessage(MesozoicIsland.getAssistant().getIdLong(), time, channel.getBattleChannel(), s);
		}
		
		time += 10_000;
		long wait = 5_000;
		
		while (boss.hasDinosaur() && getAliveTeamCount() > 0) {
			// Get attack and defend
			ArrayList<BattleTeam> alive = getAliveTeams();
			BattleTeam attack = null;
			BattleTeam defend = null;
			if (MesozoicRandom.nextInt(3) == 0) {
				attack = boss;
				defend = alive.get(MesozoicRandom.nextInt(alive.size()));
			} else {
				attack = alive.get(MesozoicRandom.nextInt(alive.size()));
				defend = boss;
			}
			
			// Calculate Damage
			String atkstr = doAttack(attack, defend, time);
			Action.sendDelayedMessage(MesozoicIsland.getAssistant().getIdLong(), time, channel.getBattleChannel(), atkstr);
			
			// Defending dinosaur was defeated
			if (!defend.getDinosaur().isAlive()) {
				
				// Add Win and Loss
				if (attack.getPlayer().getIdLong() > CustomPlayer.getUpperLimit()) {
					Action.addDinosaurWinDelayed(attack.getPlayer().getIdLong(), time, attack.getDinosaur().getId());
					Action.addItemDelayed(attack.getPlayer().getIdLong(), time, Stat.DinosaursDefeated.getId(), 1);
				}
				if (defend.getPlayer().getIdLong() > CustomPlayer.getUpperLimit()) {
					Action.addDinosaurLossDelayed(attack.getPlayer().getIdLong(), time, attack.getDinosaur().getId());
				}
				
				// Dinosaur gains XP
				if (attack.getPlayer().getIdLong() > CustomPlayer.getUpperLimit()) {
					double xp = DinoMath.getXpDropped(0, defend.getDinosaur().getLevel()) / getAliveTeamCount();
					for (BattleTeam team : getAliveTeams()) {
						if (attack.getPlayer().getIdLong() < CustomPlayer.getUpperLimit()) continue;
						double mult = Constants.XP_MULTIPLIER * Constants.DUNGEON_XP_MULTIPLIER;
						if (attack.getDinosaur().getLevel() > defend.getDinosaur().getLevel()) mult *= Math.max(1 - (attack.getDinosaur().getLevel() - defend.getDinosaur().getLevel()) / 100f, 0f);
						if (team.getPlayer().getFragranceXpTimer() > System.currentTimeMillis()) mult += Constants.XP_FRAGRANCE_BONUS;
						Action.addXpDelayed(team.getPlayer().getIdLong(), time, team.getDinosaur().getId(), Math.round(mult * xp));
					}
				}
				
				// Switch to next Dinosaur
				defend.changeToNextDinosaur();
				if (defend.hasDinosaur()) {
					Action.sendDelayedMessage(MesozoicIsland.getAssistant().getIdLong(), time, channel.getBattleChannel(), String.format("**%s switches dinosaurs to their %s.**", defend.getPlayer().getName(), defend.getDinosaur().getEffectiveName()));
				} else {
					Action.sendDelayedMessage(MesozoicIsland.getAssistant().getIdLong(), time, channel.getBattleChannel(), String.format("**%s has been defeated.**", defend.getPlayer().getName()));
				}
			}
			
			time += wait;
		}
		
		String msg = "";
		
		// Boss Wins
		if (boss.hasDinosaur()) {
			bosswin = true;
			if (boss.getPlayer().getIdLong() == CustomPlayer.Dungeon.getIdLong()) {
				if (teams.size() == 1) msg = "The player has failed to clear the " + Util.getOrdinal(floor) + " floor of the dungeon.";
				else msg = "The players have failed to clear the " + Util.getOrdinal(floor) + " floor of the dungeon.";
			} else {
				if (teams.size() == 1) msg = "The player has been defeated by " + boss.getPlayer().getName() + ".";
				else msg = "The players have been defeated by " + boss.getPlayer().getName() + ".";
			}
		}
		
		// Players win
		else {
			if (boss.getPlayer().getIdLong() == CustomPlayer.Dungeon.getIdLong()) {
				if (teams.size() == 1) msg = "The player has cleared the **" + Util.getOrdinal(floor) + " Floor** of the dungeon!";
				else msg = "The players have cleared the **" + Util.getOrdinal(floor) + " Floor** of the dungeon!";
			} else {
				if (teams.size() == 1) msg = "The player has defeated " + boss.getPlayer().getName() + ".";
				else msg = "The players have defeated " + boss.getPlayer().getName() + ".";
			}
		}
		
		// Header
		String header = "Special";
		if (boss.getPlayer().getIdLong() == CustomPlayer.Dungeon.getIdLong()) header = "Dungeon";
		if (boss.getPlayer().getIdLong() == CustomPlayer.RaidChallenge.getIdLong()) header = "Raid Challenge";
		
		// Send Message
		Action.sendDelayedMessage(MesozoicIsland.getAssistant().getIdLong(), time, channel.getBattleChannel(), msg);
		Action.sendDelayedMessage(MesozoicIsland.getAssistant().getIdLong(), time, Constants.SPAWN_CHANNEL, String.format("**%s:** %s", header, msg));
		
		time += 20_000;
		return time;
	}
	
	private String doAttack(BattleTeam attack, BattleTeam defend, long time) {
		long damage = Math.round(1d * attack.getDinosaur().getAttack() * attack.getDinosaur().getAttack() / defend.getDinosaur().getDefense() / 4d);
		double effectiveness = Element.getEffectiveness(attack.getDinosaur().getElement(), defend.getDinosaur().getElement());
		damage *= effectiveness;
		if (damage < Constants.MIN_DAMAGE) damage = Constants.MIN_DAMAGE;
		
		StringBuilder sb = new StringBuilder();
		BattleAttack atkeff = MesozoicRandom.nextAttackingBattleEffect(attack.getDinosaur().getDinosaurForm());
		BattleAttack defeff = MesozoicRandom.nextDefendingBattleEffect(defend.getDinosaur().getDinosaurForm());
		
		// Special
		if (atkeff == BattleAttack.Rune) {
			if (attack.getDinosaur().hasRune()) {
				switch (attack.getDinosaur().getRune().getType()) {
				case DealDamage:
					damage += attack.getDinosaur().getRune().getPower() + Element.getEffectiveness(attack.getDinosaur().getRune().getElement(), defend.getDinosaur().getElement());
					sb.append(String.format("%s's %s uses the %s rune and attacks %s's %s. ", attack.getPlayer().getName(), attack.getDinosaur().getEffectiveName(), attack.getDinosaur().getRune().getName(), defend.getPlayer().getName(), defend.getDinosaur().getEffectiveName()));
					
					Action.addItemDelayed(attack.getPlayer().getIdLong(), time, Stat.DamageDealt.getId(), Math.min(damage, defend.getDinosaur().getCurrentHealth()));
					Action.addItemDelayed(defend.getPlayer().getIdLong(), time, Stat.DamageReceived.getId(), Math.min(damage, defend.getDinosaur().getCurrentHealth()));
					defend.getDinosaur().damage(damage);
					if (defend.getDinosaur().getCurrentHealth() > 0) {
						sb.append(String.format("%s's %s took %,d damage and has %,d health remaining.", defend.getPlayer().getName(), defend.getDinosaur().getEffectiveName(), damage, defend.getDinosaur().getCurrentHealth()));
					} else {
						sb.append(String.format("%s's %s took %,d damage and was defeated.", defend.getPlayer().getName(), defend.getDinosaur().getEffectiveName(), damage));
					}
					return sb.toString();
				default:
					break;
				}
			}
			
			// No Rune Equipped or invalid rune type
			atkeff = BattleAttack.BaseAttack;
		} else if (atkeff == BattleAttack.Heal50) {
			long heal = Math.round(Math.min(attack.getDinosaur().getHealth() - attack.getDinosaur().getCurrentHealth(), .50d * attack.getDinosaur().getHealth()));
			attack.getDinosaur().damage(-heal);
			sb.append(String.format("%s's %s heals %,d damage and has %,d health remaining.", attack.getPlayer().getName(), attack.getDinosaur().getEffectiveName(), heal, attack.getDinosaur().getCurrentHealth()));
			return sb.toString();
		} else if (atkeff == BattleAttack.Heal10) {
			long heal = Math.round(Math.min(attack.getDinosaur().getHealth() - attack.getDinosaur().getCurrentHealth(), .10d * attack.getDinosaur().getHealth()));
			attack.getDinosaur().damage(-heal);
			sb.append(String.format("%s's %s heals %,d damage and has %,d health remaining.", attack.getPlayer().getName(), attack.getDinosaur().getEffectiveName(), heal, attack.getDinosaur().getCurrentHealth()));
			return sb.toString();
		}
		
		// No Special
		sb.append(String.format("%s's %s attacks %s's %s", attack.getPlayer().getName(), attack.getDinosaur().getEffectiveName(), defend.getPlayer().getName(), defend.getDinosaur().getEffectiveName()));
		
		// Defense
		if (defeff == BattleAttack.Block) {
			damage /= Constants.SPECIAL_DAMAGE_MODIFIER;
			sb.append(" but it blocked the attack. ");
		} else if (defeff == BattleAttack.Dodge) {
			damage = 0;
			sb.append(" but it dodged the attack. ");
		} else {
			sb.append(". ");
		}
		
		// Crit
		if (atkeff == BattleAttack.Critical && defeff != BattleAttack.Block && defeff != BattleAttack.Dodge) {
			damage *= Constants.SPECIAL_DAMAGE_MODIFIER;
			sb.append("It's a critical hit! ");
		}
		
		// Damage
		Action.addItemDelayed(attack.getPlayer().getIdLong(), time, Stat.DamageDealt.getId(), Math.min(damage, defend.getDinosaur().getCurrentHealth()));
		Action.addItemDelayed(defend.getPlayer().getIdLong(), time, Stat.DamageReceived.getId(), Math.min(damage, defend.getDinosaur().getCurrentHealth()));
		defend.getDinosaur().damage(damage);
		if (defend.getDinosaur().getCurrentHealth() > 0) {
			sb.append(String.format("%s's %s took %,d damage and has %,d health remaining.", defend.getPlayer().getName(), defend.getDinosaur().getEffectiveName(), damage, defend.getDinosaur().getCurrentHealth()));
		} else {
			sb.append(String.format("%s's %s took %,d damage and was defeated.", defend.getPlayer().getName(), defend.getDinosaur().getEffectiveName(), damage));
		}
		
		return sb.toString();
	}
	
	////////////////////////////////////////////////////////////
	
	public static boolean markPlayerBattling(long pid, boolean battling) {
		return JDBC.executeUpdate("update players set inbattle = %s where playerid = %d;", battling ? "b'1'" : "b'0'", pid);
	}
	
	public static synchronized boolean isPlayerBattling(long pid) {
		try (ResultSet res = JDBC.executeQuery("select * from players where playerid = %d;", pid)) {
			if (res.next()) {
				return res.getBoolean("inbattle");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}
}
