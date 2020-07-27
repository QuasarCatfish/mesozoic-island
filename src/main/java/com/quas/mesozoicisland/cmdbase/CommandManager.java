package com.quas.mesozoicisland.cmdbase;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.TreeMap;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class CommandManager {

	private static TreeMap<Long, ArrayList<ICommand>> commands = new TreeMap<Long, ArrayList<ICommand>>();
	
	public static void addCommand(long user, ICommand command) {
		if (!commands.containsKey(user)) commands.put(user, new ArrayList<ICommand>());
		commands.get(user).add(command);
	}
	
	public static void handleCommand(MessageReceivedEvent event) {
		handleCommand(event, false);
	}
	
	public static void handleCommand(MessageReceivedEvent event, boolean override) {
		String[] split = event.getMessage().getContentRaw().split("\\s+");
		String command = split[0].toLowerCase();
		String[] args = Arrays.copyOfRange(split, 1, split.length);
		handleCommand(event, override, command, args);
	}
	
	public static void handleCommand(MessageReceivedEvent event, String cmd, String...args) {
		handleCommand(event, false, cmd, args);
	}
	
	public static void handleCommand(MessageReceivedEvent event, boolean override, String cmd, String...args) {
		long self = event.getJDA().getSelfUser().getIdLong();
		StringBuilder raw = new StringBuilder(cmd);
		for (String arg : args) {
			raw.append(" ");
			raw.append(arg);
		}
		
		if (!commands.containsKey(self)) return;
		for (ICommand command : commands.get(self)) {
			if (override || (command.getCommand().matcher(raw.toString().toLowerCase()).matches() && command.canBeUsed(event))) {
				new Thread() {
					@Override
					public void run() {
						setName("Running Command - `" + raw + "`");
						command.run(event, args);
					}
				}.start();
				break;
			}
		}
	}
	
	public static ArrayList<ICommand> getCommands(long user) {
		return commands.get(user);
	}
	
	public static ArrayList<ICommand> values() {
		ArrayList<ICommand> cmd = new ArrayList<ICommand>();
		for (long key : commands.keySet()) cmd.addAll(commands.get(key));
		return cmd;
	}
}
