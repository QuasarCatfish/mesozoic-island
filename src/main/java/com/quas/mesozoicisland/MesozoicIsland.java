package com.quas.mesozoicisland;

import javax.security.auth.login.LoginException;

import com.quas.mesozoicisland.battle.SpawnManager;
import com.quas.mesozoicisland.cmdadmin.AnnounceCommand;
import com.quas.mesozoicisland.cmdadmin.AnnouncementMarker;
import com.quas.mesozoicisland.cmdadmin.BirthdayMailCommand;
import com.quas.mesozoicisland.cmdadmin.CheckBattlesCommand;
import com.quas.mesozoicisland.cmdadmin.CheckBirthdaysCommand;
import com.quas.mesozoicisland.cmdadmin.ClearCacheCommand;
import com.quas.mesozoicisland.cmdadmin.ClearChannelCommand;
import com.quas.mesozoicisland.cmdadmin.ColorCommand;
import com.quas.mesozoicisland.cmdadmin.ColorHexCommand;
import com.quas.mesozoicisland.cmdadmin.EchoCommand;
import com.quas.mesozoicisland.cmdadmin.EventsCommand;
import com.quas.mesozoicisland.cmdadmin.GenerateRedeemCommand;
import com.quas.mesozoicisland.cmdadmin.GiveDinosaurCommand;
import com.quas.mesozoicisland.cmdadmin.GiveItemCommand;
import com.quas.mesozoicisland.cmdadmin.GiveRuneCommand;
import com.quas.mesozoicisland.cmdadmin.GuildDemoteCommand;
import com.quas.mesozoicisland.cmdadmin.GuildKickCommand;
import com.quas.mesozoicisland.cmdadmin.GuildPromoteCommand;
import com.quas.mesozoicisland.cmdadmin.InformationChannelsCommand;
import com.quas.mesozoicisland.cmdadmin.MuteCommand;
import com.quas.mesozoicisland.cmdadmin.RadioCommand;
import com.quas.mesozoicisland.cmdadmin.RemindCommand;
import com.quas.mesozoicisland.cmdadmin.RenamePlayerCommand;
import com.quas.mesozoicisland.cmdadmin.SetColorCommand;
import com.quas.mesozoicisland.cmdadmin.SpawnDungeonCommand;
import com.quas.mesozoicisland.cmdadmin.SpawnEggCommand;
import com.quas.mesozoicisland.cmdadmin.SpawnTimeCommand;
import com.quas.mesozoicisland.cmdadmin.SpawnTimeResetCommand;
import com.quas.mesozoicisland.cmdadmin.SpawnWildCommand;
import com.quas.mesozoicisland.cmdadmin.TestCommand;
import com.quas.mesozoicisland.cmdadmin.ThreadsCommand;
import com.quas.mesozoicisland.cmdadmin.ToggleCommand;
import com.quas.mesozoicisland.cmdadmin.UnmuteCommand;
import com.quas.mesozoicisland.cmdadmin.UpdatePlayerLevelsCommand;
import com.quas.mesozoicisland.cmdbase.AdminHelpCommand;
import com.quas.mesozoicisland.cmdbase.CommandManager;
import com.quas.mesozoicisland.cmdbase.HelpCommand;
import com.quas.mesozoicisland.cmdbase.HelpListCommand;
import com.quas.mesozoicisland.cmdbase.PingCommand;
import com.quas.mesozoicisland.cmdbase.QuitCommand;
import com.quas.mesozoicisland.cmdplayer.BagCategoryCommand;
import com.quas.mesozoicisland.cmdplayer.BagCommand;
import com.quas.mesozoicisland.cmdplayer.BirthdayCommand;
import com.quas.mesozoicisland.cmdplayer.BuyCommand;
import com.quas.mesozoicisland.cmdplayer.CreateTeamCommand;
import com.quas.mesozoicisland.cmdplayer.CreditsCommand;
import com.quas.mesozoicisland.cmdplayer.DailyCommand;
import com.quas.mesozoicisland.cmdplayer.DataCommand;
import com.quas.mesozoicisland.cmdplayer.DeleteTeamCommand;
import com.quas.mesozoicisland.cmdplayer.DinodexCommand;
import com.quas.mesozoicisland.cmdplayer.DinosaursCommand;
import com.quas.mesozoicisland.cmdplayer.EggsCommand;
import com.quas.mesozoicisland.cmdplayer.ElementsCommand;
import com.quas.mesozoicisland.cmdplayer.EquipCommand;
import com.quas.mesozoicisland.cmdplayer.EquipReverseCommand;
import com.quas.mesozoicisland.cmdplayer.GuildJoinCommand;
import com.quas.mesozoicisland.cmdplayer.HatchCommand;
import com.quas.mesozoicisland.cmdplayer.InfoDinosaurCommand;
import com.quas.mesozoicisland.cmdplayer.InfoEggCommand;
import com.quas.mesozoicisland.cmdplayer.InfoRuneCommand;
import com.quas.mesozoicisland.cmdplayer.InfoStringCommand;
import com.quas.mesozoicisland.cmdplayer.InfoTeamCommand;
import com.quas.mesozoicisland.cmdplayer.ItemCommand;
import com.quas.mesozoicisland.cmdplayer.LicenseCommand;
import com.quas.mesozoicisland.cmdplayer.ListEventsCommand;
import com.quas.mesozoicisland.cmdplayer.ListTeamsCommand;
import com.quas.mesozoicisland.cmdplayer.MailCheckAllCommand;
import com.quas.mesozoicisland.cmdplayer.MailCheckCommand;
import com.quas.mesozoicisland.cmdplayer.MailOpenCommand;
import com.quas.mesozoicisland.cmdplayer.NicknameCommand;
import com.quas.mesozoicisland.cmdplayer.PingmeAllCommand;
import com.quas.mesozoicisland.cmdplayer.PingmeCommand;
import com.quas.mesozoicisland.cmdplayer.PingmeNoneCommand;
import com.quas.mesozoicisland.cmdplayer.QuestsCommand;
import com.quas.mesozoicisland.cmdplayer.RankupDinosaurCommand;
import com.quas.mesozoicisland.cmdplayer.RankupRuneCommand;
import com.quas.mesozoicisland.cmdplayer.RedeemCommand;
import com.quas.mesozoicisland.cmdplayer.RunesCommand;
import com.quas.mesozoicisland.cmdplayer.SaveTeamCommand;
import com.quas.mesozoicisland.cmdplayer.SelectDinosaursCommand;
import com.quas.mesozoicisland.cmdplayer.SelectTeamCommand;
import com.quas.mesozoicisland.cmdplayer.SelectedCommand;
import com.quas.mesozoicisland.cmdplayer.ShopCommand;
import com.quas.mesozoicisland.cmdplayer.ShopListCommand;
import com.quas.mesozoicisland.cmdplayer.StatsPlayerCommand;
import com.quas.mesozoicisland.cmdplayer.StatsSelfCommand;
import com.quas.mesozoicisland.cmdplayer.StatsServerCommand;
import com.quas.mesozoicisland.cmdplayer.SuggestionMarker;
import com.quas.mesozoicisland.cmdplayer.TopCommand;
import com.quas.mesozoicisland.cmdplayer.TradeDinosaurCommand;
import com.quas.mesozoicisland.cmdplayer.TradeEggCommand;
import com.quas.mesozoicisland.cmdplayer.TradeRuneCommand;
import com.quas.mesozoicisland.cmdplayer.UnequipDinosaurCommand;
import com.quas.mesozoicisland.cmdplayer.UnequipRuneCommand;
import com.quas.mesozoicisland.cmdplayer.UnnicknameCommand;
import com.quas.mesozoicisland.cmdplayer.UptimeCommand;
import com.quas.mesozoicisland.cmdplayer.UseCommand;
import com.quas.mesozoicisland.cmdplayer.WikiCommand;
import com.quas.mesozoicisland.cmdtutorial.Tutorial00;
import com.quas.mesozoicisland.cmdtutorial.Tutorial01;
import com.quas.mesozoicisland.cmdtutorial.Tutorial02N;
import com.quas.mesozoicisland.cmdtutorial.Tutorial02Y;
import com.quas.mesozoicisland.cmdtutorial.Tutorial03;
import com.quas.mesozoicisland.cmdtutorial.Tutorial04N;
import com.quas.mesozoicisland.cmdtutorial.Tutorial04Y;
import com.quas.mesozoicisland.cmdtutorial.Tutorial05;
import com.quas.mesozoicisland.cmdtutorial.Tutorial06;
import com.quas.mesozoicisland.cmdtutorial.Tutorial07;
import com.quas.mesozoicisland.cmdtutorial.Tutorial08N;
import com.quas.mesozoicisland.cmdtutorial.Tutorial08Y;
import com.quas.mesozoicisland.cmdtutorial.Tutorial09;
import com.quas.mesozoicisland.cmdtutorial.Tutorial10;
import com.quas.mesozoicisland.cmdtutorial.Tutorial11;
import com.quas.mesozoicisland.cmdtutorial.Tutorial12;
import com.quas.mesozoicisland.cmdtutorial.Tutorial13;
import com.quas.mesozoicisland.cmdtutorial.Tutorial14;
import com.quas.mesozoicisland.cmdtutorial.Tutorial15;
import com.quas.mesozoicisland.cmdtutorial.Tutorial16;
import com.quas.mesozoicisland.cmdtutorial.Tutorial17;
import com.quas.mesozoicisland.cmdtutorial.Tutorial18;
import com.quas.mesozoicisland.cmdtutorial.Tutorial19;
import com.quas.mesozoicisland.cmdtutorial.TutorialBagCategoryCommand;
import com.quas.mesozoicisland.cmdtutorial.TutorialBagCommand;
import com.quas.mesozoicisland.cmdtutorial.TutorialBuyCommand;
import com.quas.mesozoicisland.cmdtutorial.TutorialDailyCommand;
import com.quas.mesozoicisland.cmdtutorial.TutorialDinosaursCommand;
import com.quas.mesozoicisland.cmdtutorial.TutorialInfoCommand;
import com.quas.mesozoicisland.cmdtutorial.TutorialSelectCommand;
import com.quas.mesozoicisland.cmdtutorial.TutorialSelectedCommand;
import com.quas.mesozoicisland.cmdtutorial.TutorialShopCommand;
import com.quas.mesozoicisland.cmdtutorial.TutorialUseCommand;
import com.quas.mesozoicisland.enums.DiscordChannel;
import com.quas.mesozoicisland.enums.SpawnType;
import com.quas.mesozoicisland.objects.Event;
import com.quas.mesozoicisland.util.Action;
import com.quas.mesozoicisland.util.Constants;
import com.quas.mesozoicisland.util.Secrets;
import com.quas.mesozoicisland.util.Util;

