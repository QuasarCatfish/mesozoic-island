package com.quas.mesozoicisland.enums;

import java.awt.Color;
import java.util.ArrayList;

public enum EggColor {

	Black("Black", 0, 0, 0, false),
	AliceBlue("Alice Blue", 240, 248, 255),
	AntiqueWhite("Antique White", 250, 235, 215),
	Aqua("Aqua", 0, 255, 255),
	Aquamarine("Aquamarine", 127, 255, 212),
	Azure("Azure", 240, 255, 255),
	Beige("Beige", 245, 245, 220),
	Bisque("Bisque", 255, 228, 196),
	BlanchedAlmond("Blanched Almond", 255, 235, 205),
	Blue("Blue", 0, 0, 255),
	BlueViolet("Blue Violet", 138, 43, 226),
	Brown("Brown", 165, 42, 42),
	BurlyWood("Burly Wood", 222, 184, 135),
	CadetBlue("Cadet Blue", 95, 158, 160),
	Chartreuse("Chartreuse", 127, 255, 0),
	Chocolate("Chocolate", 210, 105, 30),
	Coral("Coral", 255, 127, 80),
	CornflowerBlue("Cornflower Blue", 100, 149, 237),
	Cornsilk("Cornsilk", 255, 248, 220),
	Crimson("Crimson", 220, 20, 60),
	Cyan("Cyan", 0, 255, 255),
	DarkBlue("Dark Blue", 0, 0, 139),
	DarkCyan("Dark Cyan", 0, 139, 139),
	DarkGoldenrod("Dark Goldenrod", 184, 134, 187),
	DarkGrey("Dark Grey", 169, 169, 169),
	DarkGreen("Dark Green", 0, 100, 0),
	DarkKhaki("Dark Khaki", 189, 183, 107),
	DarkMagenta("Dark Magenta", 139, 0, 139),
	DarkOliveGreen("Dark Olive Green", 85, 107, 47),
	DarkOrange("Dark Orange", 255, 140, 0),
	DarkOrchid("Dark Orchid", 153, 50, 204),
	DarkRed("Dark Red", 139, 0, 0),
	DarkSalmon("Dark Salmon", 233, 150, 122),
	DarkSeaGreen("Dark Sea Green", 143, 188, 139),
	DarkSlateBlue("Dark Slate Blue", 72, 61, 139),
	DarkSlateGrey("Dark Slate Grey", 47, 79, 79),
	DarkTurquoise("Dark Turquoise", 0, 206, 209),
	DarkViolet("Dark Violet", 148, 0, 211),
	DeepPink("Deep Pink", 255, 20, 147),
	DeepSkyBlue("Deep Sky Blue", 0, 191, 255),
	DimGrey("Dim Grey", 105, 105, 105),
	DodgerBlue("Dodger Blue", 30, 144, 255),
	Firebrick("Firebrick", 178, 34, 34),
	FloralWhite("Floral White", 255, 250, 240),
	ForestGreen("Forest Green", 34, 139, 34),
	Fuchsia("Fuchsia", 255, 0, 255),
	Gainsboro("Gainsboro", 220, 220, 220),
	GhostWhite("Ghost White", 248, 248, 255),
	Gold("Gold", 255, 215, 0),
	Goldenrod("Goldenrod", 218, 165, 32),
	Grey("Grey", 128, 128, 128),
	Green("Green", 0, 128, 0),
	GreenYellow("Green Yellow", 173, 255, 47),
	Honeydew("Honeydew", 240, 255, 240),
	HotPink("Hot Pink", 255, 105, 180),
	IndianRed("Indian Red", 205, 92, 92),
	Indigo("Indigo", 75, 0, 130),
	Ivory("Ivory", 255, 255, 240),
	Khaki("Khaki", 240, 230, 140),
	Lavender("Lavender", 230, 230, 250),
	LavenderBlush("Lavender Blush", 255, 240, 245),
	LawnGreen("Lawn Green", 124, 252, 0),
	LemonChiffon("Lemon Chiffon", 255, 250, 205),
	LightBlue("Light Blue", 173, 216, 230),
	LightCoral("Light Coral", 240, 128, 128),
	LightCyan("Light Cyan", 224, 255, 255),
	LightGoldenrodYellow("Light Goldenrod Yellow", 250, 250, 210),
	LightGrey("Light Grey", 211, 211, 211),
	LightGreen("Light Green", 144, 238, 144),
	LightPink("Light Pink", 255, 182, 193),
	LightSalmon("Light Salmon", 255, 160, 122),
	LightSeaGreen("Light Sea Green", 32, 178, 170),
	LightSkyBlue("Light Sky Blue", 135, 206, 250),
	LightSlateGrey("Light Slate Grey", 119, 136, 153),
	LightSteelBlue("Light Steel Blue", 176, 196, 222),
	LightYellow("Light Yellow", 255, 255, 224),
	Lime("Lime", 0, 255, 0),
	LimeGreen("Lime Green", 50, 205, 50),
	Linen("Linen", 250, 240, 230),
	Magenta("Magenta", 255, 0, 255),
	Maroon("Maroon", 128, 0, 0),
	MediumAquamarine("Medium Aquamarine", 102, 205, 170),
	MediumBlue("Medium Blue", 0, 0, 205),
	MediumOrchid("Medium Orchid", 186, 85, 211),
	MediumPurple("Medium Purple", 147, 112, 219),
	MediumSeaGreen("Medium Sea Green", 60, 179, 113),
	MediumSlateBlue("Medium Slate Blue", 123, 104, 238),
	MediumSpringGreen("Medium Spring Green", 0, 250, 154),
	MediumTurquoise("Medium Turquoise", 72, 209, 204),
	MediumVioletRed("Medium Violet Red", 199, 21, 133),
	MidnightBlue("Midnight Blue", 25, 25, 112),
	MintCream("Mint Cream", 245, 255, 250),
	MistyRose("Misty Rose", 255, 228, 225),
	Moccasin("Moccasin", 255, 228, 181),
	NavajoWhite("Navajo White", 255, 222, 173),
	Navy("Navy", 0, 0, 128),
	OldLace("Old Lace", 253, 245, 230),
	Olive("Olive", 128, 128, 0),
	OliveDrab("Olive Drab", 107, 142, 35),
	Orange("Orange", 255, 165, 0),
	OrangeRed("Orange Red", 255, 69, 0),
	Orchid("Orchid", 218, 112, 214),
	PaleGoldenrod("Pale Goldenrod", 238, 232, 170),
	PaleGreen("Pale Green", 152, 251, 152),
	PaleTurquoise("Pale Turquoise", 175, 238, 238),
	PaleVioletRed("Pale Violet Red", 219, 112, 147),
	PapayaWhip("Papaya Whip", 255, 239, 213),
	PeachPuff("Peach Puff", 255, 218, 185),
	Peru("Peru", 205, 133, 63),
	Pink("Pink", 255, 192, 203),
	Plum("Plum", 221, 160, 221),
	PowderBlue("Powder Blue", 176, 224, 230),
	Purple("Purple", 128, 0, 128),
	Red("Red", 255, 0, 0),
	RosyBrown("Rosy Brown", 188, 143, 143),
	RoyalBlue("Royal Blue", 65, 105, 225),
	SaddleBrown("Chocolate", 139, 69, 19),
	Salmon("Salmon", 250, 128, 114),
	SandyBrown("Sandy Brown", 244, 164, 96),
	SeaGreen("Sea Green", 46, 139, 87),
	SeaShell("Sea Shell", 255, 245, 238),
	Sienna("Sienna", 160, 82, 45),
	Silver("Silver", 192, 192, 192),
	SkyBlue("Sky Blue", 135, 206, 235),
	SlateBlue("Slate Blue", 106, 90, 205),
	SlateGrey("Slate Grey", 112, 128, 144),
	Snow("Snow", 255, 250, 250),
	SpringGreen("Spring Green", 0, 255, 127),
	SteelBlue("Steel Blue", 70, 130, 180),
	Tan("Tan", 210, 180, 140),
	Teal("Teal", 0, 128, 128),
	Thistle("Thistle", 216, 191, 216),
	Tomato("Tomato", 255, 99, 71),
	Turquoise("Turquoise", 64, 224, 208),
	Violet("Violet", 238, 130, 238),
	Wheat("Wheat", 245, 222, 179),
	White("White", 255, 255, 255, false),
	WhiteSmoke("White Smoke", 245, 245, 245),
	Yellow("Yellow", 255, 255, 0),
	YellowGreen("Yellow Green", 154, 205, 50);
	
