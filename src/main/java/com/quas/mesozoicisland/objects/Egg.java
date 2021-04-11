package com.quas.mesozoicisland.objects;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import com.quas.mesozoicisland.JDBC;
import com.quas.mesozoicisland.enums.DinosaurForm;
import com.quas.mesozoicisland.enums.EggColor;
import com.quas.mesozoicisland.enums.EggPattern;
import com.quas.mesozoicisland.enums.EggPattern.EggPatternTag;
import com.quas.mesozoicisland.util.Constants;
import com.quas.mesozoicisland.util.MesozoicRandom;
import com.quas.mesozoicisland.util.Pair;

public class Egg implements Comparable<Egg> {

	private int id;
	private long player;
	private int dex;
	private int form;
	private int incubator;
	private int hp;
	private int maxhp;
	private EggColor color;
	private EggColor basepatterncolor;
	private EggPattern basepattern;
	private EggColor patterncolor;
	private EggPattern pattern;
	private String eggname;
	private boolean customname;
	
	private Egg() {}
	
	public int getId() {
		return id;
	}
	
	public long getPlayerId() {
		return player;
	}
	
	public Player getPlayer() {
		return Player.getPlayer(player);
	}
	
	public String getEggName() {
		return eggname;
	}
	
	public void setEggName(String name) {
		customname = true;
		this.eggname = name;
	}
	
	public boolean hasCustomName() {
		return customname;
	}
	
	public int getDex() {
		return dex;
	}
	
	public int getForm() {
		return form;
	}
	
	public Pair<Integer, Integer> getIdPair() {
		return new Pair<Integer, Integer>(dex, form);
	}
	
	public int getCurrentHatchPoints() {
		return hp;
	}
	
	public int getMaxHatchPoints() {
		return maxhp;
	}
	
	public boolean isHatchable() {
		return hp >= maxhp;
	}
	
	public EggColor getEggColor() {
		return color;
	}
	
	public void setEggColor(EggColor color) {
		this.color = color;
	}
	
	public EggColor getBasePatternColor() {
		return basepatterncolor;
	}
	
	public EggPattern getBasePattern() {
		return basepattern;
	}
	
	public EggColor getPatternColor() {
		return patterncolor;
	}
	
	public EggPattern getPattern() {
		return pattern;
	}
	
	public File getImage() {
		File out = new File(String.format(Constants.RESOURCE_PATH + "SavedEggs\\egg_c%d_bp%d_bpc%d_p%d_pc%d.png", color.getId(), basepattern.getId(), basepatterncolor.getId(), pattern.getId(), patterncolor.getId()));
		if (out.exists()) return out;
		
		File folder = new File(Constants.RESOURCE_PATH + "Eggs");
		
		File egg = null, pattern = null, basepattern = null;
		for (File f : folder.listFiles()) {
			if (f.getName().equals("egg.png")) egg = f;
			if (f.getName().equals(this.pattern.getFile())) pattern = f;
			if (f.getName().equals(this.basepattern.getFile())) basepattern = f;
		}
		if (egg == null) return null;
		
		try {
			BufferedImage imgEgg = ImageIO.read(egg);
			BufferedImage imgBasePattern = basepattern == null ? null : ImageIO.read(basepattern);
			BufferedImage imgPattern = pattern == null ? null : ImageIO.read(pattern);
			BufferedImage img = new BufferedImage(imgEgg.getWidth(), imgEgg.getHeight(), BufferedImage.TYPE_INT_ARGB_PRE);
			Graphics g = img.getGraphics();
			
			for (int x = 0; x < img.getWidth(); x++) {
				for (int y = 0; y < img.getHeight(); y++) {
					Color c = new Color(imgEgg.getRGB(x, y));
					if (c.getRed() > 0 || c.getBlue() > 0 || c.getGreen() > 0) {
						g.setColor(Color.BLACK);
						g.drawLine(x, y, x, y);
						g.setColor(color.toColor(getAlpha(c)));
						g.drawLine(x, y, x, y);
						
						// Base Pattern
						if (imgBasePattern != null) {
							Color color = new Color(imgBasePattern.getRGB(x, y));
							if (color.getRed() > 0 || color.getBlue() > 0 || color.getGreen() > 0) {
								g.setColor(basepatterncolor.toColor(Math.min(getAlpha(c), getAlpha(color))));
								g.drawLine(x, y, x, y);
							}
						}
						
						// Egg Pattern
						if (imgPattern != null) {
							Color color = new Color(imgPattern.getRGB(x, y));
							if (color.getRed() > 0 || color.getBlue() > 0 || color.getGreen() > 0) {
								g.setColor(patterncolor.toColor(getAlpha(c)));
								g.drawLine(x, y, x, y);
							}
						}
					}
					
				}
			}
			
			ImageIO.write(img, "PNG", out);
			return out;
		} catch (IOException e) {}
		
		return null;
	}
	
	private int getAlpha(Color c) {
		return c.getRed();
//		return (int)(Math.sqrt(c.getRed() * c.getRed() + c.getGreen() * c.getGreen() + c.getBlue() * c.getBlue()) / Math.sqrt(3));
	}
	
	public int getIncubatorSlot() {
		return incubator;
	}
	
	public String getColorString() {
		switch (basepattern.getTag()) {
		case Swirling:
			return "Swirling " + color.toString();
		case TwoTone:
			return String.format("Two-Tone %s and %s", color.toString(), basepatterncolor.toString());
		default:
			return color.toString();
		}
	}
	
	public String getPatternString() {
		return patterncolor.toString() + " " + pattern.toString();
	}

