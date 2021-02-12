package com.quas.mesozoicisland.objects;

import java.io.File;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.quas.mesozoicisland.JDBC;
import com.quas.mesozoicisland.battle.BattleAttack;
import com.quas.mesozoicisland.enums.DinoID;
import com.quas.mesozoicisland.enums.DinosaurForm;
import com.quas.mesozoicisland.enums.ItemID;
import com.quas.mesozoicisland.enums.ItemTag;
import com.quas.mesozoicisland.enums.StatusAilment;
import com.quas.mesozoicisland.enums.StatusEffect;
import com.quas.mesozoicisland.util.Constants;
import com.quas.mesozoicisland.util.DinoMath;
import com.quas.mesozoicisland.util.Pair;
import com.quas.mesozoicisland.util.RomanNumeral;
import com.quas.mesozoicisland.util.Util;
import com.quas.mesozoicisland.util.Zalgo;

public class Dinosaur implements Comparable<Dinosaur> {

	private int dex;
	private int form;
	private String dinoname;
	private Element element;
	private Rarity rarity;
	private long health;
	private long attack;
	private long defense;
	private String wiki;
	private String image;
	private int discovery;
	private String type;
	private String authors;
	private String epoch;
	private String location;
	private String diet;
	
	private long player = -1;
	private String nick;
	private long xp = -1;
	private int rp = -1;
	private int rank = -1;
	private int healthmult = 0;
	private int attackmult = 0;
	private int defensemult = 0;
	private int healthboost = 0;
	private int attackboost = 0;
	private int defenseboost = 0;
	private long damage = 0L;
	private Item item;
	private Rune rune;
	private int wins = 0;
	private int losses = 0;
	private ArrayList<BattleAttack> attacks = new ArrayList<>();
	private ArrayList<BattleAttack> defenses = new ArrayList<>();
	private ArrayList<StatusAilment> ailments = new ArrayList<>();
	
	private Dinosaur() {}
	
	public int getDex() {
		return dex;
	}
	
	public int getForm() {
		return form;
	}
	
	public DinosaurForm getDinosaurForm() {
		return DinosaurForm.of(form);
	}
	
	public String getFormSymbol() {
		return DinosaurForm.of(form).getSymbol();
	}
	
	public String getFormName() {
		return DinosaurForm.of(form).getName();
	}
	
	public String getId() {
		if (getDex() < 0) return Util.mult("?", Constants.MAX_DEX_DIGITS);
		return String.format("%0" + Constants.MAX_DEX_DIGITS + "d%s", getDex(), getFormSymbol());
	}
	
	public Pair<Integer, Integer> getIdPair() {
		return new Pair<Integer, Integer>(dex, form);
	}
	
	public String getDinosaurName() {
		return dinoname;
	}
	
	public Element getElement() {
		return element;
	}
	
	public Rarity getRarity() {
		return rarity;
	}
	
	public long getHealth() {
		return Math.round(health * (1 + healthboost / 100d));
	}
	
	public long getCurrentHealth() {
		if (damage < 0) damage = 0;
		return getHealth() - damage;
	}
	
	public long getAttack() {
		return Math.round(attack * (1 + attackboost / 100d));
	}
	
	public long getDefense() {
		return Math.round(defense * (1 + defenseboost / 100d));
	}

	public long getStatTotal() {
		return getHealth() + getAttack() + getDefense();
	}
	
	public int getHealthMultiplier() {
		return healthmult;
	}
	
	public int getAttackMultiplier() {
		return attackmult;
	}
	
	public int getDefenseMultiplier() {
		return defensemult;
	}
	
	public String getWikiLink() {
		return wiki;
	}
	
	public String getImageLink() {
		return image;
	}
	
	public File getImage() {
		return new File("image");
	}
	
	public int getDiscoveryYear() {
		return discovery;
	}
	
	public String getCreatureType() {
		return type;
	}
	
	public String getAuthors() {
		return authors;
	}
	
	public String getEpoch() {
		return epoch;
	}
	
	public String getLocation() {
		return location;
	}
	
	public String getDiet() {
		return diet;
	}
	
