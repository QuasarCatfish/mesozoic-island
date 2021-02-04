package com.quas.mesozoicisland.enums;

public enum DiscordRole {

	MesozoicIslandResearcher(644346429959176192L),
	Admin(646815748181721088L),
	Moderator(646815809662091281L),
	Muted(646816160112836618L),
	
	DinosaurTrainer(646816586384277540L),
	Supporter(744136104038629428L),
	GuineaPig(646816490917724161L),
	NewPlayer(650472547833479229L),
	
	Guildmaster(650507893908570112L),
	FireGuild(650473216653000734L),
	LightningGuild(650507774194876436L),
	LeafGuild(650507796982530058L),
	EarthGuild(650507814590087170L),
	IceGuild(650507826942312468L),
	MetalGuild(650507841022722049L),
	WaterGuild(650507856730390566L),
	AirGuild(650507874698526721L),
	
	GameHost(689557709162414123L),
	GameParticipant(689557756721627172L),
	
	AnnouncementPing(653661008719511579L),
	EventPing(752045719833280522L),
	DailyPing(716676414145036370L),
	SpawnPing(646816640553713686L),
	DungeonPing(646816805544919070L),
	EggPing(686019856507994296L),
	RunePing(646816763811463179L),
	SuggestionPing(646816856899977243L),
	
	Everyone(643624107753340945L);
	
	private long roleid;
	private DiscordRole(long roleid) {
		this.roleid = roleid;
	}
	
	public long getIdLong() {
		return roleid;
	}
	
	@Override
	public String toString() {
		return "<@&" + roleid + ">";
	}
}
