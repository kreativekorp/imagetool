package com.kreative.imagetool.transform;

public class InvertGrays extends ColorFilter {
	private final int threshold;
	
	public InvertGrays(int th) {
		if (th < 0 || th > 256) throw new IllegalArgumentException();
		this.threshold = th;
	}
	
	public int transform(int pixel) {
		int r = (pixel >> 16) & 0xFF;
		int g = (pixel >>  8) & 0xFF;
		int b = (pixel >>  0) & 0xFF;
		boolean gray = (
			Math.abs(r-g) < threshold &&
			Math.abs(g-b) < threshold &&
			Math.abs(r-b) < threshold
		);
		return gray ? (pixel ^ 0xFFFFFF) : pixel;
	}
}