	// Individual Dinosaur Related
	
	public long getPlayerId() {
		return player;
	}
	
	private Player playerobj = null;
	public Player getPlayer() {
		if (playerobj == null) playerobj = Player.getPlayer(player);
		return playerobj;
	}
	
	public String getNickname() {
		return nick;
	}
	
	public long getXp() {
		return xp;
	}
	
	public int getLevel() {
		return DinoMath.getLevel(xp);
	}
	
	public long getXpMinusLevel() {
		return xp - DinoMath.getXp(getLevel());
	}
	
	public Dinosaur setLevel(int level) {
		int boost = DinoMath.getLevelBoost(level) - DinoMath.getLevelBoost(getLevel());
		healthboost += boost;
		attackboost += boost;
		defenseboost += boost;
		xp = DinoMath.getXp(level);
		return this;
	}
	
	public int getRp() {
		return rp;
	}
	
	public int getRank() {
		return rank;
	}
	
	public int getRpToRankup() {
		return rank - rp + 1;
	}
	
	public boolean canRankup() {
		return getRpToRankup() <= 0;
	}
	
	public String getRankString() {
		return RomanNumeral.of(getRank());
	}
	
	public String getNextRankString() {
		return RomanNumeral.of(getRank() + 1);
	}
	
	public Dinosaur setRank(int rank) {
		int boost = DinoMath.getRankBoost(rank) - DinoMath.getRankBoost(getRank());
		healthboost += boost;
		attackboost += boost;
		defenseboost += boost;
		this.rank = rank;
		return this;
	}
	
	public Dinosaur addBoost(int boost) {
		healthboost += boost;
		attackboost += boost;
		defenseboost += boost;
		return this;
	}

	private Dinosaur lowerAttack(int boost) {
		attackboost = Math.max(Constants.MIN_BOOST, attackboost - boost);
		return this;
	}

	private Dinosaur lowerDefense(int boost) {
		defenseboost = Math.max(Constants.MIN_BOOST, defenseboost - boost);
		return this;
	}
	
	public Dinosaur setItem(Item item) {
		this.item = item;
		applyHeldItem();
		return this;
	}

	public boolean hasItem() {
		return item != null && item.getId() != 0;
	}
	
	public Item getItem() {
		return item;
	}
	
	public boolean hasRune() {
		return rune != null && rune.getId() != 0;
	}
	
	public Rune getRune() {
		return rune;
	}
	
	public int getWins() {
		return wins;
	}
	
	public int getLosses() {
		return losses;
	}
	
	public String getEffectiveName() {
		return getEffectiveName(true);
	}

	public String getEffectiveName(boolean zalgo) {
		if (getForm() == DinosaurForm.Accursed.getId() && zalgo) {
			if (nick == null) return Zalgo.title(dinoname);
			return Zalgo.title(String.format("\"%s\" the %s", nick, dinoname));
		} else {
			if (nick == null) return dinoname;
			return String.format("\"%s\" the %s", nick, dinoname);
		}
	}
	
	public boolean isAlive() {
		return getCurrentHealth() > 0L;
	}
	
	public void heal() {
		this.damage = 0;
	}

	public long getDamage() {
		return damage;
	}
	
	public void damage(long dmg) {
		this.damage += dmg;
	}
	
	public boolean isTradable() {
		return getRp() > 0;
	}

	public ArrayList<BattleAttack> getAttacks() {
		return attacks;
	}

	public ArrayList<BattleAttack> getDefenses() {
		return defenses;
	}

	public boolean addEffect(StatusEffect effect) {
		if (hasEffect(effect)) {
			StatusAilment ailment = getAilment(effect);
			if (ailment.levelUp()) {
				applyEffect(effect);
				return true;
			} else {
				return true;
			}
		} else {
			ailments.add(new StatusAilment(effect, 1));
			applyEffect(effect);
			return true;
		}
	}

	private void applyEffect(StatusEffect effect) {
		switch (effect) {
			case ScareAttack:
				lowerAttack(Constants.SCARE_BOOST);
				break;
			case ScareDefense:
				lowerDefense(Constants.SCARE_BOOST);
				break;
			case Terror:
				lowerAttack(Constants.TERROR_BOOST + attackboost / 2);
				lowerDefense(Constants.TERROR_BOOST + defenseboost / 2);
				break;
		}
	}

