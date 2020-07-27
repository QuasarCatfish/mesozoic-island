package com.quas.mesozoicisland.util;

public class RomanNumeral {

	public static String of(long number) {
		if (number <= 0) return "N";
		
		StringBuilder ret = new StringBuilder();
		for (RN rn : RN.values()) {
			while (number >= rn.value) {
				number -= rn.value;
				ret.append(rn.string);
			}
			if (number <= 0) break;
		}
		
		return ret.toString();
	}
	
	private enum RN {
		RN1000("M", 1000),
		RN900("CM", 900),
		RN500("D", 500),
		RN400("CD", 400),
		RN100("C", 100),
		RN90("XC", 90),
		RN50("L", 50),
		RN40("XL", 40),
		RN10("X", 10),
		RN9("IX", 9),
		RN5("V", 5),
		RN4("IV", 4),
		RN1("I", 1);
		
		private String string;
		private long value;
		private RN(String string, long value) {
			this.string = string;
			this.value = value;
		}
	}
}
