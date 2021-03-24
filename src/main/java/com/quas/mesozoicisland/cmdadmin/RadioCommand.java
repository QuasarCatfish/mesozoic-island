package com.quas.mesozoicisland.cmdadmin;

import java.util.regex.Pattern;

import com.quas.mesozoicisland.cmdbase.ICommand;
import com.quas.mesozoicisland.enums.AccessLevel;
import com.quas.mesozoicisland.enums.DiscordChannel;
import com.quas.mesozoicisland.enums.DiscordRole;
import com.quas.mesozoicisland.music.PlayerManager;
import com.quas.mesozoicisland.util.Constants;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.managers.AudioManager;

public class RadioCommand implements ICommand {

	@Override
	public Pattern getCommand() {
		return pattern("admin radio");
	}

	@Override
	public AccessLevel getAccessLevel() {
		return AccessLevel.Disabled;
	}

	@Override
	public String getCommandName() {
		return "admin";
	}

	@Override
	public String getCommandSyntax() {
		return "admin radio";
	}

	@Override
	public String getCommandDescription() {
		return "Turns on the DinoRadio.";
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
		Guild g = event.getJDA().getGuildById(Constants.GUILD_ID);
		AudioManager am = g.getAudioManager();
		VoiceChannel vc = g.getVoiceChannelById(DiscordChannel.DinoRadio.getId());
		
		if (am.isConnected()) {
			am.closeAudioConnection();
			event.getChannel().sendMessageFormat("%s, the DinoRadio is now off.", event.getAuthor().getAsMention()).complete();
		} else {
			am.openAudioConnection(vc);
			PlayerManager manager = PlayerManager.getInstance();
			manager.loadAndPlay(event.getTextChannel(), "C:\\Users\\ctsn9\\Downloads\\Mesozoic_Island_Background_Theme.mp3");
			manager.getGuildMusicManager(event.getGuild()).player.setVolume(10);
			event.getChannel().sendMessageFormat("%s, the DinoRadio is now on.", event.getAuthor().getAsMention()).complete();
		}
	}
}
