package com.quas.mesozoicisland.util;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

import javax.imageio.ImageIO;

import com.quas.mesozoicisland.MesozoicIsland;
import com.quas.mesozoicisland.enums.DinosaurForm;
import com.quas.mesozoicisland.objects.Player;

import net.dv8tion.jda.api.entities.Member;

@SuppressWarnings("unused")
public class DinosaurLicense {

	private static final int WIDTH = 840;
	private static final int HEIGHT = 540;
	private static final int ARC = 60;
	private static final int PFP_SIZE = 240;
	private static final int PFP_BORDER = 10;
	private static final int STAMP_SIZE = 400;
	private static final int LEFT = 20;
	
	private static final String OLD_FONT = "Times New Roman";
	private static final int TEXT_X = LEFT + 2 * PFP_BORDER + PFP_SIZE + 10;
	private static final int TEXT_Y = 50;
	private static final int FONT_SIZE = 32;
	private static final int LINE_HEIGHT = FONT_SIZE + 10;
	
	private static final Color BORDER_COLOR = Color.BLACK;
	private static final Color OLD_LICENSE_COLOR = Color.CYAN;
	private static final Color PFP_BORDER_COLOR = Color.DARK_GRAY;
	private static final Color TEXT_COLOR = Color.BLACK;
	
	public static File of(Player p) {
		try {
			Member member = MesozoicIsland.getProfessor().getGuild().getMemberById(p.getId());
			BufferedImage bi = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_ARGB_PRE);
			Graphics g = bi.getGraphics();
			
			// Fill Rectangle with No Color
			g.setColor(new Color(0f, 0f, 0f, 0f));
			g.fillRect(0, 0, WIDTH, HEIGHT);
			
			// Border
			g.setColor(BORDER_COLOR);
			g.fillRoundRect(0, 0, WIDTH, HEIGHT, ARC, ARC);
			
			// Fill
//			g.setColor(LICENSE_COLOR);
			g.setColor(p.getColor());
			g.fillRoundRect(1, 1, WIDTH - 2, HEIGHT - 2, ARC, ARC);
			
			// PFP Border
			g.setColor(PFP_BORDER_COLOR);
			g.fillRect(LEFT, LEFT, PFP_SIZE + 2 * PFP_BORDER, PFP_SIZE + 2 * PFP_BORDER);
			
			// PFP
			URL url = new URL(Util.getAvatar(member.getUser()));
			HttpURLConnection con = (HttpURLConnection)url.openConnection();
			con.setRequestProperty("User-Agent", "");
			BufferedImage pfp = ImageIO.read(con.getInputStream());
			g.drawImage(pfp, LEFT + PFP_BORDER, LEFT + PFP_BORDER, PFP_SIZE, PFP_SIZE, null);
			
			// Stamp
//			BufferedImage stamp = ImageIO.read(new URL("https://cdn.discordapp.com/attachments/648004304606724096/653308591826141214/stamp_purple.png"));
//			g.drawImage(stamp, TEXT_X, TEXT_Y, STAMP_SIZE, STAMP_SIZE, null);
			
			// Text
			g.setColor(TEXT_COLOR);
			g.setFont(new Font(OLD_FONT, Font.BOLD, FONT_SIZE));
			g.drawString("Mesozoic Island Trainer License", TEXT_X, TEXT_Y);
			
			g.setFont(new Font(OLD_FONT, Font.PLAIN, FONT_SIZE));
			g.drawLine(TEXT_X, TEXT_Y + 4, TEXT_X + 440, TEXT_Y + 4);
			g.drawString("Name: " + p.getName(), TEXT_X, TEXT_Y + LINE_HEIGHT);
			g.drawString("ID: " + p.getId(), TEXT_X, TEXT_Y + 2 * LINE_HEIGHT);
			g.drawString("Birthday: " + (p.getBirthday() > 0 ? Util.getBirthday(p.getBirthday()) : "Unknown"), TEXT_X, TEXT_Y + 3 * LINE_HEIGHT);
			g.drawString("Trainer Level: " + p.getLevel(), TEXT_X, TEXT_Y + 4 * LINE_HEIGHT);
			int dex = p.getDexCount(DinosaurForm.AllForms.getId());
			g.drawString("Dinodex: " + dex + " Dinosaur" + (dex == 1 ? "" : "s"), TEXT_X, TEXT_Y + 5 * LINE_HEIGHT);
			if (p.getMainElement().getId() > 0) g.drawString("Guild: " + p.getMainElement(), TEXT_X, TEXT_Y + 6 * LINE_HEIGHT);
			
			g.setFont(new Font(OLD_FONT, Font.PLAIN, FONT_SIZE));
			g.drawString("Issued: " + p.getJoinDate(), LEFT, TEXT_X + LINE_HEIGHT / 2);
			
			File file = new File(Constants.RESOURCE_PATH + "Licenses\\" + p.getId() + ".png");
			ImageIO.write(bi, "PNG", file);
			return file;
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return null;
	}

