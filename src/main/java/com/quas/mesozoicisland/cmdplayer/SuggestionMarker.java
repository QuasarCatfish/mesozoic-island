package com.quas.mesozoicisland.cmdplayer;

import java.util.regex.Pattern;

import com.quas.mesozoicisland.JDBC;
import com.quas.mesozoicisland.cmdbase.ICommand;
import com.quas.mesozoicisland.enums.AccessLevel;
import com.quas.mesozoicisland.enums.DiscordChannel;
import com.quas.mesozoicisland.enums.DiscordRole;
import com.quas.mesozoicisland.objects.Player;
import com.quas.mesozoicisland.util.Constants;
import com.quas.mesozoicisland.util.Util;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class SuggestionMarker implements ICommand {

	@Override
	public Pattern getCommand() {
		return pattern("(.|\r|\n)*");
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
		event.getMessage().delete().complete();
		
		Player p = Player.getPlayer(event.getAuthor().getIdLong());
		if (p == null) return;
		if (p.getIdLong() == Constants.QUAS_ID) return;

		// get suggestion info
		int id = JDBC.getNextSuggestionId();
		String suggestion = event.getMessage().getContentRaw();
		String image = event.getMessage().getAttachments().isEmpty() ? null : event.getMessage().getAttachments().get(0).getUrl();
		
		// build embed
		EmbedBuilder eb = new EmbedBuilder();
		eb.setColor(Constants.COLOR);
		eb.setTitle(String.format("Suggestion %,d", id));
		eb.setDescription(suggestion);
		eb.setImage(image);

		// send message and add to database
		Util.setRolesMentionable(true, DiscordRole.SuggestionPing);
		Message message = event.getChannel().sendMessageFormat("%s, there is a new suggestion to vote on.", DiscordRole.SuggestionPing.toString()).embed(eb.build()).complete();
		Util.setRolesMentionable(false, DiscordRole.SuggestionPing);
		JDBC.addSuggestion(p.getIdLong(), id, suggestion, image, message.getIdLong());
		
		// add vote reactions
		for (String reaction : new String[] {Constants.EMOJI_ONE, Constants.EMOJI_TWO, Constants.EMOJI_THREE, Constants.EMOJI_FOUR, Constants.EMOJI_FIVE, Constants.EMOJI_X}) {
			message.addReaction(reaction).complete();
		}
	}
}
