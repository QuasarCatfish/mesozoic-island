package com.quas.mesozoicisland.cmdadmin;

import java.util.regex.Pattern;

import com.quas.mesozoicisland.JDBC;
import com.quas.mesozoicisland.cmdbase.ICommand;
import com.quas.mesozoicisland.enums.AccessLevel;
import com.quas.mesozoicisland.enums.DiscordChannel;
import com.quas.mesozoicisland.enums.DiscordRole;
import com.quas.mesozoicisland.enums.ItemID;
import com.quas.mesozoicisland.objects.Element;
import com.quas.mesozoicisland.objects.Player;
import com.quas.mesozoicisland.util.Util;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class GuildDemoteCommand implements ICommand {

	@Override
	public Pattern getCommand() {
		return pattern("guild demote ", PLAYER);
	}

	@Override
	public AccessLevel getAccessLevel() {
		return AccessLevel.Admin;
	}

	@Override
	public String getCommandName() {
		return "guild";
	}

	@Override
	public String getCommandSyntax() {
		return "guild demote <player>";
	}

	@Override
	public String getCommandDescription() {
		return "Demotes the given player to guild member.";
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
	public void run(MessageReceivedEvent event, String... args) {
		Player p = Player.getPlayer(Long.parseLong(args[1].replaceAll("\\D", "")));
		if (p == null) {
			event.getChannel().sendMessageFormat("%s, this is an invalid player.", event.getAuthor().getAsMention()).complete();
			return;
		}
		
		Member m = event.getGuild().getMemberById(p.getIdLong());
		Element e = p.getMainElement();
		if (e == null || e.getId() < 0) {
			event.getChannel().sendMessageFormat("%s, %s is not in a guild.", event.getAuthor().getAsMention(), p.getName()).complete();
			return;
		}
		
		if (!Util.doesMemberHaveRole(m, DiscordRole.Guildmaster.getIdLong())) {
			event.getChannel().sendMessageFormat("%s, %s is not a guildmaster.", event.getAuthor().getAsMention(), p.getName()).complete();
			return;
		}
		
		Util.removeRoleFromMember(m, DiscordRole.Guildmaster.getIdLong());
		event.getChannel().sendMessageFormat("%s, you have demoted %s to guild member.", event.getAuthor().getAsMention(), p.getName()).complete();
		event.getGuild().getTextChannelById(e.getGuild()).sendMessageFormat("%s has been demoted from guildmaster.", p.getAsMention()).complete();
		if (p.getItemCount(ItemID.GuildmasterTitle) > 0) JDBC.addItem(p.getIdLong(), ItemID.GuildmasterTitle.getId(), -1);
		JDBC.addItem(p.getIdLong(), ItemID.FormerGuildmasterTitle.getId());
	}
}
