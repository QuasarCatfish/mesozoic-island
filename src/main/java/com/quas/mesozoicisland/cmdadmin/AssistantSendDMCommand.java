package com.quas.mesozoicisland.cmdadmin;

import java.util.regex.Pattern;

public class AssistantSendDMCommand extends ProfessorSendDMCommand {

	@Override
	public Pattern getCommand() {
		return pattern("adm ", PLAYER, " (", ANY + ")+");
	}

	@Override
	public String getCommandSyntax() {
		return "adm @player <message>";
	}

	@Override
	public String getCommandDescription() {
		return "Send a message to the given player from Elise.";
	}
}