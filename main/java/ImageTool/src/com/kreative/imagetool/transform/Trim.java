package com.kreative.imagetool.transform;

import java.awt.Insets;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import com.kreative.imagetool.animation.Animation;
import com.kreative.imagetool.animation.AnimationFrame;
import com.kreative.imagetool.gci.GCIFile;
import com.kreative.imagetool.gif.GIFFile;
import com.kreative.imagetool.gif.GIFFrameIterator;

public class Trim implements Transform {
	private final boolean top, left, bottom, right;
	
	public Trim(boolean top, boolean left, boolean bottom, boolean right) {
		this.top = top;
		this.left = left;
		this.bottom = bottom;
		this.right = right;
	}
	
	public BufferedImage transform(BufferedImage image) {
		Insets i = getMargin(image);
		return new RemoveMargin(
			top ? i.top : 0,
			left ? i.left : 0,
			bottom ? i.bottom : 0,
			right ? i.right : 0
		).transform(image);
	}
	
	public GCIFile transform(GCIFile gci) {
		// Not supported for GCIs.
		return gci;
	}
	
	public GIFFile transform(GIFFile gif) {
		Insets i = getMargin(gif);
		return new RemoveMargin(
			top ? i.top : 0,
			left ? i.left : 0,
			bottom ? i.bottom : 0,
			right ? i.right : 0
		).transform(gif);
	}
	
	public Animation transform(Animation a) {
		Insets i = getMargin(a);
		return new RemoveMargin(
			top ? i.top : 0,
			left ? i.left : 0,
			bottom ? i.bottom : 0,
			right ? i.right : 0
		).transform(a);
	}
	
	public static Insets getMargin(BufferedImage image) {
		int w = image.getWidth();
		int h = image.getHeight();
		Insets i = new Insets(0, 0, 0, 0);
		while (i.top < h && rowIsEmpty(image, i.top)) i.top++;
		while (i.left < w && columnIsEmpty(image, i.left)) i.left++;
		while (i.bottom < h && rowIsEmpty(image, h - i.bottom - 1)) i.bottom++;
		while (i.right < w && columnIsEmpty(image, w - i.right - 1)) i.right++;
		return i;
	}
	
	public static Insets getMargin(GIFFile gif) {
		List<Insets> li = new ArrayList<Insets>();
		GIFFrameIterator iter = new GIFFrameIterator(gif);
		while (iter.hasNext()) li.add(getMargin(iter.next().composedImage));
		return minMargin(li);
	}
	
	public static Insets getMargin(Animation a) {
		List<Insets> li = new ArrayList<Insets>();
		for (AnimationFrame frame : a.frames) li.add(getMargin(frame.image));
		return minMargin(li);
	}
	
	private static Insets minMargin(List<Insets> li) {
		if (li.isEmpty()) return new Insets(0, 0, 0, 0);
		Insets a = li.get(0);
		Insets min = new Insets(a.top, a.left, a.bottom, a.right);
		for (int i = 1, n = li.size(); i < n; i++) {
			Insets b = li.get(i);
			if (b.top < min.top) min.top = b.top;
			if (b.left < min.left) min.left = b.left;
			if (b.bottom < min.bottom) min.bottom = b.bottom;
			if (b.right < min.right) min.right = b.right;
		}
		return min;
	}
	
	private static boolean rowIsEmpty(BufferedImage image, int row) {
		int[] pixels = new int[image.getWidth()];
		image.getRGB(0, row, pixels.length, 1, pixels, 0, pixels.length);
		for (int pixel : pixels) if ((pixel >>> 24) != 0) return false;
		return true;
	}
	
	private static boolean columnIsEmpty(BufferedImage image, int column) {
		int[] pixels = new int[image.getHeight()];
		image.getRGB(column, 0, 1, pixels.length, pixels, 0, 1);
		for (int pixel : pixels) if ((pixel >>> 24) != 0) return false;
		return true;
	}
}
