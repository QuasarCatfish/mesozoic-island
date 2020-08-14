package com.quas.mesozoicisland.cmdplayer;

import java.util.TreeMap;
import java.util.regex.Pattern;

import com.quas.mesozoicisland.JDBC;
import com.quas.mesozoicisland.MesozoicIsland;
import com.quas.mesozoicisland.battle.Battle;
import com.quas.mesozoicisland.battle.BattleChannel;
import com.quas.mesozoicisland.battle.BattleTeam;
import com.quas.mesozoicisland.battle.BattleType;
import com.quas.mesozoicisland.battle.SpawnManager;
import com.quas.mesozoicisland.cmdbase.CommandManager;
import com.quas.mesozoicisland.cmdbase.ICommand;
import com.quas.mesozoicisland.enums.AccessLevel;
import com.quas.mesozoicisland.enums.CustomPlayer;
import com.quas.mesozoicisland.enums.DinosaurForm;
import com.quas.mesozoicisland.enums.DiscordChannel;
import com.quas.mesozoicisland.enums.DiscordRole;
import com.quas.mesozoicisland.enums.EggColor;
import com.quas.mesozoicisland.enums.ItemCategory;
import com.quas.mesozoicisland.enums.ItemID;
import com.quas.mesozoicisland.enums.ItemType;
import com.quas.mesozoicisland.enums.SpawnType;
import com.quas.mesozoicisland.enums.Stat;
import com.quas.mesozoicisland.objects.Dinosaur;
import com.quas.mesozoicisland.objects.Egg;
import com.quas.mesozoicisland.objects.Item;
import com.quas.mesozoicisland.objects.Player;
import com.quas.mesozoicisland.util.Action;
import com.quas.mesozoicisland.util.Constants;
import com.quas.mesozoicisland.util.MesozoicRandom;
import com.quas.mesozoicisland.util.Util;

