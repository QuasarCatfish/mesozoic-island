package com.quas.mesozoicisland.cmdbase;

import java.util.regex.Pattern;

import com.quas.mesozoicisland.enums.AccessLevel;
import com.quas.mesozoicisland.enums.DiscordChannel;
import com.quas.mesozoicisland.enums.DiscordRole;
import com.quas.mesozoicisland.objects.Player;
import com.quas.mesozoicisland.util.Util;

import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public interface ICommand {

	// Regex
	public final String INTEGER = "(-?0*(214748364[0-7]|21474836[0-3]\\d|2147483[0-5]\\d{2}|214748[0-2]\\d{3}|21474[0-7]\\d{4}|2147[0-3]\\d{5}|214[0-6]\\d{6}|21[0-3]\\d{7}|20\\d{8}|[0-1]\\d{9}|\\d{1,9}))";
	public final String LONG = "(-?0*(922337203685477580[0-7]|9223372036854775[0-7]\\d{2}|922337203685477[0-4]\\d{3}|92233720368547[0-6]\\d{4}|9223372036854[0-6]\\d{5}|922337203685[0-3]\\d{6}|92233720368[0-4]\\d{7}|9223372036[0-7]\\d{8}|922337203[0-5]\\d{9}|92233720[0-2]\\d{10}|922337[0-1]\\d{12}|92233[0-6]\\d{13}|9223[0-2]\\d{14}|922[0-2]\\d{15}|92[0-1]\\d{16}|9[0-1]\\d{17}|[0-8]\\d{18}|\\d{1,18}))";
	public final String MENTION = "(<@(|!|&)" + LONG + ">)";
	public final String PLAYER = "(" + MENTION + "|" + LONG + ")";
	public final String DINOSAUR = "(" + INTEGER + "[a-z]{0,3})";
	public final String RUNE = "(r" + INTEGER + ")";
	public final String EGG = "(e" + INTEGER + ")";
	public final String NAME = "(([a-z0-9]| ){1,20})";
	public final String ALPHA = "([a-z]{1,100})";
	public final String ALPHANUM = "(-?[a-z0-9]{1,20})";
	public final String NICKNAME = "(([a-z0-9]| ){1,25})";
	public final String ANY = "(.|\r|\n)";
	public final String TIME = "((" + INTEGER + "[a-z])+)";
	public final String YES = "(yes|yep|yeah|yea|ya|ye|mhm|y)";
	public final String NO = "(no|nope|nah|na|n)";
	public final String TEAM_NAME = "([a-z]([a-z0-9]){1,99})";
	
	// Help Stuff
	public Pattern getCommand();
	public AccessLevel getAccessLevel();
	public String getCommandName();
	public String getCommandSyntax();
	public String getCommandDescription();
	public DiscordChannel[] getUsableChannels();
	public DiscordRole[] getRequiredRoles();
	public String getRequiredState();
	
	// Do Command
	public void run(MessageReceivedEvent event, String...args);
	
	// Pattern Compiler
	public default Pattern pattern(String...args) {
		StringBuilder sb = new StringBuilder();
		for (String arg : args) sb.append(arg);
		return Pattern.compile(sb.toString().trim());
	}
	
	// Typed Message
	public default void sendTyping(MessageChannel channel, long time) {
		while (time >= 1000) {
			channel.sendTyping().complete();
			Util.sleep(1000);
			time -= 1000;
		}
		Util.sleep(time);
	}
	
	// Can be used
	public default boolean canBeUsed(MessageReceivedEvent event) {
		Player p = Player.getPlayer(event.getAuthor().getIdLong());
		if (p == null) return false;
		
		// Check Channel
		DiscordChannel dc = DiscordChannel.getChannel(event.getChannel());
		boolean goodchannel = false;
		for (DiscordChannel ch : getUsableChannels()) {
			if (ch == DiscordChannel.All || ch == dc) {
				goodchannel = true;
				break;
			}
		}
		if (!goodchannel) return false;
		
		// Check Access Level
		if (p.getAccessLevel().getLevel() < getAccessLevel().getLevel()) return false;
		
		// Check Roles
		if (getRequiredRoles() != null && getRequiredRoles().length > 0) {
			for (DiscordRole role : getRequiredRoles()) {
				if (!Util.doesMemberHaveRole(event.getMember(), role.getIdLong())) {
					return false;
				}
			}
		}
		
		// Check Player State
		if (getRequiredState() != null) {
			if (!p.hasState(getRequiredState())) return false;
		}
		
		return true;
	}
}
