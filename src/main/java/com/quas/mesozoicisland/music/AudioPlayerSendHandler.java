package com.quas.mesozoicisland.music;

import java.nio.ByteBuffer;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.track.playback.MutableAudioFrame;

import net.dv8tion.jda.api.audio.AudioSendHandler;

public class AudioPlayerSendHandler implements AudioSendHandler {

	private final AudioPlayer player;
	private final ByteBuffer buffer;
	private final MutableAudioFrame frame;
	
	public AudioPlayerSendHandler(AudioPlayer player) {
		this.player = player;
		this.buffer = ByteBuffer.allocate(1024);
		this.frame = new MutableAudioFrame();
		this.frame.setBuffer(buffer);
	}
	
	@Override
	public boolean canProvide() {
		return player.provide(frame);
	}

	@Override
	public ByteBuffer provide20MsAudio() {
		buffer.flip();
		return buffer;
	}

	@Override
	public boolean isOpus() {
		return true;
	}
}
