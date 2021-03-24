package com.quas.mesozoicisland.cmdadmin;

import java.util.regex.Pattern;

import com.quas.mesozoicisland.MesozoicIsland;
import com.quas.mesozoicisland.cmdbase.ICommand;
import com.quas.mesozoicisland.enums.AccessLevel;
import com.quas.mesozoicisland.enums.DiscordChannel;
import com.quas.mesozoicisland.enums.DiscordRole;
import com.quas.mesozoicisland.util.Util;

import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Message.MentionType;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class AnnounceCommand implements ICommand {

	@Override
	public Pattern getCommand() {
		return pattern("admin announce ", ANY, "*");
	}

	@Override
	public AccessLevel getAccessLevel() {
		return AccessLevel.Admin;
	}

	@Override
	public String getCommandName() {
		return "admin";
	}

	@Override
	public String getCommandSyntax() {
		return "admin announce <msg>";
	}

	@Override
	public String getCommandDescription() {
		return "Sends an announcement to " + DiscordChannel.Announcements.toString() + ".";
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
		String msg = Util.join(args, " ", 1, args.length);
		msg = msg.replaceAll("@N(EW)?L(INE)?@", "\n");
		
		// Send in #announcements
		Util.setRolesMentionable(true, DiscordRole.AnnouncementPing);
		Message m = event.getGuild().getTextChannelById(DiscordChannel.Announcements.getId()).sendMessageFormat("%s\n%s", DiscordRole.AnnouncementPing.toString(), msg).complete();
		Util.setRolesMentionable(false, DiscordRole.AnnouncementPing);
		
		// Send in #past-announcements
		MessageBuilder mb = new MessageBuilder();
		mb.appendFormat("**Announcement by %s in %s:**\n\n", m.getAuthor().getAsMention(), m.getTextChannel().getAsMention());
		mb.append(m.getContentRaw());
		mb.denyMentions(MentionType.EVERYONE, MentionType.HERE, MentionType.ROLE, MentionType.USER);
		if (!m.getEmbeds().isEmpty()) mb.setEmbed(m.getEmbeds().get(0));
		DiscordChannel.AnnouncementLog.getChannel(MesozoicIsland.getProfessor()).sendMessage(mb.build()).complete();
		
		event.getChannel().sendMessageFormat("%s, announcement sent!", event.getAuthor().getAsMention()).complete();
	}
}
