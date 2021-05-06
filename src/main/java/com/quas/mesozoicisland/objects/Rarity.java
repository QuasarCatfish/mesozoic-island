package com.quas.mesozoicisland.objects;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.quas.mesozoicisland.JDBC;
import com.quas.mesozoicisland.enums.DiscordEmote;

public class Rarity implements Comparable<Rarity> {

	private int id;
	private String name;
	private String symbol;
	private int dinocount;
	private int specialcount;
	private DiscordEmote emote;
	
	public int getId() {
		return id;
	}

	@Override
	public String toString() {
		return name;
	}
	
	public String getSymbol() {
		return symbol;
	}
	
	public int getDinoCount() {
		return dinocount;
	}
	
	public int getSpecialCount() {
		return specialcount;
	}

	public DiscordEmote getEmote() {
		return emote;
	}

	public String getAsBrackets() {
		return emote == null ? String.format("[%s]", name) : emote.getEmote().getAsMention();
	}

	public String getAsString() {
		return emote == null ? name : String.format("%s %s", name, emote.getEmote().getAsMention());
	}
	
	@Override
	public int compareTo(Rarity that) {
		return Integer.compare(this.id, that.id);
	}
	
	@Override
	public boolean equals(Object that) {
		return that instanceof Rarity && this.equals((Rarity)that);
	}
	
	public boolean equals(Rarity that) {
		return this.compareTo(that) == 0;
	}
	
	////////////////////////////////////////////////////
	
	private static HashMap<Integer, Rarity> rarities = new HashMap<Integer, Rarity>();
	
	public static void refresh() {
		rarities.clear();
	}
	
	public static Rarity getRarity(int id) {
		if (rarities.containsKey(id)) return rarities.get(id);
		
		try (ResultSet res = JDBC.executeQuery("select * from rarities where rarityid = " + id + ";")) {
			if (res.next()) {
				Rarity r = new Rarity();
				r.id = res.getInt("rarityid");
				r.name = res.getString("rarityname");
				r.symbol = res.getString("rarityshort");
				r.dinocount = res.getInt("spawndino");
				r.specialcount = res.getInt("spawnspecial");
				r.emote = DiscordEmote.getEmote(res.getLong("emote"));

				rarities.put(id, r);
				return r;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	public static Rarity[] values() {
		List<Rarity> rarities = new ArrayList<Rarity>();
		
		try (ResultSet res = JDBC.executeQuery("select * from rarities;")) {
			while (res.next()) {
				rarities.add(getRarity(res.getInt("rarityid")));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return rarities.toArray(new Rarity[0]);
	}
}