	public boolean hasEffect(StatusEffect effect) {
		for (StatusAilment ailment : ailments) {
			if (ailment.getEffect() == effect) {
				return true;
			}
		}
		return false;
	}

	public StatusAilment getAilment(StatusEffect effect) {
		for (StatusAilment ailment : ailments) {
			if (ailment.getEffect() == effect) {
				return ailment;
			}
		}
		return null;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		
		// ID
		sb.append("#");
		sb.append(getId());
		
		// Level
		if (xp >= 0) {
			sb.append(" Level ");
			sb.append(Util.formatNumber(getLevel()));
		}
		
		// Rank
		if (rank > 0) {
			sb.append(" Rank ");
			sb.append(getRankString());
		}
		
		// Dino Name
		sb.append(" ");
		sb.append(getEffectiveName(false));
		
		if (getDinosaurForm() == DinosaurForm.Accursed) return Zalgo.field(sb.toString());
		return sb.toString();
	}
	
	@Override
	public int compareTo(Dinosaur that) {
		if (this.player != that.player) return Long.compare(this.player, that.player);
		return this.getIdPair().compareTo(that.getIdPair());
	}
	
	@Override
	public boolean equals(Object that) {
		return that instanceof Dinosaur && this.equals((Dinosaur)that);
	}
	
	public boolean equals(Dinosaur that) {
		return this.compareTo(that) == 0;
	}
	
	public Dinosaur clone() {
		Dinosaur d = new Dinosaur();
		
		d.dex = dex;
		d.form = form;
		d.dinoname = dinoname;
		d.element = element;
		d.rarity = rarity;
		d.health = health;
		d.attack = attack;
		d.defense = defense;
		d.wiki = wiki;
		d.image = image;
		d.discovery = discovery;
		d.type = type;
		d.authors = authors;
		d.epoch = epoch;
		d.location = location;
		d.diet = diet;
		
		d.player = player;
		d.nick = nick;
		d.xp = xp;
		d.rp = rp;
		d.rank = rank;
		d.healthmult = healthmult;
		d.attackmult = attackmult;
		d.defensemult = defensemult;
		d.item = item;
		d.rune = rune == null ? null : rune.clone();
		d.wins = wins;
		d.losses = losses;
		d.attacks = new ArrayList<>();
		for (BattleAttack ba : attacks) d.attacks.add(ba);
		d.defenses = new ArrayList<>();
		for (BattleAttack ba : defenses) d.defenses.add(ba);
		
		return d;
	}

	public void addAttack(BattleAttack atk) {
		attacks.add(atk);
	}

	public boolean removeAttack(BattleAttack atk) {
		return attacks.remove(atk);
	}

	public void addDefense(BattleAttack def) {
		defenses.add(def);
	}

	public boolean removeDefense(BattleAttack def) {
		return defenses.remove(def);
	}

	private void applyHeldItem() {
		if (!hasItem()) return;

		if (item.hasTag(ItemTag.Pendant)) {
			if ((Integer.parseInt(item.getData()) & element.getId()) > 0) {
				addBoost(Constants.PENDANT_BOOST);
			}
		} else if (item.hasTag(ItemTag.Charm)) {
			if (item.getId() == ItemID.CharmOfAccuracy.getItemId()) {
				if (attacks.remove(BattleAttack.BaseAttack)) attacks.add(BattleAttack.AlwaysHitAttack);
				if (attacks.remove(BattleAttack.BaseAttack)) attacks.add(BattleAttack.AlwaysHitAttack);
			} else if (item.getId() == ItemID.CharmOfScaring.getItemId()) {
				for (int q = 0; q < 5; q++) attacks.add(BattleAttack.Scare);
			}
		}
	}
	
	////////////////////////////////////////////////////////////////////////////
	
	private static HashMap<Pair<Integer, Integer>, Dinosaur> dinosaurs = new HashMap<Pair<Integer, Integer>, Dinosaur>();
	
