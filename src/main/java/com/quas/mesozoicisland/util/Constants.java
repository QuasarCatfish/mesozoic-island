package com.quas.mesozoicisland.util;

import java.awt.Color;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import com.quas.mesozoicisland.JDBC;
import com.quas.mesozoicisland.enums.DiscordChannel;
import com.quas.mesozoicisland.objects.Dinosaur;
import com.quas.mesozoicisland.objects.Player;

public class Constants {

	public static final Color COLOR = new Color(0x94BFE2);
	public static final String GUILD_ID = "643624107753340945";

	public static final String JAVA_PATH = "C:\\Users\\ctsn9\\mesozoic-island\\src\\main\\java\\";
	public static final String RESOURCE_PATH = "C:\\Users\\ctsn9\\mesozoic-island\\src\\main\\resources\\";
	
	// Debug
	public static boolean ROLES_ENABLED = true;
	public static boolean SHOW_READY = true;
	public static boolean DEBUG_COMMAND = false;
	public static boolean DEBUG_BATTLE = false;
	public static int MAX_DEX_DIGITS = 3;
	public static boolean HIDE_ITEMS = false;
	
	// Multipliers
	public static final float XP_MULTIPLIER = 1f;
	public static final float XP_FRAGRANCE_BONUS = 1f;
	public static final float MONEY_FRAGRANCE_BONUS = 0.5f;
	public static final float BATTLE_FRAGRANCE_BONUS = 0.2f;
	
	// Battle
	public static final float SPECIAL_DAMAGE_MODIFIER = 1.5f;
	
	// Spawn
	public static final long MIN_SPAWN_TIMER = TimeUnit.MINUTES.toMillis(2);
	public static final long MAX_SPAWN_TIMER = TimeUnit.MINUTES.toMillis(8);
	public static final long BATTLE_WAIT = TimeUnit.SECONDS.toMillis(90);
	public static final DiscordChannel SPAWN_CHANNEL = DiscordChannel.Game;
	
	// Eggs
	public static boolean SPAWN_EGGS = true;
	public static boolean UPDATE_EGG_HP = true;
	public static final int EGG_SPAWN_CHANCE = 20;
	public static final int MAX_EGG_SPAWN = 1;
	public static final long EGG_WAIT = TimeUnit.SECONDS.toMillis(90);
	
	// Dungeons
	public static boolean SPAWN_DUNGEONS = true;
	public static final int DUNGEON_SPAWN_CHANCE = 50;
	public static final int MAX_DUNGEON_DIFFICULTY = 5;
	public static final int MIN_DUNGEON_FLOORS = 3;
	public static final int MAX_DUNGEON_FLOORS = 10;
	public static final long DUNGEON_WAIT = TimeUnit.SECONDS.toMillis(150);
	
	// Raids
	public static final int REQUIRED_RAID_LEVEL = 50;
	
	// Contest
	public static boolean CONTEST = false;
	
	// Daily
	public static final long DAILY_MONEY = 1_000;
	public static final long BONUS_DAILY_PER_DAY = 25;
	public static final int MAX_BONUS_DAYS = 10;
	
	// Player Activity
	public static final long ACTIVE_PLAYER_TIMER = TimeUnit.DAYS.toMillis(7);
	
	// Leaderboard
	public static final int MAX_LEADERBOARD_LENGTH = 10;
	public static final int MAX_LEADERBOARD_CHECK = 25;
	public static final int LEADERBOARD_REQUIRED_BATTLES = 5;
	
	// Limits
	public static final int DINOS_PER_TEAM = 3;
	public static final int MAX_LEVEL = 50;
	public static final long MAX_XP = DinoMath.getXp(MAX_LEVEL);
	public static final int MAX_RANK = 20;
	public static final int MAX_RP = DinoMath.getRp(MAX_RANK);
	public static final int MAX_STAT_BOOST = 25;
	public static final int MAX_SNACK_GAIN = 3;
	public static final int LOCATION_BOOST = 10;
	public static final int MIN_HP_PER_MINUTE = 1;
	public static final int MAX_HP_PER_MINUTE = 3;
	public static final int PUBLIC_MAIL_DISPLAY = 5;
	public static final int DAYS_BETWEEN_SAME_QUEST = 14;
	
