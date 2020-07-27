package com.quas.mesozoicisland.cmdadmin;

import java.io.File;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.regex.Pattern;

import com.quas.mesozoicisland.MesozoicIsland;
import com.quas.mesozoicisland.cmdbase.ICommand;
import com.quas.mesozoicisland.enums.AccessLevel;
import com.quas.mesozoicisland.enums.DiscordChannel;
import com.quas.mesozoicisland.enums.DiscordRole;
import com.quas.mesozoicisland.util.Util;

import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.Message.Attachment;
import net.dv8tion.jda.api.entities.Message.MentionType;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.requests.restaction.MessageAction;

public class AnnouncementMarker implements ICommand {

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
		return Util.arr(DiscordChannel.Announcements);
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
		TextChannel pa = DiscordChannel.PastAnnouncemens.getChannel(MesozoicIsland.getProfessor());
		String raw = event.getMessage().getContentRaw();
		
		MessageBuilder mb = new MessageBuilder();
		mb.appendFormat("**Announcement by %s in %s:**\n\n", event.getAuthor().getAsMention(), event.getTextChannel().getAsMention());
		mb.append(raw);
		mb.denyMentions(MentionType.EVERYONE, MentionType.HERE, MentionType.ROLE, MentionType.USER);
		if (!event.getMessage().getEmbeds().isEmpty()) mb.setEmbed(event.getMessage().getEmbeds().get(0));
		
		MessageAction ma = pa.sendMessage(mb.build());
		
		ArrayList<File> delete = new ArrayList<File>();
		for (Attachment a : event.getMessage().getAttachments()) {
			try {
				File f = a.downloadToFile(new File("download/" + a.getFileName())).get();
				ma.addFile(f);
				delete.add(f);
			} catch (InterruptedException | ExecutionException e) {
				e.printStackTrace();
			}
		}
		
		ma.complete();
		for (File f : delete) if (f.exists()) f.delete();
	}
}