	public static void refresh() {
		dinosaurs.clear();
	}
	
	public static Dinosaur getDinosaur(long pid, Pair<Integer, Integer> dexform) {
		return getDinosaur(pid, dexform.getFirstValue(), dexform.getSecondValue());
	}
	
	public static Dinosaur getDinosaur(long pid, int dex, int form) {
		try (ResultSet res = JDBC.executeQuery("select * from captures where player = " + pid + " and dex = " + dex + " and form = " + form + ";")) {
			if (res.next()) {
				Dinosaur d = Dinosaur.getDinosaur(dex, form);
				d.player = res.getLong("player");
				d.nick = res.getString("nick");
				d.xp = res.getLong("xp");
				d.rp = res.getInt("rp");
				d.rank = res.getInt("rnk");
				d.healthmult = res.getInt("modhealth");
				d.attackmult = res.getInt("modattack");
				d.defensemult = res.getInt("moddefense");
				d.item = Item.getItem(new Pair<Integer, Long>(res.getInt("item"), res.getLong("itemdmg")));
				d.rune = Rune.getRune(res.getInt("rune"));
				d.wins = res.getInt("wins");
				d.losses = res.getInt("losses");
				
				int boost = DinoMath.getLevelBoost(d.getLevel());
				boost += DinoMath.getRankBoost(d.getRank());
				// Contest Dinos and Accursed Dinos do not get element boost or cursed
				if (d.getDinosaurForm() != DinosaurForm.Contest && d.getDinosaurForm() != DinosaurForm.Accursed) {
					boost += d.getPlayer().getElementBoost(d.getElement());
					if (d.getPlayer().isCursed()) {
						boost -= 50;
					}
				}
				d.healthboost = d.healthmult + boost;
				d.attackboost = d.attackmult + boost;
				d.defenseboost = d.defensemult + boost;
				d.applyHeldItem();
				
				return d;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	public static Dinosaur getDinosaur(DinoID dex, DinosaurForm form) {
		return getDinosaur(dex.getId(form));
	}

	public static Dinosaur getDinosaur(Pair<Integer, Integer> dexform) {
		return getDinosaur(dexform.getFirstValue(), dexform.getSecondValue());
	}
	
	public static Dinosaur getDinosaur(int dex, int form) {
		if (dinosaurs.containsKey(new Pair<Integer, Integer>(dex, form))) return dinosaurs.get(new Pair<Integer, Integer>(dex, form)).clone();
		
		try (ResultSet res = JDBC.executeQuery("select * from dinosaurs where dex = " + dex + " and form = " + form + ";")) {
			if (res.next()) {
				Dinosaur d = new Dinosaur();
				d.dex = res.getInt("dex");
				d.form = res.getInt("form");
				d.dinoname = res.getString("dinoname");
				d.element = Element.of(res.getInt("element"));
				d.rarity = Rarity.getRarity(res.getInt("rarity"));
				d.health = res.getLong("basehealth");
				d.attack = res.getLong("baseattack");
				d.defense = res.getLong("basedefense");
				d.wiki = res.getString("wiki");
				d.image = res.getString("image");
				d.discovery = res.getInt("discovery");
				d.type = res.getString("type");
				d.authors = res.getString("authors");
				d.epoch = res.getString("epoch");
				d.location = res.getString("location");
				d.diet = res.getString("diet");
				
				for (BattleAttack ba : d.getDinosaurForm().getAttacks()) d.attacks.add(ba);
				for (BattleAttack ba : d.getDinosaurForm().getDefenses()) d.defenses.add(ba);
				
				dinosaurs.put(new Pair<Integer, Integer>(dex, form), d);
				return d.clone();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	public static Dinosaur[] values() {
		List<Dinosaur> dinosaurs = new ArrayList<Dinosaur>();
		
		try (ResultSet res = JDBC.executeQuery("select * from dinosaurs;")) {
			while (res.next()) {
				dinosaurs.add(Dinosaur.getDinosaur(res.getInt("dex"), res.getInt("form")));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return dinosaurs.toArray(new Dinosaur[0]);
	}
}
