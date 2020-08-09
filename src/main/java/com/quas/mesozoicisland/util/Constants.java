package com.quas.mesozoicisland.util;

import java.awt.Color;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import com.quas.mesozoicisland.JDBC;
import com.quas.mesozoicisland.enums.DinoID;
import com.quas.mesozoicisland.enums.DinosaurForm;
import com.quas.mesozoicisland.enums.DiscordChannel;
import com.quas.mesozoicisland.objects.Dinosaur;
import com.quas.mesozoicisland.objects.Player;

public class Constants {

	public static final Color COLOR = new Color(0x94BFE2);
	public static final String GUILD_ID = "643624107753340945";
	public static final long QUAS_ID = 563661703124877322L;

	public static final String JAVA_PATH = "C:\\Users\\ctsn9\\mesozoic-island\\src\\main\\java\\";
	public static final String RESOURCE_PATH = "C:\\Users\\ctsn9\\mesozoic-island\\src\\main\\resources\\";
	
	// Debug
	public static boolean ROLES_ENABLED = true;
	public static boolean SHOW_READY = true;
	public static boolean DEBUG_COMMAND = false;
	public static boolean DEBUG_BATTLE = false;
	public static int MAX_DEX_DIGITS = 3;
	public static boolean HIDE_ITEMS = true;
	
	// Multipliers
	public static final float XP_MULTIPLIER = 1f;
	public static final float XP_FRAGRANCE_BONUS = 1f;
	public static final float MONEY_FRAGRANCE_BONUS = 0.5f;
	public static final float BATTLE_FRAGRANCE_BONUS = 0.2f;
	
	// Battle
	public static final float SPECIAL_DAMAGE_MODIFIER = 1.5f;
	
	// Spawn
	public static boolean SPAWN = true;
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
	public static final int MAX_SNACK_GAIN = 2;
	public static final int LOCATION_BOOST = 10;
	public static final int MIN_HP_PER_MINUTE = 1;
	public static final int MAX_HP_PER_MINUTE = 3;
	public static final int PUBLIC_MAIL_DISPLAY = 5;
	public static final int DAYS_BETWEEN_SAME_QUEST = 14;
	
	// Misc
	public static final String ZERO_WIDTH_SPACE = "\u200B";
	public static final String BULLET_POINT = "\u2022";
	
	public static ArrayList<Pair<Dinosaur, String>> getStarterDinosaurs() {
		ArrayList<Pair<Dinosaur, String>> pairs = new ArrayList<Pair<Dinosaur, String>>();
		pairs.add(new Pair<Dinosaur, String>(Dinosaur.getDinosaur(DinoID.Megalosaurus, DinosaurForm.Standard), "Megalosaurus, meaning 'Great Lizard', is a Fire-type Dinosaur. It is a large carnivore from Europe that lived during the Middle Jurassic Period. It is thought to be the apex predator at the time for its habitat."));
		pairs.add(new Pair<Dinosaur, String>(Dinosaur.getDinosaur(DinoID.Hadrosaurus, DinosaurForm.Standard), "Hadrosaurus, meaning 'Bulky Lizard', is a Leaf-type Dinosaur. It is a large herbivore from North America that lived during the Late Cretaceous Period. Hadrosaurus is the state fossil of New Jersey."));
		pairs.add(new Pair<Dinosaur, String>(Dinosaur.getDinosaur(DinoID.Mosasaurus, DinosaurForm.Standard), "Mosasaurus, meaning 'Lizard of the Meuse River', is a Water-type Mosasaur. It is a very large carnivore from the Atlantic Ocean that lived during the Late Cretaceous Period. It preyed on virtually anything, including bony fish, sharks, birds, and even other mosasaurs."));
		pairs.add(new Pair<Dinosaur, String>(Dinosaur.getDinosaur(DinoID.Stegosaurus, DinosaurForm.Standard), "Stegosaurus, meaning 'Roofed Lizard', is a Metal-type Dinosaur. It is a large herbivore from North America that lived during the Late Jurassic Period. Due to the upright plates on its back and spikes on its tail, Stegosaurus is one of the most recognizable dinosaurs."));
		pairs.add(new Pair<Dinosaur, String>(Dinosaur.getDinosaur(DinoID.Apatosaurus, DinosaurForm.Standard), "Apatosaurus, meaning 'Deceptive Lizard', is an Ice-type Dinosaur. It is a very large herbivore from from North America that lived during the Late Jurassic Period. Apatosaurus may have used its tail as a whip to create loud noises."));
		pairs.add(new Pair<Dinosaur, String>(Dinosaur.getDinosaur(DinoID.Pterodactylus, DinosaurForm.Standard), "Pterodactylus, meaning 'Winged Finger', is an Air-type Pterosaur. It is a small carnivore from Europe and Africa that lived during the Late Jurassic Period. Pterodactylus is the first genus of pterosaur to be discovered."));
		pairs.add(new Pair<Dinosaur, String>(Dinosaur.getDinosaur(DinoID.Compsognathus, DinosaurForm.Standard), "Compsognathus, meaning 'Elegant Jaw', is a Lightning-type Dinosaur. It is a small carnivore from Europe that lived during the Late Jurassic Period. Many sources say that the Compsognathus is 'chicken-sized', but those specimens are likely juvenile."));
		pairs.add(new Pair<Dinosaur, String>(Dinosaur.getDinosaur(DinoID.Dysganus, DinosaurForm.Standard), "Dysganus, meaning 'Rough Enamel', is an Earth-type Dinosaur. It is a large herbivore from North America that lived during the Later Cretaceous Period. It is a species that is known only by a handful of teeth."));
		return pairs;
	}
	
	public static void addStarterMail(Player p) {
		JDBC.addMail(p.getIdLong(), "Welcome to Mesozoic Island", "The Mesozoic Island Team",
				String.format("Welcome, %s, to Mesozoic Island! To welcome you as an official dinosaur trainer on Mesozoic Island, here's a small welcome gift to celebrate.", p.getRawName()),
				JDBC.getReward("starter"));
	}
	
	public static void addBirthdayMail(Player p) {
		JDBC.addMail(p.getIdLong(), "Happy Birthday, " + p.getRawName(), "The Mesozoic Island Team",
				"We all wish you a happy birthday! Here's a birthday present from us to you!",
				JDBC.getReward("birthday"));
	}
	
	public static void addLevelUpMail(Player p, int level) {
		JDBC.addMail(p.getIdLong(), String.format("Level %,d Reward", level), "The Mesozoic Island Team",
				String.format("Congratulations on reaching Level %,d, %s!", level, p.getRawName()),
				JDBC.getReward("level" + level));
	}
}
