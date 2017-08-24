package com.kreative.imagetool.animation;

import java.awt.image.BufferedImage;

public class ArrayOptions {
	public int sx = 0;
	public int sy = 0;
	public int cw = 0;
	public int ch = 0;
	public int dx = 0;
	public int dy = 0;
	public int cols = 0;
	public int rows = 0;
	public ArrayOrdering order = ArrayOrdering.LTR_TTB;
	public int width = 0;
	public int height = 0;
	
	public ArrayOptions forImage(int w, int h) {
		ArrayOptions o = new ArrayOptions();
		o.sx = (this.sx > 0) ? this.sx : 0;
		o.sy = (this.sy > 0) ? this.sy : 0;
		o.cw = (this.cw > 0) ? this.cw : Math.min(w - o.sx, h - o.sy);
		o.ch = (this.ch > 0) ? this.ch : Math.min(w - o.sx, h - o.sy);
		o.dx = (this.dx > 0) ? this.dx : o.cw;
		o.dy = (this.dy > 0) ? this.dy : o.ch;
		o.cols = (this.cols > 0) ? this.cols : ((w - o.sx + o.dx - o.cw) / o.dx);
		o.rows = (this.rows > 0) ? this.rows : ((h - o.sy + o.dy - o.ch) / o.dy);
		o.order = (this.order != null) ? this.order : ArrayOrdering.LTR_TTB;
		o.width = w;
		o.height = h;
		return o;
	}
	
	public ArrayOptions forFrames(int w, int h, int frames) {
		ArrayOptions o = new ArrayOptions();
		o.sx = (this.sx > 0) ? this.sx : 0;
		o.sy = (this.sy > 0) ? this.sy : 0;
		o.cw = w;
		o.ch = h;
		o.dx = (this.dx > 0) ? this.dx : o.cw;
		o.dy = (this.dy > 0) ? this.dy : o.ch;
		if (this.cols > 0 && this.rows > 0) {
			o.cols = this.cols;
			o.rows = this.rows;
		} else if (this.cols > 0) {
			o.cols = this.cols;
			o.rows = (frames + this.cols - 1) / this.cols;
		} else if (this.rows > 0) {
			o.cols = (frames + this.rows - 1) / this.rows;
			o.rows = this.rows;
		} else {
			o.cols = frames;
			o.rows = 1;
		}
		o.order = (this.order != null) ? this.order : ArrayOrdering.LTR_TTB;
		o.width = (this.width > 0) ? this.width : (o.sx + (o.cols - 1) * o.dx + o.cw);
		o.height = (this.height > 0) ? this.height : (o.sy + (o.rows - 1) * o.dy + o.ch);
		return o;
	}
	
	public BufferedImage getFrame(BufferedImage sheet, int index, BufferedImage frame, int[] buffer, int[] yx) {
		if (frame == null) frame = new BufferedImage(cw, ch, BufferedImage.TYPE_INT_ARGB);
		if (buffer == null) buffer = new int[cw * ch];
		yx = order.getYX(rows, cols, index, yx);
		sheet.getRGB(sx + dx * yx[1], sy + dy * yx[0], cw, ch, buffer, 0, cw);
		frame.setRGB(0, 0, cw, ch, buffer, 0, cw);
		return frame;
	}
	
	public BufferedImage setFrame(BufferedImage sheet, int index, BufferedImage frame, int[] buffer, int[] yx) {
		if (sheet == null) sheet = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		if (buffer == null) buffer = new int[cw * ch];
		yx = order.getYX(rows, cols, index, yx);
		frame.getRGB(0, 0, cw, ch, buffer, 0, cw);
		sheet.setRGB(sx + dx * yx[1], sy + dy * yx[0], cw, ch, buffer, 0, cw);
		return sheet;
	}
	
