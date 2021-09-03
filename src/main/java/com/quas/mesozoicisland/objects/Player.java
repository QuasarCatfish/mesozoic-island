package com.quas.mesozoicisland.objects;

import java.awt.Color;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.TreeMap;

import com.quas.mesozoicisland.JDBC;
import com.quas.mesozoicisland.enums.AccessLevel;
import com.quas.mesozoicisland.enums.DinosaurForm;
import com.quas.mesozoicisland.enums.ItemID;
import com.quas.mesozoicisland.enums.Stat;
import com.quas.mesozoicisland.util.Constants;
import com.quas.mesozoicisland.util.DinoMath;
import com.quas.mesozoicisland.util.Pair;
import com.quas.mesozoicisland.util.Util;

public class Player {

	private long pid;
	private String rawname;
	private String name;
	private String title;
	private boolean invertedtitle;
	private long daily;
	private int dailystreak;
	private int birthday;
	private Color color;
	private String starter;
	private String latest;
	private String contest;
	private String selected;
	private String state;
	private String join;
	private long xp;
	private long omegaxp;
	private AccessLevel access;
	private Element elemain;
	private Element elesub;
	private boolean inbattle;
	private long fragrancexp;
	private long fragrancebattle;
	private long fragrancemoney;
	private long fragranceegg;
	private boolean muted;
	private boolean cursed;
	private long santa;
	private String eventstate;
	private boolean coopbanned;
	private int futureDinoId;
	private int futureDinoState;
	
	private Player() {}
	
	public String getId() {
		return Long.toString(pid);
	}
	
	public long getIdLong() {
		return pid;
	}
	
	public String getAsMention() {
		return String.format("<@%d>", pid);
	}
	
	public String getRawName() {
		return rawname;
	}
	
	public String getName() {
		return name;
	}
	
	public String getTitle() {
		return title;
	}
	
	public boolean hasInvertedTitle() {
		return invertedtitle;
	}
	
	public long getDaily() {
		return daily;
	}
	
	public int getDailyStreak() {
		return dailystreak;
	}
	
	public int getBirthday() {
		return birthday;
	}
	
	public Color getColor() {
		return color;
	}
	
	public String getStarter() {
		return starter;
	}
	
	public String getLatest() {
		return latest;
	}

