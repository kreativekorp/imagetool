package com.kreative.imagetool.misc;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import com.kreative.imagetool.ImageIO;
import com.kreative.imagetool.animation.Animation;
import com.kreative.imagetool.animation.AnimationFrame;

public class FancyName {
	private Font font;
	private String name;
	private int onLength;
	private int offLength;
	private Color[] colors;
	
	public FancyName(String name) {
		try {
			InputStream in = FancyName.class.getResourceAsStream("djfancy.ttf");
			this.font = Font.createFont(Font.TRUETYPE_FONT, in).deriveFont(18f);
			in.close();
		} catch (Exception e) {
			e.printStackTrace();
			this.font = new Font("DJ Fancy", Font.PLAIN, 18);
		}
		this.name = name;
		this.onLength = 2;
		this.offLength = 1;
		this.colors = new Color[]{
			new Color(0xFFFF99FF),
			new Color(0xFFCC99FF),
			new Color(0xFF33CCFF),
			new Color(0xFFFFFF33),
		};
	}
	
	public void export(File f, String format) throws IOException {
		Animation a = createAnimation();
		ImageIO.writeFile(a, format, f);
	}
	
	public Animation createAnimation() {
		Animation a = new Animation(128, 128);
		for (int i = 0, n = onLength + offLength; i < n; i++) {
			BufferedImage img = renderFrame(i);
			AnimationFrame af = new AnimationFrame(img, 0.2);
			a.frames.add(af);
		}
		return a;
	}
	
	private BufferedImage renderFrame(int frame) {
		BufferedImage img = new BufferedImage(128, 128, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = img.createGraphics();
		g.setColor(Color.black);
		g.fillRect(0, 0, 128, 128);
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		for (int i =  0, x =   1, y =   1; i < 15; i++, x += 8) {
			if (((onLength + offLength + i - frame) % (onLength + offLength)) < onLength) {
				g.setColor(colors[i % colors.length]);
				g.fillOval(x, y, 6, 6);
			}
		}
		for (int i = 15, x = 121, y =   1; i < 30; i++, y += 8) {
			if (((onLength + offLength + i - frame) % (onLength + offLength)) < onLength) {
				g.setColor(colors[i % colors.length]);
				g.fillOval(x, y, 6, 6);
			}
		}
		for (int i = 30, x = 121, y = 121; i < 45; i++, x -= 8) {
			if (((onLength + offLength + i - frame) % (onLength + offLength)) < onLength) {
				g.setColor(colors[i % colors.length]);
				g.fillOval(x, y, 6, 6);
			}
		}
		for (int i = 45, x =   1, y = 121; i < 60; i++, y -= 8) {
			if (((onLength + offLength + i - frame) % (onLength + offLength)) < onLength) {
				g.setColor(colors[i % colors.length]);
				g.fillOval(x, y, 6, 6);
			}
		}
		g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		g.setColor(Color.white);
		g.setFont(font);
		FontMetrics fm = g.getFontMetrics();
		int x = (128 - fm.stringWidth(name)) / 2;
		int y = (128 - fm.getHeight()) / 2 + fm.getAscent() - 1;
		g.drawString(name, x, y);
		g.dispose();
		return img;
	}
	
	public static void main(String[] args) {
		FancyName fn = new FancyName("Ashley");
		int i = 0; int o;
		while (i < args.length) {
			String arg = args[i++];
			if ((arg.equals("-f") || arg.equals("--font")) && i < args.length) {
				fn.font = new Font(args[i++], fn.font.getStyle(), fn.font.getSize());
			} else if ((arg.equals("-s") || arg.equals("--size")) && i < args.length) {
				fn.font = fn.font.deriveFont(Float.parseFloat(args[i++]));
			} else if ((arg.equals("-n") || arg.equals("--name")) && i < args.length) {
				fn.name = args[i++];
			} else if ((arg.equals("-l") || arg.equals("--length")) && i < args.length) {
				String[] ls = args[i++].replaceAll("[^0-9]+", " ").trim().split(" +", 2);
				try { fn.onLength = (ls.length > 0) ? Integer.parseInt(ls[0]) : 1; }
				catch (NumberFormatException nfe) { fn.onLength = 1; }
				try { fn.offLength = (ls.length > 1) ? Integer.parseInt(ls[1]) : 1; }
				catch (NumberFormatException nfe) { fn.offLength = 1; }
			} else if ((arg.equals("-c") || arg.equals("--colors")) && i < args.length) {
				String[] cs = args[i++].replaceAll("[^0-9A-Fa-f]+", " ").trim().split(" +");
				fn.colors = new Color[cs.length];
				for (int j = 0; j < cs.length; j++) {
					try { fn.colors[j] = new Color(Integer.parseInt(cs[j], 16)); }
					catch (NumberFormatException nfe) { fn.colors[j] = Color.white; }
				}
			} else if ((o = arg.lastIndexOf('.')) <= 0) {
				System.err.println("No format specified!");
				return;
			} else try {
				fn.export(new File(arg), arg.substring(o + 1));
			} catch (IOException e) {
				System.err.println("Error writing: " + e.getClass().getSimpleName() + ": " + e.getMessage());
				return;
			}
		}
	}
}