	// Misc
	public static final String ZERO_WIDTH_SPACE = "\u200B";
	
	public static ArrayList<Pair<Dinosaur, String>> getStarterDinosaurs() {
		ArrayList<Pair<Dinosaur, String>> pairs = new ArrayList<Pair<Dinosaur, String>>();
		pairs.add(new Pair<Dinosaur, String>(Dinosaur.getDinosaur(new Pair<Integer, Integer>(1, 0)), "Pterodactylus/Air")); // Pterodactylus Air
		pairs.add(new Pair<Dinosaur, String>(Dinosaur.getDinosaur(new Pair<Integer, Integer>(4, 0)), "Mosasaurus/Water")); // Mosasaurus Water
		pairs.add(new Pair<Dinosaur, String>(Dinosaur.getDinosaur(new Pair<Integer, Integer>(5, 0)), "Megalosaurus/Fire")); // Megalosaurus Fire
		pairs.add(new Pair<Dinosaur, String>(Dinosaur.getDinosaur(new Pair<Integer, Integer>(38, 0)), "Hadrosaurus/Leaf")); // Hadrosaurus Leaf
		pairs.add(new Pair<Dinosaur, String>(Dinosaur.getDinosaur(new Pair<Integer, Integer>(39, 0)), "Compsognathus/Lightning")); // Compsognathus Lightning
		pairs.add(new Pair<Dinosaur, String>(Dinosaur.getDinosaur(new Pair<Integer, Integer>(85, 0)), "Dysganus/Earth")); // Dysganus Earth
		pairs.add(new Pair<Dinosaur, String>(Dinosaur.getDinosaur(new Pair<Integer, Integer>(90, 0)), "Apatosaurus/Ice")); // Apatosaurus Ice
		pairs.add(new Pair<Dinosaur, String>(Dinosaur.getDinosaur(new Pair<Integer, Integer>(91, 0)), "Stegosaurus/Metal")); // Stegosaurus Metal
		return pairs;
	}
	
	public static void addStarterMail(Player p) {
		JDBC.addMail(p.getIdLong(), "Welcome to Mesozoic Island", "The Mesozoic Island Team",
				String.format("Welcome, %s, to Mesozoic Island! As a thank you gift for starting your adventure on this island, here's a small welcome gift from us to you.", p.getRawName()),
				JDBC.getReward("starter"));
	}
	
	public static void addBirthdayMail(Player p) {
		JDBC.addMail(p.getIdLong(), "Happy Birthday, " + p.getRawName(), "The Mesozoic Island Team",
				"We all wish you a happy birthday! Here's a birthday present from us to you!",
				JDBC.getReward("birthday"));
	}
	