public class MesozoicIsland {
	
	private static Bot professor, assistant;
	private static final long START = System.currentTimeMillis();
	private static boolean READY = false;
	private static boolean QUIT = false;

	public static void main(String[] args) throws LoginException, InterruptedException {
		Thread.currentThread().setName("Main Thread");
		
		// Create Bots
		professor = new Bot(Secrets.PROFESSOR_TOKEN, new MesozoicListenerAdapter());
		assistant = new Bot(Secrets.ASSISTANT_TOKEN, new MesozoicListenerAdapter());
		
		// Add Extra Listeners
		professor.getJDA().addEventListener(new NewMember());
		professor.getJDA().addEventListener(new CheckNamesAndSpawn());
		professor.getJDA().addEventListener(new CheckMassPings());
		professor.getJDA().addEventListener(new CheckBattleMessage());
		
		// Professor Bot
		CommandManager.addCommand(professor.getIdLong(), new PingCommand());
		CommandManager.addCommand(professor.getIdLong(), new QuitCommand());
		CommandManager.addCommand(assistant.getIdLong(), new AnnouncementMarker());
		CommandManager.addCommand(professor.getIdLong(), new SuggestionMarker());
		CommandManager.addCommand(professor.getIdLong(), new AnnounceCommand());
		CommandManager.addCommand(professor.getIdLong(), new ClearChannelCommand());
		CommandManager.addCommand(professor.getIdLong(), new InformationChannelsCommand());
//		CommandManager.addCommand(professor.getIdLong(), new Command());
//		CommandManager.addCommand(professor.getIdLong(), new Command());
		
		// Tutorial Commands
		CommandManager.addCommand(professor.getIdLong(), new Tutorial00());
		CommandManager.addCommand(professor.getIdLong(), new Tutorial01());
		CommandManager.addCommand(professor.getIdLong(), new Tutorial02N());
		CommandManager.addCommand(professor.getIdLong(), new Tutorial02Y());
		CommandManager.addCommand(professor.getIdLong(), new Tutorial03());
		CommandManager.addCommand(professor.getIdLong(), new Tutorial04N());
		CommandManager.addCommand(professor.getIdLong(), new Tutorial04Y());
		CommandManager.addCommand(professor.getIdLong(), new Tutorial05());
		CommandManager.addCommand(professor.getIdLong(), new Tutorial06());
		CommandManager.addCommand(professor.getIdLong(), new Tutorial07());
		CommandManager.addCommand(professor.getIdLong(), new Tutorial08N());
		CommandManager.addCommand(professor.getIdLong(), new Tutorial08Y());
		CommandManager.addCommand(professor.getIdLong(), new Tutorial09());
		CommandManager.addCommand(professor.getIdLong(), new Tutorial10());
		CommandManager.addCommand(professor.getIdLong(), new Tutorial11());
		CommandManager.addCommand(professor.getIdLong(), new Tutorial12());
		CommandManager.addCommand(professor.getIdLong(), new Tutorial13());
		CommandManager.addCommand(professor.getIdLong(), new Tutorial14());
		CommandManager.addCommand(professor.getIdLong(), new Tutorial14());
		CommandManager.addCommand(professor.getIdLong(), new Tutorial15());
		CommandManager.addCommand(professor.getIdLong(), new Tutorial16());
		CommandManager.addCommand(professor.getIdLong(), new Tutorial17());
		CommandManager.addCommand(professor.getIdLong(), new Tutorial18());
		CommandManager.addCommand(professor.getIdLong(), new Tutorial19()); // Incomplete Tutorial Battle
		CommandManager.addCommand(assistant.getIdLong(), new TutorialBagCategoryCommand());
		CommandManager.addCommand(assistant.getIdLong(), new TutorialBagCommand());
		CommandManager.addCommand(assistant.getIdLong(), new TutorialBuyCommand());
		CommandManager.addCommand(assistant.getIdLong(), new TutorialDailyCommand());
		CommandManager.addCommand(assistant.getIdLong(), new TutorialDinosaursCommand());
		CommandManager.addCommand(assistant.getIdLong(), new TutorialInfoCommand());
		CommandManager.addCommand(assistant.getIdLong(), new TutorialSelectCommand());
		CommandManager.addCommand(assistant.getIdLong(), new TutorialSelectedCommand());
		CommandManager.addCommand(assistant.getIdLong(), new TutorialShopCommand());
		CommandManager.addCommand(assistant.getIdLong(), new TutorialUseCommand());
		
		// Assistant Bot Standard Commands
		CommandManager.addCommand(assistant.getIdLong(), new PingCommand());
		CommandManager.addCommand(assistant.getIdLong(), new HelpListCommand());
		CommandManager.addCommand(assistant.getIdLong(), new HelpCommand());
		CommandManager.addCommand(assistant.getIdLong(), new AdminHelpCommand());
		
		// Assistant Bot
		CommandManager.addCommand(assistant.getIdLong(), new BagCommand());
		CommandManager.addCommand(assistant.getIdLong(), new BagCategoryCommand());
		CommandManager.addCommand(assistant.getIdLong(), new DailyCommand());
		CommandManager.addCommand(assistant.getIdLong(), new BirthdayCommand());
		CommandManager.addCommand(assistant.getIdLong(), new DinosaursCommand());
		CommandManager.addCommand(assistant.getIdLong(), new BuyCommand());
		CommandManager.addCommand(assistant.getIdLong(), new ShopListCommand());
		CommandManager.addCommand(assistant.getIdLong(), new ShopCommand());
		CommandManager.addCommand(assistant.getIdLong(), new ItemCommand());
		CommandManager.addCommand(assistant.getIdLong(), new PingmeAllCommand()); // RUNES (in PingType Enum)
		CommandManager.addCommand(assistant.getIdLong(), new PingmeNoneCommand()); // RUNES (in PingType Enum)
		CommandManager.addCommand(assistant.getIdLong(), new PingmeCommand()); // RUNES (in PingType Enum)
		CommandManager.addCommand(assistant.getIdLong(), new InfoDinosaurCommand()); // RUNES (only if equipped)
		CommandManager.addCommand(assistant.getIdLong(), new InfoStringCommand());
		CommandManager.addCommand(assistant.getIdLong(), new ElementsCommand());
		CommandManager.addCommand(assistant.getIdLong(), new NicknameCommand());
		CommandManager.addCommand(assistant.getIdLong(), new UnnicknameCommand());
		CommandManager.addCommand(assistant.getIdLong(), new SelectDinosaursCommand());
		CommandManager.addCommand(assistant.getIdLong(), new SelectedCommand());
		CommandManager.addCommand(assistant.getIdLong(), new RankupDinosaurCommand());
		CommandManager.addCommand(assistant.getIdLong(), new TopCommand()); // RUNE
		CommandManager.addCommand(assistant.getIdLong(), new RunesCommand()); // RUNE RELATED - Disabled
		CommandManager.addCommand(assistant.getIdLong(), new EquipCommand()); // RUNE RELATED - Disabled
		CommandManager.addCommand(assistant.getIdLong(), new EquipReverseCommand()); // RUNE RELATED - Disabled
		CommandManager.addCommand(assistant.getIdLong(), new UnequipDinosaurCommand()); // RUNE RELATED - Disabled
		CommandManager.addCommand(assistant.getIdLong(), new UnequipRuneCommand()); // RUNE RELATED - Disabled
		CommandManager.addCommand(assistant.getIdLong(), new InfoRuneCommand()); // RUNE RELATED - Disabled
		CommandManager.addCommand(assistant.getIdLong(), new RankupRuneCommand()); // RUNE RELATED - Disabled
		CommandManager.addCommand(assistant.getIdLong(), new UptimeCommand());
		CommandManager.addCommand(assistant.getIdLong(), new UseCommand());
		CommandManager.addCommand(assistant.getIdLong(), new TradeDinosaurCommand());
		CommandManager.addCommand(assistant.getIdLong(), new TradeRuneCommand()); // RUNE RELATED - Disabled
		CommandManager.addCommand(assistant.getIdLong(), new TradeEggCommand()); // EGG RELATED
		CommandManager.addCommand(assistant.getIdLong(), new CreateTeamCommand());
		CommandManager.addCommand(assistant.getIdLong(), new DeleteTeamCommand());
		CommandManager.addCommand(assistant.getIdLong(), new SaveTeamCommand());
		CommandManager.addCommand(assistant.getIdLong(), new InfoTeamCommand());
		CommandManager.addCommand(assistant.getIdLong(), new SelectTeamCommand());
		CommandManager.addCommand(assistant.getIdLong(), new ListTeamsCommand());
		CommandManager.addCommand(assistant.getIdLong(), new RedeemCommand());
		CommandManager.addCommand(assistant.getIdLong(), new MailCheckCommand());
		CommandManager.addCommand(assistant.getIdLong(), new MailCheckAllCommand());
		CommandManager.addCommand(assistant.getIdLong(), new MailOpenCommand());
		CommandManager.addCommand(assistant.getIdLong(), new GuildJoinCommand());
		CommandManager.addCommand(assistant.getIdLong(), new GuildKickCommand());
		CommandManager.addCommand(assistant.getIdLong(), new StatsSelfCommand());
		CommandManager.addCommand(assistant.getIdLong(), new StatsPlayerCommand());
		CommandManager.addCommand(assistant.getIdLong(), new StatsServerCommand());
		CommandManager.addCommand(assistant.getIdLong(), new EggsCommand()); // EGG RELATED
		CommandManager.addCommand(assistant.getIdLong(), new HatchCommand()); // EGG RELATED
		CommandManager.addCommand(assistant.getIdLong(), new InfoEggCommand()); // EGG RELATED
		CommandManager.addCommand(assistant.getIdLong(), new ListEventsCommand());
		CommandManager.addCommand(assistant.getIdLong(), new DataCommand());
		CommandManager.addCommand(assistant.getIdLong(), new CreditsCommand());
		CommandManager.addCommand(assistant.getIdLong(), new DinodexCommand());
		CommandManager.addCommand(assistant.getIdLong(), new QuestsCommand());
		CommandManager.addCommand(assistant.getIdLong(), new WikiCommand());
		CommandManager.addCommand(assistant.getIdLong(), new LicenseCommand());
//		CommandManager.addCommand(assistant.getIdLong(), new Command());
//		CommandManager.addCommand(assistant.getIdLong(), new Command());
		
		// Admin Commands
		CommandManager.addCommand(assistant.getIdLong(), new RadioCommand()); // DISABLED
		CommandManager.addCommand(assistant.getIdLong(), new GiveDinosaurCommand());
		CommandManager.addCommand(assistant.getIdLong(), new GiveItemCommand());
		CommandManager.addCommand(assistant.getIdLong(), new GiveRuneCommand()); // RUNE RELATED
		CommandManager.addCommand(assistant.getIdLong(), new EventsCommand());
		CommandManager.addCommand(assistant.getIdLong(), new ThreadsCommand());
		CommandManager.addCommand(assistant.getIdLong(), new RemindCommand());
		CommandManager.addCommand(assistant.getIdLong(), new SpawnDungeonCommand());
		CommandManager.addCommand(assistant.getIdLong(), new SpawnWildCommand());
		CommandManager.addCommand(assistant.getIdLong(), new SpawnTimeCommand());
		CommandManager.addCommand(assistant.getIdLong(), new SpawnTimeResetCommand());
		CommandManager.addCommand(assistant.getIdLong(), new CheckBattlesCommand());
		CommandManager.addCommand(assistant.getIdLong(), new GenerateRedeemCommand());
		CommandManager.addCommand(assistant.getIdLong(), new UpdatePlayerLevelsCommand());
		CommandManager.addCommand(assistant.getIdLong(), new GuildPromoteCommand());
		CommandManager.addCommand(assistant.getIdLong(), new GuildDemoteCommand());
		CommandManager.addCommand(assistant.getIdLong(), new ClearCacheCommand());
		CommandManager.addCommand(assistant.getIdLong(), new TestCommand());
		CommandManager.addCommand(assistant.getIdLong(), new EchoCommand());
		CommandManager.addCommand(assistant.getIdLong(), new SpawnEggCommand());
		CommandManager.addCommand(assistant.getIdLong(), new ColorHexCommand());
		CommandManager.addCommand(assistant.getIdLong(), new ColorCommand());
		CommandManager.addCommand(assistant.getIdLong(), new ToggleCommand());
		CommandManager.addCommand(assistant.getIdLong(), new CheckBirthdaysCommand());
		CommandManager.addCommand(assistant.getIdLong(), new BirthdayMailCommand());
		CommandManager.addCommand(assistant.getIdLong(), new RenamePlayerCommand());
		CommandManager.addCommand(assistant.getIdLong(), new SetColorCommand());
		CommandManager.addCommand(assistant.getIdLong(), new MuteCommand());
		CommandManager.addCommand(assistant.getIdLong(), new UnmuteCommand());
//		CommandManager.addCommand(assistant.getIdLong(), new Command());
//		CommandManager.addCommand(assistant.getIdLong(), new Command());
//		CommandManager.addCommand(assistant.getIdLong(), new Command());
		
		// Other Initialization
		Event.initialize();
		
		// Send Ready Message
		Action.sendMessage(professor.getIdLong(), Constants.SPAWN_CHANNEL, "Ready.");
		Action.sendMessage(professor.getIdLong(), DiscordChannel.GameTesting, String.format(
				"**DEBUG:**%n"
				+ "UPDATE_ROLES = %b%n"
				+ "SHOW_READY = %b%n"
				+ "DEBUG_COMMAND = %b%n"
				+ "DEBUG_BATTLE = %b%n"
				+ "MAX_DEX_DIGITS = %d%n"
				+ "SPAWN_DUNGEONS = %b%n"
				+ "SPAWN_EGGS = %b%n",
				Constants.ROLES_ENABLED,
				Constants.SHOW_READY,
				Constants.DEBUG_COMMAND,
				Constants.DEBUG_BATTLE,
				Constants.MAX_DEX_DIGITS,
				Constants.SPAWN_DUNGEONS,
				Constants.SPAWN_EGGS));
		READY = true;
		
		new Thread() {
			@Override
			public void run() {
				long start = System.currentTimeMillis();
				
				while (true) {
					try {
						// Do for 10 minute.
						for (int q = 1; q <= 600; q++) {
							Action.doActions(professor.getGuild());
							Action.doActions(assistant.getGuild());
							if (Constants.UPDATE_EGG_HP && q % 60 == 0) JDBC.updateEggs();
							if (SpawnManager.doAutoSpawn()) SpawnManager.trySpawn(SpawnType.Random, false);
							Util.sleep(1_000);
						}
						
						// Ping database.
						System.out.printf("Ping at %s!\n", Util.formatTime(System.currentTimeMillis() - start));
						JDBC.ping();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			};
		}.start();

		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				System.out.println("Shutting down.");
				professor.getJDA().shutdown();
				assistant.getJDA().shutdown();
			}
		});
	}
	
	public static void setQuit(boolean quit) {
		QUIT = quit;
	}
	
	public static boolean isQuitting() {
		return QUIT;
	}
	
	public static Bot getBot(long id) {
		if (id == professor.getIdLong()) return professor;
		if (id == assistant.getIdLong()) return assistant;
		return null;
	}
	
	public static Bot getProfessor() {
		return professor;
	}
	
	public static Bot getAssistant() {
		return assistant;
	}
	
	public static long getUptime() {
		return System.currentTimeMillis() - START;
	}
	
	public static boolean isReady() {
		return READY;
	}
}
