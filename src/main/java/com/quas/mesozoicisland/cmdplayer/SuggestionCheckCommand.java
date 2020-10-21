package com.quas.mesozoicisland.cmdplayer;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.regex.Pattern;

import com.quas.mesozoicisland.JDBC;
import com.quas.mesozoicisland.cmdbase.ICommand;
import com.quas.mesozoicisland.enums.AccessLevel;
import com.quas.mesozoicisland.enums.DiscordChannel;
import com.quas.mesozoicisland.enums.DiscordRole;
import com.quas.mesozoicisland.enums.SuggestionStatus;
import com.quas.mesozoicisland.objects.Player;
import com.quas.mesozoicisland.util.Constants;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class SuggestionCheckCommand implements ICommand {

	@Override
	public Pattern getCommand() {
		return pattern("suggestion check ", INTEGER);
	}

	@Override
	public AccessLevel getAccessLevel() {
		return AccessLevel.Trainer;
	}

	@Override
	public String getCommandName() {
		return "suggestion";
	}

	@Override
	public String getCommandSyntax() {
		return "suggestion check <id>";
	}

	@Override
	public String getCommandDescription() {
		return "Checks the status of the given suggestion.";
	}

	@Override
	public DiscordChannel[] getUsableChannels() {
		return DiscordChannel.SUGGESTION_FEEDBACK_CHANNELS;
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
		if (p == null) return;

		int id = Integer.parseInt(args[1]);
		String suggestion = null, image = null;
		int status = 0;

		try (ResultSet res = JDBC.executeQuery("select * from suggestions where suggestionid = %d;", id)) {
			if (res.next()) {
				suggestion = res.getString("suggestion");
				image = res.getString("image");
				status = res.getInt("status");
			} else {
				event.getChannel().sendMessageFormat("%s, there is no suggestion with ID %d.", p.getAsMention(), id).complete();
				return;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		int[] votes = new int[6];
		int sum = 0, count = 0;
		try (ResultSet res = JDBC.executeQuery("select * from suggestionvotes where suggestionid = %d and vote > 0;", id)) {
			while (res.next()) {
				int x = res.getInt("vote");
				votes[x]++;
				sum += x;
				count++;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		EmbedBuilder eb = new EmbedBuilder();
		eb.setTitle("Suggestion " + id);
		eb.setColor(Constants.COLOR);
		if (suggestion != null) eb.setDescription(suggestion.length() < 200 ? suggestion : suggestion.substring(0, 200) + "...");
		if (image != null) eb.setThumbnail(image);
		eb.addField("Votes", String.format("%s - %,d\n%s - %,d\n%s - %,d\n%s - %,d\n%s - %,d",
				Constants.EMOJI_ONE, votes[1], Constants.EMOJI_TWO, votes[2], Constants.EMOJI_THREE, votes[3],
				Constants.EMOJI_FOUR, votes[4], Constants.EMOJI_FIVE, votes[5]), true);
		eb.addField("Average Score", String.format("%1.2f", 1f * sum / count), true);
		eb.addField("Status", SuggestionStatus.of(status).toString(), true);
		event.getChannel().sendMessage(eb.build()).complete();
	}
}
