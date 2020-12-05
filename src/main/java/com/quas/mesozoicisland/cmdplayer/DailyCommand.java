package com.quas.mesozoicisland.cmdplayer;

import java.util.regex.Pattern;

import com.quas.mesozoicisland.JDBC;
import com.quas.mesozoicisland.cmdbase.ICommand;
import com.quas.mesozoicisland.enums.AccessLevel;
import com.quas.mesozoicisland.enums.DiscordChannel;
import com.quas.mesozoicisland.enums.DiscordRole;
import com.quas.mesozoicisland.enums.ItemID;
import com.quas.mesozoicisland.enums.Stat;
import com.quas.mesozoicisland.objects.Item;
import com.quas.mesozoicisland.objects.Player;
import com.quas.mesozoicisland.util.Constants;
import com.quas.mesozoicisland.util.MesozoicDate;
import com.quas.mesozoicisland.util.MesozoicRandom;
import com.quas.mesozoicisland.util.Pair;
import com.quas.mesozoicisland.util.Util;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class DailyCommand implements ICommand {

	@Override
	public Pattern getCommand() {
		return pattern("daily");
	}

	@Override
	public AccessLevel getAccessLevel() {
		return AccessLevel.Trainer;
	}

	@Override
	public String getCommandName() {
		return "daily";
	}

	@Override
	public String getCommandSyntax() {
		return "daily";
	}

	@Override
	public String getCommandDescription() {
		return "Claims your daily paycheck.";
	}

	@Override
	public DiscordChannel[] getUsableChannels() {
		return DiscordChannel.STANDARD_CHANNELS;
	}

	@Override
	public DiscordRole[] getRequiredRoles() {
		return null;
	}

	@Override
	public String getRequiredState() {
		return null;
	}

	@Override
	public void run(MessageReceivedEvent event, String... args) {
		Player p = Player.getPlayer(event.getAuthor().getIdLong());
		if (p == null) return;
		
		MesozoicDate today = MesozoicDate.getToday();
		MesozoicDate daily = new MesozoicDate(p.getDaily());
		int delta = Math.abs(today.compareTo(daily));
		
		if (delta == 0) {
			event.getChannel().sendMessage(String.format("%s, your daily will be ready to claim in %s.", event.getAuthor().getAsMention(), Util.formatTime(Util.getTimeLeftInDay()))).complete();
		} else {
			StringBuilder sb = new StringBuilder();
			
			// Calculate Daily Streak
			int streak = (delta == 1 ? p.getDailyStreak() : 0) + 1;
			JDBC.setDailyStreak(p.getIdLong(), streak);
			
			// Daily Money
			Item money = Item.getItem(ItemID.DinosaurCoin);
			sb.append(String.format("%s, you have received %,d %s from your daily.", p.getAsMention(), Constants.DAILY_MONEY, money.toString(Constants.DAILY_MONEY)));
			
			// Daily Streak + Bonus Money
			long amt = Math.min(streak - 1, Constants.MAX_BONUS_DAYS) * Constants.BONUS_DAILY_PER_DAY;
			if (streak > 1) {
				sb.append(String.format(" Your daily streak is now **%d** days!", streak));
				sb.append(String.format("\nDaily Streak Bonus Reward: %s %s!", amt, money.toString(amt)));
			}
			
			// Give Money and Set Daily
			JDBC.addItem(p.getIdLong(), money.getIdDmg(), Constants.DAILY_MONEY + amt);
			JDBC.setDaily(p.getIdLong());
			JDBC.addItem(p.getIdLong(), Stat.DailiesClaimed.getId());
			
			// Daily Streak Bonus
			if (streak % 28 == 0) {
				// Premium Dungeon Ticket every month
				Item item = Item.getItem(ItemID.PremiumDungeonTicket);
				sb.append(String.format("\nDaily Streak Bonus Reward: %s %s!", Util.getArticle(item.toString()), item.toString()));
				JDBC.addItem(p.getIdLong(), item.getIdDmg());
			} else if (streak % 7 == 0) {
				// Dungeon Ticket every 7 days
				Item item = Item.getItem(ItemID.DungeonTicket);
				sb.append(String.format("\nDaily Streak Bonus Reward: %s %s!", Util.getArticle(item.toString()), item.toString()));
				JDBC.addItem(p.getIdLong(), item.getIdDmg());
			}
			
			// Daily Raid Pass
			String raid = JDBC.getVariable("raidpass");
			if (p.getLevel() >= Constants.REQUIRED_RAID_LEVEL && raid != null) {
				Item item = Item.getItem(new Pair<Integer, Long>(ItemID.RaidPass.getItemId(), Long.parseLong(raid)));
				sb.append(String.format("\nDaily Raid Pass: %s %s!", Util.getArticle(item.toString()), item.toString()));
				JDBC.addItem(p.getIdLong(), item.getIdDmg());
			}

			// Special Day
			String date = MesozoicDate.getToday().toString(false);
			switch (date) {

				// Halloween
				case "1031": {
					for (ItemID itemid : new ItemID[] {ItemID.DinoGalaxyBar, ItemID.DinoFruitChew, ItemID.PeanutButterDinoCup, ItemID.ChocolateDinoWafer, ItemID.CaramelDinoLog, ItemID.ChocolateDinoBar}) {
						Item item = Item.getItem(itemid);
						sb.append(String.format("\nHalloween Bonus Reward: %s %s!", Util.getArticle(item.toString()), item.toString()));
						JDBC.addItem(p.getIdLong(), item.getIdDmg());
					}
				} break;

				// Thanksgiving Week
				case "1122": case "1123": case "1124": case "1125": case "1127": case "1128": {
					Item item = Item.getItem(ItemID.ThanksgivingToken);
					int count = 25;
					sb.append(String.format("\nHappy Thanksgiving: %d %s!", count, item.toString(count)));
					JDBC.addItem(p.getIdLong(), item.getIdDmg(), count);
				} break;

				// Thanksgiving Day
				case "1126": {
					Item item = Item.getItem(ItemID.ThanksgivingToken);
					int count = 100;
					sb.append(String.format("\nHappy Thanksgiving: %d %s!", count, item.toString(count)));
					JDBC.addItem(p.getIdLong(), item.getIdDmg(), count);
				} break;

				// Christmas Month
				case "1201": case "1202": case "1203": case "1204": case "1205": case "1206": case "1207": case "1208": {
					if (p.isSecretSanta()) {
						Item item = Item.getItem(ItemID.GiftToken);
						int count = MesozoicRandom.nextInt(3, 6);
						int days = 1225 - Integer.parseInt(date);
						sb.append(String.format("\n%d Day%s until Christmas Bonus: %d %s!", days, days == 1 ? "" : "s", count, item.toString(count)));
						JDBC.addItem(p.getIdLong(), item.getIdDmg(), count);
					}
				} break;
				case "1209": case "1210": case "1211": case "1212": case "1213": case "1214": case "1215": case "1216": {
					if (p.isSecretSanta()) {
						Item item = Item.getItem(ItemID.GiftToken);
						int count = MesozoicRandom.nextInt(5, 9);
						int days = 1225 - Integer.parseInt(date);
						sb.append(String.format("\n%d Day%s until Christmas Bonus: %d %s!", days, days == 1 ? "" : "s", count, item.toString(count)));
						JDBC.addItem(p.getIdLong(), item.getIdDmg(), count);
					}
				} break;
				case "1217": case "1218": case "1219": case "1220": case "1221": case "1222": case "1223": case "1224": {
					if (p.isSecretSanta()) {
						Item item = Item.getItem(ItemID.GiftToken);
						int count = MesozoicRandom.nextInt(8, 13);
						int days = 1225 - Integer.parseInt(date);
						sb.append(String.format("\n%d Day%s until Christmas Bonus: %d %s!", days, days == 1 ? "" : "s", count, item.toString(count)));
						JDBC.addItem(p.getIdLong(), item.getIdDmg(), count);
					}
				} break;
			}
			
			// Send Message
			event.getChannel().sendMessage(sb.toString()).complete();
		}
	}
}
