package com.quas.mesozoicisland.cmdadmin;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.quas.mesozoicisland.JDBC;
import com.quas.mesozoicisland.cmdbase.ICommand;
import com.quas.mesozoicisland.enums.AccessLevel;
import com.quas.mesozoicisland.enums.DiscordChannel;
import com.quas.mesozoicisland.enums.DiscordRole;
import com.quas.mesozoicisland.enums.ItemID;
import com.quas.mesozoicisland.enums.SuggestionStatus;
import com.quas.mesozoicisland.objects.Item;
import com.quas.mesozoicisland.objects.Player;
import com.quas.mesozoicisland.util.Constants;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public abstract class SuggestionUpdateStatusCommand implements ICommand {

	@Override
	public AccessLevel getAccessLevel() {
		return AccessLevel.Admin;
	}

	@Override
	public String getCommandName() {
		return "suggestion";
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

	public synchronized void doUpdate(MessageReceivedEvent event, SuggestionStatus newStatus, String... args) {
		Player p = Player.getPlayer(event.getAuthor().getIdLong());
		if (p == null) return;

		Player suggestor = null;
		long messageid = -1L;
		SuggestionStatus status = SuggestionStatus.Pending;

		// get suggestion from database
		try (ResultSet res = JDBC.executeQuery("select * from suggestions where suggestionid = %d;", Integer.parseInt(args[1]))) {
			if (res.next()) {
				suggestor = Player.getPlayer(res.getLong("player"));
				messageid = res.getLong("messageid");
				status = SuggestionStatus.of(res.getInt("status"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		// skip if couldnt find message
		if (suggestor == null) return;

		if (!status.isEdit()) {
			event.getChannel().sendMessageFormat("%s, this suggestion is not pending.", p.getAsMention()).complete();
			return;
		}
		
		Message m = event.getGuild().getTextChannelById(DiscordChannel.GameSuggestions.getIdLong()).retrieveMessageById(messageid).complete();
		if (newStatus.isEdit()) {
			EmbedBuilder eb = new EmbedBuilder(m.getEmbeds().get(0));
			eb.setColor(newStatus.getColor());
			
			m.editMessage(eb.build()).complete();
		} else {
			m.delete().complete();
			EmbedBuilder eb = new EmbedBuilder(m.getEmbeds().get(0));
			eb.setColor(newStatus.getColor());
			eb.setFooter(null);
			event.getGuild().getTextChannelById(DiscordChannel.PreviousSuggestions.getIdLong()).sendMessageFormat("**%s Suggestion by %s**", suggestor.getName(), newStatus.name()).embed(eb.build()).complete();
			
			// give cookie for accepted
			if (newStatus == SuggestionStatus.Accepted) {
				JDBC.addItem(suggestor.getIdLong(), ItemID.Cookie.getId());
				event.getGuild().getTextChannelById(Constants.SPAWN_CHANNEL.getIdLong()).sendMessageFormat("%s, for having a suggestion accepted, you have been given 1 %s.", suggestor.getAsMention(), Item.getItem(ItemID.Cookie).toString()).complete();
			}
		}
		
		event.getChannel().sendMessageFormat("%s, this suggestion by %s has been marked as %s.", p.getAsMention(), suggestor.getName(), newStatus.name()).complete();
		JDBC.executeUpdate("update suggestions set status = %d where suggestionid = %d;", newStatus.getId(), Integer.parseInt(args[1]));
	}
}
