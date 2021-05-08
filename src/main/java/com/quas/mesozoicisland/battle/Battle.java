package com.quas.mesozoicisland.battle;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import com.quas.mesozoicisland.JDBC;
import com.quas.mesozoicisland.MesozoicIsland;
import com.quas.mesozoicisland.enums.CustomPlayer;
import com.quas.mesozoicisland.enums.DinoID;
import com.quas.mesozoicisland.enums.DinosaurForm;
import com.quas.mesozoicisland.enums.DiscordEmote;
import com.quas.mesozoicisland.enums.EventType;
import com.quas.mesozoicisland.enums.ItemID;
import com.quas.mesozoicisland.enums.ItemTag;
import com.quas.mesozoicisland.enums.Location;
import com.quas.mesozoicisland.enums.Stat;
import com.quas.mesozoicisland.enums.StatusEffect;
import com.quas.mesozoicisland.objects.Dinosaur;
import com.quas.mesozoicisland.objects.Element;
import com.quas.mesozoicisland.objects.Event;
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
	private BattlefieldEffect battlefield = null;
	private boolean meteorWin = false;
	private int impendingDoom = 0;
	private int turnCount = 0;

	public Battle(BattleChannel channel, BattleType type, Location loc) {
		teams = new ArrayList<BattleTeam>();
		this.channel = channel;
		this.type = type;
		this.loc = loc;
	}

	public Battle addTeam(BattleTeam bt) {
		return addTeam(bt, true);
	}

	public Battle addTeam(BattleTeam bt, boolean doBoost) {
		if (bt.isInvalid())
			return this;
		if (!bt.hasDinosaur())
			return this;
		teams.add(bt);

		if (doBoost) {
			for (Dinosaur d : bt.getDinosaurs()) {

				// Activate Charm of Adaptability
				if (d.hasItem() && d.getItem().getId() == ItemID.CharmOfAdaptability.getItemId()) {
					if (loc.getBoostedElements().length == 0) {
						d.setElement(Element.NEUTRAL);
					} else {
						Element e = Util.getRandomElement(loc.getBoostedElements());
						d.setElement(e);
					}
				}

				// Add Fragrance Boost
				if (d.getPlayer() != null && d.getPlayer().getFragranceBattleTimer() > System.currentTimeMillis()) {
					d.addBoost(Math.round(100 * Constants.BATTLE_FRAGRANCE_BONUS));
				}

				// Add Location Boost
				if ((d.getElement().getId() & loc.getBoostedElementsId()) > 0) {
					d.addBoost(Constants.LOCATION_BOOST);
				}
			}
		}

		return this;
	}

	public Battle addBoss(BattleTeam bt) {
		if (bt.isInvalid())
			return this;
		if (!bt.hasDinosaur())
			return this;
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
		if (boss != null)
			teams.add(boss);
		BattleTier tier = BattleTier.of(itier);

		ArrayList<String> print = new ArrayList<String>();
		for (BattleTeam bt : teams) {
			print.add("**" + bt.getPlayer().getName() + "'s Team:**");
			for (Dinosaur d : bt.getDinosaursInBattle()) {
				StringBuilder sb = new StringBuilder();
				sb.append(Constants.BULLET_POINT);
				sb.append(" ");
				sb.append(d.toString());

				if (d.hasItem()) {
					if (d.getItem().hasIcon()) {
						sb.append(" ");
						sb.append(d.getItem().getIcon().toString());
					} else {
						sb.append(" [Holding: ");
						sb.append(d.getItem().toString());
						sb.append("]");
					}
				}

				if (d.hasRune()) {
					sb.append(" [Rune: ");
					sb.append(d.getRune().getName());
					sb.append("]");
				}

				sb.append(String.format(" (%,d Health)", d.getHealth()));
				print.add(sb.toString());
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
			turnCount++;

			// Get attack and defend
			ArrayList<BattleTeam> alive = getAliveTeams();
			int atk = MesozoicRandom.nextInt(alive.size());
			int def = atk;
			while (def == atk)
				def = MesozoicRandom.nextInt(alive.size());
			BattleTeam attack = alive.get(atk);
			BattleTeam defend = alive.get(def);

			// Calculate Damage
			String atkstr = doAttack(attack, defend, time);
			Action.sendDelayedMessage(MesozoicIsland.getAssistant().getIdLong(), time, channel.getBattleChannel(),
					atkstr);

			// All dinosaurs lose
			if (meteorWin) {
				// Add Losses
				for (BattleTeam bt : teams) {
					while (bt.hasDinosaur()) {
						Action.addDinosaurLossDelayed(bt.getPlayer().getIdLong(), time, bt.getDinosaur().getId());
						Action.addItemDelayed(bt.getPlayer().getIdLong(), time, Stat.DamageReceived.getId(), bt.getDinosaur().getCurrentHealth());
						bt.changeToNextDinosaur();
					}
				}
			} else {
				ArrayList<BattleTeam> switchDino = new ArrayList<>();

				for (BattleTeam[] bt : new BattleTeam[][] {{attack, defend}, {defend, attack}}) {
					BattleTeam attackTeam = bt[0];
					BattleTeam defendTeam = bt[1];

					// Dinosaur was defeated
					if (!defendTeam.getDinosaur().isAlive()) {

						// Add Win and Loss
						if (attackTeam.hasDinosaur() && attackTeam.getPlayer().getIdLong() > CustomPlayer.getUpperLimit()) {
							Action.addDinosaurWinDelayed(attackTeam.getPlayer().getIdLong(), time, attackTeam.getDinosaur().getId());
							Action.addItemDelayed(attackTeam.getPlayer().getIdLong(), time, Stat.DinosaursDefeated.getId(), 1);
							if (attackTeam.getDinosaur().getDinosaurForm() == DinosaurForm.Accursed) {
								Action.addItemDelayed(attackTeam.getPlayer().getIdLong(), time, Stat.DinosaursDefeatedWithAccursed.getId(), 1);
							}
						}
						if (defendTeam.hasDinosaur() && defendTeam.getPlayer().getIdLong() > CustomPlayer.getUpperLimit()) {
							Action.addDinosaurLossDelayed(defendTeam.getPlayer().getIdLong(), time, defendTeam.getDinosaur().getId());
						}

						// Defeat Wild Dinosaur
						if (defendTeam.getPlayer().getIdLong() == CustomPlayer.Wild.getIdLong()) {

							// Pick up dinosaur
							if (attackTeam.getPlayer().getIdLong() > CustomPlayer.getUpperLimit() && defendTeam.hasDinosaur() && defendTeam.getDinosaur().getDex() > 0) {
								String get = "";
								int amount = MesozoicRandom.nextInt(defend.getDinosaur().getRarity().getId() % 20, 2 * (defend.getDinosaur().getRarity().getId() % 20));
								
								// crystal pickup or otherwise
								if (defendTeam.getDinosaur().getDinosaurForm() == DinosaurForm.Mechanical) {
									Item item = Item.getItem(ItemID.MechanicalComponent);
									get = String.format("%s picks up %,d %s", attackTeam.getPlayer().getName(), amount, item.toString(amount));
								} else {
									get = String.format("%s picks up the %s crystal", attackTeam.getPlayer().getName(), defendTeam.getDinosaur().getDinosaurName());
								}

								// rune pickup
								if (defendTeam.getDinosaur().hasRune()) {
									get += String.format(" and the %s rune", defendTeam.getDinosaur().getRune().getName());
								}

								// item pickup
								if (defendTeam.getDinosaur().hasItem()) {
									get += String.format(" and %s %s", Util.getArticle(defendTeam.getDinosaur().getItem().toString()), defendTeam.getDinosaur().getItem().toString());
								}

								// Send pickup message
								Action.sendDelayedMessage(MesozoicIsland.getAssistant().getIdLong(), time, channel.getBattleChannel(), get + ".");
								Action.sendDelayedMessage(MesozoicIsland.getAssistant().getIdLong(), time, Constants.SPAWN_CHANNEL, "**" + tier.toString() + ":** " + get + ".");

								// Give dinosaur, rune
								if (defendTeam.getDinosaur().getDinosaurForm() == DinosaurForm.Mechanical) {
									Action.addItemDelayed(attackTeam.getPlayer().getIdLong(), time + 1500, ItemID.MechanicalComponent.getId(), amount);
								} else {
									Action.addDinosaurDelayed(attackTeam.getPlayer().getIdLong(), time + 1500, defendTeam.getDinosaur().getId());
								}

								// give rune and item
								if (defendTeam.getDinosaur().hasRune()) Action.addRuneDelayed(attackTeam.getPlayer().getIdLong(), time + 1500, defendTeam.getDinosaur().getRune().getId());
								if (defendTeam.getDinosaur().hasItem()) Action.addItemDelayed(attackTeam.getPlayer().getIdLong(), time + 1500, defendTeam.getDinosaur().getItem().getIdDmg(), 1);
							}

							// Dinosaur dropped a lost page
							if (Event.isEventActive(EventType.LostPages)) {

							}

							// Dinosaur dopped halloween candy
							if (Event.isEventActive(EventType.Halloween) && MesozoicRandom.nextInt(7) == 0) {
								Item item = Item.getItem(Util.getRandomElement(Constants.HALLOWEEN_CANDY));
								Action.sendDelayedMessage(MesozoicIsland.getAssistant().getIdLong(), time + 1500, Constants.SPAWN_CHANNEL, String.format("%s, you found %s %s.", attackTeam.getPlayer().getAsMention(), Util.getArticle(item.toString()), item.toString()));
								Action.addItemDelayed(attackTeam.getPlayer().getIdLong(), time + 1500, item.getIdDmg(), 1);
							}

							// Thanksgiving
							if (Event.isEventActive(EventType.Thanksgiving)) {
								if (defendTeam.getDinosaur().getDex() == DinoID.Turkey.getDex()) {
									Item itemtoken = Item.getItem(ItemID.ThanksgivingToken);
									int tokens = MesozoicRandom.nextInt(2, 5); // 2-4 tokens

									Item itemturkey = Item.getItem(ItemID.TurkeyLeg);
									int turkeylegs = 2; // 2 turkey legs

									Action.sendDelayedMessage(MesozoicIsland.getAssistant().getIdLong(), time + 1500, Constants.SPAWN_CHANNEL, String.format("%s, you found %,d %s and %,d %s.", attackTeam.getPlayer().getAsMention(), tokens, itemtoken.toString(tokens), turkeylegs, itemturkey.toString(turkeylegs)));
									Action.addItemDelayed(attackTeam.getPlayer().getIdLong(), time + 1500, itemtoken.getIdDmg(), tokens);
									Action.addItemDelayed(attackTeam.getPlayer().getIdLong(), time + 1500, itemturkey.getIdDmg(), turkeylegs);
								}
							}
						}

						// Dinosaur gains XP
						if (attackTeam.getPlayer().getIdLong() > CustomPlayer.getUpperLimit()) {
							double mult = Constants.XP_MULTIPLIER;
							if (attackTeam.getPlayer().getFragranceXpTimer() > System.currentTimeMillis()) mult += Constants.XP_FRAGRANCE_BONUS;
							Action.addXpDelayed(attackTeam.getPlayer().getIdLong(), time, attackTeam.getDinosaur().getId(), Math.round(mult * DinoMath.getXpDropped(attackTeam.getDinosaur().getLevel(), defendTeam.getDinosaur().getLevel())));
						}

						// Switch to next Dinosaur
						switchDino.add(defendTeam);
					}
				}
				
				// Do the switching
				for (BattleTeam bt : switchDino) {
					bt.changeToNextDinosaur();
					if (bt.hasDinosaur()) {
						Action.sendDelayedMessage(MesozoicIsland.getAssistant().getIdLong(), time, channel.getBattleChannel(), String.format("**%s switches dinosaurs to their %s.**", bt.getPlayer().getName(), bt.getDinosaur().getEffectiveName()));
					} else {
						Action.sendDelayedMessage(MesozoicIsland.getAssistant().getIdLong(), time, channel.getBattleChannel(), String.format("**%s has been defeated.**", bt.getPlayer().getName()));
					}
				}
			}

			time += wait;
		}

		if (getAliveTeams().isEmpty()) {
			String msg = "All dinosaurs were defeated.";
			Action.sendDelayedMessage(MesozoicIsland.getAssistant().getIdLong(), time, channel.getBattleChannel(), msg);
			Action.sendDelayedMessage(MesozoicIsland.getAssistant().getIdLong(), time, Constants.SPAWN_CHANNEL, "**" + tier.toString() + ":** " + msg);
		} else {
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
				if (winner.hasAccursed()) Action.addItemDelayed(winner.getPlayer().getIdLong(), time, Stat.BattlesWonWithAccursed.getId(), 1);

				// Money - 20% Chance
				if (winner.getPlayer().getFragranceMoneyTimer() > System.currentTimeMillis() || MesozoicRandom.nextInt(5) == 0) {
					int amount = MesozoicRandom.nextInt(20, 51);
					Pair<Integer, Long> money = ItemID.DinosaurCoin.getId();
					Action.sendDelayedMessage(MesozoicIsland.getAssistant().getIdLong(), time, Constants.SPAWN_CHANNEL, String.format("%s, you found %,d %s on the battlefield.", winner.getPlayer().getAsMention(), amount, Item.getItem(money).toString(amount)));
					Action.addItemDelayed(winner.getPlayer().getIdLong(), time, money, amount);
				}

				// Lost Page
				if (Event.isEventActive(EventType.LostPages)) {

				}
			}
		}

		Action.logBattleChannelDelayed(MesozoicIsland.getAssistant().getIdLong(), channel.getBattleChannel().getIdLong(), time + 20_000);
		return time + 20_000;
	}

	private long doBossBattle(int floor) {
		ArrayList<String> print = new ArrayList<String>();
		print.add("**" + boss.getPlayer().getName() + "'s Team:**");
		for (Dinosaur d : boss.getDinosaursInBattle()) {
			StringBuilder sb = new StringBuilder();
				sb.append(Constants.BULLET_POINT);
				sb.append(" ");
				sb.append(d.toString());

				if (d.hasItem()) {
					if (d.getItem().hasIcon()) {
						sb.append(" ");
						sb.append(d.getItem().getIcon().toString());
					} else {
						sb.append(" [Holding: ");
						sb.append(d.getItem().toString());
						sb.append("]");
					}
				}

				if (d.hasRune()) {
					sb.append(" [Rune: ");
					sb.append(d.getRune().getName());
					sb.append("]");
				}

				sb.append(String.format(" (%,d Health)", d.getHealth()));
				print.add(sb.toString());
		}
		print.add(DiscordEmote.Blank.toString());

		for (BattleTeam bt : teams) {
			print.add("**" + bt.getPlayer().getName() + "'s Team:**");
			for (Dinosaur d : bt.getDinosaursInBattle()) {
				StringBuilder sb = new StringBuilder();
				sb.append(Constants.BULLET_POINT);
				sb.append(" ");
				sb.append(d.toString());

				if (d.hasItem()) {
					if (d.getItem().hasIcon()) {
						sb.append(" ");
						sb.append(d.getItem().getIcon().toString());
					} else {
						sb.append(" [Holding: ");
						sb.append(d.getItem().toString());
						sb.append("]");
					}
				}

				if (d.hasRune()) {
					sb.append(" [Rune: ");
					sb.append(d.getRune().getName());
					sb.append("]");
				}

				sb.append(String.format(" (%,d Health)", d.getHealth()));
				print.add(sb.toString());
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
			turnCount++;

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

			// All dinosaurs lose
			if (meteorWin) {
				// Add Losses
				for (BattleTeam bt : teams) {
					while (bt.hasDinosaur()) {
						Action.addDinosaurLossDelayed(bt.getPlayer().getIdLong(), time, bt.getDinosaur().getId());
						Action.addItemDelayed(bt.getPlayer().getIdLong(), time, Stat.DamageReceived.getId(), bt.getDinosaur().getCurrentHealth());
						bt.changeToNextDinosaur();
					}
				}

				while (boss.hasDinosaur()) boss.changeToNextDinosaur();
			} else {
				ArrayList<BattleTeam> switchDino = new ArrayList<>();

				for (BattleTeam[] bt : new BattleTeam[][] {{attack, defend}, {defend, attack}}) {
					BattleTeam attackTeam = bt[0];
					BattleTeam defendTeam = bt[1];

					// Dinosaur was defeated
					if (!defendTeam.getDinosaur().isAlive()) {

						// Add Win and Loss
						if (attackTeam.hasDinosaur() && attackTeam.getPlayer().getIdLong() > CustomPlayer.getUpperLimit()) {
							Action.addDinosaurWinDelayed(attackTeam.getPlayer().getIdLong(), time, attackTeam.getDinosaur().getId());
							Action.addItemDelayed(attackTeam.getPlayer().getIdLong(), time, Stat.DinosaursDefeated.getId(), 1);

							// Event stats
							if (Event.isEventActive(EventType.DarknessDescent) && defendTeam.getPlayer().getIdLong() == CustomPlayer.Dungeon.getIdLong()) {
								Action.addItemDelayed(attackTeam.getPlayer().getIdLong(), time, Stat.DarknessDescentDinosaursDefeated.getId(), 1);
							}
						}
						if (defendTeam.getPlayer().getIdLong() > CustomPlayer.getUpperLimit()) {
							Action.addDinosaurLossDelayed(defendTeam.getPlayer().getIdLong(), time, defendTeam.getDinosaur().getId());
						}

						// Dinosaur gains XP
						if (attackTeam.getPlayer().getIdLong() > CustomPlayer.getUpperLimit()) {
							double xp = DinoMath.getXpDropped(0, defendTeam.getDinosaur().getLevel()) / (getAliveTeamCount() + 1);
							double mult = Constants.XP_MULTIPLIER * Constants.getDungeonXpMultiplier();

							for (BattleTeam team : getAliveTeams()) {
								if (attackTeam.getPlayer().getIdLong() < CustomPlayer.getUpperLimit()) continue;

								double teamMult = 1;
								if (team.getDinosaur().getLevel() > defendTeam.getDinosaur().getLevel()) teamMult *= Math.max(1 - (attackTeam.getDinosaur().getLevel() - defendTeam.getDinosaur().getLevel()) / 100f, 0f);
								if (team.getPlayer().getFragranceXpTimer() > System.currentTimeMillis()) teamMult += Constants.XP_FRAGRANCE_BONUS;

								int x = 1;
								if (team.getPlayer().getIdLong() == attackTeam.getPlayer().getIdLong()) x++;
								for (int q = 0; q < x; q++) {
									Action.addXpDelayed(team.getPlayer().getIdLong(), time, team.getDinosaur().getId(), Math.round(mult * teamMult * xp));
								}
							}
						}

						// Switch to next Dinosaur
						switchDino.add(defendTeam);
					}
				}
				
				// Do the switching
				for (BattleTeam bt : switchDino) {
					bt.changeToNextDinosaur();
					if (bt.hasDinosaur()) {
						Action.sendDelayedMessage(MesozoicIsland.getAssistant().getIdLong(), time, channel.getBattleChannel(), String.format("**%s switches dinosaurs to their %s.**", bt.getPlayer().getName(), bt.getDinosaur().getEffectiveName()));
					} else {
						Action.sendDelayedMessage(MesozoicIsland.getAssistant().getIdLong(), time, channel.getBattleChannel(), String.format("**%s has been defeated.**", bt.getPlayer().getName()));
					}
				}
			}

			time += wait;
		}

		String msg = "";

		// Boss Wins
		if (boss.hasDinosaur() || meteorWin) {
			bosswin = true;
			if (boss.getPlayer().getIdLong() == CustomPlayer.Dungeon.getIdLong()) {
				if (teams.size() == 1) {
					msg = teams.get(0).getPlayer().getName() + " has failed to clear the " + Util.getOrdinal(floor) + " floor of the dungeon.";
					if (Event.isEventActive(EventType.DarknessDescent)) msg += " The player wakes up to find themself in an earlier section of the cave.";
				} else {
					msg = "The players have failed to clear the " + Util.getOrdinal(floor) + " floor of the dungeon.";
					if (Event.isEventActive(EventType.DarknessDescent)) msg += " The players wake up to find themselves in an earlier section of the cave.";
				}
			} else {
				if (teams.size() == 1) msg = teams.get(0).getPlayer().getName() + " has failed to defeat " + boss.getPlayer().getName() + ".";
				else msg = "The players have failed to defeat " + boss.getPlayer().getName() + ".";
			}
		}

		// Players win
		else {
			if (boss.getPlayer().getIdLong() == CustomPlayer.Dungeon.getIdLong()) {
				if (teams.size() == 1) msg = teams.get(0).getPlayer().getName() + " has cleared the **" + Util.getOrdinal(floor) + " Floor** of the dungeon!";
				else msg = "The players have cleared the **" + Util.getOrdinal(floor) + " Floor** of the dungeon!";
			} else {
				if (teams.size() == 1) msg = teams.get(0).getPlayer().getName() + " has defeated " + boss.getPlayer().getName() + ".";
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

		if (turnCount > Constants.MAX_TURN_COUNT) {
			meteorWin = true;
			return "A meteor crashes into the battlefield, eliminating all dinosaurs.";
		}

		// Impending Doom Activates
		if (battlefield == BattlefieldEffect.ImpendingDoom) {
			if (MesozoicRandom.nextInt(Constants.DOOM_MAX_CHANCE) < impendingDoom) {
				meteorWin = true;
				return "The meteor crashes into the battlefield, eliminating all dinosaurs.";
			} else {
				impendingDoom += Constants.DOOM_INCREASE_CHANCE;
			}
		}

		long damage = Math.round(1d * attack.getDinosaur().getAttack() * attack.getDinosaur().getAttack() / defend.getDinosaur().getDefense() / 4d);

		// Element Effectiveness - Enchanted flips the table
		if (battlefield == BattlefieldEffect.Enchanted) {
			damage /= Element.getEffectiveness(attack.getDinosaur().getElement(), defend.getDinosaur().getElement());
		} else {
			damage *= Element.getEffectiveness(attack.getDinosaur().getElement(), defend.getDinosaur().getElement());
		}

		// Bracer
		if (defend.getDinosaur().hasItem() && defend.getDinosaur().getItem().hasTag(ItemTag.Bracer)) {
			if ((attack.getDinosaur().getElement().getId() & Integer.parseInt(defend.getDinosaur().getItem().getData())) > 0) {
				damage *= Constants.BRACER_MULT;
			}
		}

		// Gauntlet
		if (attack.getDinosaur().hasItem() && attack.getDinosaur().getItem().hasTag(ItemTag.Gauntlet)) {
			if ((defend.getDinosaur().getElement().getId() & Integer.parseInt(attack.getDinosaur().getItem().getData())) > 0) {
				damage *= Constants.GAUNTLET_MULT;
			}
		}

		if (damage < Constants.MIN_DAMAGE) damage = Constants.MIN_DAMAGE;

		StringBuilder sb = new StringBuilder();
		BattleAttack atkeff = MesozoicRandom.nextAttackingBattleEffect(attack.getDinosaur());
		BattleAttack defeff = MesozoicRandom.nextDefendingBattleEffect(defend.getDinosaur());

		// Special
		if (atkeff == BattleAttack.Rune) {
			if (attack.getDinosaur().hasRune()) {
				switch (attack.getDinosaur().getRune().getType()) {
				case DealDamage:
					long runeDamage = attack.getDinosaur().getRune().getPower();
					if (battlefield == BattlefieldEffect.Enchanted) {
						runeDamage /= Element.getEffectiveness(attack.getDinosaur().getElement(), defend.getDinosaur().getElement());
					} else {
						runeDamage *= Element.getEffectiveness(attack.getDinosaur().getElement(), defend.getDinosaur().getElement());
					}

					damage += runeDamage;
					sb.append(String.format("%s's %s uses the %s rune and attacks %s's %s. ", attack.getPlayer().getName(), attack.getDinosaur().getEffectiveName(), attack.getDinosaur().getRune().getName(), defend.getPlayer().getName(), defend.getDinosaur().getEffectiveName()));

					Action.addItemDelayed(attack.getPlayer().getIdLong(), time, Stat.DamageDealt.getId(), Math.min(damage, defend.getDinosaur().getCurrentHealth()));
					Action.addItemDelayed(defend.getPlayer().getIdLong(), time, Stat.DamageReceived.getId(), Math.min(damage, defend.getDinosaur().getCurrentHealth()));
					if (attack.getDinosaur().getDinosaurForm() == DinosaurForm.Accursed) {
						Action.addItemDelayed(attack.getPlayer().getIdLong(), time, Stat.DamageDealtWithAccursed.getId(), Math.min(damage, defend.getDinosaur().getCurrentHealth()));
					}

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
		} else if (atkeff == BattleAttack.Scare) {
			if (MesozoicRandom.nextBoolean()) {
				long atk = defend.getDinosaur().getAttack();
				defend.getDinosaur().addEffect(StatusEffect.ScareAttack);
				atk -= defend.getDinosaur().getAttack();
				sb.append(String.format("%s's %s scares %s's %s, lowering its attack by %,d.", attack.getPlayer().getName(), attack.getDinosaur().getEffectiveName(), defend.getPlayer().getName(), defend.getDinosaur().getEffectiveName(), atk));
			} else {
				long def = defend.getDinosaur().getDefense();
				defend.getDinosaur().addEffect(StatusEffect.ScareDefense);
				def -= defend.getDinosaur().getDefense();
				sb.append(String.format("%s's %s scares %s's %s, lowering its defense by %,d.", attack.getPlayer().getName(), attack.getDinosaur().getEffectiveName(), defend.getPlayer().getName(), defend.getDinosaur().getEffectiveName(), def));
			}
			return sb.toString();
		} else if (atkeff == BattleAttack.DoubleScare) {
			long atk = defend.getDinosaur().getAttack();
			long def = defend.getDinosaur().getDefense();

			boolean sa = defend.getDinosaur().addEffect(StatusEffect.ScareAttack);
			boolean sd = defend.getDinosaur().addEffect(StatusEffect.ScareDefense);
			
			if (sa || sd) {
				atk -= defend.getDinosaur().getAttack();
				def -= defend.getDinosaur().getDefense();
				sb.append(String.format("%s's %s scares %s's %s, lowering its attack by %,d and its defense by %,d.", attack.getPlayer().getName(), attack.getDinosaur().getEffectiveName(), defend.getPlayer().getName(), defend.getDinosaur().getEffectiveName(), atk, def));
				return sb.toString();
			} else {
				atkeff = BattleAttack.AlwaysHitAttack;
			}
		} else if (atkeff == BattleAttack.Terror) {
			long atk = defend.getDinosaur().getAttack();
			long def = defend.getDinosaur().getDefense();

			if (defend.getDinosaur().addEffect(StatusEffect.Terror)) {
				atk -= defend.getDinosaur().getAttack();
				def -= defend.getDinosaur().getDefense();
				sb.append(String.format("%s's %s terrifies %s's %s, lowering its attack by %,d and its defense by %,d.", attack.getPlayer().getName(), attack.getDinosaur().getEffectiveName(), defend.getPlayer().getName(), defend.getDinosaur().getEffectiveName(), atk, def));
				return sb.toString();
			} else {
				atkeff = BattleAttack.AlwaysHitAttack;
			}
		} else if (atkeff == BattleAttack.CoinGrab) {
			int amount = MesozoicRandom.nextInt(20, 51);
			Pair<Integer, Long> money = ItemID.DinosaurCoin.getId();
			Action.addItemDelayed(attack.getPlayer().getIdLong(), time, money, amount);
			return String.format("%s's %s searched around and found %,d %s on the battlefield.", attack.getPlayer().getAsMention(), attack.getDinosaur().getEffectiveName(), amount, Item.getItem(money).toString(amount));
		}

		// Battlefield Charm
		BattlefieldEffect newBattlefield = null;
		if (atkeff == BattleAttack.BattlefieldFog) newBattlefield = BattlefieldEffect.Fog;
		if (atkeff == BattleAttack.BattlefieldEnchanted) newBattlefield = BattlefieldEffect.Enchanted;
		if (atkeff == BattleAttack.BattlefieldLush) newBattlefield = BattlefieldEffect.Lush;
		if (atkeff == BattleAttack.BattlefieldInhabited) newBattlefield = BattlefieldEffect.Inhabited;
		if (atkeff == BattleAttack.BattlefieldImpendingDoom) newBattlefield = BattlefieldEffect.ImpendingDoom;
		if (atkeff == BattleAttack.BattlefieldPlagued) newBattlefield = BattlefieldEffect.Plagued;
		if (atkeff == BattleAttack.BattlefieldGlistening) newBattlefield = BattlefieldEffect.Glistening;
		if (atkeff == BattleAttack.BattlefieldDank) newBattlefield = BattlefieldEffect.Dank;

		if (newBattlefield != null) {
			if (battlefield == null) {
				battlefield = newBattlefield;

				if (attack.getDinosaur().hasItem()) {
					sb.append(String.format("%s's %s activates their %s. %s", attack.getPlayer().getName(), attack.getDinosaur().getEffectiveName(), attack.getDinosaur().getItem().toString(), battlefield.getMessage()));
				} else {
					sb.append(String.format("%s's %s changes the battlefield. %s", attack.getPlayer().getName(), attack.getDinosaur().getEffectiveName(), battlefield.getMessage()));
				}

				applyBattlefield();
				return sb.toString();
			} else {
				atkeff = BattleAttack.BaseAttack;
			}
		}

		// No Special
		sb.append(String.format("%s's %s attacks %s's %s", attack.getPlayer().getName(), attack.getDinosaur().getEffectiveName(), defend.getPlayer().getName(), defend.getDinosaur().getEffectiveName()));

		// Defense
		if (defeff == BattleAttack.Block) {
			damage /= Constants.SPECIAL_DAMAGE_MODIFIER;
			if (defend.getDinosaur().getDinosaurForm() == DinosaurForm.Mechanical) damage /= Constants.SPECIAL_DAMAGE_MODIFIER;
			sb.append(" but it blocked the attack. ");
		} else if ((atkeff != BattleAttack.AlwaysHitAttack && defeff == BattleAttack.Dodge) || atkeff == BattleAttack.Miss) {
			damage = 0;
			sb.append(" but it dodged the attack. ");
		} else {
			sb.append(". ");
		}

		// Crit
		if ((atkeff == BattleAttack.Critical || defeff == BattleAttack.Vulnerable) && defeff != BattleAttack.Block && defeff != BattleAttack.Dodge) {
			if (battlefield == BattlefieldEffect.Fog) damage *= Constants.SPECIAL_DAMAGE_MULTIPLIER_DOUBLE;
			else damage *= Constants.SPECIAL_DAMAGE_MODIFIER;
			sb.append("It's a critical hit! ");
		}

		// Charm of Endurance
		boolean endure = false;
		if (defend.getDinosaur().hasItem() && defend.getDinosaur().getItem().getId() == ItemID.CharmOfEndurance.getItemId()) {
			if (defend.getDinosaur().getHealth() == defend.getDinosaur().getCurrentHealth() && damage > defend.getDinosaur().getCurrentHealth()) {
				damage = defend.getDinosaur().getCurrentHealth() - 1;
				endure = true;
			} else if (defend.getDinosaur().getCurrentHealth() > 1 && damage > defend.getDinosaur().getCurrentHealth() && MesozoicRandom.nextInt(Constants.CHARM_OF_ENDURANCE_CHANCE) == 0) {
				damage = defend.getDinosaur().getCurrentHealth() - 1;
				endure = true;
			}
		}

		// Damage
		Action.addItemDelayed(attack.getPlayer().getIdLong(), time, Stat.DamageDealt.getId(), Math.min(damage, defend.getDinosaur().getCurrentHealth()));
		Action.addItemDelayed(defend.getPlayer().getIdLong(), time, Stat.DamageReceived.getId(), Math.min(damage, defend.getDinosaur().getCurrentHealth()));
		if (attack.getDinosaur().getDinosaurForm() == DinosaurForm.Accursed) {
			Action.addItemDelayed(attack.getPlayer().getIdLong(), time, Stat.DamageDealtWithAccursed.getId(), Math.min(damage, defend.getDinosaur().getCurrentHealth()));
		}

		defend.getDinosaur().damage(damage);
		if (defend.getDinosaur().getCurrentHealth() > 0) {
			sb.append(String.format("%s's %s took %,d damage and has %,d health remaining.", defend.getPlayer().getName(), defend.getDinosaur().getEffectiveName(), damage, defend.getDinosaur().getCurrentHealth()));
			if (endure) {
				if (defend.getDinosaur().hasItem()) sb.append(String.format(" Its %s kept it from being defeated.", defend.getDinosaur().getItem().toString()));
				else sb.append(String.format(" Its endurance kept it from being defeated."));
			}
		} else {
			sb.append(String.format("%s's %s took %,d damage and was defeated.", defend.getPlayer().getName(), defend.getDinosaur().getEffectiveName(), damage));
		}

		// defending dinosaur counter attacks
		if (attack.getDinosaur().getCurrentHealth() > 0 && defend.getDinosaur().getCurrentHealth() > 0) {
			if (defeff == BattleAttack.Counter && MesozoicRandom.nextInt(Constants.COUNTER_CHANCE) == 0) {
				long counter = defend.getDinosaur().getStatTotal() / 100;
				if (counter < Constants.MIN_DAMAGE) counter = Constants.MIN_DAMAGE;

				// Damage
				Action.addItemDelayed(defend.getPlayer().getIdLong(), time, Stat.DamageDealt.getId(), Math.min(counter, attack.getDinosaur().getCurrentHealth()));
				Action.addItemDelayed(attack.getPlayer().getIdLong(), time, Stat.DamageReceived.getId(), Math.min(counter, attack.getDinosaur().getCurrentHealth()));
				if (defend.getDinosaur().getDinosaurForm() == DinosaurForm.Accursed) {
					Action.addItemDelayed(defend.getPlayer().getIdLong(), time, Stat.DamageDealtWithAccursed.getId(), Math.min(counter, attack.getDinosaur().getCurrentHealth()));
				}

				attack.getDinosaur().damage(counter);
				if (attack.getDinosaur().getCurrentHealth() > 0) {
					sb.append(String.format( "\n%s's %s counter attacks for %,d damage, leaving %s's %s with %,d health remaining.", defend.getPlayer().getName(), defend.getDinosaur().getEffectiveName(), counter, attack.getPlayer().getName(), attack.getDinosaur().getEffectiveName(), attack.getDinosaur().getCurrentHealth()));
				} else {
					sb.append(String.format("\n%s's %s counter attacks for %,d damage, defeating %s's %s.", defend.getPlayer().getName(), defend.getDinosaur().getEffectiveName(), counter, attack.getPlayer().getName(), attack.getDinosaur().getEffectiveName()));
				}
			}
		}

		// activate plague effect
		if (battlefield == BattlefieldEffect.Plagued && attack.getDinosaur().getCurrentHealth() > 0) {
			long plague = attack.getDinosaur().getLevel() * 5;

			attack.getDinosaur().damage(plague);
			if (attack.getDinosaur().getCurrentHealth() > 0) {
				sb.append(String.format("\n%s's %s takes %,d damage from the plague. It has %,d health remaining.", attack.getPlayer().getName(), attack.getDinosaur().getEffectiveName(), plague, attack.getDinosaur().getCurrentHealth()));
			} else {
				sb.append(String.format("\n%s's %s takes %,d damage from the plague, defeating it.", attack.getPlayer().getName(), attack.getDinosaur().getEffectiveName(), plague));
			}
		}

		return sb.toString();
	}

	public void applyBattlefield() {
		switch (battlefield) {

		case Fog: {
			// Lower Accuracy for player dinosaurs
			for (BattleTeam bt : teams) {
				for (Dinosaur d : bt.getDinosaurs()) {
					for (int q = 0; q < 2; q++) {
						if (d.removeAttack(BattleAttack.BaseAttack)) {
							d.addAttack(BattleAttack.Miss);
						}
					}
				}
			}

			// Lower accuracy for boss dinosaurs
			if (boss != null) {
				for (Dinosaur d : boss.getDinosaurs()) {
					for (int q = 0; q < 2; q++) {
						if (d.removeAttack(BattleAttack.BaseAttack)) {
							d.addAttack(BattleAttack.Miss);
						}
					}
				}
			}
		} break;

		case Lush: {
			// Add stats to player dinosaurs
			for (BattleTeam bt : teams) {
				for (Dinosaur d : bt.getDinosaurs()) {
					if (d.getDiet().equals("Herbivore")) {
						d.addAttackDefenseBoost(Constants.CHARM_BOOST_AMOUNT_FULL);
					} else if (d.getDiet().equals("Omnivore")) {
						d.addAttackDefenseBoost(Constants.CHARM_BOOST_AMOUNT_HALF);
					}
				}
			}

			// Lower accuracy for boss dinosaurs
			if (boss != null) {
				for (Dinosaur d : boss.getDinosaurs()) {
					if (d.getDiet().equals("Herbivore")) {
						d.addAttackDefenseBoost(Constants.CHARM_BOOST_AMOUNT_FULL);
					} else if (d.getDiet().equals("Omnivore")) {
						d.addAttackDefenseBoost(Constants.CHARM_BOOST_AMOUNT_HALF);
					}
				}
			}
		} break;

		case Inhabited: {
			// Add stats to player dinosaurs
			for (BattleTeam bt : teams) {
				for (Dinosaur d : bt.getDinosaurs()) {
					if (d.getDiet().equals("Carnivore")) {
						d.addAttackDefenseBoost(Constants.CHARM_BOOST_AMOUNT_FULL);
					} else if (d.getDiet().equals("Omnivore")) {
						d.addAttackDefenseBoost(Constants.CHARM_BOOST_AMOUNT_HALF);
					}
				}
			}

			// Lower accuracy for boss dinosaurs
			if (boss != null) {
				for (Dinosaur d : boss.getDinosaurs()) {
					if (d.getDiet().equals("Carnivore")) {
						d.addAttackDefenseBoost(Constants.CHARM_BOOST_AMOUNT_FULL);
					} else if (d.getDiet().equals("Omnivore")) {
						d.addAttackDefenseBoost(Constants.CHARM_BOOST_AMOUNT_HALF);
					}
				}
			}
		} break;

		case ImpendingDoom: {
			impendingDoom = Constants.DOOM_BASE_CHANCE;
		} break;

		case Glistening: {
			// Add stats to player dinosaurs
			for (BattleTeam bt : teams) {
				for (Dinosaur d : bt.getDinosaurs()) {
					if (d.getDinosaurForm() == DinosaurForm.Prismatic) {
						d.addAttackDefenseBoost(Constants.CHARM_BOOST_AMOUNT_FULL);
					}
				}
			}

			// Lower accuracy for boss dinosaurs
			if (boss != null) {
				for (Dinosaur d : boss.getDinosaurs()) {
					if (d.getDinosaurForm() == DinosaurForm.Prismatic) {
						d.addAttackDefenseBoost(Constants.CHARM_BOOST_AMOUNT_FULL);
					}
				}
			}
		} break;

		case Dank: {
			// Add stats to player dinosaurs
			for (BattleTeam bt : teams) {
				for (Dinosaur d : bt.getDinosaurs()) {
					if (d.getDinosaurForm() == DinosaurForm.Dungeon
							|| d.getDinosaurForm() == DinosaurForm.UncapturableDungeon
							|| d.getDinosaurForm() == DinosaurForm.UncapturableDungeonBoss) {
						d.addAttackDefenseBoost(Constants.CHARM_BOOST_AMOUNT_FULL);
					}
				}
			}

			// Lower accuracy for boss dinosaurs
			if (boss != null) {
				for (Dinosaur d : boss.getDinosaurs()) {
					if (d.getDinosaurForm() == DinosaurForm.Dungeon
							|| d.getDinosaurForm() == DinosaurForm.UncapturableDungeon
							|| d.getDinosaurForm() == DinosaurForm.UncapturableDungeonBoss) {
						d.addAttackDefenseBoost(Constants.CHARM_BOOST_AMOUNT_FULL);
					}
				}
			}
		} break;

		default: break;
		}
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
