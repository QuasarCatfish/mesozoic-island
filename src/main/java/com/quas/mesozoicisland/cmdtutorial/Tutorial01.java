package com.quas.mesozoicisland.cmdtutorial;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.regex.Pattern;

import com.quas.mesozoicisland.JDBC;
import com.quas.mesozoicisland.cmdbase.ICommand;
import com.quas.mesozoicisland.enums.AccessLevel;
import com.quas.mesozoicisland.enums.DiscordChannel;
import com.quas.mesozoicisland.enums.DiscordRole;
import com.quas.mesozoicisland.objects.Player;
import com.quas.mesozoicisland.util.Util;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class Tutorial01 implements ICommand {

	@Override
	public Pattern getCommand() {
		return pattern(".*");
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
		return Util.arr(DiscordChannel.CloneMe, DiscordChannel.ClonedChannel);
	}

	@Override
	public DiscordRole[] getRequiredRoles() {
		return null;
	}

	@Override
	public String getRequiredState() {
		return "Tutorial01";
	}
	
	@Override
	public void run(MessageReceivedEvent event, String... args) {
		Player p = Player.getPlayer(event.getAuthor().getIdLong());
		if (p == null) return;
		
		Util.sleep(500);
		JDBC.setState(p.getIdLong(), "TutorialXX");
		
		String name = event.getMessage().getContentRaw();
		
		try (ResultSet res = JDBC.executeQuery("select * from players where lower(playername) = '%s';", Util.cleanQuotes(Util.fixString(name)).toLowerCase())) {
			if (res.next()) {
				event.getChannel().sendMessage("Unfortunately, there is already a player with this name. Please enter a different name.").complete();
				JDBC.setState(p.getIdLong(), "Tutorial01");
				return;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		if (name.toLowerCase().matches(NICKNAME)) {
			sendTyping(event.getChannel(), 2000);
			event.getChannel().sendMessageFormat("So your name is %s?", name).complete();
			
			JDBC.setName(p.getIdLong(), name);
			JDBC.setState(p.getIdLong(), "Tutorial02");
		} else {
			sendTyping(event.getChannel(), 3000);
			event.getChannel().sendMessage("Unfortunately, there is something wrong with the name you provided. Please enter a different name.\n*If you believe this is an error, please contact an Administrator for help.*").complete();
			
			JDBC.setState(p.getIdLong(), "Tutorial01");
		}
	}
}
