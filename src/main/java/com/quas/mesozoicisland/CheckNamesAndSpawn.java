package com.quas.mesozoicisland;

import com.quas.mesozoicisland.battle.SpawnManager;
import com.quas.mesozoicisland.enums.SpawnType;
import com.quas.mesozoicisland.objects.Player;
import com.quas.mesozoicisland.util.Constants;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.user.update.UserUpdateNameEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class CheckNamesAndSpawn extends ListenerAdapter {

	private boolean CHECKED_NAMES = false;
	
	@Override
	public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
		// Update Members' Nicknames if they differ from Game Name
		if (!CHECKED_NAMES && event.getGuild().getIdLong() == MesozoicIsland.getProfessor().getGuild().getIdLong()) {
			CHECKED_NAMES = true;
			
			for (Member m : event.getGuild().getMembers()) {
				System.out.println("Checking Member " + m.getEffectiveName());
				Player p = Player.getPlayer(m.getIdLong());
				if (p == null) continue;
				
				if (!m.hasPermission(Permission.ADMINISTRATOR) && !m.getEffectiveName().equalsIgnoreCase(p.getRawName())) {
					m.modifyNickname(p.getRawName()).complete();
				}
			}
		}
		
		if (event.getAuthor().isBot()) return;
		if (event.getAuthor().isFake()) return;
		
		if (event.getChannel().getIdLong() == Constants.SPAWN_CHANNEL.getIdLong()) {
			if (!SpawnManager.doAutoSpawn()) {
				SpawnManager.trySpawn(SpawnType.Random, false);
			}
		}
	}
	
	@Override
	public void onUserUpdateName(UserUpdateNameEvent event) {
		Member m = MesozoicIsland.getProfessor().getGuild().getMember(event.getUser());
		if (m == null) return;
		
		Player p = Player.getPlayer(m.getIdLong());
		if (p == null) return;
		
		if (!m.hasPermission(Permission.ADMINISTRATOR)) {
			m.modifyNickname(p.getRawName()).complete();
		}
	}
}
