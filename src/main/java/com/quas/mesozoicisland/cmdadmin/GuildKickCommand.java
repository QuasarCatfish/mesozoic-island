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
import com.quas.mesozoicisland.util.Pair;
import com.quas.mesozoicisland.util.Util;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class GuildKickCommand implements ICommand {

	@Override
	public Pattern getCommand() {
		return pattern("guild kick ", PLAYER);
	}

	@Override
	public AccessLevel getAccessLevel() {
		return AccessLevel.Trainer;
	}

	@Override
	public String getCommandName() {
		return "guild";
	}

	@Override
	public String getCommandSyntax() {
		return "guild kick <player>";
	}

	@Override
	public String getCommandDescription() {
		return "Removes the given player from the guild.";
	}

	@Override
	public DiscordChannel[] getUsableChannels() {
		return DiscordChannel.GUILDMASTER_GUILD_CHANNELS;
	}

	@Override
	public DiscordRole[] getRequiredRoles() {
		return Util.arr(DiscordRole.Guildmaster);
	}

	@Override
	public String getRequiredState() {
		return null;
	}

	@Override
	public void run(MessageReceivedEvent event, String... args) {
		Player p = Player.getPlayer(event.getAuthor().getIdLong());
		if (p == null) return;
		
		Player target = Player.getPlayer(Long.parseLong(args[1].replaceAll("\\D", "")));
		if (target == null) {
			event.getChannel().sendMessageFormat("%s, this is an invalid player.", p.getAsMention()).complete();
			return;
		}
		
		Member m = event.getGuild().getMemberById(p.getIdLong());
		if (Util.doesMemberHaveRole(m, DiscordRole.Guildmaster.getIdLong())) {
			event.getChannel().sendMessageFormat("%s, you cannot kick a guildmaster.", p.getAsMention()).complete();
			return;
		}
		
		Element pe = p.getMainElement();
		Element te = target.getMainElement();
		
		if (p.getAccessLevel().getLevel() < AccessLevel.Admin.getLevel()) {
			if (pe == null || pe.getId() < 0) {
				event.getChannel().sendMessageFormat("%s, you are not in a guild.", p.getAsMention()).complete();
			}
			
			if (te == null || pe.getId() != te.getId()) {
				event.getChannel().sendMessageFormat("%s, %s is not in your guild.", p.getAsMention(), target.getName()).complete();
				return;
			}
		}
		
		Util.removeRoleFromMember(m, pe.getRole());
		event.getChannel().sendMessageFormat("%s, you have kicked %s from your guild.", p.getAsMention(), target.getName()).complete();
		JDBC.executeUpdate("update players set mainelement = -1 where playerid = %d;", target.getIdLong());
		JDBC.addItem(target.getIdLong(), new Pair<Integer, Long>(ItemID.GuildBadge.getItemId(), (long)te.getId()), -1);
		JDBC.addItem(target.getIdLong(), ItemID.GuildBadge.getId(), 1);
	}
}
