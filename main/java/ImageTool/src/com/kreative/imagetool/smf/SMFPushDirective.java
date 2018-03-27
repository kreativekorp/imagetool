package com.kreative.imagetool.smf;

import java.awt.image.BufferedImage;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Arrays;

public class SMFPushDirective implements SMFDirective {
	public int x;
	public int y;
	public int width;
	public int height;
	public short[] data;
	
	public SMFPushDirective() {
		this.x = 0;
		this.y = 0;
		this.width = 0;
		this.height = 0;
		this.data = new short[0];
	}
	
	public SMFPushDirective(int x, int y, int width, int height, short[] data) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.data = data;
	}
	
	public int read(byte opcode, DataInput in) throws IOException {
		int len;
		if (opcode < 96) {
			x = in.readUnsignedByte();
			y = in.readUnsignedByte();
			width = in.readUnsignedByte();
			height = in.readUnsignedByte();
			len = 5;
		} else {
			x = in.readUnsignedShort();
			y = in.readUnsignedShort();
			width = in.readUnsignedShort();
			height = in.readUnsignedShort();
			len = 9;
		}
		data = new short[width * height];
		for (int i = 0; i < data.length; i++) {
			data[i] = in.readShort();
			len += 2;
		}
		return len;
	}
	
	public int write(DataOutput out) throws IOException {
		int len;
		if (x < 256 && y < 256 && width < 256 && height < 256) {
			out.writeByte('P');
			out.writeByte((x > 0) ? x : 0);
			out.writeByte((y > 0) ? y : 0);
			out.writeByte((width > 0) ? width : 0);
			out.writeByte((height > 0) ? height : 0);
			len = 5;
		} else {
			out.writeByte('p');
			out.writeShort((x < 65535) ? x : 65535);
			out.writeShort((y < 65535) ? y : 65535);
			out.writeShort((width < 65535) ? width : 65535);
			out.writeShort((height < 65535) ? height : 65535);
			len = 9;
		}
		int w = (width < 0) ? 0 : (width > 65535) ? 65535 : width;
		int h = (height < 0) ? 0 : (height > 65535) ? 65535 : height;
		int n = w * h;
		for (int i = 0; i < data.length && i < n; i++) {
			out.writeShort(data[i]);
			len += 2;
		}
		for (int i = data.length; i < n; i++) {
			out.writeShort(0);
			len += 2;
		}
		return len;
	}
	
	public String toString() {
		StringBuffer s = new StringBuffer();
		s.append((x < 256 && y < 256 && width < 256 && height < 256) ? 'P' : 'p');
		s.append(' '); s.append(x);
		s.append(' '); s.append(y);
		s.append(' '); s.append(width);
		s.append(' '); s.append(height);
		s.append(' '); s.append('<'); s.append(data.length); s.append('>');
		return s.toString();
	}
	
	public int opLength() {
		return (x < 256 && y < 256 && width < 256 && height < 256) ? 5 : 9;
	}
	
	public int dataLength() {
		return data.length * 2;
	}
	
	public boolean isTerminator() {
		return false;
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
	
	public BufferedImage getImage() {
		BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		image.setRGB(0, 0, width, height, getRGB(), 0, width);
		return image;
	}
	
	public void setImage(BufferedImage image) {
		width = image.getWidth();
		height = image.getHeight();
		int[] rgb = new int[width * height];
		image.getRGB(0, 0, width, height, rgb, 0, width);
		setRGB(rgb);
	}
	
	public void setImageDiff(BufferedImage i1, BufferedImage i2) {
		x = 0;
		y = 0;
		width = i2.getWidth();
		height = i2.getHeight();
		
		int[] rgb1 = new int[width];
		int[] rgb2 = new int[width];
		while (width > 0 && height > 0 && y < i1.getHeight()) {
			i1.getRGB(x, y, width, 1, rgb1, 0, width);
			i2.getRGB(x, y, width, 1, rgb2, 0, width);
			if (!Arrays.equals(rgb1, rgb2)) break;
			y++; height--;
		}
		while (width > 0 && height > 0 && y + height - 1 < i1.getHeight()) {
			i1.getRGB(x, y + height - 1, width, 1, rgb1, 0, width);
			i2.getRGB(x, y + height - 1, width, 1, rgb2, 0, width);
			if (!Arrays.equals(rgb1, rgb2)) break;
			height--;
		}
		
		rgb1 = new int[height];
		rgb2 = new int[height];
		while (width > 0 && height > 0 && x < i1.getWidth()) {
			i1.getRGB(x, y, 1, height, rgb1, 0, 1);
			i2.getRGB(x, y, 1, height, rgb2, 0, 1);
			if (!Arrays.equals(rgb1, rgb2)) break;
			x++; width--;
		}
		while (width > 0 && height > 0 && x + width - 1 < i1.getWidth()) {
			i1.getRGB(x + width - 1, y, 1, height, rgb1, 0, 1);
			i2.getRGB(x + width - 1, y, 1, height, rgb2, 0, 1);
			if (!Arrays.equals(rgb1, rgb2)) break;
			width--;
		}
		
		int[] rgb = new int[width * height];
		if (rgb.length > 0) i2.getRGB(x, y, width, height, rgb, 0, width);
		setRGB(rgb);
	}
}
