package com.quas.mesozoicisland.cmdplayer;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
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
import com.quas.mesozoicisland.enums.ItemTag;
import com.quas.mesozoicisland.enums.ItemType;
import com.quas.mesozoicisland.enums.RaidReward;
import com.quas.mesozoicisland.enums.SpawnType;
import com.quas.mesozoicisland.enums.Stat;
import com.quas.mesozoicisland.objects.Dinosaur;
import com.quas.mesozoicisland.objects.Egg;
import com.quas.mesozoicisland.objects.Element;
import com.quas.mesozoicisland.objects.Item;
import com.quas.mesozoicisland.objects.Player;
import com.quas.mesozoicisland.objects.Rarity;
import com.quas.mesozoicisland.objects.SnackModule;
import com.quas.mesozoicisland.util.Action;
import com.quas.mesozoicisland.util.Constants;
import com.quas.mesozoicisland.util.MesozoicDate;
import com.quas.mesozoicisland.util.MesozoicRandom;
import com.quas.mesozoicisland.util.Pair;
import com.quas.mesozoicisland.util.Util;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class UseCommand implements ICommand {

	@Override
	public Pattern getCommand() {
		return pattern("use ", INTEGER, "( .+)?");
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
		
		boolean IS_CONSUME = i.getItemType() == ItemType.Held || i.getItemType() == ItemType.Consume || i.getItemType() == ItemType.ConsumeDinosaur;
		boolean IS_DINOSAUR = i.getItemType() == ItemType.Held || i.getItemType() == ItemType.ConsumeDinosaur;
		boolean SUCCESS = true;
		
		Dinosaur d = args.length > 1 && args[1].toLowerCase().matches(DINOSAUR) ? Dinosaur.getDinosaur(p.getIdLong(), Util.getDexForm(args[1])) : null;
		if (IS_DINOSAUR && d == null) {
			event.getChannel().sendMessageFormat("%s, you must use this item on a dinosaur.", p.getAsMention()).complete();
			return;
		}
		
		switch (i.getItemType()) {
		case Title: {
			boolean invert = Util.isInvertedTitle(i);
			if (invert) event.getChannel().sendMessageFormat("%s, you will now be recognized as \"%s\".", p.getAsMention(), i.getData()).complete();
			else event.getChannel().sendMessageFormat("%s, you will now be recognized as %s \"%s\".", p.getAsMention(), Util.getArticle(i.getData()), i.getData()).complete();
			JDBC.setTitle(event.getAuthor().getIdLong(), i.getData(), invert);
		} break;
			
		case PersistCount: {
			long count = bag.getOrDefault(i, 0L);
			if (count == 1L) event.getChannel().sendMessageFormat("%s shows off their %s.", p.getAsMention(), i.toString()).complete();
			else event.getChannel().sendMessageFormat("%s shows off their %,d %s.", p.getAsMention(), bag.getOrDefault(i, 0L), i.toString(bag.getOrDefault(i, 0L))).complete();
		} break;
			
		case PersistWithCustomUse: {

			if (i.getId() == ItemID.MesozoicIslandTrainerLicense.getItemId()) {
				CommandManager.handleCommand(event, "license");
			}
			
			else if (i.getId() == ItemID.GuildBadge.getItemId()) {
				if (args.length > 1) {
					Element ele = Element.of(args[1]);
					if (ele == null) {
						event.getChannel().sendMessageFormat("%s, this is not a valid element.", p.getAsMention()).complete();
					} else if (!ele.isGuild()) {
						event.getChannel().sendMessageFormat("%s, the %s element is not a valid guild option.", p.getAsMention(), ele.toString()).complete();
					} else {
						event.getChannel().sendMessageFormat("%s, you have successfully joined the %s Guild.", p.getAsMention(), ele.toString()).complete();
						JDBC.executeUpdate("update players set mainelement = %d where playerid = %d;", ele.getId(), p.getIdLong());
						Util.addRoleToMember(event.getMember(), ele.getRole());
						JDBC.addItem(p.getIdLong(), i.getIdDmg(), -1);
						JDBC.addItem(p.getIdLong(), new Pair<Integer, Long>(i.getId(), (long)ele.getId()), 1);
						event.getGuild().getTextChannelById(ele.getGuild()).sendMessageFormat("Welcome %s to the %s Guild!", p.getAsMention(), ele.toString()).complete();

						if ((p.getSubElement().getId() & ele.getId()) > 0) {
							Item emblem = Item.getItem(new Pair<Integer, Long>(ItemID.ElementalEmblem.getItemId(), (long)ele.getId()));
							event.getChannel().sendMessageFormat("%s, your %s goes inert.", p.getAsMention(), emblem.toString()).complete();
							JDBC.executeUpdate("update players set subelement = subelement - %d where playerid = %d;", ele.getId(), p.getIdLong());
							JDBC.addItem(p.getIdLong(), ItemID.ElementalEmblem.getId(), 1);
							JDBC.addItem(p.getIdLong(), emblem.getIdDmg(), -1);
						}
					}
				} else {
					event.getChannel().sendMessageFormat("%s, you must specify an element for this item.", p.getAsMention()).complete();
				}
			}

			else if (i.getId() == ItemID.ElementalEmblem.getItemId()) {
				if (args.length > 1) {
					Element ele = Element.of(args[1]);
					if (ele == null) {
						event.getChannel().sendMessageFormat("%s, this is not a valid element.", p.getAsMention()).complete();
					} else if (!ele.isEmblem()) {
						event.getChannel().sendMessageFormat("%s, the %s element is not a valid emblem option.", p.getAsMention(), ele.toString()).complete();
					} else if ((p.getMainElement().getId() & ele.getId()) > 0) {
						event.getChannel().sendMessageFormat("%s, you are in the %s Guild and cannot imbue %s %s with this element.", p.getAsMention(), ele.toString(), Util.getArticle(i.toString()), i.toString()).complete();
					} else if ((p.getSubElement().getId() & ele.getId()) > 0) {
						event.getChannel().sendMessageFormat("%s, you have already imbued %s %s with the Essence of %s.", p.getAsMention(), Util.getArticle(i.toString()), i.toString(), ele.toString()).complete();
					} else {
						event.getChannel().sendMessageFormat("%s, you have successfully imbued your %s with the Essence of %s.", p.getAsMention(), i.toString(), ele.toString()).complete();
						JDBC.executeUpdate("update players set subelement = subelement + %d where playerid = %d;", ele.getId(), p.getIdLong());
						JDBC.addItem(p.getIdLong(), i.getIdDmg(), -1);
						JDBC.addItem(p.getIdLong(), new Pair<Integer, Long>(i.getId(), (long)ele.getId()), 1);
					}
				} else {
					event.getChannel().sendMessageFormat("%s, you must specify an element for this item.", p.getAsMention()).complete();
				}
			}

			else if (i.getId() == ItemID.QuestBook.getItemId()) {
				CommandManager.handleCommand(event, "quests");
			}
			
			else if (i.getId() == ItemID.DinosaurPhotograph.getItemId()) {
				event.getChannel().sendMessageFormat("%s, you look at the photograph. %s", p.getAsMention(), i.getData()).complete();
			}

			else if (i.getId() == ItemID.EggIncubator.getItemId()) {
				CommandManager.handleCommand(event, "eggs");
			}

			else if (i.getId() == ItemID.TitleRemover.getItemId()) {
				if (p.hasInvertedTitle()) event.getChannel().sendMessageFormat("%s, you will no longer be recognized as \"%s\".", p.getAsMention(), p.getTitle()).complete();
				else event.getChannel().sendMessageFormat("%s, you will no longer be recognized as %s \"%s\".", p.getAsMention(), Util.getArticle(p.getTitle()), p.getTitle()).complete();
				JDBC.setTitle(p.getIdLong(), null, false);
			}

			else if (i.getId() == ItemID.JasonToken.getItemId()) {
				long count = bag.getOrDefault(i, 0L);
				if (count >= Constants.ACCURSED_REMOVAL_QUESTS) {
					try (ResultSet res = JDBC.executeQuery("select * from captures where player = %d and form = %d;", p.getIdLong(), DinosaurForm.Accursed.getId())) {
						if (res.next()) {
							Dinosaur dino = Dinosaur.getDinosaur(p.getIdLong(), res.getInt("dex"), res.getInt("form"));
							event.getChannel().sendMessageFormat("%s, the tokens spin around your %s bathing it in a blinding light. When the light disappears, the dinosaur is nowhere to be found.", p.getAsMention(), dino.getEffectiveName()).complete();

							// delete dinosaur, set cursed to false, and remove jason tokens
							JDBC.deleteDinosaur(p.getIdLong(), dino.getIdPair());
							JDBC.setCursed(p.getIdLong(), false);
							JDBC.addItem(p.getIdLong(), ItemID.JasonToken.getId(), -Constants.ACCURSED_REMOVAL_QUESTS);

							// remove cursed title and role
							Member m = event.getMember();
							JDBC.addItem(p.getIdLong(), ItemID.CursedTitle.getId(), -1);
							Util.removeRoleFromMember(m, DiscordRole.Cursed.getIdLong());

							// add cleansed title and role
							JDBC.addItem(p.getIdLong(), ItemID.CleansedTitle.getId(), 1);
							Util.addRoleToMember(m, DiscordRole.Cleansed.getIdLong());

							if (p.getTitle().equals("Cursed")) JDBC.setTitle(p.getIdLong(), "Cleansed", false);
						} else {
							event.getChannel().sendMessageFormat("%s, you do not have an Accursed dinosaur.", p.getAsMention()).complete();
						}
					} catch (SQLException e) {
						e.printStackTrace();
					}
				} else {
					event.getChannel().sendMessageFormat("%s, you need %,d more %s.", p.getAsMention(), Constants.ACCURSED_REMOVAL_QUESTS - count, i.toString(Constants.ACCURSED_REMOVAL_QUESTS - count)).complete();
				}
			}

			else if (i.getId() == ItemID.MechanicalComponent.getItemId()) {
				long count = bag.getOrDefault(i, 0L);
				
				if (d == null) {
					event.getChannel().sendMessageFormat("%s, you must use this item on a dinosaur.", p.getAsMention()).complete();
				} else if (d.getDinosaurForm() != DinosaurForm.Standard) {
					event.getChannel().sendMessageFormat("%s, you can only convert a Standard form dinosaur into its Mechanical form.", p.getAsMention()).complete();
				} else if (d.isTradable()) {
					Dinosaur mechanical = Dinosaur.getDinosaur(d.getDex(), DinosaurForm.Mechanical.getId());
					int cost = 5 * d.getRarity().getId();

					if (mechanical == null) {
						event.getChannel().sendMessageFormat("%s, this dinosaur does not have a Mechanical form.", p.getAsMention()).complete();
					} else if (count < cost) {
						event.getChannel().sendMessageFormat("%s, you do not have enough %s to convert this dinosaur.", p.getAsMention(), i.toString(2)).complete();
					} else {
						event.getChannel().sendMessageFormat("%s, 1 RP from your %s and %,d %s were used to create a %s.", p.getAsMention(), d.getEffectiveName(), cost, i.toString(cost), mechanical.getDinosaurName()).complete();
						JDBC.addItem(p.getIdLong(), i.getIdDmg(), -cost);
						JDBC.addDinosaur(null, p.getIdLong(), d.getIdPair(), -1);
						JDBC.addDinosaur(event.getChannel(), p.getIdLong(), mechanical.getIdPair(), 1);
					}
				} else {
					event.getChannel().sendMessageFormat("%s, your %s does not have any RP to use.", p.getAsMention(), d.getEffectiveName()).complete();
				}
			}

			else {
				sendUnimplemented(event);
				SUCCESS = false;
			}

		} break;
			
		case Consume:

			if (i.hasTag(ItemTag.BattleFragrance)) {
				long time = Long.parseLong(i.getData());
				long curtime = p.getFragranceBattleTimer();
				String adj = curtime < System.currentTimeMillis() ? "the next" : "an additional";
				long end = Math.max(curtime, System.currentTimeMillis()) + time;
				
				event.getChannel().sendMessageFormat("%s, the %s fills the air around you for %s %s.", p.getAsMention(), i.toString(), adj, Util.formatTime(time)).complete();
				JDBC.executeUpdate("update players set fragrancebattle = %d where playerid = %d;", end, p.getIdLong());
			}

			else if (i.hasTag(ItemTag.ExperienceFragrance)) {
				long time = Long.parseLong(i.getData());
				long curtime = p.getFragranceXpTimer();
				String adj = curtime < System.currentTimeMillis() ? "the next" : "an additional";
				long end = Math.max(curtime, System.currentTimeMillis()) + time;
				
				event.getChannel().sendMessageFormat("%s, the %s fills the air around you for %s %s.", p.getAsMention(), i.toString(), adj, Util.formatTime(time)).complete();
				JDBC.executeUpdate("update players set fragrancexp = %d where playerid = %d;", end, p.getIdLong());
			}

			else if (i.hasTag(ItemTag.MoneyFragrance)) {
				long time = Long.parseLong(i.getData());
				long curtime = p.getFragranceMoneyTimer();
				String adj = curtime < System.currentTimeMillis() ? "the next" : "an additional";
				long end = Math.max(curtime, System.currentTimeMillis()) + time;
				
				event.getChannel().sendMessageFormat("%s, the %s fills the air around you for %s %s.", p.getAsMention(), i.toString(), adj, Util.formatTime(time)).complete();
				JDBC.executeUpdate("update players set fragrancemoney = %d where playerid = %d;", end, p.getIdLong());
			}

			else if (i.hasTag(ItemTag.EggFragrance)) {
				long time = Long.parseLong(i.getData());
				long curtime = p.getFragranceEggTimer();
				String adj = curtime < System.currentTimeMillis() ? "the next" : "an additional";
				long end = Math.max(curtime, System.currentTimeMillis()) + time;
				
				event.getChannel().sendMessageFormat("%s, the %s fills the air around you for %s %s.", p.getAsMention(), i.toString(), adj, Util.formatTime(time)).complete();
				JDBC.executeUpdate("update players set fragranceegg = %d where playerid = %d;", end, p.getIdLong());
			}

			else if (i.getId() == ItemID.MysteryScentPouch.getItemId()) {
				Item[] scents = Item.getItemsWithTag(ItemTag.Scent);
				Item scent = Util.getRandomElement(scents);
				event.getChannel().sendMessageFormat("%s, you open the %s and find %s %s.", p.getAsMention(), i.toString(), Util.getArticle(scent.toString()), scent.toString()).complete();
				JDBC.addItem(p.getIdLong(), scent.getIdDmg());
			}

			else if (i.getId() == ItemID.MysteryFragrancePouch.getItemId()) {
				Item[] fragrances = Item.getItemsWithTag(ItemTag.Fragrance);
				Item fragrance = Util.getRandomElement(fragrances);
				event.getChannel().sendMessageFormat("%s, you open the %s and find %s %s.", p.getAsMention(), i.toString(), Util.getArticle(fragrance.toString()), fragrance.toString()).complete();
				JDBC.addItem(p.getIdLong(), fragrance.getIdDmg());
			}

			else if (i.getId() == ItemID.MysteryEauPouch.getItemId()) {
				Item[] eaux = Item.getItemsWithTag(ItemTag.Eau);
				Item eau = Util.getRandomElement(eaux);
				event.getChannel().sendMessageFormat("%s, you open the %s and find %s %s.", p.getAsMention(), i.toString(), Util.getArticle(eau.toString()), eau.toString()).complete();
				JDBC.addItem(p.getIdLong(), eau.getIdDmg());
			}

			else if (i.getId() == ItemID.DinosaurLocator.getItemId()) {
				if (!Constants.SPAWN) {
					event.getChannel().sendMessageFormat("%s tries to use the %s, but dinosaur spawning is disabled.", p.getAsMention(), i.toString()).complete();
					SUCCESS = false;
				} else if (SpawnManager.getSpawnTime() <= System.currentTimeMillis()) {
					event.getChannel().sendMessageFormat("%s tries to use the %s, but a spawn is occurring.", p.getAsMention(), i.toString()).complete();
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
					event.getChannel().sendMessageFormat("%s tries to use the %s, but a spawn is occurring.", p.getAsMention(), i.toString()).complete();
					SUCCESS = false;
				} else if (SpawnManager.trySpawn(SpawnType.Egg, true)) {
					event.getChannel().sendMessageFormat("%s uses the %s to look for some eggs.", p.getAsMention(), i.toString()).complete();
				} else {
					event.getChannel().sendMessageFormat("%s tries to use the %s, but it failed.", p.getAsMention(), i.toString()).complete();
					SUCCESS = false;
				}
			}

			else if (i.hasTag(ItemTag.DungeonLocator)) {
				if (!Constants.SPAWN || !Constants.SPAWN_DUNGEONS) {
					event.getChannel().sendMessageFormat("%s tries to use the %s, but dungeon spawning is disabled.", p.getAsMention(), i.toString()).complete();
					SUCCESS = false;
				} else if (SpawnManager.getSpawnTime() <= System.currentTimeMillis()) {
					event.getChannel().sendMessageFormat("%s tries to use the %s, but a spawn is occurring.", p.getAsMention(), i.toString()).complete();
					SUCCESS = false;
				} else if (SpawnManager.isDungeonSpawned()) {
					event.getChannel().sendMessageFormat("%s tries to use the %s, but there's already a dungeon being explored.", p.getAsMention(), i.toString()).complete();
					SUCCESS = false;
				} else if (SpawnManager.trySpawn(SpawnType.Dungeon, true, i.getData())) {
					event.getChannel().sendMessageFormat("%s uses the %s to look for a dungeon to explore.", p.getAsMention(), i.toString()).complete();
				} else {
					event.getChannel().sendMessageFormat("%s tries to use the %s, but it failed.", p.getAsMention(), i.toString()).complete();
					SUCCESS = false;
				}
			}

			else if (i.getId() == ItemID.MysteryPendantBox.getItemId()) {
				Item[] pendants = Item.getItemsWithTag(ItemTag.Pendant);
				Item pendant = Util.getWeightedItem(p, pendants);
				event.getChannel().sendMessageFormat("%s, you open the box and find %s %s!", p.getAsMention(), Util.getArticle(pendant.toString()), pendant.toString()).complete();
				JDBC.addItem(p.getIdLong(), pendant.getIdDmg());
			}

			else if (i.getId() == ItemID.MysteryBracerBox.getItemId()) {
				Item[] bracers = Item.getItemsWithTag(ItemTag.Bracer);
				Item bracer = Util.getWeightedItem(p, bracers);
				event.getChannel().sendMessageFormat("%s, you open the box and find %s %s!", p.getAsMention(), Util.getArticle(bracer.toString()), bracer.toString()).complete();
				JDBC.addItem(p.getIdLong(), bracer.getIdDmg());
			}

			else if (i.getId() == ItemID.MysteryGauntletBox.getItemId()) {
				Item[] gauntlets = Item.getItemsWithTag(ItemTag.Gauntlet);
				Item gauntlet = Util.getWeightedItem(p, gauntlets);
				event.getChannel().sendMessageFormat("%s, you open the box and find %s %s!", p.getAsMention(), Util.getArticle(gauntlet.toString()), gauntlet.toString()).complete();
				JDBC.addItem(p.getIdLong(), gauntlet.getIdDmg());
			}

			else if (i.getId() == ItemID.MysteryDinoCharmBox.getItemId()) {
				Item[] charms = Item.getItemsWithTag(ItemTag.DinosaurCharm);
				Item charm = Util.getWeightedItem(p, charms);
				event.getChannel().sendMessageFormat("%s, you open the box and find %s %s!", p.getAsMention(), Util.getArticle(charm.toString()), charm.toString()).complete();
				JDBC.addItem(p.getIdLong(), charm.getIdDmg());
			}

			else if (i.getId() == ItemID.MysteryFieldCharmBox.getItemId()) {
				Item[] charms = Item.getItemsWithTag(ItemTag.BattlefieldCharm);
				Item charm = Util.getWeightedItem(p, charms);
				event.getChannel().sendMessageFormat("%s, you open the box and find %s %s!", p.getAsMention(), Util.getArticle(charm.toString()), charm.toString()).complete();
				JDBC.addItem(p.getIdLong(), charm.getIdDmg());
			}

			else if (i.getId() == ItemID.MysteryPresent.getItemId()) {
				StringBuilder sb = new StringBuilder();
				sb.append(p.getAsMention());
				sb.append(", you open the ");
				sb.append(i.toString());
				sb.append(" and find... ");
				
				switch (MesozoicRandom.nextInt(20)) {
					// 50% coins
					case 0: case 1: case 2: case 3: case 4: case 5: case 6: case 7: case 8: case 9: {
						Item prize = Item.getItem(ItemID.DinosaurCoin);
						int count = MesozoicRandom.nextInt(10, 51);
						sb.append(count);
						sb.append(" ");
						sb.append(prize.toString(count));
						JDBC.addItem(p.getIdLong(), prize.getIdDmg(), count);
					} break;

					// 15% copper gacha
					case 10: case 11: case 12: {
						Item prize = Item.getItem(ItemID.CopperRareGachaToken);
						int count = MesozoicRandom.nextInt(1, 3);
						sb.append(count);
						sb.append(" ");
						sb.append(prize.toString(count));
						JDBC.addItem(p.getIdLong(), prize.getIdDmg(), count);
					} break;

					// 5% bronze gacha
					case 13: {
						Item prize = Item.getItem(ItemID.BronzeRareGachaToken);
						int count = 1;
						sb.append(count);
						sb.append(" ");
						sb.append(prize.toString(count));
						JDBC.addItem(p.getIdLong(), prize.getIdDmg(), count);
					} break;

					// 15% B-potion
					case 14: case 15: case 16: {
						Item prize = Item.getItem(ItemID.BTierXPPotion);
						int count = 1;
						sb.append(count);
						sb.append(" ");
						sb.append(prize.toString(count));
						JDBC.addItem(p.getIdLong(), prize.getIdDmg(), count);
					} break;

					// 15% Dino Cane
					case 17: case 18: case 19: {
						Item prize = Item.getItem(ItemID.DinoCane);
						int count = MesozoicRandom.nextInt(1, 4);
						sb.append(count);
						sb.append(" ");
						sb.append(prize.toString(count));
						JDBC.addItem(p.getIdLong(), prize.getIdDmg(), count);
					} break;
				}

				// Gift point for santa players
				if (p.isSecretSanta()) {
					Item prize = Item.getItem(ItemID.GiftToken);
					int count = 1;
					sb.append(" and ");
					sb.append(count);
					sb.append(" ");
					sb.append(prize.toString(count));
					JDBC.addItem(p.getIdLong(), prize.getIdDmg(), count);
					JDBC.addItem(p.getIdLong(), Stat.GiftPointsReceived.getId(), count);
				}
				sb.append(".");

				event.getChannel().sendMessage(sb.toString()).complete();
			}

			else if (i.hasTag(ItemTag.RecycleItem)) {
				Item recycle = Item.getItem(ItemID.RecycleToken);
				JDBC.addItem(p.getIdLong(), recycle.getIdDmg());
				event.getChannel().sendMessageFormat("%s, you properly dispose of the %s, earning %s %s.", p.getAsMention(), i.toString(), Util.getArticle(recycle.toString()), recycle.toString()).complete();
			}

			else if (i.getId() == ItemID.EggVoucher.getItemId()) {
				if (bag.getOrDefault(Item.getItem(ItemID.EggIncubator), 0L) > p.getStandardEggCount()) {
					if (i.getDamage() == 0) { // Chocolate Egg Voucher
						Egg egg = Egg.getRandomEgg(MesozoicRandom.nextEggDinosaur().getIdPair());
						egg.setEggName("Chocolate Egg");
						egg.setEggColor(EggColor.SaddleBrown);
						JDBC.addEgg(p.getIdLong(), egg);
						event.getChannel().sendMessageFormat("%s, you have redeemed a Chocolate Egg!", p.getAsMention()).complete();
					} else if (i.getDamage() == 1) { // Egg Voucher
						Egg egg = Egg.getRandomEgg(MesozoicRandom.nextEggDinosaur().getIdPair());
						JDBC.addEgg(p.getIdLong(), egg);
						event.getChannel().sendMessageFormat("%s, you have redeemed %s %s.", p.getAsMention(), Util.getArticle(egg.getEggName()), egg.getEggName()).complete();
					} else {
						event.getChannel().sendMessageFormat("%s, this %s is not redeemable.", p.getAsMention(), i.toString()).complete();
						SUCCESS = false;
					}
				} else {
					event.getChannel().sendMessageFormat("%s, you don't have any open incubator slots.", p.getAsMention()).complete();
					SUCCESS = false;
				}

			}

			else if (i.getId() == ItemID.DinosaurVoucher.getItemId()) {
				sendUnimplemented(event);
				SUCCESS = false;
			}

			else if (i.hasTag(ItemTag.DungeonTicket)) {
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
							try (ResultSet res = JDBC.executeQuery("select * from dungeonpurchase where date = '%s' and player = %d and dino = '%s';", MesozoicDate.getToday(), p.getIdLong(), d2.getId())) {
								if (res.next()) {
									event.getChannel().sendMessageFormat("%s, you cannot purchase a second %s today.", p.getAsMention(), d2.getDinosaurName()).complete();
									SUCCESS = false;
								} else {
									event.getChannel().sendMessageFormat("%s, you have redeemed %s %s with your %s.", p.getAsMention(), Util.getArticle(d2.getDinosaurName()), d2.getDinosaurName(), i.toString()).complete();
									JDBC.addDinosaur(event.getChannel(), p.getIdLong(), d2.getIdPair());
									JDBC.executeUpdate("insert into dungeonpurchase values('%s', %d, '%s');", MesozoicDate.getToday(), p.getIdLong(), d2.getId());
								}
							} catch (SQLException e) {
								e.printStackTrace();
							}
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
						sb.append(String.format("\n%s %s %s %s", lower ? Constants.NOTE : Constants.BULLET_POINT, d2.toString(), d2.getElement().getAsBrackets(), d2.getRarity().getAsBrackets()));
					}

					if (tier > 1) {
						sb.append("\n");
						sb.append(Constants.NOTE);
						sb.append(" These dinosaurs appear in a lower tier of Dungeon Ticket.");
					}
					sb.append("\n\nTo redeem one of these dinosaurs, use the command `use ");
					sb.append(i.getId());
					sb.append(" <dino id>`.\nThis selection will reset in ");
					sb.append(Util.formatTime(Util.getTimeLeftInDay()));
					sb.append(".");
					
					event.getChannel().sendMessage(sb.toString()).complete();
					SUCCESS = false;
				}
			}

			else if (i.hasTag(ItemTag.DinosaurGacha)) {
				String[] data = i.getData().split("\\s+");
				DinosaurForm form = DinosaurForm.of(Integer.parseInt(data[0]));
				Rarity rarity = Rarity.getRarity(Integer.parseInt(data[1]));

				ArrayList<Pair<Integer, Integer>> list = new ArrayList<Pair<Integer, Integer>>();
				try (ResultSet res = JDBC.executeQuery("select * from dinosaurs where form = %d and rarity = %d;", form.getId(), rarity.getId())) {
					while (res.next()) {
						list.add(new Pair<Integer, Integer>(res.getInt("dex"), res.getInt("form")));
					}
				} catch (SQLException e) {
					e.printStackTrace();
				}

				if (list.isEmpty()) {
					event.getChannel().sendMessageFormat("%s, there are no dinosaurs this gacha token can be used on.", p.getAsMention()).complete();
					SUCCESS = false;
				} else {
					int index = MesozoicRandom.nextInt(list.size());
					Pair<Integer, Integer> select = list.get(index);
					Dinosaur dino = Dinosaur.getDinosaur(select);
					event.getChannel().sendMessageFormat("%s, you insert the %s into the Gacha Machine, and out pops out %s %s crystal.", p.getAsMention(), i.toString(), Util.getArticle(dino.getDinosaurName()), dino.getDinosaurName()).complete();
					JDBC.addDinosaur(event.getChannel(), p.getIdLong(), select);
				}
			}

			else if (i.getId() == ItemID.RaidPass.getItemId()) {
				if (!Constants.SPAWN) {
					event.getChannel().sendMessageFormat("%s, you cannot challenge this Raid Boss because spawns are disabled.", p.getAsMention()).complete();
					SUCCESS = false;
				} else if (Battle.isPlayerBattling(p.getIdLong())) {
					event.getChannel().sendMessageFormat("%s, you cannot challenge this Raid Boss because you are already in another battle.", p.getAsMention()).complete();
					SUCCESS = false;
				} else if (Battle.isPlayerBattling(CustomPlayer.RaidChallenge.getIdLong())) {
					event.getChannel().sendMessageFormat("%s, you cannot challenge this Raid Boss because someone else is currently challenging it.", p.getAsMention()).complete();
					SUCCESS = false;
				} else {
					// Raid Boss Dinosaur
					String[] data = i.getData().split("\\s+");
					Dinosaur raid = Dinosaur.getDinosaur(Integer.parseInt(data[0]), DinosaurForm.RaidBoss.getId());
					raid.setLevel(2 * p.getLevel());
					if (data.length > 1) raid.setLevel(Integer.parseInt(data[1]));
					raid.setRank(0);
					raid.addBoost(-raid.getLevel() / 3);

					// Set up Battle
					Battle b = new Battle(BattleChannel.Raid, BattleType.Boss, MesozoicRandom.nextLocation());
					BattleTeam boss = new BattleTeam(Player.getPlayer(CustomPlayer.RaidChallenge.getIdLong()), Util.arr(raid));
					BattleTeam player = new BattleTeam(p);
					b.addBoss(boss);
					b.addTeam(player);
					event.getChannel().sendMessageFormat("%s is battling the %s! Go to %s to watch the battle in action!", p.getAsMention(), raid.getEffectiveName(), BattleChannel.Raid.getBattleChannel().toString()).complete();
					
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
						String prize = RaidReward.randomReward();
						Action.sendDelayedMessage(MesozoicIsland.getAssistant().getIdLong(), time + 1_000, Constants.SPAWN_CHANNEL, String.format("%s, you find these rewards from the %s:\n%s", p.getAsMention(), raid.getEffectiveName(), JDBC.getRedeemMessage(prize)));
						Action.addRedeemDelayed(MesozoicIsland.getAssistant().getIdLong(), p.getIdLong(), time + 1_000, prize);
						Action.addItemDelayed(p.getIdLong(), time + 1_000, Stat.RaidsDefeated.getId(), 1);
					}
					
					// Log Channel
					Action.logBattleChannelDelayed(MesozoicIsland.getAssistant().getIdLong(), BattleChannel.Raid.getBattleChannel().getIdLong(), time + 30_000);
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
				
				if (d.getDex() == dex && (d.getForm() == form || form == -1)) {
					if (d.getDinosaurForm() == DinosaurForm.Contest || d.getDinosaurForm() == DinosaurForm.Accursed) {
						event.getChannel().sendMessageFormat("%s, your %s refuses to drink the %s.", p.getAsMention(), d.getEffectiveName(), i.toString()).complete();
					SUCCESS = false;
					} else if (d.getLevel() == Constants.MAX_DINOSAUR_LEVEL) {
						event.getChannel().sendMessageFormat("%s, your %s is at the max level. You cannot use the %s on it.", p.getAsMention(), d.getEffectiveName(), i.toString()).complete();
						SUCCESS = false;
					} else {
						event.getChannel().sendMessageFormat("%s, your %s gained %,d XP from the %s.", p.getAsMention(), d.getEffectiveName(), xp, i.toString()).complete();
						JDBC.addXp(event.getChannel(), p.getIdLong(), d.getIdPair(), xp, false);
					}
				} else {
					if (form == -1) {
						Dinosaur d2 = Dinosaur.getDinosaur(dex, DinosaurForm.Standard.getId());
						event.getChannel().sendMessageFormat("%s, this XP Potion can only be used on %s %s.", p.getAsMention(), Util.getArticle(d2.getDinosaurName()), d2.getDinosaurName()).complete();
						SUCCESS = false;
					} else if (form == DinosaurForm.Standard.getId()) {
						Dinosaur d2 = Dinosaur.getDinosaur(dex, form);
						event.getChannel().sendMessageFormat("%s, this XP Potion can only be used on a Standard %s.", p.getAsMention(), d2.getDinosaurName()).complete();
						SUCCESS = false;
					} else {
						Dinosaur d2 = Dinosaur.getDinosaur(dex, form);
						event.getChannel().sendMessageFormat("%s, this XP Potion can only be used on %s %s.", p.getAsMention(), Util.getArticle(d2.getDinosaurName()), d2.getDinosaurName()).complete();
						SUCCESS = false;
					}
				}
			}
			
			else if (i.hasTag(ItemTag.XpPotion)) {
				if (d.getDinosaurForm() == DinosaurForm.Contest || d.getDinosaurForm() == DinosaurForm.Accursed) {
					event.getChannel().sendMessageFormat("%s, your %s refuses to drink the %s.", p.getAsMention(), d.getEffectiveName(), i.toString()).complete();
					SUCCESS = false;
				} else if (d.getLevel() == Constants.MAX_DINOSAUR_LEVEL) {
					event.getChannel().sendMessageFormat("%s, your %s is at the max level. You cannot use the %s on it.", p.getAsMention(), d.getEffectiveName(), i.toString()).complete();
					SUCCESS = false;
				} else {
					long xp = Long.parseLong(i.getData());
					event.getChannel().sendMessageFormat("%s, your %s gained %s XP from the %s.", p.getAsMention(), d.getEffectiveName(), i.getId() == 210 ? "∞" : Util.formatNumber(xp), i.toString()).complete();
					JDBC.addXp(event.getChannel(), p.getIdLong(), d.getIdPair(), xp, false);
				}
			}

			else if (i.getId() == ItemID.PrismaticConverter.getItemId()) {
				if (d.getDinosaurForm() != DinosaurForm.Standard) {
					event.getChannel().sendMessageFormat("%s, you can only convert a Standard form dinosaur into its Prismatic form.", p.getAsMention()).complete();
					SUCCESS = false;
				} else if (d.isTradable()) {
					Dinosaur prismatic = Dinosaur.getDinosaur(d.getDex(), DinosaurForm.Prismatic.getId());
					event.getChannel().sendMessageFormat("%s, 1 RP from your %s was converted into a %s.", p.getAsMention(), d.getEffectiveName(), prismatic.getDinosaurName()).complete();
					JDBC.addDinosaur(null, p.getIdLong(), d.getIdPair(), -1);
					JDBC.addDinosaur(event.getChannel(), p.getIdLong(), prismatic.getIdPair(), 1);
				} else {
					event.getChannel().sendMessageFormat("%s, your %s does not have any RP to use.", p.getAsMention(), d.getEffectiveName()).complete();
					SUCCESS = false;
				}
			}

			else if (i.hasTag(ItemTag.Snack)) {
				SnackModule sm = new SnackModule(d, i);
				SUCCESS = false;
				event.getChannel().sendMessage(sm.getResult()).complete();
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
		
		if (SUCCESS && IS_CONSUME) {
			JDBC.addItem(p.getIdLong(), i.getIdDmg(), -1);
		}
	}
	
	private void sendUnimplemented(MessageReceivedEvent event) {
		event.getChannel().sendMessageFormat("%s, the implemetation of this item is incomplete. If you believe this is an error, please contact a developer.", event.getAuthor().getAsMention()).complete();
	}
}
