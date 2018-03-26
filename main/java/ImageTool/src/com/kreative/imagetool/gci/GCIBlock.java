package com.kreative.imagetool.gci;

import java.awt.image.BufferedImage;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class GCIBlock {
	public short[] data = new short[0];
	
	public void read(GCIFile gci, DataInput in) throws IOException {
		int n = gci.width * gci.height;
		data = new short[(n > 0) ? n : 0];
		for (int i = 0; i < n; i++) data[i] = in.readShort();
	}
	
	public void write(GCIFile gci, DataOutput out) throws IOException {
		int n = gci.width * gci.height;
		for (int i = 0; i < data.length && i < n; i++) out.writeShort(data[i]);
		for (int i = data.length; i < n; i++) out.writeShort(0);
	}
	
	public int[] getRGB() {
		int[] rgb = new int[data.length];
		for (int i = 0; i < data.length; i++) {
			int r = data[i] & 0xF800; r = (r >> 8) | (r >> 13);
			int g = data[i] & 0x07E0; g = (g >> 3) | (g >>  9);
			int b = data[i] & 0x001F; b = (b << 3) | (b >>  2);
			rgb[i] = 0xFF000000 | (r << 16) | (g << 8) | b;
		}
		return rgb;
	}
	
	public void setRGB(int[] rgb) {
		data = new short[rgb.length];
		for (int i = 0; i < rgb.length; i++) {
			int r = (rgb[i] >> 19) & 0x1F;
			int g = (rgb[i] >> 10) & 0x3F;
			int b = (rgb[i] >>  3) & 0x1F;
			data[i] = (short)((r << 11) | (g << 5) | b);
		}
	}
	
	public BufferedImage getImage(GCIFile gci) {
		BufferedImage image = new BufferedImage(gci.width, gci.height, BufferedImage.TYPE_INT_ARGB);
		image.setRGB(0, 0, gci.width, gci.height, getRGB(), 0, gci.width);
		return image;
	}
	
	public void setImage(GCIFile gci, BufferedImage image) {
		int[] rgb = new int[gci.width * gci.height];
		image.getRGB(0, 0, gci.width, gci.height, rgb, 0, gci.width);
		setRGB(rgb);
	}
}
