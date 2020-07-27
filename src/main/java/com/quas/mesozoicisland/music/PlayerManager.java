package com.quas.mesozoicisland.music;

import java.util.Map;
import java.util.TreeMap;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;

public class PlayerManager {

	private static PlayerManager instance;
	
	private final AudioPlayerManager player;
	private final Map<Long, GuildMusicManager> music;
	
	private PlayerManager() {
		music = new TreeMap<Long, GuildMusicManager>();
		
		player = new DefaultAudioPlayerManager();
		AudioSourceManagers.registerRemoteSources(player);
		AudioSourceManagers.registerLocalSource(player);
	}
	
	public synchronized GuildMusicManager getGuildMusicManager(Guild guild) {
		GuildMusicManager gmm = music.get(guild.getIdLong());
		if (gmm == null) {
			gmm = new GuildMusicManager(player);
			music.put(guild.getIdLong(), gmm);
		}
		
		guild.getAudioManager().setSendingHandler(gmm.getSendHandler());
		return gmm;
	}
	
	public void loadAndPlay(TextChannel channel, String track) {
		GuildMusicManager gmm = getGuildMusicManager(channel.getGuild());
		
		player.loadItemOrdered(gmm, track, new AudioLoadResultHandler() {
			@Override
			public void trackLoaded(AudioTrack track) {
				play(gmm, track);
			}
			
			@Override
			public void playlistLoaded(AudioPlaylist playlist) {
				for (AudioTrack track : playlist.getTracks()) {
					play(gmm, track);
				}
			}
			
			@Override
			public void noMatches() {
				channel.sendMessage("404 Error.").complete();
			}
			
			@Override
			public void loadFailed(FriendlyException e) {
				channel.sendMessage("Exception Thrown").complete();
				e.printStackTrace();
			}
		});
	}
	
	private void play(GuildMusicManager gmm, AudioTrack track) {
		gmm.scheduler.queue(track);
	}
	
	public static synchronized PlayerManager getInstance() {
		if (instance == null) instance = new PlayerManager();
		return instance;
	}
}
