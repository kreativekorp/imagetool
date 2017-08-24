package com.kreative.imagetool.transform;

public class Grayscale extends ColorFilter {
	public int transform(int pixel) {
		int a = (pixel >> 24) & 0xFF;
		int r = (pixel >> 16) & 0xFF;
		int g = (pixel >>  8) & 0xFF;
		int b = (pixel >>  0) & 0xFF;
		int gray = (30 * r + 59 * g + 11 * b) / 100;
		return (a << 24) | (gray << 16) | (gray << 8) | gray;
	}
}
