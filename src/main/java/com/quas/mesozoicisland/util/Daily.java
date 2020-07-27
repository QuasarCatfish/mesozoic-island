package com.quas.mesozoicisland.util;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import com.quas.mesozoicisland.JDBC;
import com.quas.mesozoicisland.MesozoicIsland;
import com.quas.mesozoicisland.enums.CustomPlayer;
import com.quas.mesozoicisland.enums.DinosaurForm;
import com.quas.mesozoicisland.enums.DiscordChannel;
import com.quas.mesozoicisland.enums.DiscordRole;
import com.quas.mesozoicisland.objects.Dinosaur;
import com.quas.mesozoicisland.objects.Item;
import com.quas.mesozoicisland.objects.Player;

public class Daily {

	public static void doUpdate(long millis) {
		StringBuilder sb = new StringBuilder();
		sb.append(DiscordRole.DailyPing);
		MesozoicDate today = new MesozoicDate(millis);
		int day = Integer.parseInt(JDBC.getVariable("day"));
		
		// Daily & Dinosaur Tickets
		sb.append(String.format("\n**Daily Update - %s %s**", Util.getMonth(today.getMonthInt()), Util.getOrdinal(today.getDayInt())));
		sb.append("\n� Players can now claim their daily again.");
		sb.append("\n� Dungeon Ticket dinosaurs have been refreshed.");

		// Raid Passes
		{
			JDBC.executeUpdate("update bags set count = 0 where item = 701;");
			String old = JDBC.getVariable("raidpass");
			String var = JDBC.getVariable("raidpassnext");
			if (old == null && var == null) {
				// No Update
			} else if (var == null) {
				JDBC.executeUpdate("update vars set value = null where var = 'raidpass';");
				sb.append("\n� Raid Passes have been cleared. There is no raid available today.");
			} else if (old == null) {
				JDBC.executeUpdate("update vars set value = '%s' where var = 'raidpass';", Util.cleanQuotes(var));
				Item item = Item.getItem(new Pair<Integer, Long>(701, Long.parseLong(var)));
				Dinosaur raidboss = Dinosaur.getDinosaur(Integer.parseInt(item.getData().split("\\s+")[0]), DinosaurForm.RaidBoss.getId());
				sb.append("\n� Today's raid features ");
				sb.append(raidboss.getEffectiveName());
				sb.append(".");
			} else {
				JDBC.executeUpdate("update vars set value = '%s' where var = 'raidpass';", Util.cleanQuotes(var));
				Item item = Item.getItem(new Pair<Integer, Long>(701, Long.parseLong(var)));
				Dinosaur raidboss = Dinosaur.getDinosaur(Integer.parseInt(item.getData().split("\\s+")[0]), DinosaurForm.RaidBoss.getId());
				sb.append("\n� Raid Passes have been cleared. Today's raid features ");
				sb.append(raidboss.getEffectiveName());
				sb.append(".");
			}
		}
		
		// Daily Quest
		{
			ArrayList<Long> valid = new ArrayList<Long>();
			try (ResultSet res = JDBC.executeQuery("select players.playerid, x.quests, y.books from players left join (select playerid, count(*) as quests from quests where special = 0 and completed = false group by playerid) as x on players.playerid = x.playerid left join (select player, count as books from bags where item = 5 and dmg = 0) as y on players.playerid = y.player where players.playerid > %d;", CustomPlayer.getUpperLimit())) {
				while (res.next()) {
					if (res.getInt("x.quests") >= 3) continue;
					if (res.getInt("y.books") <= 0) continue;
					valid.add(res.getLong("players.playerid"));
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
			
			try (ResultSet res = JDBC.executeQuery("select *, rand() as r from questlist where lastday < %d order by r limit 1;", day - Constants.DAYS_BETWEEN_SAME_QUEST)) {
				if (res.next()) {
					JDBC.executeUpdate("update questlist set lastday = %d where questid = %d;", day, res.getInt("questid"));
					String name = res.getString("questname");
					long type = res.getLong("questtype");
					long goal = res.getLong("goal");
					String reward = res.getString("reward");
					Item item = Item.getItem(new Pair<Integer, Long>(0, type));
					
					for (long player : valid) {
						JDBC.executeUpdate("insert into quests(playerid, questname, questtype, start, goal, reward) values(%d, '%s', %d, %d, %d, '%s');",
								player, Util.cleanQuotes(name), type, Player.getPlayer(player).getBag().getOrDefault(item, 0L), goal, Util.cleanQuotes(reward));
					}
					
					sb.append("\n� Today's quest is \"");
					sb.append(name);
					sb.append("\". All players with space in their ");
					sb.append(Item.getItem(new Pair<Integer, Long>(5, 0L)).toString());
					sb.append(" have received this quest.");
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
		// Get Birthdays
		{
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
					sb.append("\n� ");
					sb.append(p.getAsMention());
					Constants.addBirthdayMail(p);
				}
				
				if (birthdays.size() == 1) sb.append("\nHappy Birthday! You have received mail.");
				else sb.append("\nHappy Birthday to all of you! Each of you have received mail!.");
			}
		}
		
		Util.setRolesMentionable(true, DiscordRole.DailyPing);
		DiscordChannel.DailyAnnouncements.getChannel(MesozoicIsland.getAssistant()).sendMessage(sb.toString()).complete();
		Util.setRolesMentionable(false, DiscordRole.DailyPing);
		JDBC.executeUpdate("update vars set value = value + 1 where var = 'day';");
	}
}
