package com.quas.mesozoicisland.cmdtutorial;

import java.util.regex.Pattern;

import com.quas.mesozoicisland.JDBC;
import com.quas.mesozoicisland.cmdbase.ICommand;
import com.quas.mesozoicisland.enums.AccessLevel;
import com.quas.mesozoicisland.enums.DiscordChannel;
import com.quas.mesozoicisland.enums.DiscordRole;
import com.quas.mesozoicisland.objects.Player;
import com.quas.mesozoicisland.util.Util;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class Tutorial03 implements ICommand {

	@Override
	public Pattern getCommand() {
		return pattern(".*");
	}

	@Override
	public AccessLevel getAccessLevel() {
		return AccessLevel.Trainer;
	}

	@Override
	public String getCommandName() {
		return null;
	}

	@Override
	public String getCommandSyntax() {
		return null;
	}

	@Override
	public String getCommandDescription() {
		return null;
	}

	@Override
	public DiscordChannel[] getUsableChannels() {
		return Util.arr(DiscordChannel.CloneMe, DiscordChannel.ClonedChannel);
	}

	@Override
	public DiscordRole[] getRequiredRoles() {
		return null;
	}

	@Override
	public String getRequiredState() {
		return "Tutorial03";
	}
	
	@Override
	public void run(MessageReceivedEvent event, String... args) {
		Player p = Player.getPlayer(event.getAuthor().getIdLong());
		if (p == null) return;
		
		Util.sleep(500);
		JDBC.setState(p.getIdLong(), "TutorialXX");
		
		String[] bday = event.getMessage().getContentRaw().toLowerCase().split("\\s+");
		int month = bday.length < 1 ? -1 : getMonth(bday[0]);
		int day = bday.length < 2 ? -1 : getDay(bday[1]);
		
		if (isValidBirthday(month, day)) {
			int birthday = 100 * month + day;
			
			sendTyping(event.getChannel(), 2000);
			event.getChannel().sendMessageFormat("So your birthday is %s?", Util.getBirthday(birthday)).complete();
			
			JDBC.setBirthday(p.getIdLong(), birthday);
			JDBC.setState(p.getIdLong(), "Tutorial04");
		} else {
			sendTyping(event.getChannel(), 3000);
			event.getChannel().sendMessage("Unfortunately, there is something wrong with the birthday you provided. Please try again. The expected format is `01 31` for January 31st.\n*If you believe this is an error, please contact an Administrator for help.*").complete();
			
			JDBC.setState(p.getIdLong(), "Tutorial03");
		}
	}
	
	private int getMonth(String month) {
		switch (month.toLowerCase()) {
		case "january":
		case "jan":
		case "01":
		case "1":
			return 1;
		case "february":
		case "feb":
		case "02":
		case "2":
			return 2;
		case "march":
		case "mar":
		case "03":
		case "3":
			return 3;
		case "april":
		case "apr":
		case "04":
		case "4":
			return 4;
		case "may":
		case "05":
		case "5":
			return 5;
		case "june":
		case "jun":
		case "06":
		case "6":
			return 6;
		case "july":
		case "jul":
		case "07":
		case "7":
			return 7;
		case "august":
		case "aug":
		case "08":
		case "8":
			return 8;
		case "september":
		case "sept":
		case "sep":
		case "09":
		case "9":
			return 9;
		case "october":
		case "oct":
		case "10":
			return 10;
		case "november":
		case "nov":
		case "11":
			return 11;
		case "december":
		case "dec":
		case "12":
			return 12;
		default:
			return -1;
		}
	}
	
	private int getDay(String day) {
		switch (day) {
		case "1": case "01": case "1st": return 1;
		case "2": case "02": case "2nd": return 2;
		case "3": case "03": case "3rd": return 3;
		case "4": case "04": case "4th": return 4;
		case "5": case "05": case "5th": return 5;
		case "6": case "06": case "6th": return 6;
		case "7": case "07": case "7th": return 7;
		case "8": case "08": case "8th": return 8;
		case "9": case "09": case "9th": return 9;
		case "10": case "10th": return 10;
		case "11": case "11th": return 11;
		case "12": case "12th": return 12;
		case "13": case "13th": return 13;
		case "14": case "14th": return 14;
		case "15": case "15th": return 15;
		case "16": case "16th": return 16;
		case "17": case "17th": return 17;
		case "18": case "18th": return 18;
		case "19": case "19th": return 19;
		case "20": case "20th": return 20;
		case "21": case "21st": return 21;
		case "22": case "22nd": return 22;
		case "23": case "23rd": return 23;
		case "24": case "24th": return 24;
		case "25": case "25th": return 25;
		case "26": case "26th": return 26;
		case "27": case "27th": return 27;
		case "28": case "28th": return 28;
		case "29": case "29th": return 29;
		case "30": case "30th": return 30;
		case "31": case "31st": return 31;
		default: return -1;
		}
	}
	
	private boolean isValidBirthday(int month, int day) {
		if (month < 1) return false;
		if (day < 1) return false;
		if (day > Util.getDaysInMonth(month, true)) return false;
		return true;
	}
}
