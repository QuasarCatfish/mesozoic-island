package com.quas.mesozoicisland.objects;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.TreeMap;

import com.quas.mesozoicisland.util.Constants;

public class Leaderboard {

	private TreeMap<Long, List<String>> entries;
	private String regex;
	public Leaderboard(String regex) {
		this.entries = new TreeMap<Long, List<String>>((a, b) -> -Long.compare(a, b));
		this.regex = regex;
	}
	
	public void addEntry(long value, Object...objects) {
		if (!entries.containsKey(value)) entries.put(value, new ArrayList<String>());
		entries.get(value).add(String.format(regex, objects));
	}
	
	public List<String> getLeaderboard() {
		if (entries.keySet().isEmpty()) return Arrays.asList("This leaderboard is empty.");
		
		List<String> print = new ArrayList<String>();
		int disp = 0, real = 0;
		long last = entries.firstKey() + 1;
		for (long key : entries.keySet()) {
			if (key <= 0L) break;
			if (key < last) disp = real + 1;
			if (disp > Constants.MAX_LEADERBOARD_LENGTH) break;
			
			final int tdisp = disp;
			real += entries.get(key).size();
			entries.get(key).forEach(a -> print.add(tdisp + ") " + a));
		}
		
		if (print.isEmpty()) return Arrays.asList("This leaderboard is empty.");
		return print;
	}
}