import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class UseCommand implements ICommand {

	@Override
	public Pattern getCommand() {
		return pattern("use ", INTEGER, "( ", DINOSAUR, ")?");
	}

	@Override
	public AccessLevel getAccessLevel() {
		return AccessLevel.Trainer;
	}

	@Override
	public String getCommandName() {
		return "use";
	}

	@Override
	public String getCommandSyntax() {
		return "use <item> [dinosaur]";
	}

	@Override
	public String getCommandDescription() {
		return "Uses an item.";
	}

	@Override
	public DiscordChannel[] getUsableChannels() {
		return DiscordChannel.STANDARD_CHANNELS;
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
	public synchronized void run(MessageReceivedEvent event, String... args) {
		Player p = Player.getPlayer(event.getAuthor().getIdLong());
		if (p == null) return;
		TreeMap<Item, Long> bag = p.getBag();
		
		Item[] items = Item.getItems(Integer.parseInt(args[0]));
		if (items.length == 0) {
			event.getChannel().sendMessageFormat("%s, this item doesn't exist.", p.getAsMention()).complete();
			return;
		}
		
		Item i = items[0];
		for (Item it : items) {
			if (bag.getOrDefault(it, 0L) > 0) {
				i = it;
				break;
			}
		}
		
		if (i == null || i.getItemCategory() == ItemCategory.None) {
			event.getChannel().sendMessageFormat("%s, this item doesn't exist.", p.getAsMention()).complete();
			return;
		} else if (!i.isDiscovered()) {
			event.getChannel().sendMessageFormat("%s, you don't have this unknown item.", p.getAsMention()).complete();
			return;
		} else if (bag.getOrDefault(i, 0L) <= 0) {
			event.getChannel().sendMessageFormat("%s, you don't have %s %s.", p.getAsMention(), Util.getArticle(i.toString()), i.toString()).complete();
			return;
		}
		
		boolean CONSUME = i.getItemType() == ItemType.Held || i.getItemType() == ItemType.Consume || i.getItemType() == ItemType.ConsumeDinosaur;
		boolean DINOSAUR = i.getItemType() == ItemType.Held || i.getItemType() == ItemType.ConsumeDinosaur;
		boolean SUCCESS = true;
		
		Dinosaur d = args.length > 1 ? Dinosaur.getDinosaur(p.getIdLong(), Util.getDexForm(args[1])) : null;
		if (DINOSAUR && d == null) {
			event.getChannel().sendMessageFormat("%s, you must use this item on a dinosaur.", p.getAsMention()).complete();
			return;
		}
		
		switch (i.getItemType()) {
		case Title:
			boolean invert = Util.isInvertedTitle(i);
			if (invert) event.getChannel().sendMessageFormat("%s, you will now be recognized as \"%s\".", p.getAsMention(), i.getData()).complete();
			else event.getChannel().sendMessageFormat("%s, you will now be recognized as %s \"%s\".", p.getAsMention(), Util.getArticle(i.getData()), i.getData()).complete();
			JDBC.setTitle(event.getAuthor().getIdLong(), i.getData(), invert);
			break;
			
		case PersistCount:
			long count = bag.getOrDefault(i, 0L);
			if (count == 1L) event.getChannel().sendMessageFormat("%s shows off their %s.", p.getAsMention(), i.toString()).complete();
			else event.getChannel().sendMessageFormat("%s shows off their %,d %s.", p.getAsMention(), bag.getOrDefault(i, 0L), i.toString(bag.getOrDefault(i, 0L))).complete();
			break;
			
		case PersistWithCustomUse:

			if (i.getId() == ItemID.MesozoicIslandTrainerLicense.getItemId()) {
				CommandManager.handleCommand(event, "license");
			}
			
			else if (i.getId() == ItemID.QuestBook.getItemId()) {
				CommandManager.handleCommand(event, "quests");
			}
			
			else if (i.getId() == ItemID.EggIncubator.getItemId()) {
				CommandManager.handleCommand(event, "eggs");
			}

			else if (i.getId() == ItemID.TitleRemover.getItemId()) {
				if (p.hasInvertedTitle()) event.getChannel().sendMessageFormat("%s, you will no longer be recognized as \"%s\".", p.getAsMention(), p.getTitle()).complete();
				else event.getChannel().sendMessageFormat("%s, you will no longer be recognized as %s \"%s\".", p.getAsMention(), Util.getArticle(p.getTitle()), p.getTitle()).complete();
				JDBC.setTitle(p.getIdLong(), null, false);
			}

			else {
				sendUnimplemented(event);
				SUCCESS = false;
			}
			break;
			
		case Consume:

			if (i.getId() == ItemID.ScentOfBattle.getItemId() || i.getId() == ItemID.FragranceOfBattle.getItemId() || i.getId() == ItemID.EauDeBataille.getItemId()) {
				long time = Long.parseLong(i.getData());
				long curtime = p.getFragranceBattleTimer();
				String adj = curtime < System.currentTimeMillis() ? "the next" : "an additional";
				long end = Math.max(curtime, System.currentTimeMillis()) + time;
				
				event.getChannel().sendMessageFormat("%s, the %s fills the air around you for %s %s.", p.getAsMention(), i.toString(), adj, Util.formatTime(time)).complete();
				JDBC.executeUpdate("update players set fragrancebattle = %d where playerid = %d;", end, p.getIdLong());
			}

			else if (i.getId() == ItemID.ScentOfExperience.getItemId() || i.getId() == ItemID.FragranceOfExperience.getItemId() || i.getId() == ItemID.EauDeExperience.getItemId()) {
				long time = Long.parseLong(i.getData());
				long curtime = p.getFragranceXpTimer();
				String adj = curtime < System.currentTimeMillis() ? "the next" : "an additional";
				long end = Math.max(curtime, System.currentTimeMillis()) + time;
				
				event.getChannel().sendMessageFormat("%s, the %s fills the air around you for %s %s.", p.getAsMention(), i.toString(), adj, Util.formatTime(time)).complete();
				JDBC.executeUpdate("update players set fragrancexp = %d where playerid = %d;", end, p.getIdLong());
			}

			else if (i.getId() == ItemID.ScentOfMoney.getItemId() || i.getId() == ItemID.FragranceOfMoney.getItemId() || i.getId() == ItemID.EauDeArgent.getItemId()) {
				long time = Long.parseLong(i.getData());
				long curtime = p.getFragranceMoneyTimer();
				String adj = curtime < System.currentTimeMillis() ? "the next" : "an additional";
				long end = Math.max(curtime, System.currentTimeMillis()) + time;
				
				event.getChannel().sendMessageFormat("%s, the %s fills the air around you for %s %s.", p.getAsMention(), i.toString(), adj, Util.formatTime(time)).complete();
				JDBC.executeUpdate("update players set fragrancemoney = %d where playerid = %d;", end, p.getIdLong());
			}

			else if (i.getId() == ItemID.DinosaurLocator.getItemId()) {
				if (!Constants.SPAWN) {
					event.getChannel().sendMessageFormat("%s tries to use the %s, but dinosaur spawning is disabled.", p.getAsMention(), i.toString()).complete();
					SUCCESS = false;
				} else if (SpawnManager.getSpawnTime() <= System.currentTimeMillis()) {
					event.getChannel().sendMessageFormat("%s tries to use the %s, but a spawn is occuring.", p.getAsMention(), i.toString()).complete();
					SUCCESS = false;
				} else if (SpawnManager.trySpawn(SpawnType.Wild, true)) {
					event.getChannel().sendMessageFormat("%s uses the %s to look for dinosaurs to battle.", p.getAsMention(), i.toString()).complete();
				} else {
					event.getChannel().sendMessageFormat("%s tries to use the %s, but there's already a spawn or battle.", p.getAsMention(), i.toString()).complete();
					SUCCESS = false;
				}
			}

			else if (i.getId() == ItemID.EggLocator.getItemId()) {
				if (!Constants.SPAWN || !Constants.SPAWN_EGGS) {
					event.getChannel().sendMessageFormat("%s tries to use the %s, but egg spawning is disabled.", p.getAsMention(), i.toString()).complete();
					SUCCESS = false;
				} else if (SpawnManager.getSpawnTime() <= System.currentTimeMillis()) {
					event.getChannel().sendMessageFormat("%s tries to use the %s, but a spawn is occuring.", p.getAsMention(), i.toString()).complete();
					SUCCESS = false;
				} else if (SpawnManager.trySpawn(SpawnType.Egg, true)) {
					event.getChannel().sendMessageFormat("%s uses the %s to look for some eggs.", p.getAsMention(), i.toString()).complete();
				} else {
					event.getChannel().sendMessageFormat("%s tries to use the %s, but it failed.", p.getAsMention(), i.toString()).complete();
					SUCCESS = false;
				}
			}

			else if (i.getId() == ItemID.DungeonLocator.getItemId()) {
				if (!Constants.SPAWN || !Constants.SPAWN_DUNGEONS) {
					event.getChannel().sendMessageFormat("%s tries to use the %s, but dungeon spawning is disabled.", p.getAsMention(), i.toString()).complete();
					SUCCESS = false;
				} else if (SpawnManager.getSpawnTime() <= System.currentTimeMillis()) {
					event.getChannel().sendMessageFormat("%s tries to use the %s, but a spawn is occuring.", p.getAsMention(), i.toString()).complete();
					SUCCESS = false;
				} else if (SpawnManager.isDungeonSpawned()) {
					event.getChannel().sendMessageFormat("%s tries to use the %s, but there's already a dungeon being explored.", p.getAsMention(), i.toString()).complete();
					SUCCESS = false;
				} else if (SpawnManager.trySpawn(SpawnType.Dungeon, true)) {
					event.getChannel().sendMessageFormat("%s uses the %s to look for a dungeon to explore.", p.getAsMention(), i.toString()).complete();
				} else {
					event.getChannel().sendMessageFormat("%s tries to use the %s, but there's already a dungeon being explored.", p.getAsMention(), i.toString()).complete();
					SUCCESS = false;
				}
			}

			else if (i.getId() == ItemID.EggVoucher.getItemId()) {
				if (bag.getOrDefault(Item.getItem(ItemID.EggIncubator), 0L) > p.getEggCount()) {
					event.getChannel().sendMessageFormat("%s, you have redeemed a Chocolate Egg!", p.getAsMention()).complete();
					Egg egg = Egg.getRandomEgg(MesozoicRandom.nextDinosaur().getIdPair());
					egg.setEggName("Chocolate Egg");
					egg.setEggColor(EggColor.SaddleBrown);
					JDBC.addEgg(p.getIdLong(), egg);
				} else {
					event.getChannel().sendMessageFormat("%s, you don't have any open incubator slots.", p.getAsMention()).complete();
					SUCCESS = false;
				}
			}

			else if (i.getId() == ItemID.DinosaurVoucher.getItemId()) {
				sendUnimplemented(event);
				SUCCESS = false;
			}

			else if (i.getId() == ItemID.DungeonTicket.getItemId() || i.getId() == ItemID.PremiumDungeonTicket.getItemId()) {
				int tier = Integer.parseInt(i.getData());
				JDBC.generateDungeonTickets();
				if (args.length > 1) {
					Dinosaur d2 = Dinosaur.getDinosaur(Util.getDexForm(args[1]));
					if (d2 == null) {
						event.getChannel().sendMessageFormat("%s, this dinosaur does not exist.", p.getAsMention()).complete();
						SUCCESS = false;
					} else {
						String[] dinos = JDBC.getDungeonTickets(tier).split("\\s+");
						boolean contained = false;
						for (String dino : dinos) if (dino.equalsIgnoreCase(d2.getId())) contained = true;
						
						if (contained) {
							event.getChannel().sendMessageFormat("%s, you have redeemed %s %s with your %s.", p.getAsMention(), Util.getArticle(d2.getDinosaurName()), d2.getDinosaurName(), i.toString()).complete();
							JDBC.addDinosaur(event.getChannel(), p.getIdLong(), d2.getIdPair());
						} else {
							event.getChannel().sendMessageFormat("%s, the %s is not offered in today's selection of dungeon dinosaurs.", p.getAsMention(), d2.getDinosaurName()).complete();
							SUCCESS = false;
						}
					}
				} else {
					String[] dinos = JDBC.getDungeonTickets(tier).split("\\s+");
					String[] lowtier = JDBC.getDungeonTickets(tier - 1).split("\\s+");
					
					StringBuilder sb = new StringBuilder();
					sb.append(p.getAsMention());
					sb.append(", here is today's selection of dinosaurs:");
					for (String dino : dinos) {
						boolean lower = false;
						for (String s : lowtier) if (s.equals(dino)) lower = true;

						Dinosaur d2 = Dinosaur.getDinosaur(Util.getDexForm(dino));
						sb.append(String.format("\n• %s%s [%s] [%s]", d2.toString(), lower ? "\u2020" : "", d2.getElement().getName(), d2.getRarity().getName()));
					}
					sb.append("\n\n\u2020This dinosaur appears in a lower tier of Dungeon Ticket.\nTo redeem one of these dinosaurs, use the command `use ");
					sb.append(i.getId());
					sb.append(" <dino id>`.\nThis selection will reset in ");
					sb.append(Util.formatTime(Util.getTimeLeftInDay()));
					sb.append(".");
					
					event.getChannel().sendMessage(sb.toString()).complete();
					SUCCESS = false;
				}
			}

			else if (i.getId() == ItemID.RaidPass.getItemId()) {
				if (Battle.isPlayerBattling(p.getIdLong())) {
					event.getChannel().sendMessageFormat("%s, you cannot challenge this Raid Boss because you are already in another battle.", p.getAsMention()).complete();
					SUCCESS = false;
				} else if (Battle.isPlayerBattling(CustomPlayer.RaidChallenge.getIdLong())) {
					event.getChannel().sendMessageFormat("%s, you cannot challenge this Raid Boss because someone else is currently challenging it.", p.getAsMention()).complete();
					SUCCESS = false;
				} else {
					// Raid Boss Dinosaur
					String[] data = i.getData().split("\\s+");
					Dinosaur raid = Dinosaur.getDinosaur(Integer.parseInt(data[0]), DinosaurForm.RaidBoss.getId());
					raid.setLevel(Integer.parseInt(data[1]));
					raid.setRank(0);
					
					// Set up Battle
					Battle b = new Battle(BattleChannel.Special, BattleType.Boss, MesozoicRandom.nextUnusedLocation());
					BattleTeam boss = new BattleTeam(Player.getPlayer(CustomPlayer.RaidChallenge.getIdLong()), Util.arr(raid));
					BattleTeam player = new BattleTeam(p);
					b.addBoss(boss);
					b.addTeam(player);
					event.getChannel().sendMessageFormat("%s is battling the %s! Go to %s to watch the battle in action!", p.getAsMention(), raid.getEffectiveName(), BattleChannel.Special.getBattleChannel().toString()).complete();
					
					// Start Battle
					Battle.markPlayerBattling(CustomPlayer.RaidChallenge.getIdLong(), true);
					Battle.markPlayerBattling(p.getIdLong(), true);
					JDBC.addItem(p.getIdLong(), Stat.RaidsAttempted.getId());
					long time = b.start(0);
					
					// End Battle
					Action.removePlayerFromBattleDelayed(CustomPlayer.RaidChallenge.getIdLong(), time);
					Action.removePlayerFromBattleDelayed(p.getIdLong(), time);
					
					// Prizes
					if (!b.didBossWin()) {
						String prize = "item 100 0 1000";
						Action.sendDelayedMessage(MesozoicIsland.getAssistant().getIdLong(), time + 1_000, Constants.SPAWN_CHANNEL, String.format("%s, you find these rewards from the %s:\n%s", p.getAsMention(), raid.getEffectiveName(), JDBC.getRedeemMessage(prize)));
						Action.addRedeemDelayed(MesozoicIsland.getAssistant().getIdLong(), p.getIdLong(), time + 1_000, prize);
						Action.addItemDelayed(p.getIdLong(), time + 1_000, Stat.RaidsAttempted.getId(), 1);
					}
					
					// Log Channel
					Action.logBattleChannelDelayed(MesozoicIsland.getAssistant().getIdLong(), BattleChannel.Special.getBattleChannel().getIdLong(), time + 30_000);
				}
			}

			else {
				sendUnimplemented(event);
				SUCCESS = false;
			}
			break;
			
		case ConsumeDinosaur:
			
			if (i.getId() == ItemID.XPPotion.getItemId()) {
				String[] split = i.getData().split("\\s+");
				int dex = Integer.parseInt(split[0]);
				int form = Integer.parseInt(split[1]);
				long xp = Long.parseLong(split[2]);
				
				if (d.getDex() == dex && d.getForm() == form) {
					if (d.getLevel() == Constants.MAX_LEVEL) {
						event.getChannel().sendMessageFormat("%s, your %s is at the max level. You cannot use the %s on it.", p.getAsMention(), d.getEffectiveName(), i.toString()).complete();
						SUCCESS = false;
					} else {
						event.getChannel().sendMessageFormat("%s, your %s gained %s XP from the %s.", p.getAsMention(), d.getEffectiveName(), i.getId() == 210 ? "∞" : Util.formatNumber(xp), i.toString()).complete();
						JDBC.addXp(event.getChannel(), p.getIdLong(), d.getIdPair(), xp);
					}
				} else {
					Dinosaur d2 = Dinosaur.getDinosaur(dex, form);
					event.getChannel().sendMessageFormat("%s, this XP Potion can only be used on a %s.", p.getAsMention(), d2.getDinosaurName()).complete();
					SUCCESS = false;
				}
			}
			
			else if (i.getId() == ItemID.FTierXPPotion.getItemId() || i.getId() == ItemID.ETierXPPotion.getItemId() || i.getId() == ItemID.DTierXPPotion.getItemId() || i.getId() == ItemID.CTierXPPotion.getItemId() || i.getId() == ItemID.BTierXPPotion.getItemId() || i.getId() == ItemID.ATierXPPotion.getItemId() || i.getId() == ItemID.STierXPPotion.getItemId() || i.getId() == ItemID.SSTierXPPotion.getItemId() || i.getId() == ItemID.SSSTierXPPotion.getItemId() || i.getId() == ItemID.StarTierXPPotion.getItemId()) {
				if (d.getLevel() == Constants.MAX_LEVEL) {
					event.getChannel().sendMessageFormat("%s, your %s is at the max level. You cannot use the %s on it.", p.getAsMention(), d.getEffectiveName(), i.toString()).complete();
					SUCCESS = false;
				} else {
					long xp = Long.parseLong(i.getData());
					event.getChannel().sendMessageFormat("%s, your %s gained %s XP from the %s.", p.getAsMention(), d.getEffectiveName(), i.getId() == 210 ? "∞" : Util.formatNumber(xp), i.toString()).complete();
					JDBC.addXp(event.getChannel(), p.getIdLong(), d.getIdPair(), xp);
				}
			}

			else if (i.getId() == ItemID.PrismaticConverter.getItemId()) {
				if (d.getDinosaurForm() != DinosaurForm.Standard) {
					event.getChannel().sendMessageFormat("%s, you can only convert a Standard form dinosaur into its Prismatic form.", p.getAsMention()).complete();
					SUCCESS = false;
				} else if (d.isTradeable()) {
					Dinosaur prismatic = Dinosaur.getDinosaur(d.getDex(), DinosaurForm.Prismatic.getId());
					event.getChannel().sendMessageFormat("%s, 1 RP from your %s was converted into a %s.", p.getAsMention(), d.getEffectiveName(), prismatic.getDinosaurName()).complete();
					JDBC.addDinosaur(null, p.getIdLong(), d.getIdPair(), -1);
					JDBC.addDinosaur(event.getChannel(), p.getIdLong(), prismatic.getIdPair(), 1);
				} else {
					event.getChannel().sendMessageFormat("%s, your %s does not have any RP to use.", p.getAsMention(), d.getEffectiveName()).complete();
					SUCCESS = false;
				}
			}

			else if (i.getId() == ItemID.DinosaurTreat.getItemId()) {
				SUCCESS = useTreat(event.getChannel(), d, i);
			}

			else {
				sendUnimplemented(event);
				SUCCESS = false;
			}
			break;
			
		case Held:
			if (d.hasItem()) {
				event.getChannel().sendMessageFormat("%s, the %s has been removed from your %s.", p.getAsMention(), d.getItem().toString(), d.getEffectiveName()).complete();
				JDBC.addItem(p.getIdLong(), d.getItem().getIdDmg());
			}
			event.getChannel().sendMessageFormat("%s, the %s has been given to your %s.", p.getAsMention(), i.toString(), d.getEffectiveName()).complete();
			JDBC.setItem(p.getIdLong(), d.getIdPair(), i.getIdDmg());
			break;
			
		default:
			sendUnimplemented(event);
			SUCCESS = false;
			break;
		}
		
		if (SUCCESS && CONSUME) {
			JDBC.addItem(p.getIdLong(), i.getIdDmg(), -1);
		}
	}
	
	private void sendUnimplemented(MessageReceivedEvent event) {
		event.getChannel().sendMessageFormat("%s, the implemetation of this item is incomplete. If you believe this is an error, please contact a developer.", event.getAuthor().getAsMention()).complete();
	}
	
	private boolean useTreat(MessageChannel channel, Dinosaur d, Item item) {
		String name = null, column = null;
		int max = 0;
		
		switch (MesozoicRandom.nextInt(3)) {
		// HEALTH
		case 0:
			max = Constants.MAX_STAT_BOOST - d.getHealthMultiplier();
			if (max > 0) {
				name = "Health";
				column = "modhealth";
				break;
			}
		
		// ATTACK
		case 1:
			max = Constants.MAX_STAT_BOOST - d.getAttackMultiplier();
			if (max > 0) {
				name = "ATK";
				column = "modattack";
				break;
			}
			
		// DEFENSE
		case 2:
			max = Constants.MAX_STAT_BOOST - d.getDefenseMultiplier();
			if (max > 0) {
				name = "DEF";
				column = "moddefense";
				break;
			}
			
		// DEFAULT
		default:
			max = Constants.MAX_STAT_BOOST - d.getHealthMultiplier();
			if (max > 0) {
				name = "Health";
				column = "modhealth";
				break;
			}
			
			max = Constants.MAX_STAT_BOOST - d.getAttackMultiplier();
			if (max > 0) {
				name = "ATK";
				column = "modattack";
				break;
			}
			
			max = Constants.MAX_STAT_BOOST - d.getDefenseMultiplier();
			if (max > 0) {
				name = "DEF";
				column = "moddefense";
				break;
			}
		}
		
		if (max > 0) {
			int value = MesozoicRandom.nextInt(Math.min(max, Constants.MAX_SNACK_GAIN)) + 1;
			channel.sendMessageFormat("%s, you feed the %s to your %s, and it gained +%d%% %s.", d.getPlayer().getAsMention(), item.toString(), d.getEffectiveName(), value, name).complete();
			JDBC.executeUpdate("update captures set %s = %s + %d where player = %d and dex = %d and form = %d;", column, column, value, d.getPlayerId(), d.getDex(), d.getForm());
			JDBC.addItem(d.getPlayerId(), Stat.SnacksFed.getId());
			return true;
		} else {
			channel.sendMessageFormat("%s, your %s has maxed stats and refuses to eat the %s.", d.getPlayer().getAsMention(), d.getEffectiveName(), item.toString()).complete();
			return false;
		}
	}
}
