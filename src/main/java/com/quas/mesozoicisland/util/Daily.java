package com.quas.mesozoicisland.util;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;

import com.quas.mesozoicisland.JDBC;
import com.quas.mesozoicisland.MesozoicIsland;
import com.quas.mesozoicisland.enums.CustomPlayer;
import com.quas.mesozoicisland.enums.DinosaurForm;
import com.quas.mesozoicisland.enums.DiscordChannel;
import com.quas.mesozoicisland.enums.DiscordRole;
import com.quas.mesozoicisland.enums.ItemID;
import com.quas.mesozoicisland.enums.Stat;
import com.quas.mesozoicisland.objects.Dinosaur;
import com.quas.mesozoicisland.objects.Egg;
import com.quas.mesozoicisland.objects.Event;
import com.quas.mesozoicisland.objects.Item;
import com.quas.mesozoicisland.objects.Player;

import net.dv8tion.jda.api.entities.TextChannel;

public class Daily {

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
		
		// Daily Quest
		{
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
					JDBC.addEgg(CustomPlayer.EggSalesman.getIdLong(), Egg.getRandomEgg(MesozoicRandom.nextDinosaur().getIdPair()));
				}

				sb.append(String.format("\n%s %s has received more eggs. There are %,d eggs in stock today.", Constants.BULLET_POINT, CustomPlayer.EggSalesman.getPlayer().getName(), Math.max(count, max)));
			}
		}

		// Events
		{
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
				
				if (birthdays.size() == 1) sb.append("\nHappy Birthday! You have received mail.");
				else sb.append("\nHappy Birthday to all of you! Each of you have received mail!.");
			}
		}
		
		Util.setRolesMentionable(true, DiscordRole.DailyPing);
		DiscordChannel.DailyAnnouncements.getChannel(MesozoicIsland.getAssistant()).sendMessage(sb.toString()).complete();
		Util.setRolesMentionable(false, DiscordRole.DailyPing);
		JDBC.executeUpdate("update vars set value = value + 1 where var = 'day';");
	}

	public static void doHourly(long millis) {

		TextChannel channel = DiscordChannel.GameTesting.getChannel(MesozoicIsland.getAssistant());

		// Check for achievement titles
		for (Player p : Player.values()) {
			if (p.getIdLong() < CustomPlayer.getUpperLimit()) continue;

			if (p.getItemCount(ItemID.NoviceDamagerTitle) <= 0 && p.getItemCount(Stat.DamageDealt) >= 1_000_000) {
				Item item = Item.getItem(ItemID.NoviceDamagerTitle);
				JDBC.addItem(p.getIdLong(), item.getIdDmg());
				channel.sendMessageFormat("%s, for dealing 1,000,000 damage, you have earned the %s.", p.getAsMention(), item.toString()).complete();
			}
				
			if (p.getItemCount(ItemID.AdvancedDamagerTitle) <= 0 && p.getItemCount(Stat.DamageDealt) >= 3_000_000) {
				Item item = Item.getItem(ItemID.AdvancedDamagerTitle);
				JDBC.addItem(p.getIdLong(), item.getIdDmg());
				channel.sendMessageFormat("%s, for dealing 3,000,000 damage, you have earned the %s.", p.getAsMention(), item.toString()).complete();
			}
				
			if (p.getItemCount(ItemID.EliteDamagerTitle) <= 0 && p.getItemCount(Stat.DamageDealt) >= 10_000_000) {
				Item item = Item.getItem(ItemID.EliteDamagerTitle);
				JDBC.addItem(p.getIdLong(), item.getIdDmg());
				channel.sendMessageFormat("%s, for dealing 10,000,000 damage, you have earned the %s.", p.getAsMention(), item.toString()).complete();
			}
				
			if (p.getItemCount(ItemID.MasterDamagerTitle) <= 0 && p.getItemCount(Stat.DamageDealt) >= 40_000_000) {
				Item item = Item.getItem(ItemID.MasterDamagerTitle);
				JDBC.addItem(p.getIdLong(), item.getIdDmg());
				channel.sendMessageFormat("%s, for dealing 40,000,000 damage, you have earned the %s.", p.getAsMention(), item.toString()).complete();
			}
				
			if (p.getItemCount(ItemID.LegendaryDamagerTitle) <= 0 && p.getItemCount(Stat.DamageDealt) >= 100_000_000) {
				Item item = Item.getItem(ItemID.LegendaryDamagerTitle);
				JDBC.addItem(p.getIdLong(), item.getIdDmg());
				channel.sendMessageFormat("%s, for dealing 100,000,000 damage, you have earned the %s.", p.getAsMention(), item.toString()).complete();
			}
				
			if (p.getItemCount(ItemID.NoviceSurvivorTitle) <= 0 && p.getItemCount(Stat.DamageReceived) >= 1_000_000) {
				Item item = Item.getItem(ItemID.NoviceSurvivorTitle);
				JDBC.addItem(p.getIdLong(), item.getIdDmg());
				channel.sendMessageFormat("%s, for taking 1,000,000 damage, you have earned the %s.", p.getAsMention(), item.toString()).complete();
			}
				
			if (p.getItemCount(ItemID.AdvancedSurvivorTitle) <= 0 && p.getItemCount(Stat.DamageReceived) >= 3_000_000) {
				Item item = Item.getItem(ItemID.AdvancedSurvivorTitle);
				JDBC.addItem(p.getIdLong(), item.getIdDmg());
				channel.sendMessageFormat("%s, for taking 3,000,000 damage, you have earned the %s.", p.getAsMention(), item.toString()).complete();
			}
				
			if (p.getItemCount(ItemID.EliteSurvivorTitle) <= 0 && p.getItemCount(Stat.DamageReceived) >= 10_000_000) {
				Item item = Item.getItem(ItemID.EliteSurvivorTitle);
				JDBC.addItem(p.getIdLong(), item.getIdDmg());
				channel.sendMessageFormat("%s, for taking 10,000,000 damage, you have earned the %s.", p.getAsMention(), item.toString()).complete();
			}
				
			if (p.getItemCount(ItemID.MasterSurvivorTitle) <= 0 && p.getItemCount(Stat.DamageReceived) >= 40_000_000) {
				Item item = Item.getItem(ItemID.MasterSurvivorTitle);
				JDBC.addItem(p.getIdLong(), item.getIdDmg());
				channel.sendMessageFormat("%s, for taking 40,000,000 damage, you have earned the %s.", p.getAsMention(), item.toString()).complete();
			}
				
			if (p.getItemCount(ItemID.LegendarySurvivorTitle) <= 0 && p.getItemCount(Stat.DamageReceived) >= 100_000_000) {
				Item item = Item.getItem(ItemID.LegendarySurvivorTitle);
				JDBC.addItem(p.getIdLong(), item.getIdDmg());
				channel.sendMessageFormat("%s, for taking 100,000,000 damage, you have earned the %s.", p.getAsMention(), item.toString()).complete();
			}
				
			if (p.getItemCount(ItemID.NoviceBattlerTitle) <= 0 && p.getItemCount(Stat.BattlesEntered) >= 1_000) {
				Item item = Item.getItem(ItemID.NoviceBattlerTitle);
				JDBC.addItem(p.getIdLong(), item.getIdDmg());
				channel.sendMessageFormat("%s, for entering 1,000 battles, you have earned the %s.", p.getAsMention(), item.toString()).complete();
			}
				
			if (p.getItemCount(ItemID.AdvancedBattlerTitle) <= 0 && p.getItemCount(Stat.BattlesEntered) >= 3_000) {
				Item item = Item.getItem(ItemID.AdvancedBattlerTitle);
				JDBC.addItem(p.getIdLong(), item.getIdDmg());
				channel.sendMessageFormat("%s, for entering 3,000 battles, you have earned the %s.", p.getAsMention(), item.toString()).complete();
			}
			
			if (p.getItemCount(ItemID.EliteBattlerTitle) <= 0 && p.getItemCount(Stat.BattlesEntered) >= 10_000) {
				Item item = Item.getItem(ItemID.EliteBattlerTitle);
				JDBC.addItem(p.getIdLong(), item.getIdDmg());
				channel.sendMessageFormat("%s, for entering 10,000 battles, you have earned the %s.", p.getAsMention(), item.toString()).complete();
			}
				
			if (p.getItemCount(ItemID.MasterBattlerTitle) <= 0 && p.getItemCount(Stat.BattlesEntered) >= 40_000) {
				Item item = Item.getItem(ItemID.MasterBattlerTitle);
				JDBC.addItem(p.getIdLong(), item.getIdDmg());
				channel.sendMessageFormat("%s, for entering 40,000 battles, you have earned the %s.", p.getAsMention(), item.toString()).complete();
			}
				
			if (p.getItemCount(ItemID.LegendaryBattlerTitle) <= 0 && p.getItemCount(Stat.BattlesEntered) >= 100_000) {
				Item item = Item.getItem(ItemID.LegendaryBattlerTitle);
				JDBC.addItem(p.getIdLong(), item.getIdDmg());
				channel.sendMessageFormat("%s, for entering 100,000 battles, you have earned the %s.", p.getAsMention(), item.toString()).complete();
			}
				
			if (p.getItemCount(ItemID.NoviceVictorTitle) <= 0 && p.getItemCount(Stat.BattlesWon) >= 1_000) {
				Item item = Item.getItem(ItemID.NoviceVictorTitle);
				JDBC.addItem(p.getIdLong(), item.getIdDmg());
				channel.sendMessageFormat("%s, for winning 1,000 battles, you have earned the %s.", p.getAsMention(), item.toString()).complete();
			}
				
			if (p.getItemCount(ItemID.AdvancedVictorTitle) <= 0 && p.getItemCount(Stat.BattlesWon) >= 3_000) {
				Item item = Item.getItem(ItemID.AdvancedVictorTitle);
				JDBC.addItem(p.getIdLong(), item.getIdDmg());
				channel.sendMessageFormat("%s, for winning 3,000 battles, you have earned the %s.", p.getAsMention(), item.toString()).complete();
			}
				
			if (p.getItemCount(ItemID.EliteVictorTitle) <= 0 && p.getItemCount(Stat.BattlesWon) >= 10_000) {
				Item item = Item.getItem(ItemID.EliteVictorTitle);
				JDBC.addItem(p.getIdLong(), item.getIdDmg());
				channel.sendMessageFormat("%s, for winning 10,000 battles, you have earned the %s.", p.getAsMention(), item.toString()).complete();
			}
				
			if (p.getItemCount(ItemID.MasterVictorTitle) <= 0 && p.getItemCount(Stat.BattlesWon) >= 40_000) {
				Item item = Item.getItem(ItemID.MasterVictorTitle);
				JDBC.addItem(p.getIdLong(), item.getIdDmg());
				channel.sendMessageFormat("%s, for winning 40,000 battles, you have earned the %s.", p.getAsMention(), item.toString()).complete();
			}
				
			if (p.getItemCount(ItemID.LegendaryVictorTitle) <= 0 && p.getItemCount(Stat.BattlesWon) >= 100_000) {
				Item item = Item.getItem(ItemID.LegendaryVictorTitle);
				JDBC.addItem(p.getIdLong(), item.getIdDmg());
				channel.sendMessageFormat("%s, for winning 100,000 battles, you have earned the %s.", p.getAsMention(), item.toString()).complete();
			}
				
			if (p.getItemCount(ItemID.NoviceShopperTitle) <= 0 && p.getItemCount(Stat.DinosaurCoinsSpent) >= 50_000) {
				Item item = Item.getItem(ItemID.NoviceShopperTitle);
				JDBC.addItem(p.getIdLong(), item.getIdDmg());
				channel.sendMessageFormat("%s, for spending 50,000 Dinosaur Coins, you have earned the %s.", p.getAsMention(), item.toString()).complete();
			}
				
			if (p.getItemCount(ItemID.AdvancedShopperTitle) <= 0 && p.getItemCount(Stat.DinosaurCoinsSpent) >= 200_000) {
				Item item = Item.getItem(ItemID.AdvancedShopperTitle);
				JDBC.addItem(p.getIdLong(), item.getIdDmg());
				channel.sendMessageFormat("%s, for spending 200,000 Dinosaur Coins, you have earned the %s.", p.getAsMention(), item.toString()).complete();
			}
				
			if (p.getItemCount(ItemID.EliteShopperTitle) <= 0 && p.getItemCount(Stat.DinosaurCoinsSpent) >= 1_000_000) {
				Item item = Item.getItem(ItemID.EliteShopperTitle);
				JDBC.addItem(p.getIdLong(), item.getIdDmg());
				channel.sendMessageFormat("%s, for spending 1,000,000 Dinosaur Coins, you have earned the %s.", p.getAsMention(), item.toString()).complete();
			}
				
			if (p.getItemCount(ItemID.MasterShopperTitle) <= 0 && p.getItemCount(Stat.DinosaurCoinsSpent) >= 5_000_000) {
				Item item = Item.getItem(ItemID.MasterShopperTitle);
				JDBC.addItem(p.getIdLong(), item.getIdDmg());
				channel.sendMessageFormat("%s, for spending 5,000,000 Dinosaur Coins, you have earned the %s.", p.getAsMention(), item.toString()).complete();
			}
				
			if (p.getItemCount(ItemID.LegendaryShopperTitle) <= 0 && p.getItemCount(Stat.DinosaurCoinsSpent) >= 10_000_000) {
				Item item = Item.getItem(ItemID.LegendaryShopperTitle);
				JDBC.addItem(p.getIdLong(), item.getIdDmg());
				channel.sendMessageFormat("%s, for spending 10,000,000 Dinosaur Coins, you have earned the %s.", p.getAsMention(), item.toString()).complete();
			}
				
			if (p.getItemCount(ItemID.NoviceSnackerTitle) <= 0 && p.getItemCount(Stat.SnacksFed) >= 500) {
				Item item = Item.getItem(ItemID.NoviceSnackerTitle);
				JDBC.addItem(p.getIdLong(), item.getIdDmg());
				channel.sendMessageFormat("%s, for feeding 500 snacks, you have earned the %s.", p.getAsMention(), item.toString()).complete();
			}
			
			if (p.getItemCount(ItemID.AdvancedSnackerTitle) <= 0 && p.getItemCount(Stat.SnacksFed) >= 2_000) {
				Item item = Item.getItem(ItemID.AdvancedSnackerTitle);
				JDBC.addItem(p.getIdLong(), item.getIdDmg());
				channel.sendMessageFormat("%s, for feeding 2,000 snacks, you have earned the %s.", p.getAsMention(), item.toString()).complete();
			}
			
			if (p.getItemCount(ItemID.EliteSnackerTitle) <= 0 && p.getItemCount(Stat.SnacksFed) >= 10_000) {
				Item item = Item.getItem(ItemID.EliteSnackerTitle);
				JDBC.addItem(p.getIdLong(), item.getIdDmg());
				channel.sendMessageFormat("%s, for feeding 10,000 snacks, you have earned the %s.", p.getAsMention(), item.toString()).complete();
			}
			
			if (p.getItemCount(ItemID.MasterSnackerTitle) <= 0 && p.getItemCount(Stat.SnacksFed) >= 50_000) {
				Item item = Item.getItem(ItemID.MasterSnackerTitle);
				JDBC.addItem(p.getIdLong(), item.getIdDmg());
				channel.sendMessageFormat("%s, for feeding 50,000 snacks, you have earned the %s.", p.getAsMention(), item.toString()).complete();
			}
			
			if (p.getItemCount(ItemID.LegendarySnackerTitle) <= 0 && p.getItemCount(Stat.SnacksFed) >= 200_000) {
				Item item = Item.getItem(ItemID.LegendarySnackerTitle);
				JDBC.addItem(p.getIdLong(), item.getIdDmg());
				channel.sendMessageFormat("%s, for feeding 200,000 snacks, you have earned the %s.", p.getAsMention(), item.toString()).complete();
			}
			
			if (p.getItemCount(ItemID.NoviceHatcherTitle) <= 0 && p.getItemCount(Stat.EggsHatched) >= 100) {
				Item item = Item.getItem(ItemID.NoviceHatcherTitle);
				JDBC.addItem(p.getIdLong(), item.getIdDmg());
				channel.sendMessageFormat("%s, for hatching 100 eggs, you have earned the %s.", p.getAsMention(), item.toString()).complete();
			}
			
			if (p.getItemCount(ItemID.AdvancedHatcherTitle) <= 0 && p.getItemCount(Stat.EggsHatched) >= 250) {
				Item item = Item.getItem(ItemID.AdvancedHatcherTitle);
				JDBC.addItem(p.getIdLong(), item.getIdDmg());
				channel.sendMessageFormat("%s, for hatching 250 eggs, you have earned the %s.", p.getAsMention(), item.toString()).complete();
			}
			
			if (p.getItemCount(ItemID.EliteHatcherTitle) <= 0 && p.getItemCount(Stat.EggsHatched) >= 1_000) {
				Item item = Item.getItem(ItemID.EliteHatcherTitle);
				JDBC.addItem(p.getIdLong(), item.getIdDmg());
				channel.sendMessageFormat("%s, for hatching 1,000 eggs, you have earned the %s.", p.getAsMention(), item.toString()).complete();
			}
			
			if (p.getItemCount(ItemID.MasterHatcherTitle) <= 0 && p.getItemCount(Stat.EggsHatched) >= 3_000) {
				Item item = Item.getItem(ItemID.MasterHatcherTitle);
				JDBC.addItem(p.getIdLong(), item.getIdDmg());
				channel.sendMessageFormat("%s, for hatching 3,000 eggs, you have earned the %s.", p.getAsMention(), item.toString()).complete();
			}
			
			if (p.getItemCount(ItemID.LegendaryHatcherTitle) <= 0 && p.getItemCount(Stat.EggsHatched) >= 10_000) {
				Item item = Item.getItem(ItemID.LegendaryHatcherTitle);
				JDBC.addItem(p.getIdLong(), item.getIdDmg());
				channel.sendMessageFormat("%s, for hatching 10,000 eggs, you have earned the %s.", p.getAsMention(), item.toString()).complete();
			}
			
			if (p.getItemCount(ItemID.NoviceRaiderTitle) <= 0 && p.getItemCount(Stat.RaidsAttempted) >= 10) {
				Item item = Item.getItem(ItemID.NoviceRaiderTitle);
				JDBC.addItem(p.getIdLong(), item.getIdDmg());
				channel.sendMessageFormat("%s, for attempting 10 raids, you have earned the %s.", p.getAsMention(), item.toString()).complete();
			}
			
			if (p.getItemCount(ItemID.AdvancedRaiderTitle) <= 0 && p.getItemCount(Stat.RaidsAttempted) >= 50) {
				Item item = Item.getItem(ItemID.AdvancedRaiderTitle);
				JDBC.addItem(p.getIdLong(), item.getIdDmg());
				channel.sendMessageFormat("%s, for attempting 50 raids, you have earned the %s.", p.getAsMention(), item.toString()).complete();
			}
			
			if (p.getItemCount(ItemID.EliteRaiderTitle) <= 0 && p.getItemCount(Stat.RaidsAttempted) >= 200) {
				Item item = Item.getItem(ItemID.EliteRaiderTitle);
				JDBC.addItem(p.getIdLong(), item.getIdDmg());
				channel.sendMessageFormat("%s, for attempting 200 raids, you have earned the %s.", p.getAsMention(), item.toString()).complete();
			}
			
			if (p.getItemCount(ItemID.MasterRaiderTitle) <= 0 && p.getItemCount(Stat.RaidsAttempted) >= 1_000) {
				Item item = Item.getItem(ItemID.MasterRaiderTitle);
				JDBC.addItem(p.getIdLong(), item.getIdDmg());
				channel.sendMessageFormat("%s, for attempting 1,000 raids, you have earned the %s.", p.getAsMention(), item.toString()).complete();
			}
			
			if (p.getItemCount(ItemID.LegendaryRaiderTitle) <= 0 && p.getItemCount(Stat.RaidsAttempted) >= 5_000) {
				Item item = Item.getItem(ItemID.LegendaryRaiderTitle);
				JDBC.addItem(p.getIdLong(), item.getIdDmg());
				channel.sendMessageFormat("%s, for attempting 5,000 raids, you have earned the %s.", p.getAsMention(), item.toString()).complete();
			}
			
			if (p.getItemCount(ItemID.NoviceRaidVictorTitle) <= 0 && p.getItemCount(Stat.RaidsDefeated) >= 10) {
				Item item = Item.getItem(ItemID.NoviceRaidVictorTitle);
				JDBC.addItem(p.getIdLong(), item.getIdDmg());
				channel.sendMessageFormat("%s, for defeating 10 raids, you have earned the %s.", p.getAsMention(), item.toString()).complete();
			}
			
			if (p.getItemCount(ItemID.AdvancedRaidVictorTitle) <= 0 && p.getItemCount(Stat.RaidsDefeated) >= 50) {
				Item item = Item.getItem(ItemID.AdvancedRaidVictorTitle);
				JDBC.addItem(p.getIdLong(), item.getIdDmg());
				channel.sendMessageFormat("%s, for defeating 50 raids, you have earned the %s.", p.getAsMention(), item.toString()).complete();
			}
			
			if (p.getItemCount(ItemID.EliteRaidVictorTitle) <= 0 && p.getItemCount(Stat.RaidsDefeated) >= 200) {
				Item item = Item.getItem(ItemID.EliteRaidVictorTitle);
				JDBC.addItem(p.getIdLong(), item.getIdDmg());
				channel.sendMessageFormat("%s, for defeating 200 raids, you have earned the %s.", p.getAsMention(), item.toString()).complete();
			}
			
			if (p.getItemCount(ItemID.MasterRaidVictorTitle) <= 0 && p.getItemCount(Stat.RaidsDefeated) >= 1_000) {
				Item item = Item.getItem(ItemID.MasterRaidVictorTitle);
				JDBC.addItem(p.getIdLong(), item.getIdDmg());
				channel.sendMessageFormat("%s, for defeating 1,000 raids, you have earned the %s.", p.getAsMention(), item.toString()).complete();
			}
			
			if (p.getItemCount(ItemID.LegendaryRaidVictorTitle) <= 0 && p.getItemCount(Stat.RaidsDefeated) >= 5_000) {
				Item item = Item.getItem(ItemID.LegendaryRaidVictorTitle);
				JDBC.addItem(p.getIdLong(), item.getIdDmg());
				channel.sendMessageFormat("%s, for defeating 5,000 raids, you have earned the %s.", p.getAsMention(), item.toString()).complete();
			}
		}
	}
}
