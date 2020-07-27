package com.quas.mesozoicisland.cmdadmin;

import java.util.regex.Pattern;

import com.quas.mesozoicisland.battle.SpawnManager;
import com.quas.mesozoicisland.cmdbase.ICommand;
import com.quas.mesozoicisland.enums.AccessLevel;
import com.quas.mesozoicisland.enums.DiscordChannel;
import com.quas.mesozoicisland.enums.DiscordRole;
import com.quas.mesozoicisland.util.Constants;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class ToggleCommand implements ICommand {

	@Override
	public Pattern getCommand() {
		return pattern("admin toggle \\w+");
	}

	@Override
	public AccessLevel getAccessLevel() {
		return AccessLevel.Admin;
	}

	@Override
	public String getCommandName() {
		return "admin";
	}

	@Override
	public String getCommandSyntax() {
		return "admin toggle <field>";
	}

	@Override
	public String getCommandDescription() {
		return "Toggles the given field.";
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
		Toggle t = null;
		for (Toggle toggle : Toggle.values()) {
			if (toggle.name().equalsIgnoreCase(args[1])) {
				t = toggle;
			}
		}
		
		if (t == null) {
			event.getChannel().sendMessageFormat("%s, `%s` is an invalid field to toggle.", event.getAuthor().getAsMention(), args[1]).complete();
		} else if (t.isEnabled()) {
			switch (t) {
			case Spawn:
				SpawnManager.setSpawnTime(Long.MAX_VALUE - 1);
				break;
			case Dungeon:
				Constants.SPAWN_DUNGEONS = false;
				break;
			case Egg:
				Constants.SPAWN_EGGS = false;
				break;
			case HP:
				Constants.UPDATE_EGG_HP = false;
				break;
			}
			
			t.toggle();
			event.getChannel().sendMessageFormat("%s, %s has been disabled.", event.getAuthor().getAsMention(), t.getMessage()).complete();
		} else {
			switch (t) {
			case Spawn:
				SpawnManager.setSpawnTime();
				break;
			case Dungeon:
				Constants.SPAWN_DUNGEONS = true;
				break;
			case Egg:
				Constants.SPAWN_EGGS = true;
				break;
			case HP:
				Constants.UPDATE_EGG_HP = true;
				break;
			}
			
			t.toggle();
			event.getChannel().sendMessageFormat("%s, %s has been enabled.", event.getAuthor().getAsMention(), t.getMessage()).complete();
		}
	}
	
	private enum Toggle {
		Spawn("dinosaur spawning", true),
		Egg("egg spawning", Constants.SPAWN_EGGS),
		Dungeon("dungeon spawning", Constants.SPAWN_DUNGEONS),
		HP("hatch point gains", Constants.UPDATE_EGG_HP);
		
		private String msg;
		private boolean value;
		private Toggle(String msg, boolean init) {
			this.msg = msg;
			this.value = init;
		}
		
		public boolean isEnabled() {
			return value;
		}
		
		public void toggle() {
			this.value = !this.value;
		}
		
		public String getMessage() {
			return msg;
		}
		
		@Override
		public String toString() {
			return String.format("Toggle.%s[value=%b]", name(), value);
		};
	}
}
