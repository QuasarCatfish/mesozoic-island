package com.quas.mesozoicisland.util;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;

import com.quas.mesozoicisland.JDBC;
import com.quas.mesozoicisland.MesozoicIsland;
import com.quas.mesozoicisland.enums.AchievementTitle;
import com.quas.mesozoicisland.enums.CustomPlayer;
import com.quas.mesozoicisland.enums.DinoID;
import com.quas.mesozoicisland.enums.DinosaurForm;
import com.quas.mesozoicisland.enums.DiscordChannel;
import com.quas.mesozoicisland.enums.DiscordRole;
import com.quas.mesozoicisland.enums.ItemID;
import com.quas.mesozoicisland.enums.QuestType;
import com.quas.mesozoicisland.enums.Stat;
import com.quas.mesozoicisland.objects.Dinosaur;
import com.quas.mesozoicisland.objects.Egg;
import com.quas.mesozoicisland.objects.Event;
import com.quas.mesozoicisland.objects.Item;
import com.quas.mesozoicisland.objects.Player;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.PrivateChannel;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.exceptions.ErrorResponseException;

public class Daily {

	private static String readyDay = new MesozoicDate(System.currentTimeMillis()).toString();

	public static void doUpdate(long millis) {
		StringBuilder sb = new StringBuilder();
		sb.append(DiscordRole.DailyPing);
		MesozoicDate today = new MesozoicDate(millis);
		int day = Integer.parseInt(JDBC.getVariable("day"));
		
		// Daily & Dinosaur Tickets
		sb.append(String.format("\n**Daily Update - %s %s**", Util.getMonth(today.getMonthInt()), Util.getOrdinal(today.getDayInt())));
		sb.append("\n" + Constants.BULLET_POINT + " Players can now claim their daily again.");
		sb.append("\n" + Constants.BULLET_POINT + " Dungeon Ticket dinosaurs have been refreshed.");

		// Raid Passes
		{
			System.out.println("[DAILY] Processing raid passes");
			JDBC.executeUpdate("update bags set count = 0 where item = 701;");
			String old = JDBC.getVariable("raidpass");
			String var = JDBC.getVariable("raidpassnext");
			if (old == null && var == null) {
				// No Update
			} else if (var == null) {
				JDBC.executeUpdate("update vars set value = null where var = 'raidpass';");
				sb.append("\n" + Constants.BULLET_POINT + " Raid Passes have been cleared. There is no raid available today.");
			} else if (old == null) {
				JDBC.executeUpdate("update vars set value = '%s' where var = 'raidpass';", Util.cleanQuotes(var));
				Item item = Item.getItem(new Pair<Integer, Long>(ItemID.RaidPass.getItemId(), Long.parseLong(var)));
				Dinosaur raidboss = Dinosaur.getDinosaur(Integer.parseInt(item.getData().split("\\s+")[0]), DinosaurForm.RaidBoss.getId());
				sb.append("\n" + Constants.BULLET_POINT + " Today's raid features ");
				sb.append(raidboss.getEffectiveName());
				sb.append(".");
			} else {
				JDBC.executeUpdate("update vars set value = '%s' where var = 'raidpass';", Util.cleanQuotes(var));
				Item item = Item.getItem(new Pair<Integer, Long>(ItemID.RaidPass.getItemId(), Long.parseLong(var)));
				Dinosaur raidboss = Dinosaur.getDinosaur(Integer.parseInt(item.getData().split("\\s+")[0]), DinosaurForm.RaidBoss.getId());
				sb.append("\n" + Constants.BULLET_POINT + " Raid Passes have been cleared. Today's raid features ");
				sb.append(raidboss.getEffectiveName());
				sb.append(".");
			}
		}
		
		// Daily Claim Quests
		{
			System.out.println("[DAILY] Processing completed quests");
			try (ResultSet res = JDBC.executeQuery("select * from quests where completed = false;")) {
				while (res.next()) {
					Player p = Player.getPlayer(res.getLong("playerid"));
					if (p == null) continue;

					String name = res.getString("questname");
					long quest = res.getLong("questtype");
					Item item = Item.getItem(Stat.of(quest));
					if (item == null) continue;
					
					long start = res.getLong("start");
					long end = res.getLong("goal");
					if (end <= 0) continue;
					
					long progress = p.getItemCount(item) - start;
					if (progress < end) continue;

					QuestType qt = QuestType.of(res.getInt("special"));
					if (qt != QuestType.Standard) continue;
					
					Member m = null;
					try {
						m = MesozoicIsland.getAssistant().getGuild().retrieveMemberById(p.getId()).complete();
					} catch (ErrorResponseException e) {}
					if (m == null) continue;
					
					PrivateChannel pc = m.getUser().openPrivateChannel().complete();
					try {
						pc.sendMessageFormat("%s, for completing the \"%s\" quest, you have received the following rewards:\n%s", p.getAsMention(), name, JDBC.getRedeemMessage(res.getString("reward"))).complete();
					} catch (Exception e) {}

					JDBC.executeUpdate("update quests set completed = true where questid = %d;", res.getInt("questid"));
					JDBC.addItem(p.getIdLong(), Stat.QuestsCompleted.getId(), 1);
					JDBC.redeem(pc, p.getIdLong(), res.getString("reward"));
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

		// Daily New Quests
		{
			System.out.println("[DAILY] Processing quests.");
			TreeMap<Long, Integer> valid = new TreeMap<Long, Integer>();
			try (ResultSet res = JDBC.executeQuery("select players.playerid, x.quests, y.books from players left join (select playerid, count(*) as quests from quests where special = 0 and completed = false group by playerid) as x on players.playerid = x.playerid left join (select player, count as books from bags where item = 5 and dmg = 0) as y on players.playerid = y.player where players.playerid > %d;", CustomPlayer.getUpperLimit())) {
				while (res.next()) {
					if (res.getInt("x.quests") >= Constants.MAX_QUESTS) continue;
					if (res.getInt("y.books") <= 0) continue;
					valid.put(res.getLong("players.playerid"), Constants.MAX_QUESTS - res.getInt("x.quests"));
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
			
			final int quests = MesozoicRandom.nextInt(Constants.MAX_QUESTS_PER_DAY) + 1;

			ArrayList<String> questnames = new ArrayList<String>();
			try (ResultSet res = JDBC.executeQuery("select *, rand() as r from questlist where lastday < %d and special = 0 order by r limit %d;", day - Constants.DAYS_BETWEEN_SAME_QUEST, quests)) {
				while (res.next()) {
					JDBC.executeUpdate("update questlist set lastday = %d where questid = %d;", day, res.getInt("questid"));
					String name = res.getString("questname");
					long type = res.getLong("questtype");
					long goal = res.getLong("goal");
					String reward = res.getString("reward");
					int special = res.getInt("special");
					Item item = Item.getItem(Stat.of(type));
					
					for (long player : valid.keySet()) {
						if (valid.get(player) <= 0) continue;
						valid.put(player, valid.get(player) - 1);
						JDBC.executeUpdate("insert into quests(playerid, questname, questtype, start, goal, reward, special) values(%d, '%s', %d, %d, %d, '%s', %d);", player, Util.cleanQuotes(name), type, Player.getPlayer(player).getItemCount(item), goal, Util.cleanQuotes(reward), special);
					}
					
					questnames.add("\"" + name + "\"");
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
			
			sb.append("\n");
			sb.append(Constants.BULLET_POINT);
			sb.append(" Today's Quest");
			if (quests > 1) sb.append("s");
			sb.append(": ");
			sb.append(Util.join(questnames, ", ", 0, questnames.size()));
			sb.append(". All players with space in their ");
			sb.append(Item.getItem(ItemID.QuestBook).toString());
			sb.append(" have received ");
			if (quests > 1) sb.append("these quests.");
			else sb.append("this quest.");
		}

		// Egg Salesman Benedict
		{
			System.out.println("[DAILY] Processing Egg Salesman Benedict.");
			int count = -1;
			try (ResultSet res = JDBC.executeQuery("select count(*) as count from eggs where player = %d;", CustomPlayer.EggSalesman.getIdLong())) {
				if (res.next()) {
					count = res.getInt("count");
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}

			if (count > -1) {
				int max = Integer.parseInt(JDBC.getVariable("benedict"));
				if (count == 0) {
					max += 5;
					JDBC.setVariable("benedict", Integer.toString(max));
				}

				for (int q = count; q < max; q++) {
					JDBC.addEgg(CustomPlayer.EggSalesman.getIdLong(), Egg.getRandomEgg(MesozoicRandom.nextEggDinosaur().getIdPair()));
				}

				sb.append(String.format("\n%s %s has received more eggs. There are %,d eggs in stock today.", Constants.BULLET_POINT, CustomPlayer.EggSalesman.getPlayer().getName(), Math.max(count, max)));
			}
		}

		// Events
		{
			System.out.println("[DAILY] Processing events");

			// Event started within past day
			for (Event e : Event.values()) {
				if (!e.isAnnounce()) continue;
				if (millis >= e.getStartTime() && e.getStartTime() + TimeUnit.DAYS.toMillis(1) > millis) {
					sb.append("\n" + Constants.BULLET_POINT + " Event Start: " + e.getName());
				}
			}

			// Event end within past day
			for (Event e : Event.values()) {
				if (!e.isAnnounce()) continue;
				if (millis >= e.getEndTime() && e.getEndTime() + TimeUnit.DAYS.toMillis(1) > millis) {
					sb.append("\n" + Constants.BULLET_POINT + " Event End: " + e.getName());
				}
			}
		}
		
		// Get Birthdays
		{
			System.out.println("[DAILY] Processing birthdays.");

			ArrayList<Long> birthdays = new ArrayList<Long>();
			try (ResultSet res = JDBC.executeQuery("select playerid from players where birthday = '%s' order by playerid;", today.toString(false))) {
				while (res.next()) birthdays.add(res.getLong("playerid"));
			} catch (SQLException e) {
				e.printStackTrace();
			}
			
			// Birthday Message
			if (!birthdays.isEmpty()) {
				sb.append("\n\n**Birthdays**");
				for (long id : birthdays) {
					Player p = Player.getPlayer(id);
					sb.append("\n" + Constants.BULLET_POINT + " ");
					sb.append(p.getAsMention());
					Constants.addBirthdayMail(p);
				}
				
				if (birthdays.size() == 1) sb.append("\nHappy Birthday! Check your mail for your presents!");
				else if (birthdays.size() == 2) sb.append("\nHappy Birthday to both of you! Check your mail for your presents!");
				else sb.append("\nHappy Birthday to all of you! Check your mail for your presents!");
			}
		}
		
		// Do Daily Ping
		Util.setRolesMentionable(true, DiscordRole.DailyPing);
		DiscordChannel.DailyAnnouncements.getChannel(MesozoicIsland.getAssistant()).sendMessage(sb.toString()).complete();
		Util.setRolesMentionable(false, DiscordRole.DailyPing);

		// Update Day Count
		JDBC.executeUpdate("update vars set value = value + 1 where var = 'day';");

		// Update Raid Pass
		if (Integer.parseInt(JDBC.getVariable("day")) % Constants.RAID_CYCLE_DAYS == 0) {
			JDBC.setNextRaidPass(MesozoicRandom.nextRaidPass());
		}

		readyDay = today.toString();
	}

	public static boolean isDailyReady() {
		return readyDay.equals(new MesozoicDate(System.currentTimeMillis()).toString());
	}

	public static void doHourly(long millis) {

		TextChannel channel = Constants.SPAWN_CHANNEL.getChannel(MesozoicIsland.getAssistant());

		// Check for achievement titles
		for (Player p : Player.values()) {
			if (p.getIdLong() < CustomPlayer.getUpperLimit()) continue;

			for (AchievementTitle title : AchievementTitle.values()) {
				if (p.getItemCount(title.getItem()) <= 0 && p.getItemCount(title.getStat()) >= title.getStatAmount()) {
					JDBC.addItem(p.getIdLong(), title.getItem().getIdDmg());
					channel.sendMessage(title.toString(p)).complete();
				}
			}

			Dinosaur chicken = Dinosaur.getDinosaur(p.getIdLong(), DinoID.Chicken.getId());
			if (p.getItemCount(ItemID.ChickenTamer) <= 0 && chicken != null && chicken.getLevel() >= 25) {
				JDBC.addItem(p.getIdLong(), ItemID.ChickenTamer.getId());
				channel.sendMessageFormat("%s, for training your %s to Level 25, you have earned the %s.", p.getAsMention(), chicken.getEffectiveName(), Item.getItem(ItemID.ChickenTamer).toString()).complete();
			}
		}
	}
}
