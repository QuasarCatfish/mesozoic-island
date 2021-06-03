package com.quas.mesozoicisland.cmdplayer;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import com.quas.mesozoicisland.JDBC;
import com.quas.mesozoicisland.cmdbase.ICommand;
import com.quas.mesozoicisland.enums.AccessLevel;
import com.quas.mesozoicisland.enums.DinosaurForm;
import com.quas.mesozoicisland.enums.DiscordChannel;
import com.quas.mesozoicisland.enums.DiscordRole;
import com.quas.mesozoicisland.objects.Dinosaur;
import com.quas.mesozoicisland.objects.Player;
import com.quas.mesozoicisland.util.Constants;
import com.quas.mesozoicisland.util.Util;
import com.quas.mesozoicisland.util.Zalgo;

import net.dv8tion.jda.api.entities.PrivateChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class DinosaursCommand implements ICommand {

	@Override
	public Pattern getCommand() {
		return pattern("dinos( ", ALPHA, ")*");
	}

	@Override
	public AccessLevel getAccessLevel() {
		return AccessLevel.Trainer;
	}

	@Override
	public String getCommandName() {
		return "dinos";
	}

	@Override
	public String getCommandSyntax() {
		return "dinos [filter] [order]";
	}

	@Override
	public String getCommandDescription() {
		return "Lists the dinosaurs that you own.\n• Filters: " + DinosaurFilter.listValues() + ".\n• Orderings: " + DinosaurOrder.listValues() + ".";
	}

	@Override
	public DiscordChannel[] getUsableChannels() {
		return DiscordChannel.STANDARD_CHANNELS_DMS;
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
		
		StringBuilder sb = new StringBuilder();
		sb.append("**");
		sb.append(event.getAuthor().getAsMention());
		sb.append("'s Dinosaurs:**\n");
		
		sb.append(String.format("**Species:** %,d/%,d\n", p.getDexCount(DinosaurForm.AnyForms.getId()), JDBC.getDexCount(DinosaurForm.AnyForms.getId())));
		sb.append(String.format("**Total:** %,d/%,d\n", p.getDexCount(DinosaurForm.AllForms.getId()), JDBC.getDexCount(DinosaurForm.AllForms.getId())));
		for (DinosaurForm form : DinosaurForm.values()) {
			if (form.getId() < 0) continue;
			if (p.getDexCount(form.getId()) > 0) {
				sb.append(String.format("**%s:** %,d/%,d\n", form.getName(), p.getDexCount(form.getId()), JDBC.getDexCount(form.getId())));
			}
		}
		
		// Calculate filters
		ArrayList<String> where = new ArrayList<String>();
		where.add("player = " + p.getId());
		ArrayList<String> order = new ArrayList<String>();
		ArrayList<String> filter = new ArrayList<String>();
		boolean silent = false;
		boolean fail = false;

		for (int q = 0; q < args.length; q++) {
			DinosaurFilter df = DinosaurFilter.of(args[q]);
			DinosaurOrder dor = DinosaurOrder.of(args[q]);
			if (df == DinosaurFilter.Dummy) continue;
			if (df == null && dor == null) {
				fail = true;
				break;
			}
			if (df == DinosaurFilter.Silent) {
				silent = true;
				continue;
			}

			if (df == null) {
				filter.add(dor.getName());
				order.add(dor.getOrderClause());
			} else {
				filter.add(df.getName());
				if (df.hasInput()) {
					
				} else {
					where.add(df.getWhereClause());
				}
			}
		}
		order.add("dinosaurs.dex asc");

		if (fail) return;

		// Send message in channel
		if (silent) {
			event.getChannel().sendMessage(sb.toString()).complete();
			return;
		} else {
			sb.append("You'll find a full list of your dinosaurs in your DMs.");
			event.getChannel().sendMessage(sb.toString()).complete();
		}
		
		List<String> print = new ArrayList<String>();
		if (filter.isEmpty()) print.add("**Your Dinosaurs:**");
		else print.add("**Your Dinosaurs (" + Util.join(filter, ", ", 0, filter.size()) + "):**");
		
		int rescount = 0;
		try (ResultSet res = JDBC.executeQuery("select * from captures join dinosaurs on captures.dex = dinosaurs.dex and captures.form = dinosaurs.form where %s order by %s;", Util.join(where, " and ", 0, where.size()), Util.join(order, ", ", 0, order.size()))) {
			while (res.next()) {
				Dinosaur d = Dinosaur.getDinosaur(p.getIdLong(), res.getInt("dex"), res.getInt("form"));
				sb = new StringBuilder();
				sb.append(String.format("**%s**", d.toString()));
				if (d.getDinosaurForm() == DinosaurForm.Accursed) {
					sb.append(Zalgo.field(String.format(" %s", d.getElement().getAsBrackets())));
					if (d.getItem() != null && d.getItem().getId() != 0) sb.append(Zalgo.field(String.format(" [Holding: %s]", d.getItem().toString())));
					if (d.getRune() != null && d.getRune().getId() != 0) sb.append(Zalgo.field(String.format(" [Rune: #%03d %s]", d.getRune().getId(), d.getRune().getName())));
				} else {
					sb.append(String.format(" %s", d.getElement().getAsBrackets()));
					sb.append(String.format(" %s", d.getRarity().getAsBrackets()));
					sb.append(String.format(" [%,d Health, %,d Attack, %,d Defense, %,d Total]", d.getHealth(), d.getAttack(), d.getDefense(), d.getStatTotal()));
					if (d.getItem() != null && d.getItem().getId() != 0) {
						if (d.getItem().hasIcon()) {
							sb.append(" " + d.getItem().getIcon().toString());
						} else {
							sb.append(String.format(" [Holding: %s]", d.getItem().toString()));
						}
					}
					if (d.getRune() != null && d.getRune().getId() != 0) sb.append(String.format(" [Rune: #%03d %s]", d.getRune().getId(), d.getRune().getName()));
				}
				print.add(sb.toString());
				rescount++;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		if (rescount == 0) {
			if (filter.size() == 1) print.add("You own no dinosaurs that match this filter.");
			else print.add("You own no dinosaurs that match these filters.");
		}
		
		PrivateChannel pc = event.getAuthor().openPrivateChannel().complete();
		for (String msg : Util.bulkify(print)) {
			pc.sendMessage(msg).complete();
		}
	}
	
	private enum DinosaurFilter {
		// Dinosaur Form
		Standard("Standard", "standard", "captures.form = " + DinosaurForm.Standard.getId(), false),
		Prismatic("Prismatic", "prismatic|prism|pris", "captures.form = " + DinosaurForm.Prismatic.getId(), false),
		Dungeon("Dungeon", "dungeon", "captures.form = " + DinosaurForm.Dungeon.getId(), false),
		Halloween("Halloween", "halloween", "captures.form = " + DinosaurForm.Halloween.getId(), false),
		Thanksgiving("Thanksgiving", "thanksgiving", "captures.form = " + DinosaurForm.Thanksgiving.getId(), false),
		Chaos("Chaos", "chaos", "captures.form = " + DinosaurForm.Chaos.getId(), false),
		Mechanical("Mechanical", "mechanical", "captures.form = " + DinosaurForm.Mechanical.getId(), false),
		Statue("Statue", "statue", "captures.form = " + DinosaurForm.Statue.getId(), false),
		
//		MinLevel
//		MaxLevel
//		MinRank
//		MaxRank
//		DexNumber
		
		// Dinosaur Element
		Fire("Fire", "fire", "dinosaurs.element & 2 > 0", false),
		Lightning("Lightning", "lightning", "dinosaurs.element & 4 > 0", false),
		Leaf("Leaf", "leaf", "dinosaurs.element & 8 > 0", false),
		Earth("Earth", "earth", "dinosaurs.element & 16 > 0", false),
		Ice("Ice", "ice", "dinosaurs.element & 32 > 0", false),
		Metal("Metal", "metal", "dinosaurs.element & 64 > 0", false),
		Water("Water", "water", "dinosaurs.element & 128 > 0", false),
		Air("Air", "air", "dinosaurs.element & 256 > 0", false),
		
		// Dinosaur Rarity
		Special("Special", "special", "dinosaurs.rarity % 10 = 0", false),
		CopperRare("Copper Rare", "copper(rare)?|cr", "dinosaurs.rarity % 10 = 1", false),
		BronzeRare("Bronze Rare", "bronze(rare)?|br", "dinosaurs.rarity % 10 = 2", false),
		IronRare("Iron Rare", "iron(rare)?|ir", "dinosaurs.rarity % 10 = 3", false),
		SilverRare("Silver Rare", "silver(rare)?|sr", "dinosaurs.rarity % 10 = 4", false),
		GoldRare("Gold Rare", "gold(rare)?|gr", "dinosaurs.rarity % 10 = 5", false),
		PlatinumRare("Platinum Rare", "plat(inum)?(rare)?|pr", "dinosaurs.rarity % 10 = 6", false),
		
		// Type
		Dinosaur("Dinosaur", "dino(saur)?", "dinosaurs.type = 'Dinosaur'", false),
		Pterosaur("Pterosaur", "ptero(saur)?", "dinosaurs.type = 'Pterosaur'", false),
		Plesiosaur("Plesiosaur", "plesio(saur)?", "dinosaurs.type = 'Plesiosaur'", false),
		Ichthyosaur("Ichthyosaur", "ichthyo(saur)?", "dinosaurs.type = 'Ichthyosaur'", false),
		Mosasaur("Mosasaur", "mosa(saur)?", "dinosaurs.type = 'Mosasaur'", false),
		
		// Epoch
		// Location
		// Diet
		Carnivore("Carnivore", "carni(vore)?", "dinosaurs.diet = 'Carnivore'", false),
		Herbivore("Herbivore", "herbi(vore)?", "dinosaurs.diet = 'Herbivore'", false),
		Omnivore("Omnivore", "omni(vore)?", "dinosaurs.diet = 'Omnivore'", false),
//		UnknownDiet("", "", "", false),
		
		// Other
		Tradeable("Tradable", "trad(e|able)|dupes?", "captures.rp > 0", false),
		Rankable("Rankable", "rank(up|(up)?able)", "captures.rp > captures.rnk and captures.rnk < " + Constants.MAX_RANK, false),
		Nickname("HasNickname", "(has)?nick(name)?", "!isnull(nick)", false),
		NoNickname("NoNickname", "(no|un)nick(name)?", "isnull(nick)", false),
		Item("HasItem", "(has)?item", "captures.item > 0", false),

		// Non-Filters
		Silent("Silent", "silent|nodm|count", "captures.form = 0", false),
		Dummy(null, "rare|can|has|type", "captures.form = 0", false);
		;
		
		private String name, regex, where;
		private boolean input;
		private DinosaurFilter(String name, String regex, String where, boolean input) {
			this.name = name;
			this.regex = regex;
			this.where = where;
			this.input = input;
		}
		
		public String getName() {
			return name;
		}
		
		public String getWhereClause() {
			return where;
		}
		
		public boolean hasInput() {
			return input;
		}
		
		////////////////////////
		
		public static String listValues() {
			ArrayList<String> vals = new ArrayList<String>();
			for (DinosaurFilter df : values()) {
				if (df.name == null) continue;
				vals.add(String.format("`%s`", df.name.toLowerCase()));
			}
			
			return String.join(", ", vals);
		}
		
		public static DinosaurFilter of(String value) {
			for (DinosaurFilter df : values()) {
				if (Pattern.matches(df.regex, value.toLowerCase())) {
					return df;
				}
			}
			return null;
		}
	}
	
	private enum DinosaurOrder {
		
		// Dex
		Dex("Dex Number", "dexnum", "dex(|no|num(ber)?)", "dinosaurs.dex asc"),
		DexReverse("Reverse Dex Number", "reversedexnum", "rev(erse)?dex(|no|num(ber)?)|dex(|no|num(ber)?)rev(erse)?", "dinosaurs.dex desc"),
		
		// Alpha
		Alpha("Alphabetical", "alpha", "alpha(bet(ic)?)?", "dinosaurs.dinoname asc"),
		AlphaReverse("Reverse Alphabetical", "reversealpha", "rev(erse)?alpha(bet(ic)?)?|alpha(bet(ic)?)?rev(erse)?", "dinosaurs.dinoname desc"),
		
		// Level
		Level("Level", "level", "level", "captures.xp desc"),
		LevelReverse("Reverse Level", "reverselevel", "reverselevel|levelreverse", "captures.xp asc"),

		// Rank
		Rank("Rank", "rank", "rank", "captures.rnk desc"),
		RankReverse("Reverse Rank", "reverserank", "reverserank|rankreverse", "captures.rnk asc"),
		;
		
		private String name, displayname, regex, order;
		private DinosaurOrder(String name, String displayname, String regex, String order) {
			this.name = name;
			this.displayname = displayname;
			this.regex = regex;
			this.order = order;
		}
		
		public String getName() {
			return name;
		}
		
		public String getOrderClause() {
			return order;
		}
		
		///////////////////////////////////////////////////
		
		public static String listValues() {
			String[] vals = new String[values().length];
			for (int q = 0; q < values().length; q++) {
				vals[q] = String.format("`%s`", values()[q].displayname.toLowerCase());
			}
			return String.join(", ", vals);
		}
		
		public static DinosaurOrder of(String value) {
			for (DinosaurOrder dor : values()) {
				if (Pattern.matches(dor.regex, value.toLowerCase())) {
					return dor;
				}
			}
			return null;
		}
	}
}