	public String getContest() {
		return contest;
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

	public boolean isMaxLevel() {
		return xp >= Constants.MAX_PLAYER_XP;
	}
	
	public long getOmegaXp() {
		return omegaxp;
	}

	public int getOmegaLevel() {
		return DinoMath.getOmegaLevel(omegaxp);
	}

	public long getOmegaXpMinusLevel() {
		return omegaxp - DinoMath.getOmegaXp(getOmegaLevel());
	}


	public boolean isOmega() {
		return omegaxp > 0 || isMaxLevel();
	}

	public String getSelected() {
		return selected;
	}
	
	public Dinosaur[] getSelectedDinosaurs() {
		if (selected == null) return null;
		String[] split = selected.split("\\s+");
		Dinosaur[] select = new Dinosaur[split.length];
		for (int q = 0; q < split.length; q++) {
			select[q] = Dinosaur.getDinosaur(getIdLong(), Util.getDexForm(split[q]));
		}
		return select;
	}
	
	public boolean hasState(String state) {
		return this.state.matches(state);
	}

	public AccessLevel getAccessLevel() {
		return access;
	}
	
	public int getElementBoost(Element element) {
		if (elemain.getId() < 0) return 0;
		if ((element.getId() & elemain.getId()) > 0) return Constants.MAIN_ELEMENT_BOOST;
		if (elesub.getId() < 0) return 0;
		if ((element.getId() & elesub.getId()) > 0) return Constants.SUB_ELEMENT_BOOST;
		return 0;
	}
	
	public Element getMainElement() {
		return elemain;
	}
	
	public Element getSubElement() {
		return elesub;
	}
	
	public String getJoinDate() {
		return join;
	}
	
	public boolean isInBattle() {
		return inbattle;
	}
	
	public long getFragranceXpTimer() {
		return fragrancexp;
	}
	
	public long getFragranceBattleTimer() {
		return fragrancebattle;
	}
	
	public long getFragranceMoneyTimer() {
		return fragrancemoney;
	}

	public long getFragranceEggTimer() {
		return fragranceegg;
	}
	
	public boolean isMuted() {
		return muted;
	}

	public boolean isCursed() {
		return cursed;
	}

	public boolean isSecretSanta() {
		return santa > 0;
	}

	public long getSecretSanta() {
		return santa;
	}

	public String getEventState() {
		return eventstate;
	}

	public boolean isBannedFromCoop() {
		return coopbanned;
	}

	public int getFutureDinoId() {
		return futureDinoId;
	}

	public int getFutureDinoState() {
		return futureDinoState;
	}
	
	public long getItemCount(Item item) {
		if (item == null) return 0L;
		return getItemCount(item.getIdDmg());
	}

	public long getItemCount(ItemID item) {
		if (item == null) return 0L;
		return getItemCount(item.getId());
	}

	public long getItemCount(Stat stat) {
		if (stat == null) return 0L;
		return getItemCount(stat.getId());
	}

	private long getItemCount(Pair<Integer, Long> item) {
		if (item == null) return 0L;
		
		try (ResultSet res = JDBC.executeQuery("select * from bags where player = %d and item = %d and dmg = %d and count > 0;", pid, item.getFirstValue(), item.getSecondValue())) {
			if (res.next()) {
				return res.getLong("count");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return 0L;
	}

	public TreeMap<Item, Long> getBag() {
		TreeMap<Item, Long> map = new TreeMap<Item, Long>();
		
		try (ResultSet res = JDBC.executeQuery("select * from bags where player = %d and count > 0;", pid)) {
			while (res.next()) {
				map.put(Item.getItem(new Pair<Integer, Long>(res.getInt("item"), res.getLong("dmg"))), res.getLong("count"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return map;
	}
	
	public Egg getEgg(int number) {
		try (ResultSet res = JDBC.executeQuery("select * from eggs where player = %d;", pid)) {
			while (res.next()) {
				if (res.getInt("incubator") == number) {
					return Egg.getEgg(res.getInt("eggid"));
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	public Egg[] getEggs() {
		ArrayList<Egg> eggs = new ArrayList<>();

		try (ResultSet res = JDBC.executeQuery("select * from eggs where player = %d;", pid)) {
			while (res.next()) {
				eggs.add(Egg.getEgg(res.getInt("eggid")));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return eggs.toArray(new Egg[0]);
	}
	
	public int getStandardEggCount() {
		try (ResultSet res = JDBC.executeQuery("select count(*) as count from eggs where player = %d and form != %d;", pid, DinosaurForm.Chaos.getId())) {
			if (res.next()) {
				return res.getInt("count");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return 0;
	}

	public int getChaosEggCount() {
		try (ResultSet res = JDBC.executeQuery("select count(*) as count from eggs where player = %d and form = %d;", pid, DinosaurForm.Chaos.getId())) {
			if (res.next()) {
				return res.getInt("count");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return 0;
	}
	
	////////////////////////////////////////////
	
	public int getDexCount(int form) {
		if (form == DinosaurForm.AllForms.getId()) {
			try (ResultSet res = JDBC.executeQuery("select count(*) as count from captures join dinosaurs on captures.dex = dinosaurs.dex and captures.form = dinosaurs.form where player = %d and captures.form >= 0 and rarity >= 0;", pid)) {
				if (res.next()) return res.getInt("count");
			} catch (SQLException e) {
				e.printStackTrace();
			}
		} else if (form == DinosaurForm.AnyForms.getId()) {
			try (ResultSet res = JDBC.executeQuery("select count(distinct dinosaurs.dex) as count from captures join dinosaurs on captures.dex = dinosaurs.dex and captures.form = dinosaurs.form where player = %d and captures.form >= 0 and rarity >= 0;", pid)) {
				if (res.next()) return res.getInt("count");
			} catch (SQLException e) {
				e.printStackTrace();
			}
		} else {
			try (ResultSet res = JDBC.executeQuery("select count(*) as count from captures where player = %d and form = %d;", pid, form)) {
				if (res.next()) return res.getInt("count");
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
		return 0;
	}
	
	public int getRuneCount() {
		try (ResultSet res = JDBC.executeQuery("select count(*) as count from runepouches where player = %s and rune > 0;", getId())) {
			if (res.next()) return res.getInt("count");
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return 0;
	}
	
	////////////////////////////////////////////

	@Override
	public int hashCode() {
		return Long.hashCode(pid);
	}

	////////////////////////////////////////////
	
	public static Player getPlayer(long pid) {
		try (ResultSet res = JDBC.executeQuery("select * from players where playerid = " + pid + ";")) {
			if (res.next()) {
				Player p = new Player();
				p.pid = res.getLong("playerid");
				p.rawname = res.getString("playername");
				p.title = res.getString("title");
				p.invertedtitle = res.getBoolean("invertedtitle");
				if (p.title == null) p.name = p.rawname;
				else if (p.invertedtitle) p.name = String.format("%s %s", p.rawname, p.title);
				else p.name = String.format("%s %s", p.title, p.rawname);
				p.daily = res.getLong("daily");
				p.dailystreak = res.getInt("dailystreak");
				p.birthday = res.getInt("birthday");
				p.color = new Color(res.getInt("color"));
				p.starter = res.getString("starter");
				p.latest = res.getString("latest");
				p.contest = res.getString("contest");
				p.selected = res.getString("selected");
				p.state = res.getString("gamestate");
				p.join = res.getString("joindate");
				p.xp = res.getLong("xp");
				p.omegaxp = res.getLong("omegaxp");
				p.access = AccessLevel.of(res.getInt("access"));
				p.elemain = Element.of(res.getInt("mainelement"));
				p.elesub = Element.of(res.getInt("subelement"));
				p.inbattle = res.getBoolean("inbattle");
				p.fragrancexp = res.getLong("fragrancexp");
				p.fragrancebattle = res.getLong("fragrancebattle");
				p.fragrancemoney = res.getLong("fragrancemoney");
				p.fragranceegg = res.getLong("fragranceegg");
				p.muted = res.getBoolean("muted");
				p.cursed = res.getBoolean("cursed");
				p.santa = res.getLong("santa");
				p.eventstate = res.getString("eventstate");
				p.coopbanned = res.getBoolean("coopbanned");
				p.futureDinoId = res.getInt("futuredino");
				p.futureDinoState = res.getInt("futuredinostate");
				return p;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	public static Player[] values() {
		ArrayList<Player> players = new ArrayList<Player>();
		
		try (ResultSet res = JDBC.executeQuery("select playerid from players;")) {
			while (res.next()) {
				players.add(getPlayer(res.getLong("playerid")));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return players.toArray(new Player[0]);
	}
}
