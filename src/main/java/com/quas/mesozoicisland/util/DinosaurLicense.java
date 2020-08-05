package com.quas.mesozoicisland.util;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.net.URL;

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
	
	private static final String FONT = "Times New Roman";
	private static final int TEXT_X = LEFT + 2 * PFP_BORDER + PFP_SIZE + 10;
	private static final int TEXT_Y = 50;
	private static final int FONT_SIZE = 32;
	private static final int LINE_HEIGHT = FONT_SIZE + 10;
	
	private static final Color BORDER_COLOR = Color.BLACK;
	private static final Color LICENSE_COLOR = Color.CYAN;
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
			BufferedImage pfp = ImageIO.read(new URL(Util.getAvatar(member.getUser())));
			g.drawImage(pfp, LEFT + PFP_BORDER, LEFT + PFP_BORDER, PFP_SIZE, PFP_SIZE, null);
			
			// Stamp
//			BufferedImage stamp = ImageIO.read(new URL("https://cdn.discordapp.com/attachments/648004304606724096/653308591826141214/stamp_purple.png"));
//			g.drawImage(stamp, TEXT_X, TEXT_Y, STAMP_SIZE, STAMP_SIZE, null);
			
			// Text
			g.setColor(TEXT_COLOR);
			g.setFont(new Font(FONT, Font.BOLD, FONT_SIZE));
			g.drawString("Mesozoic Island Trainer License", TEXT_X, TEXT_Y);
			
			g.setFont(new Font(FONT, Font.PLAIN, FONT_SIZE));
			g.drawLine(TEXT_X, TEXT_Y + 4, TEXT_X + 440, TEXT_Y + 4);
			g.drawString("Name: " + p.getName(), TEXT_X, TEXT_Y + LINE_HEIGHT);
			g.drawString("ID: " + p.getId(), TEXT_X, TEXT_Y + 2 * LINE_HEIGHT);
			g.drawString("Birthday: " + (p.getBirthday() > 0 ? Util.getBirthday(p.getBirthday()) : "Unknown"), TEXT_X, TEXT_Y + 3 * LINE_HEIGHT);
			g.drawString("Trainer Level: " + p.getLevel(), TEXT_X, TEXT_Y + 4 * LINE_HEIGHT);
			g.drawString("Dinodex: " + p.getDexCount(DinosaurForm.AllForms.getId()) + " Dinosaurs", TEXT_X, TEXT_Y + 5 * LINE_HEIGHT);
			if (p.getMainElement().getId() > 0) g.drawString("Guild: " + p.getMainElement(), TEXT_X, TEXT_Y + 6 * LINE_HEIGHT);
			
			g.setFont(new Font(FONT, Font.PLAIN, FONT_SIZE));
			g.drawString("Issued: " + p.getJoinDate(), LEFT, TEXT_X + LINE_HEIGHT / 2);
			
			File file = new File(Constants.RESOURCE_PATH + "Licenses\\" + p.getId() + ".png");
			ImageIO.write(bi, "PNG", file);
			return file;
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return null;
	}
}
