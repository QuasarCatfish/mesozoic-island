package com.quas.mesozoicisland.cmdadmin;

import java.util.regex.Pattern;

import com.quas.mesozoicisland.cmdbase.ICommand;
import com.quas.mesozoicisland.enums.AccessLevel;
import com.quas.mesozoicisland.enums.DiscordChannel;
import com.quas.mesozoicisland.enums.DiscordRole;
import com.quas.mesozoicisland.util.Constants;
import com.quas.mesozoicisland.util.Util;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class ProfessorSendDMCommand implements ICommand {

	@Override
	public Pattern getCommand() {
		return pattern("pdm ", PLAYER, " (", ANY + ")+");
	}

	@Override
	public AccessLevel getAccessLevel() {
		return AccessLevel.Admin;
	}

	@Override
	public String getCommandName() {
		return "dm";
	}

	@Override
	public String getCommandSyntax() {
		return "adm @player <message>";
	}

	@Override
	public String getCommandDescription() {
		return "Send a message to the given player from Megan.";
	}

	@Override
	public DiscordChannel[] getUsableChannels() {
		return Util.arr(DiscordChannel.BotDMs);
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
		User u = event.getGuild().getMemberById(args[0].replaceAll("\\D+", "")).getUser();
		String msg = Util.join(args, " ", 1, args.length);
		u.openPrivateChannel().complete().sendMessage(msg).complete();
		
		EmbedBuilder eb = new EmbedBuilder();
		eb.setColor(Constants.COLOR);
		eb.setDescription(msg);
		eb.addField("User", event.getJDA().getSelfUser().getAsMention(), true);
		eb.addField("Recipient", u.getAsMention(), true);
		eb.addField("Recipient ID", u.getId(), true);
		event.getChannel().sendMessage(eb.build()).complete();
	}
}