package com.quas.mesozoicisland.cmdplayer;

import java.util.regex.Pattern;

import com.quas.mesozoicisland.cmdbase.CommandManager;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class EquipReverseCommand extends EquipCommand {

	@Override
	public Pattern getCommand() {
		return pattern("equip ", RUNE, " ", DINOSAUR);
	}

	@Override
	public String getCommandName() {
		return null;
	}

	@Override
	public String getCommandSyntax() {
		return null;
	}

	@Override
	public String getCommandDescription() {
		return null;
	}

	@Override
	public void run(MessageReceivedEvent event, String... args) {
		CommandManager.handleCommand(event, "equip", args[1], args[0]);
	}
}
