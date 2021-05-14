package com.quas.mesozoicisland.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.concurrent.TimeUnit;

import com.quas.mesozoicisland.JDBC;
import com.quas.mesozoicisland.MesozoicIsland;
import com.quas.mesozoicisland.enums.DinosaurForm;
import com.quas.mesozoicisland.enums.DiscordRole;
import com.quas.mesozoicisland.objects.Dinosaur;
import com.quas.mesozoicisland.objects.Item;
import com.quas.mesozoicisland.objects.Player;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.MessageHistory;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.requests.RestAction;

public class Util {

	@SafeVarargs
	public static <T> T[] arr(T...arr) {
		return arr;
	}

	public static char getRandomElement(char[] arr) {
		return arr[MesozoicRandom.nextInt(arr.length)];
	}

	public static <T> T getRandomElement(T[] arr) {
		return arr[MesozoicRandom.nextInt(arr.length)];
	}

	public static <T> T getRandomElement(List<T> list) {
		return list.get(MesozoicRandom.nextInt(list.size()));
	}

	public static <T> ArrayList<T> union(T[] a, T[] b) {
		ArrayList<T> arr = new ArrayList<T>();
		for (T t : a) arr.add(t);
		for (T t : b) if (!arr.contains(t)) arr.add(t);
		return arr;
	}

	public static <T extends Comparable<T>> boolean contains(T[] arr, T obj) {
		for (T t : arr) {
			if (t.compareTo(obj) == 0) {
				return true;
			}
		}
		return false;
	}
	
	@SafeVarargs
	public static <T> List<T> list(T...arr) {
		return Arrays.asList(arr);
	}
	
	public static <T> String join(T[] arr, String join, int start, int end) {
		StringBuilder sb = new StringBuilder();
		for (int q = Math.max(0, start); q < Math.min(arr.length, end) - 1; q++) {
			sb.append(arr[q].toString());
			sb.append(join);
		}
		if (end - 1 >= 0 && end - 1 < arr.length) sb.append(arr[end - 1]);
		return sb.toString();
	}
	
	public static String join(Collection<?> arr, String join, int start, int end) {
		StringBuilder sb = new StringBuilder();
		int q = -1;
		for (Object var : arr) {
			q++;
			if (q < start || q >= end) continue;
			sb.append(var.toString());
			sb.append(join);
		}
		if (sb.length() >= join.length()) sb.setLength(sb.length() - join.length());
		return sb.toString();
	}
	
	public static <T extends Comparable<T>> T[] removeDuplciates(T[] arr) {
		TreeSet<T> set = new TreeSet<T>();
		for (int q = 0; q < arr.length; q++) {
			if (arr[q] == null) continue;
			if (set.contains(arr[q])) {
				arr[q] = null;
			} else {
				set.add(arr[q]);
			}
		}
		
		ArrayList<T> list = new ArrayList<T>();
		for (T t : arr) {
			if (t != null) {
				list.add(t);
			}
		}
		
		for (int q = 0; q < arr.length; q++) {
			arr[q] = q < list.size() ? list.get(q) : null;
		}
		
		return Arrays.copyOf(arr, list.size());
	}
	
	public static <T> String mult(T str, int count) {
		if (count <= 0) return "";
		if (count == 1) return str.toString();
		if (count % 2 == 0) {
			StringBuilder sb = new StringBuilder();
			sb.append(str.toString());
			sb.append(str.toString());
			return mult(sb.toString(), count / 2);
		} else {
			StringBuilder sb = new StringBuilder();
			sb.append(str.toString());
			sb.append(mult(str, count - 1));
			return sb.toString();
		}
	}

