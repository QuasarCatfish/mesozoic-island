package com.quas.mesozoicisland.enums;

import com.quas.mesozoicisland.util.Pair;

public enum ItemID {
	
	Nothing(0, 0),
	MesozoicIslandTrainerLicense(1, 0),
	GuildBadge(2, 0),
	QuestBook(5, 0),
	Cookie(90, 0),
	GoldenCookie(90, 1),
	EggIncubator(91, 0),
	DinosaurCoin(100, 0),
	PlasticDinosaurCoin(100, 1),
	TutorialDinosaurCoin(100, 2),
	DungeonToken(101, 0),
	TeamToken(102, 0),
	XPPotion(200, 0),
	FTierXPPotion(201, 0),
	ETierXPPotion(202, 0),
	DTierXPPotion(203, 0),
	CTierXPPotion(204, 0),
	BTierXPPotion(205, 0),
	ATierXPPotion(206, 0),
	STierXPPotion(207, 0),
	SSTierXPPotion(208, 0),
	SSSTierXPPotion(209, 0),
	StarTierXPPotion(210, 0),
	PrismaticConverter(211, 0),
	ScentOfBattle(212, 0),
	FragranceOfBattle(213, 0),
	EauDeBataille(214, 0),
	ScentOfExperience(215, 0),
	FragranceOfExperience(216, 0),
	EauDeExperience(217, 0),
	ScentOfMoney(218, 0),
	FragranceOfMoney(219, 0),
	EauDeArgent(220, 0),
	DinosaurLocator(221, 0),
	EggLocator(222, 0),
	DungeonLocator(223, 0),
	ScentOfEgg(224, 0),
	FragranceOfEgg(225, 0),
	EauDeOeuf(226, 0),
	RubyPendant(301, 0),
	TopazPendant(302, 0),
	JadePendant(303, 0),
	GarnetPendant(304, 0),
	DiamondPendant(305, 0),
	CubanitePendant(306, 0),
	AquamarinePendant(307, 0),
	AmberPendant(308, 0),
	LuckyRibbon(309, 0),
	RaffleTicket(401, 0),
	RaffleTicketStub(402, 0),
	MysteryPresent(403, 0),
	DinosaurTreat(404, 0),
	ChocolateEgg(404, 4),
	HalloweenCandy(404, 10),
	TurkeyLeg(404, 11),
	DinoCane(404, 12),
	ChickenNugget(404, 13),
	Token(405, 0),
	ThanksgivingToken(405, 11),
	GiftPoint(405, 12),
	ChickenToken(405, 13),
	EggVoucher(406, 0),
	DinosaurVoucher(501, 0),
	DungeonTicket(502, 0),
	PremiumDungeonTicket(503, 0),
	CopperRareGachaToken(601, 0),
	BronzeRareGachaToken(602, 0),
	IronRareGachaToken(603, 0),
	SilverRareGachaToken(604, 0),
	GoldRareGachaToken(605, 0),
	PlatinumRareGachaToken(606, 0),
	RaidPass(701, 0),
	TitleRemover(10000, 0),
	DeveloperTitle(10001, 0),
	ModeratorTitle(10002, 0),
	GuineaPigTitle(10003, 0),
	FormerGuineaPigTitle(10003, 1),
	SupporterTitle(10004, 0),
	YouTuberTitle(10005, 0),
	VeteranTitle(10006, 0),
	GuildmasterTitle(10007, 0),
	FormerGuildmasterTitle(10007, 1),
	ChampionTitle(10008, 0),
	FormerChampionTitle(10008, 1),
	CustomTitle(10010, 0),
	CookieMonsterTitle(10010, 484873357045792769L),
	BeginnerTrainerTitle(10011, 0),
	NoviceTrainerTitle(10012, 0),
	RisingTrainerTitle(10013, 0),
	Level35Title(10014, 0),
	Level50Title(10015, 0),
	ExpertTrainerTitle(10016, 0),
	MasterTrainerTitle(10017, 0),
	Level200Title(10018, 0),
	Level300Title(10019, 0),
	Level500Title(10020, 0),
	GenerousTitle(10021, 0),
	ChickenHunterTitle(10022, 0);

	private int id;
	private long dmg;
	private ItemID(int id, long dmg) {
		this.id = id;
		this.dmg = dmg;
	}

	public Pair<Integer, Long> getId() {
		return new Pair<Integer, Long>(id, dmg);
	}

	public int getItemId() {
		return id;
	}

	public long getItemDamage() {
		return dmg;
	}
}