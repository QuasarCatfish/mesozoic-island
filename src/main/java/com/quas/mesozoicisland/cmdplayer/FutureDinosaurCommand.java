package com.quas.mesozoicisland.cmdplayer;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.regex.Pattern;

import com.quas.mesozoicisland.JDBC;
import com.quas.mesozoicisland.cmdbase.ICommand;
import com.quas.mesozoicisland.enums.AccessLevel;
import com.quas.mesozoicisland.enums.DiscordChannel;
import com.quas.mesozoicisland.enums.DiscordRole;
import com.quas.mesozoicisland.objects.Player;
import com.quas.mesozoicisland.util.Pair;
import com.quas.mesozoicisland.util.Util;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class FutureDinosaurCommand implements ICommand {

	@Override
	public Pattern getCommand() {
		return pattern("fd .+");
	}

	@Override
	public AccessLevel getAccessLevel() {
		return AccessLevel.Trainer;
	}

	@Override
	public String getCommandName() {
		return "fd";
	}

	@Override
	public String getCommandSyntax() {
		return "fd <info>";
	}

	@Override
	public String getCommandDescription() {
		return "Command for adding information about future dinosaurs to Mesozoic Island. Use `fd get` to get a new prompt or `fd <info>` to complete the current prompt.";
	}

	@Override
	public DiscordChannel[] getUsableChannels() {
		return Util.arr(DiscordChannel.GameTesting, DiscordChannel.DirectMessages);
	}

	@Override
	public DiscordRole[] getRequiredRoles() {
		return null;
	}

	@Override
	public String getRequiredState() {
		return null;
	}

	final int STATE_NONE = 0;
	final int STATE_EXISTS = 1;
	final int STATE_DIET = 2;
	final int STATE_FAMILY = 3;
	final int STATE_EPOCH = 4;
	final int STATE_AGE = 5;
	final int STATE_LOCATION = 6;

	@Override
	public synchronized void run(MessageReceivedEvent event, String... args) {
		Player p = Player.getPlayer(event.getAuthor().getIdLong());
		if (p == null) return;
		if (args.length == 0) return;

		if (args[0].equalsIgnoreCase("get")) {
			boolean nextDino = p.getFutureDinoId() == 0;
			int nextState = STATE_EXISTS;

			if (!nextDino) {
				try (ResultSet res = JDBC.executeQuery("select * from futuredinoinfo where player = %d and fdinoid = %d;", p.getIdLong(), p.getFutureDinoId())) {
					if (res.next()) {
						boolean exists = res.getBoolean("exists");
						boolean nullExists = res.wasNull();
						String diet = res.getString("diet");
						String family = res.getString("family");
						String epoch = res.getString("epoch");
						String age = res.getString("age");
						String location = res.getString("location");

						if (nullExists) nextState = STATE_EXISTS;
						else if (!exists) nextDino = true;
						else if (diet == null) nextState = STATE_DIET;
						else if (family == null) nextState =  STATE_FAMILY;
						else if (epoch == null) nextState = STATE_EPOCH;
						else if (age == null) nextState = STATE_AGE;
						else if (location == null) nextState = STATE_LOCATION;
						else nextDino = true;
					}
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}

			if (nextDino) {
				ArrayList<Pair<Integer, String>> futureDinos = new ArrayList<>();
				try (ResultSet res = JDBC.executeQuery("select * from futuredinos where fdinoid > 0 and fdinoid not in (select fdinoid from futuredinoinfo where player = %d);", p.getIdLong())) {
					while (res.next()) {
						futureDinos.add(new Pair<>(res.getInt("fdinoid"), res.getString("dinoname")));
					}
				} catch (SQLException e) {
					e.printStackTrace();
				}

				Pair<Integer, String> selected = Util.getRandomElement(futureDinos);
				event.getChannel().sendMessageFormat("Does this link to the Wikipedia page for %s? <https://en.wikipedia.org/wiki/%s>\nPlease open the link and reply with `yes` or `no` depending on whether the page's title is %s.", selected.getSecondValue(), selected.getSecondValue(), selected.getSecondValue()).complete();
				JDBC.executeUpdate("update players set futuredino = %d, futuredinostate = %d where playerid = %d;", selected.getFirstValue(), STATE_EXISTS, p.getIdLong());
				JDBC.executeUpdate("insert into futuredinoinfo(player, fdinoid) values(%d, %d);", p.getIdLong(), selected.getFirstValue());
			} else {
				Pair<Integer, String> selected = null;
				try (ResultSet res = JDBC.executeQuery("select * from futuredinos where fdinoid = %d;", p.getFutureDinoId())) {
					if (res.next()) {
						selected = new Pair<>(res.getInt("fdinoid"), res.getString("dinoname"));
					}
				} catch (SQLException e) {
					e.printStackTrace();
				}

				if (selected == null) {
					event.getChannel().sendMessage("Error. Please contact an administrator.").complete();
				} else {
					switch (nextState) {
						case STATE_EXISTS: {
							event.getChannel().sendMessageFormat("Does this link to the Wikipedia page for %s? <https://en.wikipedia.org/wiki/%s>\nPlease open the link and reply with `fd yes` or `fd no` depending on whether the page's title is %s.", selected.getSecondValue(), selected.getSecondValue(), selected.getSecondValue()).complete();
						} break;
	
						case STATE_DIET: {
							event.getChannel().sendMessageFormat("What is the diet of %s? <https://en.wikipedia.org/wiki/%s>\nReply with `fd Carnivore`, `fd Herbivore`, `fd Omnivore`, `fd Unknown`, or `fd Other`.", selected.getSecondValue(), selected.getSecondValue()).complete();
						} break;

						case STATE_FAMILY: {
							event.getChannel().sendMessageFormat("What is the family of %s? If no family is present, what is its suborder? <https://en.wikipedia.org/wiki/%s>\nReply with the Family this dinosaur was a member of using `fd <family>` or `fd Unknown` if this information does not exist.", selected.getSecondValue(), selected.getSecondValue()).complete();
						} break;

						case STATE_EPOCH: {
							event.getChannel().sendMessageFormat("What Epoch did %s live in? <https://en.wikipedia.org/wiki/%s>\nReply with the Geological Epoch this dinosaur lived in using `fd <epoch>` or `fd Unknown` if this information does not exist.", selected.getSecondValue(), selected.getSecondValue()).complete();
						} break;

						case STATE_AGE: {
							event.getChannel().sendMessageFormat("What Age did %s live in? <https://en.wikipedia.org/wiki/%s>\nReply with the Geological Age this dinosaur lived in using `fd <age>` or `fd Unknown` if this information does not exist.", selected.getSecondValue(), selected.getSecondValue()).complete();
						} break;

						case STATE_LOCATION: {
							event.getChannel().sendMessageFormat("What countries or continents did %s live on? <https://en.wikipedia.org/wiki/%s>\nReply with a comma separated list of what continents this dinosaur lived on using `fd <location>` or `fd Unknown` if this information does not exist.", selected.getSecondValue(), selected.getSecondValue()).complete();
						} break;
					}

					JDBC.executeUpdate("update players set futuredinostate = %d;", nextState);
				}
			}
		} else if (p.getFutureDinoState() == STATE_NONE) {
			event.getChannel().sendMessageFormat("You need to use `fd get` first.").complete();
		} else {
			String input = Util.join(args, " ", 0, args.length);
			Pair<Integer, String> selected = null;
			try (ResultSet res = JDBC.executeQuery("select * from futuredinos where fdinoid = %d;", p.getFutureDinoId())) {
				if (res.next()) {
					selected = new Pair<>(res.getInt("fdinoid"), res.getString("dinoname"));
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}

			if (selected == null) {
				event.getChannel().sendMessage("Error. Please contact an administrator.").complete();
			} else {
				switch (p.getFutureDinoState()) {
					case STATE_EXISTS: {
						if (input.toLowerCase().matches(YES + "|" + NO)) {
							boolean b = input.matches(YES);
							JDBC.executeUpdate("update futuredinoinfo set `exists` = %b where player = %d and fdinoid = %d;", b, p.getIdLong(), p.getFutureDinoId());
							event.getChannel().sendMessageFormat("Existence data for %s: **%s**\nType `fd get` for your next entry.", selected.getSecondValue(), b ? "Does exist" : "Does not exist").complete();
						} else {
							event.getChannel().sendMessageFormat("Invalid input. Try again.").complete();
						}
					} break;
	
					case STATE_DIET: {
						if (input.toLowerCase().matches("carnivore|herbivore|omnivore|unknown|other")) {
							JDBC.executeUpdate("update futuredinoinfo set diet = '%s' where player = %d and fdinoid = %d;", Util.cleanQuotes(input), p.getIdLong(), p.getFutureDinoId());
							event.getChannel().sendMessageFormat("Diet data for %s: **%s**\nType `fd get` for your next entry.", selected.getSecondValue(), input).complete();
						} else {
							event.getChannel().sendMessageFormat("Invalid input. Try again.").complete();
						}
					} break;
	
					case STATE_FAMILY: {
						if (Util.cleanQuotes(input).length() < 50) {
							JDBC.executeUpdate("update futuredinoinfo set family = '%s' where player = %d and fdinoid = %d;", Util.cleanQuotes(input), p.getIdLong(), p.getFutureDinoId());
							event.getChannel().sendMessageFormat("Family data for %s: **%s**\nType `fd get` for your next entry.", selected.getSecondValue(), input).complete();
						} else {
							event.getChannel().sendMessageFormat("Input too long. Try again.").complete();
						}
					} break;
	
					case STATE_EPOCH: {
						if (Util.cleanQuotes(input).length() < 100) {
							JDBC.executeUpdate("update futuredinoinfo set epoch = '%s' where player = %d and fdinoid = %d;", Util.cleanQuotes(input), p.getIdLong(), p.getFutureDinoId());
							event.getChannel().sendMessageFormat("Epoch data for %s: **%s**\nType `fd get` for your next entry.", selected.getSecondValue(), input).complete();
						} else {
							event.getChannel().sendMessageFormat("Input too long. Try again.").complete();
						}
					} break;
	
					case STATE_AGE: {
						if (Util.cleanQuotes(input).length() < 100) {
							JDBC.executeUpdate("update futuredinoinfo set age = '%s' where player = %d and fdinoid = %d;", Util.cleanQuotes(input), p.getIdLong(), p.getFutureDinoId());
							event.getChannel().sendMessageFormat("Age data for %s: **%s**\nType `fd get` for your next entry.", selected.getSecondValue(), input).complete();
						} else {
							event.getChannel().sendMessageFormat("Input too long. Try again.").complete();
						}
					} break;
	
					case STATE_LOCATION: {
						if (Util.cleanQuotes(input).length() < 100) {
							JDBC.executeUpdate("update futuredinoinfo set location = '%s' where player = %d and fdinoid = %d;", Util.cleanQuotes(input), p.getIdLong(), p.getFutureDinoId());
							event.getChannel().sendMessageFormat("Location data for %s: **%s**\nType `fd get` for your next entry.", selected.getSecondValue(), input).complete();
						} else {
							event.getChannel().sendMessageFormat("Input too long. Try again.").complete();
						}
					} break;
				}
			}
		}
	}
}