package com.quas.mesozoicisland;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.TreeMap;

import com.quas.mesozoicisland.cmdbase.ICommand;
import com.quas.mesozoicisland.enums.DiscordChannel;
import com.quas.mesozoicisland.enums.DiscordRole;
import com.quas.mesozoicisland.enums.ItemID;
import com.quas.mesozoicisland.enums.NewPlayerStatus;
import com.quas.mesozoicisland.objects.Item;
import com.quas.mesozoicisland.objects.Player;
import com.quas.mesozoicisland.util.Action;
import com.quas.mesozoicisland.util.Constants;
import com.quas.mesozoicisland.util.Util;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.exceptions.ErrorResponseException;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class JoinServer extends ListenerAdapter {

	@Override
	public void onGuildMemberJoin(GuildMemberJoinEvent event) {
		if (event.getUser().isBot()) return;
		if (event.getUser().isFake()) return;
		if (!event.getGuild().getId().equals(Constants.GUILD_ID)) return;
		activate(event.getGuild(), event.getMember());
	}
	
	@Override
	public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
		if (event.getAuthor().isBot()) return;
		if (event.getAuthor().isFake()) return;
		if (!event.getMessage().getContentRaw().toLowerCase().matches("begin " + ICommand.PLAYER)) return;
		
		if (event.getAuthor().getIdLong() != Constants.QUAS_ID) return;
		
		String id = event.getMessage().getContentRaw().replaceAll("\\D", "");
		Member m = event.getGuild().getMemberById(id);
		activate(event.getGuild(), m);
	}
	
	private void activate(Guild g, Member m) {
		NewPlayerStatus nps = NewPlayerStatus.of(m.getIdLong());
		if (nps == NewPlayerStatus.New || nps == NewPlayerStatus.NewToVersion) {
			TextChannel c = Util.complete(g.getTextChannelById(DiscordChannel.CloneMe.getIdLong()).createCopy());
			Action.sendMessage(MesozoicIsland.getAssistant().getIdLong(), c, "Type `start` to begin the tutorial. It should take you roughly 5 to 10 minutes to complete. After completing the tutorial, you'll be able to access the remainder of Mesozoic Island.");
			if (nps == NewPlayerStatus.NewToVersion) Action.sendMessage(MesozoicIsland.getAssistant().getIdLong(), c, "It appears that you have played Mesozoic Island Version 1. Your progress from that version is currently not transfered to this version. When you join a Guild, you will receive mail containing all of your previously owned dinosaurs that are currently released. In future updates, you will receive your previously owned dinosaurs when the update drops, if you are in a Guild, or when you join a Guild.");
			
			Util.addRoleToMember(m, DiscordRole.NewPlayer.getIdLong());
			c.getManager().setName("tutorial").putPermissionOverride(m, Util.list(Permission.MESSAGE_READ, Permission.MESSAGE_WRITE), new ArrayList<Permission>()).setTopic(String.format("Tutorial channel for %s (ID %d).", m.getUser().getName(), m.getUser().getIdLong())).complete();
			JDBC.executeUpdate("insert into tutorialchannels values(%d, %d);", m.getIdLong(), c.getIdLong());
		} else if (nps == NewPlayerStatus.Returning) {
			Player p = Player.getPlayer(m.getIdLong());

			// Send Welcome Back DM
			try {
				m.getUser().openPrivateChannel().complete().sendMessageFormat("Welcome back to Mesozoic Island, %s.", p.getName()).complete();
			} catch (ErrorResponseException e) {}

			// Add special roles
			TreeMap<Item, Long> bag = p.getBag();
			if (bag.getOrDefault(Item.getItem(ItemID.ModeratorTitle), 0L) > 0L) Util.addRoleToMember(m, DiscordRole.Moderator.getIdLong());
			if (bag.getOrDefault(Item.getItem(ItemID.GuildmasterTitle), 0L) > 0L) Util.addRoleToMember(m, DiscordRole.Guildmaster.getIdLong());
			if (bag.getOrDefault(Item.getItem(ItemID.SupporterTitle), 0L) > 0L) Util.addRoleToMember(m, DiscordRole.Supporter.getIdLong());
			if (bag.getOrDefault(Item.getItem(ItemID.GuineaPigTitle), 0L) > 0L) Util.addRoleToMember(m, DiscordRole.GuineaPig.getIdLong());
			if (bag.getOrDefault(Item.getItem(ItemID.ProficientTrainerTitle), 0L) > 0L) Util.addRoleToMember(m, DiscordRole.ProficientTrainer.getIdLong());
			if (bag.getOrDefault(Item.getItem(ItemID.CursedTitle), 0L) > 0L) Util.addRoleToMember(m, DiscordRole.Cursed.getIdLong());
			if (bag.getOrDefault(Item.getItem(ItemID.CleansedTitle), 0L) > 0L) Util.addRoleToMember(m, DiscordRole.Cleansed.getIdLong());

			// Add Roles
			Util.addRoleToMember(m, DiscordRole.DinosaurTrainer.getIdLong());
			if (p.getMainElement() != null && p.getMainElement().getId() > 0) Util.addRoleToMember(m, p.getMainElement().getRole());
			if (p.isMuted()) Util.addRoleToMember(m, DiscordRole.Muted.getIdLong());

			// Edit Nickname
			if (!m.hasPermission(Permission.ADMINISTRATOR)) {
				m.modifyNickname(p.getRawName()).complete();
			}
		} else if (nps == NewPlayerStatus.InTutorial) {
			Player p = Player.getPlayer(m.getIdLong());

			// Send Welcome Back DM
			try {
				m.getUser().openPrivateChannel().complete().sendMessageFormat("Welcome back to Mesozoic Island, %s.", p.getName()).complete();
			} catch (ErrorResponseException e) {}

			Util.addRoleToMember(m, DiscordRole.NewPlayer.getIdLong());
			try (ResultSet res = JDBC.executeQuery("select * from tutorialchannels where playerid = %d;", p.getIdLong())) {
				if (res.next()) {
					long channel = res.getLong("channelid");
					g.getTextChannelById(channel).getManager().putPermissionOverride(m, Util.list(Permission.MESSAGE_READ, Permission.MESSAGE_WRITE), new ArrayList<Permission>()).complete();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
}
