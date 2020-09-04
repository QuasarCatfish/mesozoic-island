package com.quas.mesozoicisland.util;

import java.util.ArrayList;
import java.util.regex.Pattern;

import com.quas.mesozoicisland.cmdbase.ICommand;
import com.quas.mesozoicisland.cmdplayer.BagCommand;
import com.quas.mesozoicisland.cmdplayer.NicknameCommand;
import com.quas.mesozoicisland.cmdplayer.QuestsCommand;
import com.quas.mesozoicisland.cmdplayer.RemoveItemCommand;
import com.quas.mesozoicisland.cmdplayer.UnnicknameCommand;
import com.quas.mesozoicisland.enums.AccessLevel;
import com.quas.mesozoicisland.enums.DiscordChannel;
import com.quas.mesozoicisland.enums.DiscordRole;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class DummyCommand implements ICommand {

	private String name, syntax, description;
	private ICommand command;

	private DummyCommand(ICommand command) {
		this.name = command.getCommandName();
		this.syntax = command.getCommandSyntax();
		this.description = command.getCommandDescription();
		this.command = command;
	}

	private DummyCommand setName(String name) {
		this.name = name;
		return this;
	}

	private DummyCommand setSyntax(String syntax, String description) {
		this.syntax = syntax;
		this.description = description;
		return this;
	}

	@Override
	public Pattern getCommand() {
		return pattern(Util.mult("x", Message.MAX_CONTENT_LENGTH + 1));
	}

	@Override
	public AccessLevel getAccessLevel() {
		return command.getAccessLevel();
	}

	@Override
	public String getCommandName() {
		return name;
	}

	@Override
	public String getCommandSyntax() {
		return syntax;
	}

	@Override
	public String getCommandDescription() {
		return description;
	}

	@Override
	public DiscordChannel[] getUsableChannels() {
		return command.getUsableChannels();
	}

	@Override
	public DiscordRole[] getRequiredRoles() {
		return command.getRequiredRoles();
	}

	@Override
	public String getRequiredState() {
		return command.getRequiredState();
	}

	@Override
	public void run(MessageReceivedEvent event, String... args) {

	}

	//////////////////////////////////////////////////////////////////

	public static ArrayList<DummyCommand> getProfessorDummyCommands() {
		ArrayList<DummyCommand> dummy = new ArrayList<DummyCommand>();


		return dummy;
	}
	
	public static ArrayList<DummyCommand> getAssistantDummyCommands() {
		ArrayList<DummyCommand> dummy = new ArrayList<DummyCommand>();

		dummy.add(new DummyCommand(new BagCommand()).setSyntax("bag <item>", "Checks the items with the given name or ID."));
		dummy.add(new DummyCommand(new NicknameCommand()).setName("nick"));
		dummy.add(new DummyCommand(new UnnicknameCommand()).setName("unnick"));
		dummy.add(new DummyCommand(new RemoveItemCommand()).setName("ri"));
		dummy.add(new DummyCommand(new QuestsCommand()).setName("quest"));

		return dummy;
	}
}