	public static Item getWeightedItem(Player p, Item[] items) {
		TreeMap<Item, Long> bag = p.getBag();
		long max = 0;

		for (Item item : items) {
			long count = bag.getOrDefault(item, 0L);

			try (ResultSet res = JDBC.executeQuery("select count(*) as count from captures where player = %d and item = %d and itemdmg = %d;", p.getIdLong(), item.getId(), item.getDamage())) {
				if (res.next()) {
					count += res.getInt("count");
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}

			if (count > max) max = count;
		}

		ArrayList<Item> choice = new ArrayList<Item>();
		for (Item item : items) {
			long count = bag.getOrDefault(item, 0L);
			for (int q = 0; q < max - count + 1; q++) {
				choice.add(item);
			}
		}

		return Util.getRandomElement(choice);
	}
	
	public static <T> T complete(RestAction<T> action) {
		while (true) {
			try {
				T t = action.complete();
				return t;
			} catch (Exception e) {
				System.out.println("Caught: " + e.toString());
			}
		}
	}
	
	public static List<Message> getMessages(MessageChannel channel) {
		List<Message> list = new ArrayList<Message>();
		MessageHistory mh = channel.getHistory();
		
		while (true) {
			List<Message> r = mh.retrievePast(100).complete();
			if (r == null || r.size() == 0) break;
			list.addAll(r);
		}
		
		return list;
	}
	
	public static String getAvatar(User user) {
		if (user.getAvatarUrl() != null) return user.getAvatarUrl();
		switch (Integer.parseInt(user.getDiscriminator()) % 5) {
		case 0:
			return "https://discordapp.com/assets/6debd47ed13483642cf09e832ed0bc1b.png";
		case 1:
			return "https://discordapp.com/assets/322c936a8c8be1b803cd94861bdfa868.png";
		case 2:
			return "https://discordapp.com/assets/dd4dbc0016779df1378e7812eabaa04d.png";
		case 3:
			return "https://discordapp.com/assets/0e291f67c9274a1abdddeb3fd919cbaa.png";
		case 4:
			return "https://discordapp.com/assets/1cbd08c76f8af6dddce02c5138971129.png";
		default:
			return "https://discordapp.com/assets/f8389ca1a741a115313bede9ac02e2c0.svg";
		}
	}
	
	/////////////////////////////////////////////////////////////
	
	public static String formatTeam(Dinosaur...team) {
		StringBuilder sb = new StringBuilder();

		for (int q = 0; q < team.length; q++) {
			if (team[q].getDinosaurForm() == DinosaurForm.Accursed) {
				sb.append(String.format("%,d) %s %s", q + 1, team[q], Zalgo.field(team[q].getElement().getAsBrackets())));
				
				if (team[q].hasItem()) {
					sb.append(" ");
					if (team[q].getItem().hasIcon()) {
						sb.append(team[q].getItem().getIcon().getEmote().getAsMention());
					} else {
						sb.append(Zalgo.field(String.format("[Holding: %s]", team[q].getItem())));
					}
				}

				if (team[q].hasRune()) {
					sb.append(" ");
					sb.append(Zalgo.field(String.format("[Rune: %s]", team[q].getRune().getName())));
				}
			} else {
				sb.append(String.format("%,d) %s %s", q + 1, team[q], team[q].getElement().getAsBrackets()));
				
				if (team[q].hasItem()) {
					sb.append(" ");
					if (team[q].getItem().hasIcon()) {
						sb.append(team[q].getItem().getIcon().getEmote().getAsMention());
					} else {
						sb.append(String.format("[Holding: %s]", team[q].getItem()));
					}
				}

				if (team[q].hasRune()) {
					sb.append(" ");
					sb.append(String.format("[Rune: %s]", team[q].getRune().getName()));
				}
			}

			sb.append("\n");
		}

		return sb.toString();
	}
	
	//////////////////////////////////////////////////////////////
	
	public static String fixString(String string) {
		if (string == null) return null;
		string = string.replace("\\", "\\\\");
		string = string.replace("*", "\\*");
		string = string.replace("_", "\\_");
		string = string.replace("`", "\\`");
		string = string.replace("~", "\\~");
		string = string.replace("|", "\\|");
		return string;
	}
	
	public static String cleanQuotes(String string) {
		string = string.replace("'", "\\'");
		return string;
	}
	
	public static String generateRandomString(int length) {
		StringBuilder sb = new StringBuilder();
		char[] arr = "AaBbCcDdEeFfGgHhIiJjKkLlMmNnOoPpQqRrSsTtUuVvWwXxYyZz0123456789".toCharArray();
		while (sb.length() < length) sb.append(arr[MesozoicRandom.nextInt(arr.length)]);
		return sb.toString();
	}
	
	public static boolean isInvertedTitle(Item item) {
		if (item.getId() != 10010) return false;
		if (item.getDamage() == 484873357045792769L) return true; // Jamie
		return false;
	}
	
	////////////////////////////////////////////////////////////////
	
	public static long getTimeLeftInDay() {
		MesozoicCalendar gc = new MesozoicCalendar();
		return TimeUnit.HOURS.toMillis(23 - gc.get(MesozoicCalendar.HOUR_OF_DAY)) + TimeUnit.MINUTES.toMillis(59 - gc.get(MesozoicCalendar.MINUTE));
	}
	
	public static void sleep(long sleep) {
		long time = System.currentTimeMillis() + sleep;
		while (System.currentTimeMillis() < time);
	}
	
	public static String getArticle(String word) {
		return "aeiou".contains(word.toLowerCase().substring(0, 1)) ? "an" : "a";
	}
	
	public static long getTime(String time) {
		time = time.replace("\\s+", "");
		String[] numbers = time.split("[a-z]");
		String[] times = time.toLowerCase().split("\\d+");
		
		long ret = 0;
		for (int q = 0; q < numbers.length && q + 1 < times.length; q++) {
			long num = Long.parseLong(numbers[q]);
			switch (times[q + 1]) {
			case "w":
				ret += TimeUnit.DAYS.toMillis(7 * num);
				break;
			case "d":
				ret += TimeUnit.DAYS.toMillis(num);
				break;
			case "h":
				ret += TimeUnit.HOURS.toMillis(num);
				break;
			case "m":
				ret += TimeUnit.MINUTES.toMillis(num);
				break;
			case "s":
				ret += TimeUnit.SECONDS.toMillis(num);
				break;
			}
		}
		
		return ret;
	}
	
	public static String formatNumber(long number) {
		return DecimalFormat.getNumberInstance().format(number);
	}
	
	public static String formatTime(long time) {
		if (time < 1_000L) return "0s";
		StringBuilder sb = new StringBuilder();
		
		if (time >= TimeUnit.DAYS.toMillis(1)) {
			sb.append(String.format("%,dd ", time / TimeUnit.DAYS.toMillis(1)));
			time %= TimeUnit.DAYS.toMillis(1);
		}
		
		if (time >= TimeUnit.HOURS.toMillis(1)) {
			sb.append(String.format("%,dh ", time / TimeUnit.HOURS.toMillis(1)));
			time %= TimeUnit.HOURS.toMillis(1);
		}
		
		if (time >= TimeUnit.MINUTES.toMillis(1)) {
			sb.append(String.format("%,dm ", time / TimeUnit.MINUTES.toMillis(1)));
			time %= TimeUnit.MINUTES.toMillis(1);
		}
		
		if (time >= TimeUnit.SECONDS.toMillis(1)) {
			sb.append(String.format("%,ds ", time / TimeUnit.SECONDS.toMillis(1)));
			time %= TimeUnit.SECONDS.toMillis(1);
		}
		
		return sb.toString().trim();
	}

	public static String formatDateTime(long millis) {
		MesozoicCalendar mc = new MesozoicCalendar();
		mc.setTimeInMillis(millis);
		return String.format("%1$tB %1$td, %1$tY at %1$tR UTC", mc);
	}
	
	public static String getBirthday(int birthday) {
		return getMonth(birthday / 100) + " " + getOrdinal(birthday % 100);
	}
	
	public static String getMonth(int month) {
		switch (month) {
		case 1:
			return "January";
		case 2:
			return "February";
		case 3:
			return "March";
		case 4:
			return "April";
		case 5:
			return "May";
		case 6:
			return "June";
		case 7:
			return "July";
		case 8:
			return "August";
		case 9:
			return "September";
		case 10:
			return "October";
		case 11:
			return "November";
		case 12:
			return "December";
		case 13:
			return "Undecimber";
		default:
			return "Unknownber";
		}
	}
	
	public static int getDaysInMonth(int month, boolean leap) {
		switch (month) {
		case 1: case 3: case 5: case 7: case 8: case 10: case 12:
			return 31;
		case 4: case 6: case 9: case 11:
			return 30;
		case 2:
			return leap ? 29 : 28;
		default:
			return -1;
		}
	}
	
	public static int getDaysInYear(int year) {
		return isLeapYear(year) ? 366 : 365;
	}
	
	public static boolean isLeapYear(int year) {
		if (year % 400 == 0) return true;
		if (year % 100 == 0) return false;
		if (year % 4 == 0) return true;
		return false;
	}
	
	public static String getOrdinal(int cardinal) {
		if (cardinal < 0) return "-" + getOrdinal(-cardinal);
		
		switch (cardinal % 100) {
		case 1: case 21: case 31: case 41: case 51: case 61: case 71: case 81: case 91:
			return cardinal + "st";
		case 2: case 22: case 32: case 42: case 52: case 62: case 72: case 82: case 92:
			return cardinal + "nd";
		case 3: case 23: case 33: case 43: case 53: case 63: case 73: case 83: case 93:
			return cardinal + "rd";
		default:
			return cardinal + "th";
		}
	}
	
	public static List<String> bulkify(List<String> list) {
		return bulkify(list, "\n");
	}
	
	public static List<String> bulkify(List<String> list, String sep) {
		List<String> newlist = new ArrayList<String>();
		StringBuilder sb = new StringBuilder();
		
		for (String string : list) {
			if (sb.length() + sep.length() + string.length() > Message.MAX_CONTENT_LENGTH) {
				if (newlist.isEmpty()) newlist.add(sb.substring(sep.length()));
				else newlist.add(sb.toString());
				sb.setLength(0);
			}
			sb.append(sep);
			sb.append(string);
		}
		
		if (sb.length() > 0) newlist.add(sb.toString());
		return newlist;
	}
	
	public static Pair<Integer, Integer> getDexForm(String dino) {
		String sdex = dino.replaceAll("[^\\d]", "");
		if (dino.charAt(0) == '-') sdex = "-" + sdex;
		String sform = dino.replaceAll("(\\d|-)", "");
		DinosaurForm form = DinosaurForm.of(sform);
		return new Pair<Integer, Integer>(Integer.parseInt(sdex), form.getId());
	}

	public static String printDinosaur(Dinosaur d) {
		Dinosaur base = Dinosaur.getDinosaur(d.getIdPair());
		StringBuilder sb = new StringBuilder();
		sb.append(String.format("\n\n== %s's %s ==", d.getPlayer().getName(), d.getEffectiveName()));
		sb.append(String.format("\nHealth: %,d (%,d Base) (%d%% Boost)", d.getHealth(), base.getHealth(), d.getHealthMultiplier()));
		sb.append(String.format("\nCurrent Damage: %,d", d.getDamage()));
		return sb.toString();
	}
	
	///////////////////////////////////////////////////////////////
	
	public static long getLineCount() {
		File main = new File(Constants.JAVA_PATH);
		long lines = 0;
		
		ArrayDeque<File> queue = new ArrayDeque<File>();
		queue.add(main);

		while (!queue.isEmpty()) {
			File f = queue.poll();

			if (f.isDirectory()) {
				for (File ff : f.listFiles()) {
					queue.add(ff);
				}
			} else {
				try (Scanner read = new Scanner(f)) {
					int linenum = 0;
					while (read.hasNextLine()) {
						String line = read.nextLine();
						linenum++;
						
						if (line.contains("rrrrrrrrrrrrrrrrrrrrrrrrrrrr") && !line.contains("line.contains")) {
							System.out.printf("%s Line %d :: %s%n", f.getName(), linenum, line.trim());
						}
						
						if (line.trim().length() == 0) continue;
						if (line.trim().startsWith("//")) continue;
						lines++;
					}
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				}
			}
		}

		return lines;
	}
	
	///////////////////////////////////////////////////
	
	public static boolean doesMemberHaveRole(Member m, long role) {
		if (m == null) return false;
		if (!Constants.ROLES_ENABLED) {
			System.out.printf("Failed to check if player %d has the role %d.%n", m.getIdLong(), role);
			return false;
		}
		Guild g = MesozoicIsland.getProfessor().getGuild();
		Role r = g.getRoleById(role);
		if (r == null) return false;
		return m.getRoles().contains(r);
	}
	
	public static void addRoleToMember(Member m, long... roles) {
		if (m == null) return;
		
		if (!Constants.ROLES_ENABLED) {
			System.out.printf("Failed to give to player %d the roles %s.%n", m.getIdLong(), Arrays.toString(roles));
			return;
		}
		Guild g = MesozoicIsland.getProfessor().getGuild();
		
		for (long role : roles) {
			Role r = g.getRoleById(role);
			if (r == null) continue;
			g.addRoleToMember(m, r).complete();
		}
	}
	
	public static void removeRoleFromMember(Member m, long... roles) {
		if (m == null) return;
		
		if (!Constants.ROLES_ENABLED) {
			System.out.printf("Failed to remove from player %d the roles %s.%n", m.getIdLong(), Arrays.toString(roles));
			return;
		}
		Guild g = MesozoicIsland.getProfessor().getGuild();
		
		for (long role : roles) {
			Role r = g.getRoleById(role);
			if (r == null) continue;
			g.removeRoleFromMember(m, r).complete();
		}
	}
	
	public static void setRolesMentionable(boolean mentionable, DiscordRole... roles) {
		if (!Constants.ROLES_ENABLED) {
			System.out.printf("Failed to make %smentionable the roles %s.%n", mentionable ? "" : "un", Arrays.toString(roles));
			return;
		}
		Guild g = MesozoicIsland.getProfessor().getGuild();
		for (DiscordRole role : roles) {
			Role r = g.getRoleById(role.getIdLong());
			if (r == null) continue;
			r.getManager().setMentionable(mentionable).complete();
		}
	}
	
	public static boolean isChannelInCategory(TextChannel tc, long category) {
		return MesozoicIsland.getProfessor().getGuild().getCategoryById(category).getTextChannels().contains(tc);
	}

	/////////////////////////////////////////////////////////

	public static int getFirstOpenIncubator(long player) {
		HashSet<Integer> used = new HashSet<Integer>();
		try (ResultSet res = JDBC.executeQuery("select * from eggs where player = %d;", player)) {
			while (res.next()) {
				used.add(res.getInt("incubator"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		for (int q = 1;; q++) {
			if (used.contains(q)) continue;
			return q;
		}
	}
}
