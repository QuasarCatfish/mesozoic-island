package com.quas.mesozoicisland.cmdadmin;

import java.util.ArrayList;
import java.util.regex.Pattern;

import com.quas.mesozoicisland.cmdbase.ICommand;
import com.quas.mesozoicisland.enums.AccessLevel;
import com.quas.mesozoicisland.enums.DiscordChannel;
import com.quas.mesozoicisland.enums.DiscordEmote;
import com.quas.mesozoicisland.enums.DiscordRole;
import com.quas.mesozoicisland.util.Util;

import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.Message.MentionType;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class RulesCommand implements ICommand {

	@Override
	public Pattern getCommand() {
		return pattern("admin rules");
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
		return "admin rules";
	}

	@Override
	public String getCommandDescription() {
		return "Posts the rules to this channel.";
	}

	@Override
	public DiscordChannel[] getUsableChannels() {
		return Util.arr(DiscordChannel.Rules);
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
		
		for (String s : Util.bulkify(rules, "\n" + DiscordEmote.Blank.toString() + "\n")) {
			MessageBuilder mb = new MessageBuilder(s);
			mb.denyMentions(MentionType.EVERYONE, MentionType.HERE, MentionType.ROLE, MentionType.USER);
			event.getChannel().sendMessage(mb.build()).complete();
		}
	}
}
