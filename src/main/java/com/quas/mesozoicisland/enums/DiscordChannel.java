package com.quas.mesozoicisland.enums;

import com.quas.mesozoicisland.Bot;
import com.quas.mesozoicisland.MesozoicIsland;
import com.quas.mesozoicisland.util.Util;

import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.TextChannel;

public enum DiscordChannel {

	// Generic Channels
	None(-1), All(-1), DirectMessages(-1),
	
	// Staff
	Admins(648004304606724096L),
	Mods(646819619490889748L),
	Moderation(650470913183318036L),
	BotDMs(748380700864151684L),
	
	// Information
	Rules(650471046436356107L),
	Introduction(650471102933630995L),
	Channels(730762074401275914L),
	Announcements(650471070180180021L),
	DailyAnnouncements(717248577839300618L),
	AnnouncementLog(726486491177680977L),
	Changelog(650471168872022069L),
	
	// Game
	Game(650474865459068990L),
	Trading(747106027450925176L),
	BotCommands(650477334801154062L),
	Wiki(651587468503547920L),
	Events(650477850469597212L),
	Contest(751301085372612608L),
	GameTesting(644340347090108436L),
	Supporters(744136913727783023L),
	EventPlanning(653383425927151639L),
	
	// Guilds
	GuildAnnouncements(716437696897220608L),
	Guildmasters(650508685113688094L),
	FireGuild(650508750494498816L),
	LightningGuild(650508811358044161L),
	LeafGuild(650508854093676544L),
	EarthGuild(650508889451921458L),
	IceGuild(650508926176985107L),
	MetalGuild(650508960054640662L),
	WaterGuild(650508992392724530L),
	AirGuild(650509034700668969L),

	// World
	CaveOfLostHope(752724608955973662L),
	DebugCaveOfLostHope(753933646125203547L),
	
	// Chatting
	Chatting(-1L),
	
	// Battle
	Battle1(650478148106059818L),
	Battle2(650478188006473731L),
	Battle3(650478222496104469L),
	Battle4(748271152597565634L),
	BattlePVP(650480534102999042L),
	BattleContest(650480579887890483L),
	BattleLog(650530845899227147L),
	BattleDungeon(655592891720269855L),
	BattleDungeonLog(655592913845354506L),
	BattleTest(650478290716327956L),
	BattleTestLog(650530876563652660L),
	BattleSpecial(650478308739514379L),
	BattleSpecialLog(650530946881159198L),
	BattleStatus(650479327598870541L),
	
	// Dinosaur Arcade
	HostedGame(650494520584175626L),
	ArcadeMachines(650494537537683466L),
	
	// Suggestions
	GameSuggestions(650482395857813562L),
	SuggestionFeedback(650482450237227020L),
	PreviousSuggestions(650484317180395521L),
	BugReports(650522213942034463L),
	Feedback(744439646645256203L),
	
	// Welcome
	Readme(681425620777041930L),
	Welcome(650473995984044042L),
	CloneMe(650474025126199363L),
	ClonedChannel(-1L),
	
	// Voice Channels
	DinoRadio(-1L);
	
	// Channel Categories
	public static final long CATEGORY_STAFF = 646819581801005076L;
	public static final long CATEGORY_INFORMATION = 650470957680427040L;
	public static final long CATEGORY_WELCOME = 650473166434598943L;
	public static final long CATEGORY_GAME = 650474782738874387L;
	public static final long CATEGORY_GUILDS = 650508616251736091L;
	public static final long CATEGORY_CHATTING = 650475185157308419L;
	public static final long CATEGORY_BATTLE = 650478092825001994L;
	public static final long CATEGORY_ARCADE = 650494504842952714L;
	public static final long CATEGORY_SUGGESTIONS = 650480904527151134L;
	
	// Channel Groups
	public static final DiscordChannel[] NONE = new DiscordChannel[0];
	public static final DiscordChannel[] ALL_CHANNELS = Util.arr(All);
	public static final DiscordChannel[] SUGGESTION_CHANNELS = Util.arr(GameSuggestions);
	public static final DiscordChannel[] SUGGESTION_FEEDBACK_CHANNELS = Util.arr(Admins, GameTesting, BotCommands, SuggestionFeedback, DirectMessages);
	public static final DiscordChannel[] STANDARD_CHANNELS = Util.arr(Game, BotCommands, Admins, GameTesting, FireGuild, LightningGuild, LeafGuild, EarthGuild, IceGuild, MetalGuild, WaterGuild, AirGuild);
	public static final DiscordChannel[] STANDARD_CHANNELS_DMS = Util.arr(Game, BotCommands, Admins, GameTesting, DirectMessages, FireGuild, LightningGuild, LeafGuild, EarthGuild, IceGuild, MetalGuild, WaterGuild, AirGuild);
	public static final DiscordChannel[] STANDARD_CHANNELS_TRADE_DMS = Util.arr(Game, Trading, BotCommands, Admins, GameTesting, DirectMessages, FireGuild, LightningGuild, LeafGuild, EarthGuild, IceGuild, MetalGuild, WaterGuild, AirGuild);
	public static final DiscordChannel[] TRADE_CHANNELS = Util.arr(Trading, GameTesting, Admins);
	public static final DiscordChannel[] TRADE_CHANNELS_BOT_DMS = Util.arr(Trading, BotCommands, GameTesting, Admins, DirectMessages);
	public static final DiscordChannel[] TESTING_CHANNELS = Util.arr(Admins, GameTesting);
	public static final DiscordChannel[] TESTING_CHANNELS_DMS = Util.arr(Admins, GameTesting, DirectMessages);
	public static final DiscordChannel[] GUILD_CHANNELS = Util.arr(FireGuild, LightningGuild, LeafGuild, EarthGuild, IceGuild, MetalGuild, WaterGuild, AirGuild);
	public static final DiscordChannel[] GUILDMASTER_GUILD_CHANNELS = Util.arr(Guildmasters, FireGuild, LightningGuild, LeafGuild, EarthGuild, IceGuild, MetalGuild, WaterGuild, AirGuild);
	public static final DiscordChannel[] BATTLE_CHANNELS = Util.arr(Battle1, Battle2, Battle3, BattlePVP, BattleContest, BattleDungeon, BattleTest, BattleSpecial);
	
	/////////////////////////////////////////
	
	private long channelid;
	private DiscordChannel(long channelid) {
		this.channelid = channelid;
	}
	
	public String getId() {
		return Long.toString(channelid);
	}
	
	public long getIdLong() {
		return channelid;
	}
	
	@Override
	public String toString() {
		return "<#" + channelid + ">";
	}
	
	public TextChannel getChannel(Bot bot) {
		if (channelid < 0) return null;
		return bot.getGuild().getTextChannelById(channelid);
	}
	
	public static DiscordChannel getChannel(MessageChannel channel) {
		for (DiscordChannel dc : values()) {
			if (dc.channelid == channel.getIdLong()) {
				return dc;
			}
		}
		
		TextChannel tc = MesozoicIsland.getProfessor().getGuild().getTextChannelById(channel.getId());
		if (tc == null) return DirectMessages;
		if (Util.isChannelInCategory(tc, CATEGORY_WELCOME)) return ClonedChannel;
		if (Util.isChannelInCategory(tc, CATEGORY_CHATTING)) return Chatting;
		return None;
	}
}
