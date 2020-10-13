package com.quas.mesozoicisland.objects;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.quas.mesozoicisland.JDBC;

public class Rarity implements Comparable<Rarity> {

	private int id;
	private String name;
	private String symbol;
	private int dinocount;
	private int specialcount;
	
	public int getId() {
		return id;
	}
	
	public String getName() {
		return name;
	}
	
	@Override
	public String toString() {
		return getName();
	}
	
	public String getSymbol() {
		return symbol;
	}
	
	public int getDinoCount() {
		return dinocount;
	}
	
	public int getSpecialcount() {
		return specialcount;
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
