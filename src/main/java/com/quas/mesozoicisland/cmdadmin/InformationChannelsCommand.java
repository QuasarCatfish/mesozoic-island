package com.quas.mesozoicisland.cmdadmin;

import java.util.ArrayList;
import java.util.regex.Pattern;

import com.quas.mesozoicisland.MesozoicIsland;
import com.quas.mesozoicisland.cmdbase.ICommand;
import com.quas.mesozoicisland.enums.AccessLevel;
import com.quas.mesozoicisland.enums.DiscordChannel;
import com.quas.mesozoicisland.enums.DiscordEmote;
import com.quas.mesozoicisland.enums.DiscordRole;
import com.quas.mesozoicisland.util.Constants;
import com.quas.mesozoicisland.util.Util;

import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.Message.MentionType;
import net.dv8tion.jda.api.entities.IMentionable;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class InformationChannelsCommand implements ICommand {

	@Override
	public Pattern getCommand() {
		return pattern("admin information( ", CHANNEL, ")+");
	}

	@Override
	public AccessLevel getAccessLevel() {
		return AccessLevel.Admin;
	}

	@Override
	public String getCommandName() {
		return "admin";
	}

	@Override
	public String getCommandSyntax() {
		return "admin information";
	}

	@Override
	public String getCommandDescription() {
		return "Posts the necessary information to " + DiscordChannel.Rules.toString() + ", " + DiscordChannel.Introduction.toString() + ", and " + DiscordChannel.ServersAndChannels.toString() + ".";
	}

	@Override
	public DiscordChannel[] getUsableChannels() {
		return DiscordChannel.ALL_CHANNELS;
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
	public synchronized void run(MessageReceivedEvent event, String... args) {
		ArrayList<Long> c = new ArrayList<Long>();
		for (IMentionable tc : event.getMessage().getMentions(MentionType.CHANNEL)) {
			c.add(tc.getIdLong());
		}

		final String bullet = "\u25AB";
		
		if (c.contains(DiscordChannel.Rules.getIdLong())) {
			ArrayList<String> rules = new ArrayList<String>();
			rules.add("__**Rules**__");
			rules.add(bullet + "**Follow Discord's Terms of Service and Community Guidelines**\nAbide by Discord's terms of service (<https://discord.com/terms>) and community guidelines (<https://discord.com/guidelines>).");
			rules.add(bullet + "**No trading in-game goods for outside currency**\nBuying or selling assets, such as dinosaurs, for real-world money is prohibited. Additionally, you are not allowed to trade for assets of another bot.");
			rules.add(bullet + "**No NSFW images or context**\nThis server should be safe for everyone. Any NSFW pictures or discussions will result in a ban.");
			rules.add(bullet + "**No illegal discussion**\nThis includes any discussion about illegal drugs or activities, as well as hacking and piracy.");
			rules.add(bullet + "**Be respectful**\nDo not use terms that are discriminitary or demeaning to other players. Additionally, if other users are asking you to not ping them, please refrain from doing so.");
			rules.add(bullet + "**Swearing**\nSwearing is allowed, as long as it is not being used as an insult.");
			rules.add(bullet + "**No advertising**\nDo not advertise other Discord servers, YouTube channels, streams, etc. This includes advertising through DMs.");
			rules.add(bullet + "**No outside conflicts**\nBringing conflicts or drama from another server to this one is not allowed.");
			rules.add(bullet + "**Dinosaur nicknames**\nDo not name your dinosaur something that violates the rules.");
			rules.add(bullet + "**Do not ignore warnings and mutes**\nIf you have been warned or muted, there was a reason for it. Make changes to improve your behavior so it doesn't happen again. Additionally, do not use alts to talk if you have been muted.");
			rules.add(bullet + "**No toxic behavior**\nExcessive passive-agressiveness, talking back to other players, putting others down regularly, and not being a kind person is considered toxic. Please avoid this.");
			rules.add(bullet + "**No spam**\nDo not send the same message repeatedly in a short time period. Additionally, do not send messages excessively filled with emoticons.");
			rules.add(bullet + "**Do not mass ping**\nPinging @everyone and @here is disabled, but you will receive a warning if you attempt to. This includes pinging a large number of people in one message.");
			rules.add(bullet + "**Don't mini-mod**\nIf you are not a moderator, don't try to moderate other players. Notifying a player that they are breaking a rule, or about to, is acceptable, as long as you don't do it excessively. DM a moderator if you have an issue with another user, and they will handle it.");
			rules.add(bullet + "**Do not impersonate someone else**\nThis includes admins, moderators, bots, and other users.");
			rules.add(bullet + "**Respect the Moderators**\nRespect the moderators and don't talk back to them! They are here to make sure the rules are followed. If you believe a moderator is not acting appropriately for a mod, DM an admin about the issue.");
			rules.add(bullet + "**Don't ping <@&646815748181721088> or <@&646815809662091281>**\nMass pinging all admins or moderators should only be done in dire circumstances. Moderators can take care of issues with users but might not be able to help with game-related issues.");
			rules.add(bullet + "**Keep conversations in English**\nMesozoic Island is an English-speaking server. Please aim to keep all conversations in English.");
			rules.add("Breaking one or more of these rules will result in a warning, mute, or ban, depending on the severity of the infraction and your past sanctions. If you believe another user is breaking the rules, please DM a moderator, with evidence, and do not make a scene with the user.");
			rules.add("If you believe you have unjustly received a sanction, or if you are appealing a sanction, bring up the issue with an admin.");
			rules.add("Thank you and enjoy your stay here on Mesozoic Island.");
			
			TextChannel channel = DiscordChannel.Rules.getChannel(MesozoicIsland.getProfessor());
			Util.clearChannelOldMessages(channel);

			for (String s : Util.bulkify(rules, "\n" + DiscordEmote.getBlank().toString() + "\n")) {
				MessageBuilder mb = new MessageBuilder(s);
				mb.denyMentions(MentionType.EVERYONE, MentionType.HERE, MentionType.ROLE, MentionType.USER);
				channel.sendMessage(mb.build()).complete();
			}
		}

		if (c.contains(DiscordChannel.Introduction.getIdLong())) {
			ArrayList<String> introduction = new ArrayList<String>();
			introduction.add("Welcome, trainers, to **Mesozoic Island**, an island inhabited by hundreds of dinosaur species! I am the resident researcher on this island, Professor Megan Lowe, and it is my dream to discover all the secrets and mysteries that this island holds.");
			introduction.add("My assistant is <@644300007259897856>, but most people just call her Elise. She's a robot that helps me and all of you a whole bunch, even if you don't realize it.");
			introduction.add("A lot of information about Mesozoic Island can be found on various pages on our wiki. Check it out at <https://mesozoic-island.amazingwikis.org/wiki/Main_Page>.");
			introduction.add(DiscordEmote.getBlank().toString());
			
			introduction.add("__Helpful Commands:__");
			introduction.add(Constants.BULLET_POINT + " Using the `help` command lists all usable commands. You can also use this command to learn more about other commands.");
			introduction.add(Constants.BULLET_POINT + " You can use the `daily` command to receive 1,000 Dinosaur Coins every day. The timer to use this command resets every midnight. Some events might give you extra items when you claim your daily.");
			introduction.add(Constants.BULLET_POINT + " The `bag` command lists all of the items you have with you.");
			introduction.add(Constants.BULLET_POINT + " To learn more about a specific item, you can use the `item` command.");
			introduction.add(Constants.BULLET_POINT + " To receive pings for various happenings, use the `pingme` command.");
			introduction.add(Constants.BULLET_POINT + " The `shop` command lists all items available for purchase, which you can purchase with the `buy` command.");
			introduction.add(Constants.BULLET_POINT + " For more information about commands, you can visit the wiki at <https://mesozoic-island.amazingwikis.org/wiki/Commands>.");
			introduction.add(DiscordEmote.getBlank().toString());
			
			introduction.add("__Dinosaurs:__");
			introduction.add(Constants.BULLET_POINT + " There are a few key stats to note about each dinosaur: Element, Rarity, Health, Attack, Defense, Level, and Rank.");
			introduction.add(Constants.BULLET_POINT + " The Element of your dinosaur determines if its attacks are strong or weak against another dinosaur and your own.");
			introduction.add(Constants.BULLET_POINT + " The Rarity of your dinosaur governs how likely it is to spawn. The rarer your dinosaur, the less likely you are to encounter it in a wild battle.");
			introduction.add(Constants.BULLET_POINT + " The Health of your dinosaur dictates how much damage your dinosaur can take before it gets knocked out. The higher your dinosaur's health, the longer it will last in battle.");
			introduction.add(Constants.BULLET_POINT + " The Attack of your dinosaur dictates how much damage your dinosaur deals to other dinosaurs each time it attacks. The higher your dinosaur's attack, the faster your opponents will get knocked out.");
			introduction.add(Constants.BULLET_POINT + " The Defense of your dinosaur dictates how much damage your dinosaur resists when getting attacked. The higher your dinosaur's defense, the less damage it will take each attack.");
			introduction.add(Constants.BULLET_POINT + " The Level of your dinosaur relates to how much Experience your dinosaur has earned. You can earn experience from defeating other dinosaurs or giving it potions.");
			introduction.add(Constants.BULLET_POINT + " The Rank of your dinosaur relates to how many of that dinosaur you have caught. Since you can only have one of each type of dinosaur, defeating another wild one will yield a Rank Point instead of a dinosaur.");
			introduction.add(Constants.BULLET_POINT + " The higher the Level and Rank of your dinosaur, the stronger it will be.");
			introduction.add(Constants.BULLET_POINT + " The `dinos` command lists all dinosaurs you currently own.");
			introduction.add(Constants.BULLET_POINT + " To create more of a bond, you can give your dinosaur a name with the `nickname` command.");
			introduction.add(Constants.BULLET_POINT + " Using the `elements` command will list which elements are strong or weak against other elements.");
			introduction.add(Constants.BULLET_POINT + " Learn more about a specific dinosaur of yours with the `info` command.");
			introduction.add(Constants.BULLET_POINT + " For more information about dinosaurs, including a list of every dinosaur currently in the game, you can visit the wiki at <https://mesozoic-island.amazingwikis.org/wiki/Dinosaurs>.");
			introduction.add(DiscordEmote.getBlank().toString());
			
			/**
			introduction.add("__Runes:__");
			introduction.add(Constants.BULLET_POINT + "");
			introduction.add(DiscordEmote.Blank.toString());
			*/

			introduction.add("__Battles:__");
			introduction.add(Constants.BULLET_POINT + " To enter a battle, you must select a team of up to three dinosaurs to participate, using the `select` command.");
			introduction.add(Constants.BULLET_POINT + " Wild dinosaurs can spawn in groups of one, two, or three for each of the tiers of battle. This is also the maximum number of dinosaurs that will be sent into battle, even if you have more selected.");
			introduction.add(Constants.BULLET_POINT + " Battles against wild dinosaurs are free-for-alls. Trainers and the wild dinosaurs can each attack all trainers in the battle. Any trainer who defeats a wild dinosaur will receive its crystal and add it to their collection.");
			introduction.add(Constants.BULLET_POINT + " Defeating any dinosaur in a battle will reward your dinosaur with Experience, used to level up your dinosaur.");
			introduction.add(Constants.BULLET_POINT + " The tier of battle that you enter is dictated by which dinosaurs you have selected. When selecting your dinosaurs, or when you use the `selected` command, the tier of your team will be displayed.");
			introduction.add(DiscordEmote.getBlank().toString());

			introduction.add("__Eggs:__");
			introduction.add(Constants.BULLET_POINT + " Occasionally, instead of wild dinosaurs spawning, an Egg will be found. As long as you have a free Egg Incubator, you have a chance of taking home that egg.");
			introduction.add(Constants.BULLET_POINT + " Eggs have a set number of Hatch Points they need before they are able to be hatched.");
			introduction.add(Constants.BULLET_POINT + " Every minute, all eggs gain a random amount of Hatch Points between one and three.");
			introduction.add(Constants.BULLET_POINT + " To see the list of eggs you have, and how many Hatch Points they have and need, you can use the `eggs` command.");
			introduction.add(Constants.BULLET_POINT + " To see more detailed information about your egg, you can use the `info` command.");
			introduction.add(Constants.BULLET_POINT + " If your egg has the required hatch points, you can hatch the egg with the `hatch` command.");
			introduction.add(Constants.BULLET_POINT + " Egg Salesman Benedict can give or hatch your eggs in exchange for Dinosaur Coins");
			introduction.add(Constants.BULLET_POINT + " For more information about Eggs, you can visit the wiki at <https://mesozoic-island.amazingwikis.org/wiki/Eggs>.");
			introduction.add(DiscordEmote.getBlank().toString());

			introduction.add("__Standard Dungeons:__");
			introduction.add(Constants.BULLET_POINT + " Occasionally, instead of wild dinosuars spawning, a Dungeon will appear. All trainers of any tier are able to join the dungeon expedition.");
			introduction.add(Constants.BULLET_POINT + " Unlike wild battles, dinosaur trainers work together to defeat the Dungeon Dinosaurs present in the mysterious Dungeons.");
			introduction.add(Constants.BULLET_POINT + " Dungeons are made up of multiple floors, each floor having a difficult battle. There can range between three and ten floors in a dungeon.");
			introduction.add(Constants.BULLET_POINT + " The final floor of the dungeon houses a single dinosaur, the Dungeon Boss. This dinosaur is extremely difficult to defeat.");
			introduction.add(Constants.BULLET_POINT + " Dungeons also have an associated difficulty. The more stars a dungeon has, the harder each battle will be, but the better rewards you'll find at the end.");
			introduction.add(Constants.BULLET_POINT + " Defeating a dungeon rewards all trainers with Dungeon Tokens, which can be used to obtain Dungeon Dinosaurs of your own.");
			introduction.add(Constants.BULLET_POINT + " In addition to Dungeon Tokens, trainers can earn Charm Shards based on the number of dinosaurs in the dungeon holding a Charm.");
			introduction.add(Constants.BULLET_POINT + " For more information about Standard Dungeons, you can visit the wiki at <https://mesozoic-island.amazingwikis.org/wiki/Dungeons>.");
			introduction.add(DiscordEmote.getBlank().toString());

			introduction.add("__Chaos Dungeons:__");
			introduction.add(Constants.BULLET_POINT + " Unlike regular dungeons, which have a single Dungeon Boss Dinosaur on the final floor, a Chaos Dungeon will feature a team of Chaos Dinosaurs and a Chaos Boss Dinosaur.");
			introduction.add(Constants.BULLET_POINT + " The rest of the dungeon features stronger than normal Dungeon Dinosaurs, which are powered up by the Chaos Boss Dinosaur.");
			introduction.add(Constants.BULLET_POINT + " At the very end of the Chaos Dungeon is a mysterious egg. A trainer at random will take this egg with them if they make it past the Chaos Boss. What could be inside?");
			introduction.add(Constants.BULLET_POINT + " Chaos Dungeons will also yield more Dungeon Tokens than a Standard Dungeon.");
			introduction.add(Constants.BULLET_POINT + " For more information about Chaos Dungeons, you can visit the wiki at <https://mesozoic-island.amazingwikis.org/wiki/Dungeons>.");
			introduction.add(DiscordEmote.getBlank().toString());

			introduction.add("__InfiniDungeons:__");
			introduction.add(Constants.BULLET_POINT + " Unlike regular dungeons, which have a set number of floors, the InfiniDungeon is endless and trainers will continue until they lose a battle.");
			introduction.add(Constants.BULLET_POINT + " Each floor is significantly more difficult than the last, but will give a lot more Experience too.");
			introduction.add(Constants.BULLET_POINT + " Since traversal through an InfiniDungeon is guaranteed to end in failure, the reward is instead based on the number of floors cleared.");
			introduction.add(Constants.BULLET_POINT + " For more information about InfiniDungeons, you can visit the wiki at <https://mesozoic-island.amazingwikis.org/wiki/Dungeons>.");
			introduction.add(DiscordEmote.getBlank().toString());

			introduction.add("__Raid Battles:__");
			introduction.add(Constants.BULLET_POINT + " Once a trainer reaches Level 50, alongside their normal daily, the trainer will receive a Raid Pass.");
			introduction.add(Constants.BULLET_POINT + " Using the Raid Pass will immediately start a battle against the daily Raid Boss, so be sure your team is ready before using it.");
			introduction.add(Constants.BULLET_POINT + " The Raid Boss's level scales with your player level, starting at Level 100.");
			introduction.add(Constants.BULLET_POINT + " For every 10 levels above Level 50, trainers will receive an additional Raid Pass every day.");
			introduction.add(Constants.BULLET_POINT + " Defeating the Raid Boss will grant various rewards randomly, including Dinosaur Coins, Colored Dungeon Locators, or Prismatic Converters.");
			introduction.add(Constants.BULLET_POINT + " For more information about Raids, you can visit the wiki at <https://mesozoic-island.amazingwikis.org/wiki/Raids>.");
			introduction.add(DiscordEmote.getBlank().toString());

			TextChannel channel = DiscordChannel.Introduction.getChannel(MesozoicIsland.getProfessor());
			Util.clearChannelOldMessages(channel);

			for (String s : Util.bulkify(introduction, "\n")) {
				MessageBuilder mb = new MessageBuilder(s);
				mb.denyMentions(MentionType.EVERYONE, MentionType.HERE, MentionType.ROLE, MentionType.USER);
				channel.sendMessage(mb.build()).complete();
			}
		}

		if (c.contains(DiscordChannel.ServersAndChannels.getIdLong())) {
			ArrayList<String> channels = new ArrayList<String>();
			channels.add(DiscordEmote.getBlank().toString());

			channels.add("__**Information**__");
			channels.add(DiscordChannel.Rules.toString() + " - The rules of the server! All trainers must follow them if they don't want to get muted or banned.");
			channels.add(DiscordChannel.Introduction.toString() + " - An introduction to major features of the game are listed here. More information about these features can be found on the wiki.");
			channels.add(DiscordChannel.ServersAndChannels.toString() + " - A list of related servers and all the channels in this server.");
			channels.add(DiscordChannel.Announcements.toString() + " - Recent and relevant announcements can be found here. You can enable pings for the various types of announcements if you want to be notified immediately.");
			channels.add(DiscordChannel.AnnouncementLog.toString() + " - All announcements get logged here (unless the bot is offline when the announcement is made).");
			channels.add(DiscordChannel.DailyAnnouncements.toString() + " - Every day, an announcement here is made on relevant daily topics, such as raids and quests.");
			channels.add(DiscordChannel.Changelog.toString() + " - A changelog of all the major updates to the game. More information about each update can be found on the wiki.");
			channels.add(DiscordEmote.getBlank().toString());
			
			channels.add("__**Game**__");
			channels.add(DiscordChannel.Game.toString() + " - Any dinosaurs, eggs, and dungeons that spawn will spawn in this channel.");
			channels.add(DiscordChannel.Trading.toString() + " - Discusion about trading and trading dinosaurs, eggs, and items.");
			channels.add(DiscordChannel.BotCommands.toString() + " - Mass usage of commands should be performed in this channel to prevent spam.");
			channels.add(DiscordChannel.Wiki.toString() + " - Discussion about the Mesozoic Island wiki and any changes made to the wiki.");
			channels.add(DiscordChannel.Giveaways.toString() + " - A channel for various giveaways of Mesozoic Island items and other prizes.");
			channels.add(DiscordChannel.Events.toString() + " - Information about currently active and past events on Mesozoic Island.");
			channels.add(DiscordChannel.Contest.toString() + " - Information about contests overall and the current or most recent contest.");
			channels.add(DiscordEmote.getBlank().toString());
			
			channels.add("__**Chatting**__");
			channels.add("Channels for chatting about various topics. See channel topics for more information.");
			channels.add(DiscordEmote.getBlank().toString());

			channels.add("__**Battle**__");
			channels.add(DiscordChannel.Battle1.toString() + " - Battles that are in the Novice Tier happen here.");
			channels.add(DiscordChannel.Battle2.toString() + " - Battles that are in the Advanced Tier happen here.");
			channels.add(DiscordChannel.Battle3.toString() + " - Battles that are in the Elite Tier happen here.");
			// channels.add(DiscordChannel.Battle4.toString() + " - Battles that are in the Master Tier happen here.");
			channels.add(DiscordChannel.BattleContest.toString() + " - Battles that are in the Contest Tier happen here.");
			channels.add(DiscordChannel.BattleLog.toString() + " - A log of all battles from the aforementioned channels.");
			channels.add(DiscordChannel.BattleDungeon.toString() + " - Battles against dungeons happen here.");
			channels.add(DiscordChannel.BattleDungeonLog.toString() + " - A log of all dungeon battles.");
			channels.add(DiscordChannel.BattleRaid.toString() + " - Battles against Raid Boss Dinosaurs happen here.");
			channels.add(DiscordChannel.BattleSpecialLog.toString() + " - A log of all special battles.");
			channels.add(DiscordEmote.getBlank().toString());

			channels.add("__**Suggestions**__");
			channels.add(DiscordChannel.GameSuggestions.toString() + " - Suggestions for major features or changes to the game should be here. You can also vote on other people's suggestions.");
			channels.add(DiscordChannel.SuggestionFeedback.toString() + " - A channel to discuss suggestions for the game.");
			channels.add(DiscordChannel.PreviousSuggestions.toString() + " - A list of all previously accepted or rejected suggestions.");
			channels.add(DiscordChannel.BugReports.toString() + " - Where you can report bugs that you find.");
			channels.add(DiscordChannel.Feedback.toString() + " - Feedback or small suggestions for the game should be in this channel.");
			channels.add(DiscordEmote.getBlank().toString());

			TextChannel channel = DiscordChannel.ServersAndChannels.getChannel(MesozoicIsland.getProfessor());
			Util.clearChannelOldMessages(channel);

			// send server info
			String[] serverInfo = new String[] {
				"Want more game emotes? Join the Extra Emotes server at https://discord.gg/xxmneMuc54 .",
				"Want dual-type element emotes? Join the Element Emotes server at https://discord.gg/AaWMSJSCdW .",
			};
			for (String s : serverInfo) {
				MessageBuilder mb = new MessageBuilder(s);
				mb.denyMentions(MentionType.EVERYONE, MentionType.HERE, MentionType.ROLE, MentionType.USER);
				channel.sendMessage(mb.build()).complete();
			}

			// send channel info
			for (String s : Util.bulkify(channels, "\n")) {
				MessageBuilder mb = new MessageBuilder(s);
				mb.denyMentions(MentionType.EVERYONE, MentionType.HERE, MentionType.ROLE, MentionType.USER);
				channel.sendMessage(mb.build()).complete();
			}
		}
	}
}
