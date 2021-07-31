package com.quas.mesozoicisland.objects;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.TreeMap;

import com.quas.mesozoicisland.JDBC;
import com.quas.mesozoicisland.enums.DiscordEmote;
import com.quas.mesozoicisland.util.Pair;

public class Element implements Comparable<Element> {

	private int id;
	private String name;
	private long role;
	private long guild;
	private boolean visible;
	private boolean hasguild;
	private boolean hasemblem;
	private DiscordEmote emote;

	private Element() {}
	
	private Element(int id) {
		this.id = id;
	}

	public int getId() {
		return id;
	}
	
	@Override
	public String toString() {
		return name;
	}
	
	public long getRole() {
		return role;
	}
	
	public long getGuild() {
		return guild;
	}
	
	public boolean isVisible() {
		return visible;
	}

	public boolean isGuild() {
		return hasguild;
	}

	public boolean isEmblem() {
		return hasemblem;
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
	public int compareTo(Element that) {
		return Integer.compare(this.id, that.id);
	}
	
	@Override
	public boolean equals(Object that) {
		return that instanceof Element && this.equals((Element)that);
	}
	
	public boolean equals(Element that) {
		return this.compareTo(that) == 0;
	}
	
	public double getEffectivenessAgainst(Element that) {
		return Element.getEffectiveness(this, that);
	}
	
	////////////////////////////////////////////
	
	private static HashMap<Integer, Element> elements = new HashMap<Integer, Element>();
	public static final Element NEUTRAL = Element.of(1);
	public static final Element FIRE = Element.of(2);
	public static final Element LIGHTNING = Element.of(4);
	public static final Element LEAF = Element.of(8);
	public static final Element EARTH = Element.of(16);
	public static final Element ICE = Element.of(32);
	public static final Element METAL = Element.of(64);
	public static final Element WATER = Element.of(128);
	public static final Element AIR = Element.of(256);
	public static final Element CHAOS = Element.of(1024);
	
	public static final Element ALL = new Element(2047);

	public static void refresh() {
		elements.clear();
		readEffectiveness = false;
		effectiveness.clear();
	}
	
	public static Element of(int id) {
		if (elements.containsKey(id)) return elements.get(id);
		
		try (ResultSet res = JDBC.executeQuery("select * from elements where elementid = " + id + ";")) {
			if (res.next()) {
				Element e = new Element();
				e.id = res.getInt("elementid");
				e.name = res.getString("elementname");
				e.role = res.getLong("elementrole");
				e.guild = res.getLong("guild");
				e.visible = res.getBoolean("visible");
				e.hasguild = res.getBoolean("isguild");
				e.hasemblem = res.getBoolean("isemblem");
				e.emote = DiscordEmote.getEmote(res.getLong("emote"));
				
				elements.put(id, e);
				return e;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return null;
	}

	public static Element of(String name) {
		for (Element ele : values()) {
			if (ele.name.equalsIgnoreCase(name)) {
				return ele;
			}
		}
		return null;
	}
	
	public static Element[] values() {
		List<Element> elements = new ArrayList<Element>();
		
		try (ResultSet res = JDBC.executeQuery("select * from elements;")) {
			while (res.next()) {
				elements.add(of(res.getInt("elementid")));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return elements.toArray(new Element[0]);
	}
	
	private static boolean readEffectiveness = false;
	private static TreeMap<Pair<Element, Element>, Float> effectiveness = new TreeMap<Pair<Element, Element>, Float>();
	
	public static double getEffectiveness(Element attack, Element defend) {
		if (!readEffectiveness) {
			readElements();
		}

		double mult = 1d;
		for (int amask = 1; amask <= attack.getId(); amask <<= 1) {
			if ((amask & attack.getId()) == 0) continue;
			for (int dmask = 1; dmask <= defend.getId(); dmask <<= 1) {
				if ((dmask & defend.getId()) == 0) continue;
				mult *= effectiveness.getOrDefault(new Pair<Element, Element>(of(amask), of(dmask)), 1f);
			}
		}
		
		return mult;
	}
	
	private static void readElements() {
		try (ResultSet res = JDBC.executeQuery("select * from typechart;")) {
			while (res.next()) {
				Element attack = Element.of(res.getInt("attack"));
				Element defend = Element.of(res.getInt("defend"));
				float mult = res.getFloat("effectiveness");
				
				System.out.printf("Adding effectiveness: %s deals x%1.1f damage on %s.\n", attack.name, mult, defend.name);
				effectiveness.put(new Pair<Element, Element>(attack, defend), mult);
			}
			
			readEffectiveness = true;
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