	public int parseForImage(String[] a, int i) {
		if (a == null || i < 0 || i >= a.length) {
			return -1;
		} else if (a[i].equals("-icb") || a[i].equalsIgnoreCase("--inputcellorigin")) {
			this.sx = Integer.parseInt(a[i + 1]);
			this.sy = Integer.parseInt(a[i + 2]);
			return i + 3;
		} else if (a[i].equals("-ics") || a[i].equalsIgnoreCase("--inputcellsize")) {
			this.cw = Integer.parseInt(a[i + 1]);
			this.ch = Integer.parseInt(a[i + 2]);
			return i + 3;
		} else if (a[i].equals("-icd") || a[i].equalsIgnoreCase("--inputcelldelta")) {
			this.dx = Integer.parseInt(a[i + 1]);
			this.dy = Integer.parseInt(a[i + 2]);
			return i + 3;
		} else if (a[i].equals("-icc") || a[i].equalsIgnoreCase("--inputcellcount")) {
			this.cols = Integer.parseInt(a[i + 1]);
			this.rows = Integer.parseInt(a[i + 2]);
			return i + 3;
		} else if (a[i].equals("-ico") || a[i].equalsIgnoreCase("--inputcellorder")) {
			this.order = ArrayOrdering.fromString(a[i + 1]);
			if (this.order == null) throw new IllegalArgumentException();
			return i + 2;
		} else {
			return -1;
		}
	}
	
	public int parseForFrames(String[] a, int i) {
		if (a == null || i < 0 || i >= a.length) {
			return -1;
		} else if (a[i].equals("-ocb") || a[i].equalsIgnoreCase("--outputcellorigin")) {
			this.sx = Integer.parseInt(a[i + 1]);
			this.sy = Integer.parseInt(a[i + 2]);
			return i + 3;
		} else if (a[i].equals("-ocd") || a[i].equalsIgnoreCase("--outputcelldelta")) {
			this.dx = Integer.parseInt(a[i + 1]);
			this.dy = Integer.parseInt(a[i + 2]);
			return i + 3;
		} else if (a[i].equals("-occ") || a[i].equalsIgnoreCase("--outputcellcount")) {
			this.cols = Integer.parseInt(a[i + 1]);
			this.rows = Integer.parseInt(a[i + 2]);
			return i + 3;
		} else if (a[i].equals("-oco") || a[i].equalsIgnoreCase("--outputcellorder")) {
			this.order = ArrayOrdering.fromString(a[i + 1]);
			if (this.order == null) throw new IllegalArgumentException();
			return i + 2;
		} else if (a[i].equals("-ois") || a[i].equalsIgnoreCase("--outputimagesize")) {
			this.width = Integer.parseInt(a[i + 1]);
			this.height = Integer.parseInt(a[i + 2]);
			return i + 3;
		} else {
			return -1;
		}
	}
	
	public static void printHelpForImage() {
		System.out.println("-icb, --inputcellorigin <x> <y>");
		System.out.println("    Determines the position of the first frame within a static image.");
		System.out.println("-ics, --inputcellsize <width> <height>");
		System.out.println("    Determines the width and height of each frame within a static image.");
		System.out.println("-icd, --inputcelldelta <dx> <dy>");
		System.out.println("    Determines the X and Y offset between each frame within a static image.");
		System.out.println("-icc, --inputcellcount <columns> <rows>");
		System.out.println("    Determines the number of frames horizontally and vertically.");
		System.out.println("-ico, --inputcellorder <order>");
		System.out.println("    Determines the ordering of frames (LTR-TTB, TTB-LTR, etc.).");
	}
	
	public static void printHelpForFrames() {
		System.out.println("-ocb, --outputcellorigin <x> <y>");
		System.out.println("    Determines the position of the first frame within a static image.");
		System.out.println("-ocd, --outputcelldelta <dx> <dy>");
		System.out.println("    Determines the X and Y offset between each frame within a static image.");
		System.out.println("-occ, --outputcellcount <columns> <rows>");
		System.out.println("    Determines the number of frames horizontally and vertically.");
		System.out.println("-oco, --outputcellorder <order>");
		System.out.println("    Determines the ordering of frames (LTR-TTB, TTB-LTR, etc.).");
		System.out.println("-ois, --outputimagesize <width> <height>");
		System.out.println("    Determines the total width and height of the static image.");
	}
}
