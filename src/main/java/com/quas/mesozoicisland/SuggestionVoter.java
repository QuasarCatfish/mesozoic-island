package com.quas.mesozoicisland;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.quas.mesozoicisland.enums.DiscordChannel;
import com.quas.mesozoicisland.objects.Player;
import com.quas.mesozoicisland.util.Constants;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class SuggestionVoter extends ListenerAdapter {

	@Override
	public void onGuildMessageReactionAdd(GuildMessageReactionAddEvent event) {
		if (event.getUser().isBot()) return;
		if (event.getUser().isFake()) return;
		
		if (event.getChannel().getIdLong() == DiscordChannel.GameSuggestions.getIdLong()) {
			Player p = Player.getPlayer(event.getUser().getIdLong());
			if (p == null) return;

			event.getReaction().removeReaction(event.getUser()).complete();
			int x = getValue(event.getReactionEmote().getEmoji());
			int suggestion = -1;

			try (ResultSet res = JDBC.executeQuery("select * from suggestions where messageid = %d;", event.getMessageIdLong())) {
				if (res.next()) {
					suggestion = res.getInt("suggestionid");
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}

			try (ResultSet res = JDBC.executeQuery("select * from suggestionvotes where suggestionid = %d and player = %d;", suggestion, p.getIdLong())) {
				if (res.next()) {
					JDBC.executeUpdate("update suggestionvotes set vote = %d where suggestionid = %d and player = %d;", x, suggestion, p.getIdLong());
				} else {
					JDBC.executeUpdate("insert into suggestionvotes values(%d, %d, %d);", suggestion, p.getIdLong(), x);
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}

			int count = 0;
			try (ResultSet res = JDBC.executeQuery("select count(*) as count from suggestionvotes where suggestionid = %d and vote > 0;", suggestion)) {
				if (res.next()) {
					count = res.getInt("count");
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}

			Message m = event.getChannel().retrieveMessageById(event.getMessageId()).complete();
			if (!m.getEmbeds().isEmpty()) {
				EmbedBuilder eb = new EmbedBuilder(m.getEmbeds().get(0));
				eb.setFooter(String.format("%,d players have voted.", count));
				m.editMessage(m.getContentRaw()).embed(eb.build()).complete();
			}
		}
	}

	private int getValue(String emoji) {
		switch (emoji) {
			case Constants.EMOJI_ONE:
				return 1;
			case Constants.EMOJI_TWO:
				return 2;
			case Constants.EMOJI_THREE:
				return 3;
			case Constants.EMOJI_FOUR:
				return 4;
			case Constants.EMOJI_FIVE:
				return 5;
			case Constants.EMOJI_X:
				return 0;
			default:
				return 0;
		}
	}
}