	private static EggColor[] choosableColors;
	static {
		int count = 0;
		ArrayList<EggColor> list = new ArrayList<EggColor>();
		for (EggColor ec : values()) {
			if (ec.choosable) list.add(ec);
			ec.id = count++;
		}
		
		choosableColors = new EggColor[list.size()];
		for (int q = 0; q < list.size(); q++) {
			choosableColors[q] = list.get(q);
		}
	}
	
	private int id;
	private String name;
	private int red, green, blue;
	private boolean choosable;
	
	private EggColor(String name, int red, int green, int blue) {
		this(name, red, green, blue, true);
	}
	
	private EggColor(String name, int red, int green, int blue, boolean choosable) {
		this.name = name;
		this.red = red;
		this.green = green;
		this.blue = blue;
		this.choosable = choosable;
	}
	
	public int getId() {
		return id;
	}
	
	public int getRed() {
		return red;
	}
	
	public int getGreen() {
		return green;
	}
	
	public int getBlue() {
		return blue;
	}
	
	public int getRGB() {
		return new Color(red, green, blue).getRGB();
	}
	
	public Color toColor() {
		return new Color(red, green, blue);
	}
	
	public Color toColor(int alpha) {
		return new Color(red, green, blue, alpha);
	}
	
	public String getHex() {
		return String.format("%02X%02X%02X", red, green, blue);
	}
	
	@Override
	public String toString() {
		return name;
	}
	
	/////////////////////////////////////////////////
	
	public static EggColor of(int id) {
		for (EggColor ec : values()) {
			if (ec.id == id) {
				return ec;
			}
		}
		
		return Black;
	}
	
	public static EggColor[] getChoosableColors() {
		return choosableColors;
	}
}