	// License
	private static final int LICENSE_WIDTH = 840;
	private static final int LICENSE_HEIGHT = 540;
	private static final String FONT = "Book Antiqua Bold";

	// Profile Picture
	private static final int PROFILE_X = 55;
	private static final int PROFILE_Y = 85;
	private static final int PROFILE_SIZE = 290;
	
	// Title
	private static final int TITLE_FONT_SIZE = 45;
	private static final int TITLE_KERNING = 4;
	private static final int TITLE_TOP = 25;
	
	// Player Name
	private static final int MAX_NAME_RENDER_WIDTH = 300;
	private static final int NAME_FONT_SIZE = 30;
	private static final int NAME_LEFT = 63;
	private static final int NAME_TOP = 380;
	private static final int NAME_WIDTH = 275;
	
	// Player Stats
	private static final int TEXT_FONT_SIZE = 30;
	private static final int TEXT_LEFT = 400;
	private static final int TEXT_WIDTH = 400;
	private static final int TEXT_TOP = 100;
	private static final int TEXT_HEIGHT = 200;
	
	// Favorite Dinosaur
	private static final int FAVORITE_FONT_SIZE = 32;
	private static final int FAVORITE_LEFT = TEXT_LEFT - 25;
	private static final int FAVORITE_TOP = TEXT_TOP + TEXT_HEIGHT + 25;
	
	// File Paths
	private static final String ASSET_PATH = Constants.RESOURCE_PATH + "Licenses\\Assets\\";
	private static final String LICENSE_COLOR = "license_color.png";
	private static final String LICENSE_BACKGROUND = "license_background.png";
	private static final String LICENSE_GLARE = "license_glare.png";
	private static final String LICENSE_LOGO = "license_logo.png";
	private static final String LICENSE_EXTENSION = "license_appendname.png";
	
