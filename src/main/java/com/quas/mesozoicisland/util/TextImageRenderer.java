package com.quas.mesozoicisland.util;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;

public class TextImageRenderer {

	private final Font font;
	private final int height;
	private final Color color;
	
	public TextImageRenderer(Font font) {
		this(font, Color.black);
	}
	
	public TextImageRenderer(Font font, Color color) {
		this.font = new Font(font.getFontName(), font.getStyle(), font.getSize() * 4);
		this.height = fontHeight(font.getSize());
		this.color = color;
	}
	
	public int getDefaultRenderWidth(String text) {
		FontMetrics fm = new Canvas().getFontMetrics(font);
		final int height = (4 * font.getSize() + 2) / 3;
		int[] string = prepare(text);
		int width = 0;
		
		for (int q = 0; q < string.length; q++) {
			if (string[q] < 0x10000 && font.canDisplay((char)string[q])) {
				width += fm.charWidth(string[q]);
			} else {
				width += height;
			}
		}
		
		return width * this.height / height;
	}
	
	public int getRenderHeight() {
		return height;
	}
	
	private BufferedImage render(String text, int kerning) {
		FontMetrics fm = new Canvas().getFontMetrics(font);
		int[] string = prepare(text);
		
		final int HEIGHT = (4 * font.getSize() + 2) / 3;
		int WIDTH = -kerning;
		final int BASELINE = fm.getAscent();
		final int EMOJI_SIZE = HEIGHT;
		boolean[] renderable = new boolean[string.length];
		
		for (int q = 0; q < string.length; q++) {
			if (string[q] < 0x10000 && font.canDisplay((char)string[q])) {
				WIDTH += fm.charWidth(string[q]) + kerning;
				renderable[q] = true;
			} else {
				WIDTH += EMOJI_SIZE + kerning;
			}
		}
		
		BufferedImage image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_ARGB_PRE);
		Graphics g = image.getGraphics();
		g.setFont(font);
		g.setColor(color);
		int pos = 0;
		
		for (int q = 0; q < string.length; q++) {
			if (renderable[q]) {
				g.drawString(Character.toString((char)string[q]), pos, BASELINE);
				pos += fm.charWidth(string[q]) + kerning;
			} else {
				try {
					BufferedImage emoji = ImageIO.read(new File(String.format("emoji/%x.png", string[q])));
					g.drawImage(emoji, pos, 0, EMOJI_SIZE, EMOJI_SIZE, null);
					pos += EMOJI_SIZE + kerning;
				} catch (IOException e) {}
			}
		}
		
		BufferedImage sizeDown = new BufferedImage(WIDTH * this.height / HEIGHT, this.height, BufferedImage.TYPE_INT_ARGB_PRE);
		Graphics g2 = sizeDown.getGraphics();
		g2.drawImage(image, 0, 0, sizeDown.getWidth(), sizeDown.getHeight(), null);
		return sizeDown;
	}
	
	public BufferedImage renderCentered(String text, int setWidth) {
		return renderCentered(text, setWidth, 0);
	}
	
	public BufferedImage renderCentered(String text, int setWidth, int kerning) {
		BufferedImage base = render(text, kerning);
		if (base == null) return null;
		if (base.getWidth() < setWidth) {
			BufferedImage centered = new BufferedImage(setWidth, base.getHeight(), BufferedImage.TYPE_INT_ARGB_PRE);
			Graphics g = centered.getGraphics();
			g.drawImage(base, (centered.getWidth() - base.getWidth()) / 2, 0, base.getWidth(), base.getHeight(), null);
			return centered;
		}
		
		return squish(base, setWidth);
	}
	
	public BufferedImage renderLeft(String text, int setWidth) {
		return renderLeft(text, setWidth, 0);
	}
	
	public BufferedImage renderLeft(String text, int setWidth, int kerning) {
		BufferedImage base = render(text, kerning);
		if (base == null) return null;
		if (base.getWidth() < setWidth) return base;
		return squish(base, setWidth);
	}
	
	private BufferedImage squish(BufferedImage base, int setWidth) {
		BufferedImage squish = new BufferedImage(setWidth, base.getHeight(), BufferedImage.TYPE_INT_ARGB_PRE);
		Graphics g = squish.getGraphics();
		g.drawImage(base, 0, 0, squish.getWidth(), squish.getHeight(), null);
		return squish;
	}
	
	private int[] prepare(String str) {
		ArrayList<Integer> list = new ArrayList<Integer>();
		
		char[] arr = str.toCharArray();
		for (int q = 0; q < arr.length; q++) {
			if (q < arr.length - 1) {
				if (isSurrogateHigh(arr[q]) && isSurrogateLow(arr[q + 1])) {
					list.add(getSurrogate(arr[q], arr[q + 1]));
					q++;
					continue;
				}
			}
			
			list.add((int)arr[q]);
		}
		
		int[] ret = new int[list.size()];
		for (int q = 0; q < ret.length; q++) ret[q] = list.get(q);
		return ret;
	}
	
	private boolean isSurrogateHigh(char c) {
		return 0xD800 <= c && c <= 0xDBFF;
	}
	
	private boolean isSurrogateLow(char c) {
		return 0xDC00 <= c && c <= 0xDFFF;
	}
	
	private int getSurrogate(char high, char low) {
		return (high - 0xD800) * 0x400 + (low - 0xDC00) + 0x10000;
	}
	
	private int fontHeight(int size) {
		return (4 * size + 2) / 3;
	}
}
