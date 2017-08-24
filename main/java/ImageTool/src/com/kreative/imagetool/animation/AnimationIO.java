package com.kreative.imagetool.animation;

import java.awt.Insets;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.imageio.ImageIO;
import javax.swing.SwingConstants;

import com.kreative.imagetool.gif.GIFApplicationExtension;
import com.kreative.imagetool.gif.GIFDisposalMethod;
import com.kreative.imagetool.gif.GIFFile;
import com.kreative.imagetool.gif.GIFFrame;
import com.kreative.imagetool.gif.GIFFrameIterator;
import com.kreative.imagetool.gif.GIFGraphicControlExtension;
import com.kreative.imagetool.gif.GIFImageDescriptor;
import com.kreative.imagetool.gif.Histogram;
import com.kreative.imagetool.transform.CanvasSize;
import com.kreative.imagetool.transform.RemoveMargin;
import com.kreative.imagetool.transform.Trim;

public class AnimationIO implements SwingConstants {
	public static Animation fromGIFFile(GIFFile gif) {
		Animation a = new Animation(gif.width, gif.height);
		GIFFrameIterator iter = new GIFFrameIterator(gif);
		while (iter.hasNext()) {
			GIFFrame frame = iter.next();
			double d = (frame.gce != null) ? (frame.gce.delayTime / 100.0) : 1;
			a.frames.add(new AnimationFrame(frame.composedImage, d));
		}
		return a;
	}
	
	public static Animation fromImageArray(BufferedImage image, ArrayOptions o) {
		if (o == null) o = new ArrayOptions();
		o = o.forImage(image.getWidth(), image.getHeight());
		Animation a = new Animation(o.cw, o.ch);
		int[] buffer = new int[o.cw * o.ch];
		int[] yx = new int[2];
		for (int i = 0, n = o.cols * o.rows; i < n; i++) {
			BufferedImage frame = o.getFrame(image, i, null, buffer, yx);
			a.frames.add(new AnimationFrame(frame, 1));
		}
		return a;
	}
	
	public static Animation fromDirectory(File directory, int anchor) throws IOException {
		Animation a = new Animation(0, 0);
		File[] files = directory.listFiles();
		Arrays.sort(files, new FileNameNaturalComparator());
		for (File file : files) {
			if (file.getName().startsWith(".")) continue;
			if (file.isDirectory()) continue;
			BufferedImage image = ImageIO.read(file);
			if (image == null) continue;
			if (image.getWidth() > a.width) a.width = image.getWidth();
			if (image.getHeight() > a.height) a.height = image.getHeight();
			Matcher m = DURATION_PATTERN.matcher(file.getName());
			double d = m.find() ? Double.parseDouble(m.group(1)) : 1;
			a.frames.add(new AnimationFrame(image, d));
		}
		CanvasSize s = new CanvasSize(a.width, a.height, anchor);
		for (AnimationFrame f : a.frames) f.image = s.transform(f.image);
		return a;
	}
	
	public static GIFFile toGIFFile(Animation a, int repeat) {
		Histogram<Integer> h = new Histogram<Integer>();
		int[] pixels = new int[a.width * a.height];
		for (AnimationFrame frame : a.frames) {
			frame.image.getRGB(0, 0, a.width, a.height, pixels, 0, a.width);
			for (int p : pixels) if (p < 0) h.add(p | 0xFF000000);
		}
		List<Integer> palette = h.toList();
		Collections.sort(palette, h.byCount(true));
		int s0 = Math.min(palette.size(), 255);
		int[] p0 = new int[s0 + 1];
		for (int i = 0; i < s0; i++) {
			p0[i] = palette.get(i);
		}
		
		GIFFile gif = new GIFFile();
		gif.version89a = true;
		gif.width = a.width;
		gif.height = a.height;
		gif.depth = log(s0 + 1);
		gif.palette = p0;
		gif.paletteSorted = true;
		gif.backgroundIndex = s0;
		
		GIFApplicationExtension nab = new GIFApplicationExtension();
		nab.setRepeatCount(repeat);
		gif.blocks.add(nab);
		
		for (AnimationFrame frame : a.frames) {
			BufferedImage image = frame.image;
			Insets margin = Trim.getMargin(image);
			image = new RemoveMargin(margin).transform(image);
			
			GIFGraphicControlExtension gce = new GIFGraphicControlExtension();
			gce.disposalMethod = GIFDisposalMethod.RESTORE_TO_BACKGROUND;
			gce.delayTime = (int)Math.round(frame.duration * 100);
			gce.transparency = true;
			gce.transparencyIndex = s0;
			
			GIFImageDescriptor gid = new GIFImageDescriptor();
			gid.left = margin.left;
			gid.top = margin.top;
			gid.minKeySize = GIFImageDescriptor.bestMinKeySize(s0 + 1);
			try { gid.setImage(gif, gce, image); }
			catch (IOException e) { continue; }
			
			gif.blocks.add(gce);
			gif.blocks.add(gid);
		}
		
		return gif;
	}
	
	public static BufferedImage toImageArray(Animation a, BufferedImage image, ArrayOptions o) {
		if (o == null) o = new ArrayOptions();
		o = o.forFrames(a.width, a.height, a.frames.size());
		int[] buffer = new int[a.width * a.height];
		int[] yx = new int[2];
		for (int i = 0, n = a.frames.size(); i < n; i++) {
			BufferedImage frame = a.frames.get(i).image;
			image = o.setFrame(image, i, frame, buffer, yx);
		}
		return image;
	}
	
	public static void toDirectory(Animation a, File directory) throws IOException {
		int indexLength = Integer.toString(a.frames.size()).length();
		String indexPadding = zeroes(indexLength);
		directory.mkdirs();
		for (int i = 0, n = a.frames.size(); i < n; i++) {
			BufferedImage image = a.frames.get(i).image;
			String name = indexPadding + Integer.toString(i + 1);
			name = name.substring(name.length() - indexLength);
			name = name + "-" + a.frames.get(i).duration + "s.png";
			ImageIO.write(image, "png", new File(directory, name));
		}
	}
	
	private static final Pattern DURATION_PATTERN =
		Pattern.compile("\\b([0-9]+(\\.[0-9]+)?)[Ss]\\b");
	
	private static int log(int size) {
		if (size <=   2) return 1;
		if (size <=   4) return 2;
		if (size <=   8) return 3;
		if (size <=  16) return 4;
		if (size <=  32) return 5;
		if (size <=  64) return 6;
		if (size <= 128) return 7;
		return 8;
	}
	
	private static String zeroes(int count) {
		StringBuffer sb = new StringBuffer();
		while (count-- > 0) sb.append('0');
		return sb.toString();
	}
}
