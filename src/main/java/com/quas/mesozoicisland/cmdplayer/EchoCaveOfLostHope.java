package com.quas.mesozoicisland.cmdplayer;

import java.util.regex.Pattern;

import com.quas.mesozoicisland.MesozoicIsland;
import com.quas.mesozoicisland.cmdbase.ICommand;
import com.quas.mesozoicisland.enums.AccessLevel;
import com.quas.mesozoicisland.enums.DiscordChannel;
import com.quas.mesozoicisland.enums.DiscordRole;
import com.quas.mesozoicisland.util.Util;

import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.Message.MentionType;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class EchoCaveOfLostHope implements ICommand {

	@Override
	public Pattern getCommand() {
		return pattern(".+");
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
		return Util.arr(DiscordChannel.CaveOfLostHope);
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
		MessageBuilder mb = new MessageBuilder();
		mb.append("**Message from ");
		mb.append(event.getAuthor().getAsMention());
		mb.append(":**\n");
		mb.append(event.getMessage().getContentRaw());
		mb.denyMentions(MentionType.EVERYONE, MentionType.HERE, MentionType.ROLE, MentionType.USER);
		DiscordChannel.DebugCaveOfLostHope.getChannel(MesozoicIsland.getAssistant()).sendMessage(mb.build()).complete();
	}
}