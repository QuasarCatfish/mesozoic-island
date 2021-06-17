package com.quas.mesozoicisland.cmdplayer;

import java.util.StringJoiner;
import java.util.regex.Pattern;

import com.quas.mesozoicisland.cmdbase.ICommand;
import com.quas.mesozoicisland.enums.AccessLevel;
import com.quas.mesozoicisland.enums.DiscordChannel;
import com.quas.mesozoicisland.enums.DiscordRole;
import com.quas.mesozoicisland.util.Util;

import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.Message.MentionType;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class CreditsCommand implements ICommand {

	@Override
	public Pattern getCommand() {
		return pattern("credits");
	}

	@Override
	public AccessLevel getAccessLevel() {
		return AccessLevel.Trainer;
	}

	@Override
	public String getCommandName() {
		return "credits";
	}

	@Override
	public String getCommandSyntax() {
		return "credits";
	}

	@Override
	public String getCommandDescription() {
		return "Lists the credits for the game.";
	}

	@Override
	public DiscordChannel[] getUsableChannels() {
		return Util.arr(DiscordChannel.DirectMessages);
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
		StringJoiner sj = new StringJoiner("\n");

		sj.add("__**Credits:**__");
		sj.add("Developer: <@563661703124877322>"); // Quas
		sj.add("Emote Artist: <@484873357045792769>"); // Jamie
		sj.add("Emote Artist: <@133374430905761792>"); // Link
		
		MessageBuilder mb = new MessageBuilder(sj.toString());
		mb.denyMentions(MentionType.values());
		event.getChannel().sendMessage(mb.build()).complete();
	}
}
