package com.kreative.imagetool.transform;

public class WebsafePalette extends ColorFilter {
	public int transform(int pixel) {
		int a = (pixel >> 24) & 0xFF;
		int r = (pixel >> 16) & 0xFF;
		int g = (pixel >>  8) & 0xFF;
		int b = (pixel >>  0) & 0xFF;
		a = (a < 128) ? 0 : 255;
		r = (r < 26) ? 0 : (r < 77) ? 51 : (r < 128) ? 102 : (r < 179) ? 153 : (r < 230) ? 204 : 255;
		g = (g < 26) ? 0 : (g < 77) ? 51 : (g < 128) ? 102 : (g < 179) ? 153 : (g < 230) ? 204 : 255;
		b = (b < 26) ? 0 : (b < 77) ? 51 : (b < 128) ? 102 : (b < 179) ? 153 : (b < 230) ? 204 : 255;
		return (a << 24) | (r << 16) | (g << 8) | b;
	}
}
