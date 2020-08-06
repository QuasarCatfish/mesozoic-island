package com.quas.mesozoicisland.objects;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

import com.quas.mesozoicisland.JDBC;
import com.quas.mesozoicisland.enums.ItemCategory;
import com.quas.mesozoicisland.enums.ItemID;
import com.quas.mesozoicisland.enums.ItemType;
import com.quas.mesozoicisland.enums.Stat;
import com.quas.mesozoicisland.util.Pair;

public class Item implements Comparable<Item> {

	private int id;
	private long dmg;
	private String name;
	private String plural;
	private String desc;
	private int type;
	private int cat;
	private String data;
	
	private Item() {}
	
	public int getId() {
		return id;
	}
	
	public long getDamage() {
		return dmg;
	}
	
	public Pair<Integer, Long> getIdDmg() {
		return new Pair<Integer, Long>(id, dmg);
	}
	
	public String getDescription() {
		return desc;
	}
	
	public ItemType getItemType() {
		return ItemType.of(type);
	}
	
	public ItemCategory getItemCategory() {
		return ItemCategory.of(cat);
	}
	
	public String getData() {
		return data;
	}
	
	public String toString(long count) {
		return count == 1 || count == -1 ? name : plural;
	}
	
	@Override
	public String toString() {
		return name;
	}
	
	@Override
	public int compareTo(Item that) {
		int comp = Integer.compare(this.id, that.id);
		if (comp != 0) return comp;
		comp = Long.compare(this.dmg, that.dmg);
		if (comp != 0) return comp;
		return 0;
	}
	
	@Override
	public boolean equals(Object that) {
		return that instanceof Item && this.equals((Item)that);
	}
	
	public boolean equals(Item that) {
		return this.compareTo(that) == 0;
	}
	
	/////////////////////////////////////////////////////
	
	private static TreeMap<Pair<Integer, Long>, Item> map = new TreeMap<Pair<Integer, Long>, Item>();
	
	public static void refresh() {
		map.clear();
	}
	
	public static Item getItem(ItemID itemid) {
		return getItem(itemid.getId());
	}

	public static Item getItem(Stat stat) {
		return getItem(stat.getId());
	}

	public static Item getItem(Pair<Integer, Long> itemid) {
		if (map.containsKey(itemid)) return map.get(itemid);
		
		try (ResultSet res = JDBC.executeQuery("select * from items where itemid = %d and itemdmg = %d;", itemid.getFirstValue(), itemid.getSecondValue())) {
			if (res.next()) {
				Item i = new Item();
				i.id = res.getInt("itemid");
				i.dmg = res.getLong("itemdmg");
				i.name = res.getString("itemname");
				i.plural = res.getString("itemnameplural");
				i.desc = res.getString("itemdesc");
				i.type = res.getInt("itemtype");
				i.cat = res.getInt("itemcat");
				i.data = res.getString("data");
				map.put(itemid, i);
				return i;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	public static Item[] getItems(int itemid) {
		List<Item> items = new ArrayList<Item>();
		
		try (ResultSet res = JDBC.executeQuery("select * from items where itemid = %d;", itemid)) {
			while (res.next()) {
				items.add(Item.getItem(new Pair<Integer, Long>(res.getInt("itemid"), res.getLong("itemdmg"))));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return items.toArray(new Item[0]);
	}
	
	public static Item[] values() {
		List<Item> items = new ArrayList<Item>();
		
		try (ResultSet res = JDBC.executeQuery("select * from items;")) {
			while (res.next()) {
				items.add(Item.getItem(new Pair<Integer, Long>(res.getInt("itemid"), res.getLong("itemdmg"))));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return items.toArray(new Item[0]);
	}
}