	public String getNumbers() {
		return String.format("%d %d %d %d %d %d %d", dex, form, color.getId(), basepatterncolor.getId(), basepattern.getId(), patterncolor.getId(), pattern.getId());
	}
	
	@Override
	public String toString() {
		return eggname;
	}
	
	@Override
	public int compareTo(Egg that) {
		return Integer.compare(this.id, that.id);
	}
	
	@Override
	public boolean equals(Object that) {
		return that instanceof Egg && this.equals((Egg)that);
	}
	
	public boolean equals(Egg that) {
		return this.compareTo(that) == 0;
	}
	
	////////////////////////////////////////////////////////////////////////////
	
	public static Egg getEgg(int id) {
		try (ResultSet res = JDBC.executeQuery("select * from eggs where eggid = %d;", id)) {
			if (res.next()) {
				Egg e = new Egg();
				e.id = res.getInt("eggid");
				e.player = res.getLong("player");
				e.dex = res.getInt("dex");
				e.form = res.getInt("form");
				e.incubator = res.getInt("incubator");
				e.hp = res.getInt("hp");
				e.maxhp = res.getInt("maxhp");
				e.color = EggColor.of(res.getInt("color"));
				e.basepatterncolor = EggColor.of(res.getInt("patterncolorbase"));
				e.basepattern = EggPattern.of(res.getInt("patternbase"));
				e.patterncolor = EggColor.of(res.getInt("patterncolor"));
				e.pattern = EggPattern.of(res.getInt("pattern"));
				
				String name = res.getString("eggname");
				if (name == null) {
					e.eggname = e.getColorString() + " Egg" + (e.pattern == EggPattern.None ? "" : " with " + e.getPatternString());
					e.customname = false;
				} else {
					e.eggname = name;
					e.customname = true;
				}
				return e;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	public static Egg getRandomEgg(Pair<Integer, Integer> dino) {
		Egg e = new Egg();
		
		e.id = -1;
		e.player = -1;
		e.hp = 0;

		e.dex = -1;
		e.form = 0;
		e.dex = dino.getFirstValue();
		e.form = dino.getSecondValue();
		
		Dinosaur dinosaur = Dinosaur.getDinosaur(e.dex, e.form);
		e.maxhp = MesozoicRandom.nextHatchPoints(dinosaur.getRarity(), dinosaur.getDinosaurForm());
		e.color = nextRandomColor(dinosaur.getDinosaurForm());
		e.basepattern = EggPattern.None;
		e.basepatterncolor = EggColor.Black;
		e.patterncolor = e.color;
		while (e.patterncolor == e.color) e.patterncolor = nextRandomPatternColor(dinosaur.getDinosaurForm());
		
		if (MesozoicRandom.nextInt(4) == 0) {
			e.basepatterncolor = nextRandomColor(dinosaur.getDinosaurForm());
			while (e.basepatterncolor == e.color || e.basepatterncolor == e.patterncolor) e.basepatterncolor = nextRandomPatternColor(dinosaur.getDinosaurForm());
			e.basepattern = nextRandomPattern(EggPatternTag.TwoTone);
		}
		
		e.pattern = nextRandomPattern(dinosaur.getDinosaurForm());
		if (e.pattern == EggPattern.None) {
			e.patterncolor = EggColor.Black;
		}

		e.eggname = e.getColorString() + " Egg" + (e.pattern == EggPattern.None ? "" : " with " + e.getPatternString());
		return e;
	}

	public static Egg getEgg(String numbers) {
		String[] split = numbers.split("\\s+");
		int[] nums = new int[split.length];
		for (int q = 0; q < split.length; q++) nums[q] = Integer.parseInt(split[q]);

		Egg egg = new Egg();
		egg.dex = nums[0];
		egg.form = nums[1];
		egg.color = EggColor.of(nums[2]);
		egg.basepatterncolor = EggColor.of(nums[3]);
		egg.basepattern = EggPattern.of(nums[4]);
		egg.patterncolor = EggColor.of(nums[5]);
		egg.pattern = EggPattern.of(nums[6]);
		return egg;
	}
	
	public static Egg getEgg(EggColor color) {
		return getEgg(color, EggPattern.None, EggColor.Black, EggPattern.None, EggColor.Black);
	}

	public static Egg getEgg(EggColor color, EggPattern basepattern, EggColor basepatterncolor, EggPattern pattern, EggColor patterncolor) {
		Egg egg = new Egg();
		egg.color = color;
		egg.basepattern = basepattern;
		egg.basepatterncolor = basepatterncolor;
		egg.pattern = pattern;
		egg.patterncolor = patterncolor;
		return egg;
	}

	public static EggColor nextRandomColor(DinosaurForm form) {
		return EggColor.getChoosableColors()[MesozoicRandom.nextInt(EggColor.getChoosableColors().length)];
	}
	
	public static EggColor nextRandomPatternColor(DinosaurForm form) {
		return EggColor.getChoosableColors()[MesozoicRandom.nextInt(EggColor.getChoosableColors().length)];
	}
	
	public static EggPattern nextRandomPattern(DinosaurForm form) {
		if (form == DinosaurForm.Thanksgiving) {
			return EggPattern.Thanksgiving;
		}
		return EggPattern.getChoosablePatterns()[MesozoicRandom.nextInt(EggPattern.getChoosablePatterns().length)];
	}
	
	public static EggPattern nextRandomPattern(EggPatternTag tag) {
		ArrayList<EggPattern> patterns = new ArrayList<EggPattern>();
		for (EggPattern ep : EggPattern.values()) {
			if (ep.getTag() == tag) {
				patterns.add(ep);
			}
		}
		return patterns.get(MesozoicRandom.nextInt(patterns.size()));
	}
}
