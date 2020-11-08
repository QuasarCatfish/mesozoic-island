package com.quas.mesozoicisland.enums;

import java.util.ArrayList;

public enum EggPattern {

	None("None", "", EggPatternTag.None),
	Star("Star", "star.png", EggPatternTag.None),
	Circle("Circles", "circle.png", EggPatternTag.None),
	TwoTone1("Two-Tone", "twotone1.png", EggPatternTag.TwoTone, false),
	TwoTone2("Two-Tone", "twotone2.png", EggPatternTag.TwoTone, false),
	TwoTone3("Two-Tone", "twotone3.png", EggPatternTag.TwoTone, false),
	TwoTone4("Two-Tone", "twotone4.png", EggPatternTag.TwoTone, false),
	Lace1("Lace", "lace1.png", EggPatternTag.None),
	Lace2("Lace", "lace2.png", EggPatternTag.None),
	Stripes1("Stripes", "stripes1.png", EggPatternTag.None),
	Stripes2("Stripes", "stripes2.png", EggPatternTag.None),
	Thanksgiving("Drumstick", "thanksgiving.png", EggPatternTag.None, false),
	;
	
	private static EggPattern[] choosablePatterns;
	static {
		int count = 0;
		ArrayList<EggPattern> list = new ArrayList<EggPattern>();
		for (EggPattern ec : values()) {
			if (ec.choosable) list.add(ec);
			ec.id = count++;
		}
		
		choosablePatterns = new EggPattern[list.size()];
		for (int q = 0; q < list.size(); q++) {
			choosablePatterns[q] = list.get(q);
		}
	}
	
	private int id;
	private String name, file;
	private EggPatternTag tag;
	private boolean choosable;
	
	private EggPattern(String name, String file, EggPatternTag tag) {
		this(name, file, tag, true);
	}
	
	private EggPattern(String name, String file, EggPatternTag tag, boolean choosable) {
		this.name = name;
		this.file = file;
		this.tag = tag;
		this.choosable = choosable;
	}
	
	public int getId() {
		return id;
	}
	
	public String getFile() {
		return file;
	}
	
	public EggPatternTag getTag() {
		return tag;
	}
	
	@Override
	public String toString() {
		return name;
	}
	
	/////////////////////////////////////////////////
	
	public static EggPattern of(int id) {
		for (EggPattern ec : values()) {
			if (ec.id == id) {
				return ec;
			}
		}
		
		return None;
	}
	
	public static EggPattern[] getChoosablePatterns() {
		return choosablePatterns;
	}
	
	public static enum EggPatternTag {
		None, TwoTone, Swirling;
	}
}
