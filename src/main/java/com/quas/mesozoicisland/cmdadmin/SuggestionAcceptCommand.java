package com.quas.mesozoicisland.cmdadmin;

import java.awt.Color;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.regex.Pattern;

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

public class SuggestionAcceptCommand implements ICommand {

	@Override
	public Pattern getCommand() {
		return pattern("suggestion accept ", INTEGER);
	}

	@Override
	public AccessLevel getAccessLevel() {
		return AccessLevel.Admin;
	}

	@Override
	public String getCommandName() {
		return "suggestion";
	}

	@Override
	public String getCommandSyntax() {
		return "suggestion accept <id>";
	}

	@Override
	public String getCommandDescription() {
		return "Accepts the given suggestion.";
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
		Player p = Player.getPlayer(event.getAuthor().getIdLong());
		if (p == null) return;

		Player suggestor = null;
		long messageid = -1L;
		SuggestionStatus status = SuggestionStatus.Pending;

		try (ResultSet res = JDBC.executeQuery("select * from suggestions where suggestionid = %d;", Integer.parseInt(args[1]))) {
			if (res.next()) {
				suggestor = Player.getPlayer(res.getLong("player"));
				messageid = res.getLong("messageid");
				status = SuggestionStatus.of(res.getInt("status"));
			} else {
				event.getChannel().sendMessageFormat("format").complete();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		if (suggestor == null) return;
		if (status != SuggestionStatus.Pending) {
			event.getChannel().sendMessageFormat("%s, this suggestion is not pending.", p.getAsMention()).complete();
			return;
		}

		Message m = event.getGuild().getTextChannelById(DiscordChannel.GameSuggestions.getIdLong()).retrieveMessageById(messageid).complete();
		m.delete().complete();

		EmbedBuilder eb = new EmbedBuilder(m.getEmbeds().get(0));
		eb.setColor(Color.GREEN);
		eb.setFooter(null);

		event.getChannel().sendMessageFormat("%s, this suggestion by %s has been accepted.", p.getAsMention(), suggestor.getName()).complete();
		event.getGuild().getTextChannelById(DiscordChannel.PreviousSuggestions.getIdLong()).sendMessageFormat("**Accepted Suggestion by %s**", suggestor.getName()).embed(eb.build()).complete();
		JDBC.executeUpdate("update suggestions set status = %d where suggestionid = %d;", SuggestionStatus.Accepted.getId(), Integer.parseInt(args[1]));

		JDBC.addItem(suggestor.getIdLong(), ItemID.Cookie.getId());
		event.getGuild().getTextChannelById(Constants.SPAWN_CHANNEL.getIdLong()).sendMessageFormat("%s, for having a suggestion accepted, you have been given 1 %s.", suggestor.getAsMention(), Item.getItem(ItemID.Cookie).toString()).complete();
	}
}
