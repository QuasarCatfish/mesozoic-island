package com.quas.mesozoicisland.cmdadmin;

import java.util.ArrayList;
import java.util.regex.Pattern;

import com.quas.mesozoicisland.MesozoicIsland;
import com.quas.mesozoicisland.cmdbase.ICommand;
import com.quas.mesozoicisland.enums.AccessLevel;
import com.quas.mesozoicisland.enums.DiscordChannel;
import com.quas.mesozoicisland.enums.DiscordEmote;
import com.quas.mesozoicisland.enums.DiscordRole;
import com.quas.mesozoicisland.util.Util;

import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.Message.MentionType;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class InformationChannelsCommand implements ICommand {

	@Override
	public Pattern getCommand() {
		return pattern("admin information");
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
		return "Posts the necessary information to " + DiscordChannel.Rules.toString() + ", " + DiscordChannel.Introduction.toString() + ", and " + DiscordChannel.Channels.toString() + ".";
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
	public void run(MessageReceivedEvent event, String... args) {
		final String bullet = "\u25AB";
		
		{ // RULES
			ArrayList<String> rules = new ArrayList<String>();
			rules.add("__**Rules**__");
			rules.add(bullet + "**Follow Discord's Terms of Service and Community Guidelines**\nAbide by Discord's terms of service (https://discordapp.com/terms) and community guidelines (https://discordapp.com/guidelines).");
			rules.add(bullet + "**No trading in-game goods for outside currency**\nBuying or selling assets, such as dinosaurs, for real-world money is prohibited. Additionally, you are not allowed to trade for assets of another bot.");
			rules.add(bullet + "**No NSFW images or context**\nThis server should be safe for everyone. Any NSFW pictures or discussions will result in a ban.");
			rules.add(bullet + "**No illegal discussion**\nThis includes any discussion about illegal drugs or activities, as well as hacking and piracy.");
			rules.add(bullet + "**Be respectful**\nDo not use terms that are discriminitary or demeaning to other players. Additionally, if other users are asking you to not ping them, please refrain from doing so.");
			rules.add(bullet + "**Swearing**\nSwearing is allowed, as long as it is not being used as an insult.");
			rules.add(bullet + "**No advertising**\nDo not advertise other Discord servers, YouTube channels, streams, etc. This includes advertising through DMs.");
			rules.add(bullet + "**No outside conflicts**\nBringing conflicts or drama from another server to this one is not allowed.");
			rules.add(bullet + "**Dinosaur nicknames**\nDo not name your dinosaur something that violates the rules.");
			rules.add(bullet + "**Do not ignore warnings and mutes**\nIf you have been warned or muted, there was a reason for it. Make chances to improve your behavior so it doesn't happen again. Additionally, do not use alts to talk if you have been muted.");
			rules.add(bullet + "**No toxic behavior**\nExcessive passive-agressiveness, talking back to other players, putting others down regularly, and not being a kind person is considered toxic. Please avoid this.");
			rules.add(bullet + "**No spam**\nDo not send the same message repeatedly in a short time period. Additionally, do not send messages excessively filled with emoticons.");
			rules.add(bullet + "**Do not mass ping**\nPinging @everyone and @here is disabled, but you will receive a warning if you attempt to. This includes pinging a large number of people in one message.");
			rules.add(bullet + "**Don't mini-mod**\nIf you are not a moderator, don't try to moderate other players. Notifying a player that they are breaking a rule, or about to, is accetable, as long as you don't do it excessively. DM a moderator if you have an issue with another user, and they will handle it.");
			rules.add(bullet + "**Do not impersonate someone else**\nThis includes admins, moderators, bots, and other users.");
			rules.add(bullet + "**Don't talk back to Mods**\nRespect the moderators! They are here to make sure the rules are followed. If you believe a moderator is not acting appropriately for a mod, DM an admin about the issue.");
			rules.add(bullet + "**Don't ping <@&646815748181721088> or <@&646815809662091281>**\nMass pinging all admins or moderators should only be done in dire circumstances. Moderators can take care of issues with users but might not be able to help with game-related issues.");
			rules.add(bullet + "**Keep conversations in English**\nMesozoic Island is an English-speaking server. Please aim to keep all conversations in English.");
			rules.add("Breaking one or more of these rules will result in a warning, mute, or ban, depending on the severity of the infraction and your past sanctions. If you believe another user is breaking the rules, please DM a moderator, with evidence, and do not make a scene with the user.");
			rules.add("If you believe you have unjustly received a sanction, or if you are appealing a sanction, bring up the issue with an admin.");
			rules.add("Thank you and enjoy your stay here on Mesozoic Island.");
			
			TextChannel channel = DiscordChannel.Rules.getChannel(MesozoicIsland.getProfessor());
			for (String s : Util.bulkify(rules, "\n" + DiscordEmote.Blank.toString() + "\n")) {
				MessageBuilder mb = new MessageBuilder(s);
				mb.denyMentions(MentionType.EVERYONE, MentionType.HERE, MentionType.ROLE, MentionType.USER);
				channel.sendMessage(mb.build()).complete();
			}
		}

		{ // INTRODUCTION
			ArrayList<String> introduction = new ArrayList<String>();
			introduction.add("WIP");

			TextChannel channel = DiscordChannel.Introduction.getChannel(MesozoicIsland.getProfessor());
			for (String s : Util.bulkify(introduction, "\n")) {
				MessageBuilder mb = new MessageBuilder(s);
				mb.denyMentions(MentionType.EVERYONE, MentionType.HERE, MentionType.ROLE, MentionType.USER);
				channel.sendMessage(mb.build()).complete();
			}
		}

		{ // CHANNELS
			ArrayList<String> channels = new ArrayList<String>();
			channels.add("__**Introduction**__");
			channels.add(DiscordChannel.Rules.toString() + " - A list of the rules that everyone must follow.");
			channels.add(DiscordChannel.Introduction.toString() + " - Basic information on the game and links for more information.");
			channels.add(DiscordChannel.Channels.toString() + " - A list of all channels and their uses.");
			channels.add(DiscordChannel.Announcements.toString() + " - Announcements that are currently relevant.");
			channels.add(DiscordChannel.AnnouncementLog.toString() + " - Log of all announcements.");
			channels.add(DiscordChannel.DailyAnnouncements.toString() + " - Announcements for daily features and updates.");
			channels.add(DiscordChannel.Changelog.toString() + " - List of new and updated features to the game.");
			channels.add(DiscordEmote.Blank.toString());
			
			channels.add("__**Game**__");
			channels.add(DiscordChannel.Game.toString() + " - Where all the action happens!");
			channels.add(DiscordChannel.Events.toString() + " - Information about in-game events.");
			channels.add(DiscordChannel.BotCommands.toString() + " - A channel for users to use their spammy commands.");
			channels.add(DiscordChannel.Wiki.toString() + " - A channel for the Mesozoic Island wiki.");
			channels.add(DiscordEmote.Blank.toString());
			
			channels.add("__**Chatting**__");
			channels.add("Channels for chatting about various topics. See channel topics for more information.");
			channels.add(DiscordEmote.Blank.toString());

			channels.add("__**Battle**__");
			channels.add(DiscordChannel.Battle1.toString() + " - Novice Tier wild battles.");
			channels.add(DiscordChannel.Battle2.toString() + " - Advanced Tier wild battles.");
			channels.add(DiscordChannel.Battle3.toString() + " - Master Tier wild battles.");
			channels.add(DiscordChannel.BattleLog.toString() + " - Logs battles from wild battles.");
			channels.add(DiscordChannel.BattleDungeon.toString() + " - Dungeon battles.");
			channels.add(DiscordChannel.BattleDungeonLog.toString() + " - Logs battles from dungeons.");
			channels.add(DiscordChannel.BattleSpecial.toString() + " - Special battles that do not fit in any other battle channel.");
			channels.add(DiscordChannel.BattleSpecialLog.toString() + " - Logs special battles.");
			channels.add(DiscordEmote.Blank.toString());

			channels.add("__**Suggestions**__");
			channels.add(DiscordChannel.GameSuggestions.toString() + " - Where you can make suggestions for the game or server.");
			channels.add(DiscordChannel.SuggestionFeedback.toString() + " - Where you can discuss other peoples' suggestions.");
			channels.add(DiscordChannel.PreviousSuggestions.toString() + " - Where you can see what suggestions have been previously made.");
			channels.add(DiscordChannel.BugReports.toString() + " - Where you can report bugs that you find.");
			channels.add(DiscordEmote.Blank.toString());

			TextChannel channel = DiscordChannel.Channels.getChannel(MesozoicIsland.getProfessor());
			for (String s : Util.bulkify(channels, "\n")) {
				MessageBuilder mb = new MessageBuilder(s);
				mb.denyMentions(MentionType.EVERYONE, MentionType.HERE, MentionType.ROLE, MentionType.USER);
				channel.sendMessage(mb.build()).complete();
			}
		}
	}
}
