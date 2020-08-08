package com.quas.mesozoicisland;

import java.util.ArrayList;

import com.quas.mesozoicisland.cmdbase.ICommand;
import com.quas.mesozoicisland.enums.DiscordChannel;
import com.quas.mesozoicisland.enums.DiscordRole;
import com.quas.mesozoicisland.enums.NewPlayerStatus;
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
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class NewMember extends ListenerAdapter {

	@Override
	public void onGuildMemberJoin(GuildMemberJoinEvent event) {
		if (event.getUser().isBot()) return;
		if (event.getUser().isFake()) return;
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
			
			if (!m.hasPermission(Permission.ADMINISTRATOR)) Util.addRoleToMember(m, DiscordRole.NewPlayer.getIdLong());
			c.getManager().setName(m.getEffectiveName().trim().toLowerCase().replaceAll(" |-|_", "A").replaceAll("[^Aa-z0-9]", "").replace("A", "-") + "-tutorial").putPermissionOverride(m, Util.list(Permission.MESSAGE_READ, Permission.MESSAGE_WRITE), new ArrayList<Permission>()).complete();
		} else if (nps == NewPlayerStatus.Returning) {
			Player p = Player.getPlayer(m.getIdLong());
			m.getUser().openPrivateChannel().complete().sendMessageFormat("Welcome back to Mesozoic Island, %s.", p.getName()).complete();
			if (!m.hasPermission(Permission.ADMINISTRATOR)) {
				Util.addRoleToMember(m, DiscordRole.DinosaurTrainer.getIdLong());
				if (p.getMainElement() != null && p.getMainElement().getId() > 0) Util.addRoleToMember(m, p.getMainElement().getRole());
				if (p.isMuted()) Util.addRoleToMember(m, DiscordRole.Muted.getIdLong());
				m.modifyNickname(p.getRawName()).complete();
			}
		}
	}
}
