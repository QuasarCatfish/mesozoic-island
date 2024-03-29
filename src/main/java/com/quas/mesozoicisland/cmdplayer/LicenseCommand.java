package com.quas.mesozoicisland.cmdplayer;

import java.io.File;
import java.util.regex.Pattern;

import com.quas.mesozoicisland.cmdbase.ICommand;
import com.quas.mesozoicisland.enums.AccessLevel;
import com.quas.mesozoicisland.enums.DiscordChannel;
import com.quas.mesozoicisland.enums.DiscordRole;
import com.quas.mesozoicisland.objects.Player;
import com.quas.mesozoicisland.util.Constants;
import com.quas.mesozoicisland.util.DinosaurLicense;
import com.quas.mesozoicisland.util.Util;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class LicenseCommand implements ICommand {

	@Override
	public Pattern getCommand() {
		return pattern("license");
	}

	@Override
	public AccessLevel getAccessLevel() {
		return AccessLevel.Trainer;
	}

	@Override
	public String getCommandName() {
		return "license";
	}

	@Override
	public String getCommandSyntax() {
		return "license";
	}

	@Override
	public String getCommandDescription() {
		return "Displays your Trainer License.";
	}

	@Override
	public DiscordChannel[] getUsableChannels() {
		return Util.union(DiscordChannel.STANDARD_CHANNELS_DMS, Util.arr(DiscordChannel.CloneMe, DiscordChannel.ClonedChannel)).toArray(new DiscordChannel[0]);
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
		Player p = Player.getPlayer(event.getAuthor().getIdLong());
		if (p == null) return;

		File f = DinosaurLicense.of2(p, event.getMember());

		EmbedBuilder eb = new EmbedBuilder();
		eb.setTitle(String.format("%s's Mesozoic Island License", p.getName()));
		eb.setColor(Constants.COLOR);
		eb.setImage("attachment://" + f.getName());
		event.getChannel().sendMessage(eb.build()).addFile(f, f.getName()).complete();
	}
}