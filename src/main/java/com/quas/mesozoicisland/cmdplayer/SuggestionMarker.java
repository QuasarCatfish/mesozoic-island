package com.quas.mesozoicisland.cmdplayer;

import java.util.regex.Pattern;

import com.quas.mesozoicisland.cmdbase.ICommand;
import com.quas.mesozoicisland.enums.AccessLevel;
import com.quas.mesozoicisland.enums.DiscordChannel;
import com.quas.mesozoicisland.enums.DiscordRole;
import com.quas.mesozoicisland.objects.Player;
import com.quas.mesozoicisland.util.Constants;
import com.quas.mesozoicisland.util.Util;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class SuggestionMarker implements ICommand {

	@Override
	public Pattern getCommand() {
		return pattern("(.|\r|\n)+");
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
		return DiscordChannel.SUGGESTION_CHANNELS;
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
		if (p == null) {
			event.getMessage().delete().complete();
			return;
		}
		
		Message message = event.getMessage();
		message.addReaction("1\u20E3").complete();
		message.addReaction("2\u20E3").complete();
		message.addReaction("3\u20E3").complete();
		message.addReaction("4\u20E3").complete();
		message.addReaction("5\u20E3").complete();
		
		Util.setRolesMentionable(true, DiscordRole.SuggestionPing);
		event.getGuild().getTextChannelById(Constants.SPAWN_CHANNEL.getIdLong()).sendMessageFormat("%s\nThere is a new suggestion by **%s**! Go to %s to vote on it.", DiscordRole.SuggestionPing.toString(), p.getName(), event.getTextChannel().getAsMention()).complete();
		Util.setRolesMentionable(false, DiscordRole.SuggestionPing);
	}
}