	public static File of2(Player p, Member m) {
		try {
			BufferedImage license = new BufferedImage(LICENSE_WIDTH, LICENSE_HEIGHT, BufferedImage.TYPE_INT_ARGB_PRE);
			Graphics g = license.getGraphics();
			
			// Fill Rectangle with No Color
			g.setColor(new Color(0f, 0f, 0f, 0f));
			g.fillRect(0, 0, LICENSE_WIDTH, LICENSE_HEIGHT);
			
			// Draw colored background
			g.setColor(p.getColor());
			BufferedImage color = ImageIO.read(new File(ASSET_PATH + LICENSE_COLOR));
			for (int x = 0; x < LICENSE_WIDTH; x++) {
				for (int y = 0; y < LICENSE_HEIGHT; y++) {
					Color c = new Color(color.getRGB(x, y), true);
					if (c.getAlpha() < 255) continue;
					
					g.drawLine(x, y, x, y);
				}
			}
			
			// Draw outlines
			g.drawImage(ImageIO.read(new File(ASSET_PATH + LICENSE_BACKGROUND)), 0, 0, null);
			
			// overlay glare and logo
			overlay(license, ImageIO.read(new File(ASSET_PATH + LICENSE_GLARE)));
			overlay(license, ImageIO.read(new File(ASSET_PATH + LICENSE_LOGO)));
			
			// pfp
			BufferedImage finalLicense = new BufferedImage(LICENSE_WIDTH, LICENSE_HEIGHT, BufferedImage.TYPE_INT_ARGB_PRE);
			g = finalLicense.getGraphics();
			
			URL url = new URL(Util.getAvatar(m.getUser()));
			HttpURLConnection con = (HttpURLConnection)url.openConnection();
			con.setRequestProperty("User-Agent", "");
			BufferedImage pfp = ImageIO.read(con.getInputStream());
			g.drawImage(pfp, PROFILE_X, PROFILE_Y, PROFILE_SIZE, PROFILE_SIZE, null);
			g.drawImage(license, 0, 0, null);
			
			// draw title
			TextImageRenderer titleRenderer = new TextImageRenderer(new Font(FONT, Font.BOLD, TITLE_FONT_SIZE));
			BufferedImage title = titleRenderer.renderCentered("Mesozoic Island - Trainer License", LICENSE_WIDTH, TITLE_KERNING);
			g.drawImage(title, 0, TITLE_TOP, null);
			
			// draw name
			TextImageRenderer nameRenderer = new TextImageRenderer(new Font(FONT, Font.BOLD, NAME_FONT_SIZE), Color.white);
			
			String playerName = p.getName();
			if (p.getOmegaLevel() > 0) playerName += String.format(" [%s Level %,d]", Constants.OMEGA, p.getOmegaLevel());
			else playerName += String.format(" [Level %,d]", p.getLevel());

			// if name is too long, do 2 rows
			if (nameRenderer.getDefaultRenderWidth(playerName) > MAX_NAME_RENDER_WIDTH) {
				
				String[] split = splitString(playerName);
				BufferedImage nameTop = nameRenderer.renderCentered(split[0], NAME_WIDTH);
				BufferedImage nameBottom = nameRenderer.renderCentered(split[1], NAME_WIDTH);
				
				g.drawImage(ImageIO.read(new File(ASSET_PATH + LICENSE_EXTENSION)), 0, nameTop.getHeight(), null);
				g.drawImage(nameTop, NAME_LEFT, NAME_TOP, null);
				g.drawImage(nameBottom, NAME_LEFT, NAME_TOP + nameTop.getHeight(), null);
			} else {
				BufferedImage name = nameRenderer.renderCentered(playerName, NAME_WIDTH);
				g.drawImage(name, NAME_LEFT, NAME_TOP, null);
			}
			
			// generate text
			int dex = p.getDexCount(DinosaurForm.AllForms.getId());
			ArrayList<String> text = new ArrayList<>();
			text.add(String.format("ID: %s", p.getId()));
			text.add(String.format("Birthday: %s", p.getBirthday() > 0 ? Util.getBirthday(p.getBirthday()) : "Unknown"));
			text.add(String.format("Dinodex: %,d Dinosaur%s", dex, dex == 1 ? "" : "s"));
			text.add(String.format("Issued: %s", p.getJoinDate()));
			
			// draw text
			TextImageRenderer textRenderer = new TextImageRenderer(new Font(FONT, Font.PLAIN, TEXT_FONT_SIZE));
			int spacing = (TEXT_HEIGHT - textRenderer.getRenderHeight()) / (text.size() - 1);
			
			for (int q = 0; q < text.size(); q++) {
				if (text.get(q).length() == 0) continue;
				BufferedImage line = textRenderer.renderLeft(text.get(q), TEXT_WIDTH);
				g.drawImage(line, TEXT_LEFT, TEXT_TOP + q * spacing, null);
			}
			
			// draw favorite dino
			TextImageRenderer favoriteRenderer = new TextImageRenderer(new Font(FONT, Font.ITALIC, FAVORITE_FONT_SIZE));
			BufferedImage favorite = favoriteRenderer.renderLeft("Favorite Dinosaur:", TEXT_WIDTH);
			g.drawImage(favorite, FAVORITE_LEFT, FAVORITE_TOP, null);
			BufferedImage favoriteDinosaur = textRenderer.renderLeft("None", TEXT_WIDTH);
			g.drawImage(favoriteDinosaur, TEXT_LEFT, FAVORITE_TOP + favorite.getHeight(), null);
			
			// draw guild

			// draw emblems

			// draw stars

			File file = new File(Constants.RESOURCE_PATH + "Licenses\\" + p.getId() + ".png");
			ImageIO.write(finalLicense, "PNG", file);
			return file;
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return null;
	}

	private static BufferedImage overlay(BufferedImage bottom, BufferedImage top) {
		Graphics g = bottom.getGraphics();
		
		for (int x = 0; x < bottom.getWidth(); x++) {
			for (int y = 0; y < bottom.getHeight(); y++) {
				Color tc = new Color(top.getRGB(x, y), true);
				if (tc.getAlpha() == 0) continue;
				Color bc = new Color(bottom.getRGB(x, y), true);
				if (bc.getAlpha() == 0) continue;
				g.setColor(overlay(bc, tc));
				g.drawLine(x, y, x, y);
			}
		}
		
		return bottom;
	}
	
	private static HashMap<Color, HashMap<Color, Color>> map = new HashMap<>();
	private static Color overlay(Color bottom, Color top) {
		if (map.containsKey(bottom)) if (map.get(bottom).containsKey(top)) return map.get(bottom).get(top);
		
		Color ret = new Color(overlay(bottom.getRed(), top.getRed()), overlay(bottom.getGreen(), top.getGreen()), overlay(bottom.getBlue(), top.getBlue()), top.getAlpha());
		if (!map.containsKey(bottom)) map.put(bottom, new HashMap<>());
		map.get(bottom).put(top, ret);
		return ret;
	}
	
	private static int overlay(int bottom, int top) {
		return Math.round(bottom / 255f * (bottom + 2f * top / 255f * (255f - bottom)));
	}
	
	private static String[] splitString(String str) {
		String[] split = str.split("\\s+");
		String left = split[0], right = split[split.length - 1];
		
		int a = 1, b = split.length - 2;
		while (a <= b) {
			if (left.length() + split[a].length() <= right.length() + split[b].length()) {
				left = left + " " + split[a];
				a++;
			} else {
				right = split[b] + " " + right;
				b--;
			}
		}
		
		return new String[] {left, right};
	}
}