	private static final String[] LEVEL_UP_REWARDS = new String[] {
			null, // Level 0
			null, // Level 1
			"item 100 0 1000", // Level 2
			"item 100 0 1000", // Level 3
			"item 100 0 1000", // Level 4
			"item 100 0 1000 item 5 0 1", // Level 5
			"item 100 0 1000", // Level 6
			"item 100 0 1000", // Level 7
			"item 100 0 1000", // Level 8
			"item 100 0 1000", // Level 9
			"item 100 0 1000", // Level 10
			"item 100 0 1000", // Level 11
			"item 100 0 1000", // Level 12
			"item 100 0 1000", // Level 13
			"item 100 0 1000", // Level 14
			"item 100 0 1000", // Level 15
			"item 100 0 1000", // Level 16
			"item 100 0 1000", // Level 17
			"item 100 0 1000", // Level 18
			"item 100 0 1000", // Level 19
			"item 100 0 1000 item 2 0 1", // Level 20
			"item 100 0 1000", // Level 21
			"item 100 0 1000", // Level 22
			"item 100 0 1000", // Level 23
			"item 100 0 1000", // Level 24
			"item 100 0 1000", // Level 25
			"item 100 0 1000", // Level 26
			"item 100 0 1000", // Level 27
			"item 100 0 1000", // Level 28
			"item 100 0 1000", // Level 29
			"item 100 0 1000", // Level 30
			"item 100 0 1000", // Level 31
			"item 100 0 1000", // Level 32
			"item 100 0 1000", // Level 33
			"item 100 0 1000", // Level 34
			"item 100 0 1000", // Level 35
			"item 100 0 1000", // Level 36
			"item 100 0 1000", // Level 37
			"item 100 0 1000", // Level 38
			"item 100 0 1000", // Level 39
			"item 100 0 1000", // Level 40
			"item 100 0 1000", // Level 41
			"item 100 0 1000", // Level 42
			"item 100 0 1000", // Level 43
			"item 100 0 1000", // Level 44
			"item 100 0 1000", // Level 45
			"item 100 0 1000", // Level 46
			"item 100 0 1000", // Level 47
			"item 100 0 1000", // Level 48
			"item 100 0 1000", // Level 49
			"item 100 0 1000", // Level 50
			"item 100 0 1000", // Level 51
			"item 100 0 1000", // Level 52
			"item 100 0 1000", // Level 53
			"item 100 0 1000", // Level 54
			"item 100 0 1000", // Level 55
			"item 100 0 1000", // Level 56
			"item 100 0 1000", // Level 57
			"item 100 0 1000", // Level 58
			"item 100 0 1000", // Level 59
			"item 100 0 1000", // Level 60
			"item 100 0 1000", // Level 61
			"item 100 0 1000", // Level 62
			"item 100 0 1000", // Level 63
			"item 100 0 1000", // Level 64
			"item 100 0 1000", // Level 65
			"item 100 0 1000", // Level 66
			"item 100 0 1000", // Level 67
			"item 100 0 1000", // Level 68
			"item 100 0 1000", // Level 69
			"item 100 0 1000", // Level 70
			"item 100 0 1000", // Level 71
			"item 100 0 1000", // Level 72
			"item 100 0 1000", // Level 73
			"item 100 0 1000", // Level 74
			"item 100 0 1000", // Level 75
			"item 100 0 1000", // Level 76
			"item 100 0 1000", // Level 77
			"item 100 0 1000", // Level 78
			"item 100 0 1000", // Level 79
			"item 100 0 1000", // Level 80
			"item 100 0 1000", // Level 81
			"item 100 0 1000", // Level 82
			"item 100 0 1000", // Level 83
			"item 100 0 1000", // Level 84
			"item 100 0 1000", // Level 85
			"item 100 0 1000", // Level 86
			"item 100 0 1000", // Level 87
			"item 100 0 1000", // Level 88
			"item 100 0 1000", // Level 89
			"item 100 0 1000", // Level 90
			"item 100 0 1000", // Level 91
			"item 100 0 1000", // Level 92
			"item 100 0 1000", // Level 93
			"item 100 0 1000", // Level 94
			"item 100 0 1000", // Level 95
			"item 100 0 1000", // Level 96
			"item 100 0 1000", // Level 97
			"item 100 0 1000", // Level 98
			"item 100 0 1000", // Level 99
			"item 100 0 1000", // Level 100
	};
	
	public static void addLevelUpMail(Player p, int level) {
		JDBC.addMail(p.getIdLong(), String.format("Level %,d Reward", level), "The Mesozoic Island Team",
				String.format("Congratulations on reaching Level %,d, %s!", level, p.getRawName()),
				level < LEVEL_UP_REWARDS.length ? LEVEL_UP_REWARDS[level] : null);
	}
}
