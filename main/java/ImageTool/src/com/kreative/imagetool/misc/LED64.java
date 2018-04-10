package com.kreative.imagetool.misc;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.text.CharacterIterator;
import java.text.StringCharacterIterator;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import com.kreative.imagetool.ImageIO;
import com.kreative.imagetool.animation.Animation;
import com.kreative.imagetool.animation.AnimationFrame;

public class LED64 {
	private Color offColor = new Color(0xFF444444);
	private Color ledColor = new Color(0xFFFF0000);
	private List<Long> frames = new ArrayList<Long>();
	private List<Integer> durations = new ArrayList<Integer>();
	
	public void export(File f, String format) throws IOException {
		Animation a = createAnimation();
		ImageIO.writeFile(a, format, f);
	}
	
	public Animation createAnimation() {
		Animation a = new Animation(128, 128);
		Iterator<Long> fi = frames.iterator();
		Iterator<Integer> di = durations.iterator();
		while (fi.hasNext() && di.hasNext()) {
			BufferedImage img = renderFrame(fi.next());
			double dur = di.next() / 1000.0;
			AnimationFrame af = new AnimationFrame(img, dur);
			a.frames.add(af);
		}
		return a;
	}
	
	private BufferedImage renderFrame(long frame) {
		BufferedImage img = new BufferedImage(128, 128, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = img.createGraphics();
		g.setColor(Color.black);
		g.fillRect(0, 0, 128, 128);
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		for (int y = 2; y < 128; y += 16) {
			for (int x = 2; x < 128; x += 16) {
				g.setColor(((frame & Long.MIN_VALUE) == 0) ? offColor : ledColor);
				g.fillOval(x, y, 13, 13);
				frame <<= 1;
			}
		}
		return img;
	}
	
	private static final Pattern colorPattern = Pattern.compile("^#([0-9A-Fa-f]+)$");
	private static final Pattern binaryPattern = Pattern.compile("^([01]{17,})(:([0-9]+))?$");
	private static final Pattern hexPattern = Pattern.compile("^([0-9A-Fa-f]{1,16})(:([0-9]+))?$");
	
	public static void main(String[] args) {
		LED64 led64 = new LED64();
		Matcher m; int o;
		for (String arg : args) {
			if ((m = colorPattern.matcher(arg)).matches()) {
				led64.ledColor = new Color(parseInt(m.group(1), 16));
			} else if ((m = binaryPattern.matcher(arg)).matches()) {
				long frame = parseLong(m.group(1), 2);
				int duration = parseInt(m.group(3), 10);
				if (duration <= 0) duration = 1000;
				led64.frames.add(frame);
				led64.durations.add(duration);
			} else if ((m = hexPattern.matcher(arg)).matches()) {
				long frame = parseLong(m.group(1), 16);
				int duration = parseInt(m.group(3), 10);
				if (duration <= 0) duration = 1000;
				led64.frames.add(frame);
				led64.durations.add(duration);
			} else if (led64.frames.isEmpty() || led64.durations.isEmpty()) {
				System.err.println("No frames specified!");
				return;
			} else if ((o = arg.lastIndexOf('.')) <= 0) {
				System.err.println("No format specified!");
				return;
			} else try {
				led64.export(new File(arg), arg.substring(o + 1));
				led64.frames.clear();
				led64.durations.clear();
			} catch (IOException e) {
				System.err.println("Error writing: " + e.getClass().getSimpleName() + ": " + e.getMessage());
				return;
			}
		}
	}
	
	private static int parseInt(String s, int radix) {
		int value = 0;
		CharacterIterator ci = new StringCharacterIterator(s);
		for (char ch = ci.first(); ch != CharacterIterator.DONE; ch = ci.next()) {
			value = (value * radix) + Character.digit(ch, radix);
		}
		return value;
	}
	
	private static long parseLong(String s, int radix) {
		long value = 0;
		CharacterIterator ci = new StringCharacterIterator(s);
		for (char ch = ci.first(); ch != CharacterIterator.DONE; ch = ci.next()) {
			value = (value * radix) + Character.digit(ch, radix);
		}
		return value;
	}
}
