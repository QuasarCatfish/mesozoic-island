package com.quas.mesozoicisland.objects;

import java.util.ArrayList;

import com.quas.mesozoicisland.util.Pair;

public class TradeManager {

	private static ArrayList<Pair<Dinosaur, Dinosaur>> dinosaurs = new ArrayList<Pair<Dinosaur, Dinosaur>>();
	private static ArrayList<Pair<Rune, Rune>> runes = new ArrayList<Pair<Rune, Rune>>();
	private static ArrayList<Pair<Egg, Egg>> eggs = new ArrayList<Pair<Egg, Egg>>();
	
	// Dinosaur
	public static boolean isDinosaurTradeDuplicate(Pair<Dinosaur, Dinosaur> trade) {
		return isTradeDuplicate(dinosaurs, trade);
	}
	
	public static boolean doesDinosaurTradeExist(Pair<Dinosaur, Dinosaur> trade) {
		return doesTradeExist(dinosaurs, trade);
	}
	
	public static void addDinosaurTrade(Pair<Dinosaur, Dinosaur> trade) {
		addTrade(dinosaurs, trade);
	}
	
	// Rune
	public static boolean isRuneTradeDuplicate(Pair<Rune, Rune> trade) {
		return isTradeDuplicate(runes, trade);
	}
	
	public static boolean doesRuneTradeExist(Pair<Rune, Rune> trade) {
		return doesTradeExist(runes, trade);
	}
	
	public static void addRuneTrade(Pair<Rune, Rune> trade) {
		addTrade(runes, trade);
	}
	
	// Egg
	public static boolean isEggTradeDuplicate(Pair<Egg, Egg> trade) {
		return isTradeDuplicate(eggs, trade);
	}
	
	public static boolean doesEggTradeExist(Pair<Egg, Egg> trade) {
		return doesTradeExist(eggs, trade);
	}
	
	public static void addEggTrade(Pair<Egg, Egg> trade) {
		addTrade(eggs, trade);
	}
	
	private static <T extends Comparable<T>> boolean isTradeDuplicate(ArrayList<Pair<T, T>> list, Pair<T, T> trade) {
		for (Pair<T, T> t : list) {
			if (trade.getFirstValue().equals(t.getFirstValue()) && trade.getSecondValue().equals(t.getSecondValue())) {
				return true;
			}
		}
		return false;
	}
	
	private static <T extends Comparable<T>> boolean doesTradeExist(ArrayList<Pair<T, T>> list, Pair<T, T> trade) {
		for (Pair<T, T> t : list) {
			if (trade.getFirstValue().equals(t.getSecondValue()) && trade.getSecondValue().equals(t.getFirstValue())) {
				return true;
			}
		}
		return false;
	}
	
	private static <T extends Comparable<T>> void addTrade(ArrayList<Pair<T, T>> list, Pair<T, T> trade) {
		if (doesTradeExist(list, trade)) {
			for (int q = 0; q < list.size(); q++) {
				boolean flag = false;
				Pair<T, T> t = list.get(q);
				if (trade.getFirstValue().equals(t.getFirstValue())) flag = true;
				if (trade.getFirstValue().equals(t.getSecondValue())) flag = true;
				if (trade.getSecondValue().equals(t.getFirstValue())) flag = true;
				if (trade.getSecondValue().equals(t.getSecondValue())) flag = true;
				
				if (flag) list.remove(q--);
			}
		} else {
			list.add(trade);
		}
	}
}
