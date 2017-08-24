package com.kreative.imagetool.transform;

public class Colorize extends ColorFilter {
	private final int cr, cg, cb, cgray;
	private final int cr1, cg1, cb1, cgray1;
	
	public Colorize(int color) {
		this.cr = (color >> 16) & 0xFF;
		this.cg = (color >>  8) & 0xFF;
		this.cb = (color >>  0) & 0xFF;
		this.cgray = (30 * cr + 59 * cg + 11 * cb);
		this.cgray1 = 25500 - cgray;
		this.cr1 = 255 - cr;
		this.cg1 = 255 - cg;
		this.cb1 = 255 - cb;
		if (cgray < 1 || cgray1 < 1) throw new IllegalArgumentException();
	}
	
	public int transform(int pixel) {
		int a = (pixel >> 24) & 0xFF;
		int r = (pixel >> 16) & 0xFF;
		int g = (pixel >>  8) & 0xFF;
		int b = (pixel >>  0) & 0xFF;
		int gray = (30 * r + 59 * g + 11 * b);
		if (gray <= cgray) {
			r = cr * gray / cgray;
			g = cg * gray / cgray;
			b = cb * gray / cgray;
		} else {
			r = cr + cr1 * (gray - cgray) / cgray1;
			g = cg + cg1 * (gray - cgray) / cgray1;
			b = cb + cb1 * (gray - cgray) / cgray1;
		}
		return (a << 24) | (r << 16) | (g << 8) | b;
	}
}
