package com.quas.mesozoicisland.objects;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

import com.quas.mesozoicisland.JDBC;
import com.quas.mesozoicisland.enums.DiscordEmote;
import com.quas.mesozoicisland.enums.ItemCategory;
import com.quas.mesozoicisland.enums.ItemID;
import com.quas.mesozoicisland.enums.ItemTag;
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
	private DiscordEmote icon;
	private String data;
	private String tags;
	private boolean tradable;
	
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

	public boolean hasIcon() {
		return icon != null;
	}

	public DiscordEmote getIcon() {
		return icon;
	}
	
	public String getData() {
		return data;
	}

	public boolean hasTag(ItemTag tag) {
		if (tags == null) return false;
		return tags.contains(tag.getTag());
	}

	public boolean isTradable() {
		return tradable;
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

	public boolean isDiscovered() {
		try (ResultSet res = JDBC.executeQuery("select * from bags where item = %d and dmg = %d;", getId(), getDamage())) {
			return res.next();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return false;
	}
	
	/////////////////////////////////////////////////////
	
	private static TreeMap<Pair<Integer, Long>, Item> map = new TreeMap<Pair<Integer, Long>, Item>();
	
	public static void refresh() {
		map.clear();
	}
	
	public static Item of(String string) {
		while (string.charAt(0) == '0' && string.length() > 1) string = string.substring(1);

		for (Item item : values()) {
			if (item.name.toLowerCase().equalsIgnoreCase(string)) {
				return item;
			} else if (item.plural.toLowerCase().equalsIgnoreCase(string)) {
				return item;
			} else if (Integer.toString(item.id).equals(string)) {
				return item;
			}
		}

		return null;
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
				i.icon = DiscordEmote.getEmote(res.getLong("icon"));
				i.data = res.getString("data");
				i.tags = res.getString("tags");
				i.tradable = res.getBoolean("tradable");
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

	public static Item[] getItemsWithTag(ItemTag tag) {
		ArrayList<Item> items = new ArrayList<Item>();
		for (Item item : values()) {
			if (item.hasTag(tag)) {
				items.add(item);
			}
		}
		
		return items.toArray(new Item[0]);
	}
}
