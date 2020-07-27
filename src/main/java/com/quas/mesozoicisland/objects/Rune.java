package com.quas.mesozoicisland.objects;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.quas.mesozoicisland.JDBC;
import com.quas.mesozoicisland.enums.RuneType;
import com.quas.mesozoicisland.util.RomanNumeral;

public class Rune implements Comparable<Rune> {

	private int id;
	private String name;
	private Element element;
	private Rarity rarity;
	private String[] dinos;
	private RuneType type;
	private int power;
	private String image;
	
	private long player = -1;
	private int rp = -1;
	private int rank = -1;
	private String equipped;
	
	private Rune() {}
	
	public int getId() {
		return id;
	}
	
	public String getName() {
		return name;
	}
	
	public Element getElement() {
		return element;
	}
	
	public Rarity getRarity() {
		return rarity;
	}
	
	public String[] getEquippableDinosaurs() {
		return dinos;
	}
	
	public RuneType getType() {
		return type;
	}
	
	public int getPower() {
		return power;
	}
	
	public String getEffect() {
		switch (type) {
		case DealDamage:
			return String.format("+%,d Damage", power);
		default:
			return "No Effect";
		}
	}
	
	public String getImageLink() {
		return image;
	}
	
	public long getPlayerId() {
		return player;
	}
	
	public Player getPlayer() {
		return Player.getPlayer(player);
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
	
	public boolean isTradeable() {
		return rp > 0;
	}
	
	public String getRankString() {
		return RomanNumeral.of(getRank());
	}
	
	public String getNextRankString() {
		return RomanNumeral.of(getRank() + 1);
	}
	
	public boolean isEquipped() {
		return equipped != null;
	}
	
	public String getEquipped() {
		return equipped;
	}
	
	public boolean canBeUsedBy(Dinosaur d) {
		return false;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		
		// Dex
		if (getId() < 0) sb.append("#R???");
		else sb.append(String.format("#R%03d", getId()));
		
		// Rank
		if (getRank() > 0) {
			sb.append(" Rank ");
			sb.append(getRankString());
		}
		
		// Card Name
		sb.append(" ");
		sb.append(getName());
		
		return sb.toString();
	}
	
	@Override
	public int compareTo(Rune that) {
		if (this.player != that.player) return Long.compare(this.player, that.player);
		return Integer.compare(this.id, that.id);
	}
	
	@Override
	public boolean equals(Object that) {
		return that instanceof Rune && this.equals((Rune)that);
	}
	
	public boolean equals(Rune that) {
		return this.compareTo(that) == 0;
	}
	
	public Rune clone() {
		Rune r = new Rune();
		
		r.id = id;
		r.name = name;
		r.element = element;
		r.rarity = rarity;
		r.dinos = dinos;
		r.type = type;
		r.power = power;
		r.image = image;
		
		r.player = player;
		r.rp = rp;
		r.rank = rank;
		r.equipped = equipped;
		
		return r;
	}
	
	/////////////////////////////////////////////////////////////////////////////////
	
	private static HashMap<Integer, Rune> runes = new HashMap<Integer, Rune>();
	
	public static void refresh() {
		runes.clear();
	}
	
	public static Rune getRune(long pid, int id) {
		try (ResultSet res = JDBC.executeQuery("select * from runepouches where player = " + pid + " and rune = " + id + ";")) {
			if (res.next()) {
				Rune r = Rune.getRune(id);
				r.player = res.getLong("player");
				r.rp = res.getInt("rp");
				r.rank = res.getInt("rnk");
				r.equipped = res.getString("equipped");
				return r;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	public static Rune getRune(int id) {
		if (runes.containsKey(id)) return runes.get(id).clone();
		
		try (ResultSet res = JDBC.executeQuery("select * from runes where runeid = " + id + ";")) {
			if (res.next()) {
				Rune r = new Rune();
				r.id = res.getInt("runeid");
				r.name = res.getString("name");
				r.element = Element.of(res.getInt("element"));
				r.rarity = Rarity.getRarity(res.getInt("rarity"));
				r.dinos = res.getString("dinos") == null ? null : res.getString("dinos").split("\\s+");
				r.type = RuneType.of(res.getInt("runetype"));
				r.power = res.getInt("runepower");
				r.image = res.getString("image");
				
				runes.put(id, r);
				return r.clone();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	public static Rune[] values() {
		List<Rune> runes = new ArrayList<Rune>();
		
		try (ResultSet res = JDBC.executeQuery("select * from runes;")) {
			while (res.next()) {
				runes.add(getRune(res.getInt("runeid")));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return runes.toArray(new Rune[0]);
	}
}
