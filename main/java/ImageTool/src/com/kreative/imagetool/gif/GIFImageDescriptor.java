package com.kreative.imagetool.gif;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.io.PrintStream;

public class GIFImageDescriptor implements GIFBlock {
	public static int bestMinKeySize(int paletteLength) {
		if (paletteLength <=   4) return 2;
		if (paletteLength <=   8) return 3;
		if (paletteLength <=  16) return 4;
		if (paletteLength <=  32) return 5;
		if (paletteLength <=  64) return 6;
		if (paletteLength <= 128) return 7;
		return 8;
	}
	
	public int left = 0;
	public int top = 0;
	public int width = 0;
	public int height = 0;
	public int[] palette = null;
	public boolean paletteSorted = false;
	public boolean interlaced = false;
	public int minKeySize = 8;
	public byte[] compressedData = new byte[0];
	
	public void read(DataInput in) throws IOException {
		// Image Descriptor
		left = Short.reverseBytes(in.readShort()) & 0xFFFF;
		top = Short.reverseBytes(in.readShort()) & 0xFFFF;
		width = Short.reverseBytes(in.readShort()) & 0xFFFF;
		height = Short.reverseBytes(in.readShort()) & 0xFFFF;
		int flags = in.readUnsignedByte();
		palette = ((flags & 0x80) != 0) ? new int[exp(flags & 0x07)] : null;
		paletteSorted = ((flags & 0x20) != 0);
		interlaced = ((flags & 0x40) != 0);
		
		// Local Color Table
		if (palette != null) {
			for (int i = 0; i < palette.length; i++) {
				int r = in.readUnsignedByte();
				int g = in.readUnsignedByte();
				int b = in.readUnsignedByte();
				palette[i] = 0xFF000000 | (r << 16) | (g << 8) | b;
			}
		}
		
		// Table Based Image Data
		minKeySize = in.readUnsignedByte();
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		byte[] buf = new byte[256];
		while (true) {
			int len = in.readUnsignedByte();
			if (len == 0) break;
			in.readFully(buf, 0, len);
			out.write(buf, 0, len);
		}
		compressedData = out.toByteArray();
	}
	
	public void write(DataOutput out) throws IOException {
		// Image Descriptor
		out.writeShort(Short.reverseBytes((short)left));
		out.writeShort(Short.reverseBytes((short)top));
		out.writeShort(Short.reverseBytes((short)width));
		out.writeShort(Short.reverseBytes((short)height));
		int flags = (palette != null) ? (0x80 | log(palette.length)) : 0;
		if (paletteSorted) flags |= 0x20;
		if (interlaced) flags |= 0x40;
		out.writeByte(flags);
		
		// Local Color Table
		if (palette != null) {
			for (int i = 0, n = exp(log(palette.length)); i < n; i++) {
				if (i < palette.length) {
					out.writeByte(palette[i] >> 16);
					out.writeByte(palette[i] >> 8);
					out.writeByte(palette[i]);
				} else {
					out.writeByte(0);
					out.writeByte(0);
					out.writeByte(0);
				}
			}
		}
		
		// Table Based Image Data
		out.writeByte(minKeySize);
		if (compressedData != null) {
			int offset = 0;
			int length = compressedData.length;
			while (length > 0) {
				int len = (length < 255) ? length : 255;
				out.writeByte(len);
				out.write(compressedData, offset, len);
				offset += len;
				length -= len;
			}
		}
		out.writeByte(0);
	}
	
	public void print(PrintStream out, String prefix) {
		out.println(prefix + "Left: " + left);
		out.println(prefix + "Top: " + top);
		out.println(prefix + "Width: " + width);
		out.println(prefix + "Height: " + height);
		out.println(prefix + "Palette Present: " + ((palette == null) ? "No" : "Yes"));
		out.println(prefix + "Palette Size: " + ((palette == null) ? 0 : palette.length));
		out.println(prefix + "Palette Sorted: " + (paletteSorted ? "Yes" : "No"));
		out.println(prefix + "Interlaced: " + (interlaced ? "Yes" : "No"));
		out.println(prefix + "Min Key Size: " + minKeySize);
		out.println(prefix + "Compressed Data Length: " + ((compressedData == null) ? 0 : compressedData.length));
	}
	
	public byte[] getUncompressedData() throws IOException {
		if (compressedData == null || compressedData.length == 0) {
			return new byte[0];
		} else {
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			ByteArrayInputStream in = new ByteArrayInputStream(compressedData);
			LZWDecompressor lzw = new LZWDecompressor(in, out, minKeySize + 1, 2);
			lzw.setReservedKeyBehavior(1, LZWDecompressor.EXIT_LOOP);
			lzw.read();
			lzw.finish();
			in.close();
			out.close();
			return out.toByteArray();
		}
	}
	
	public void setUncompressedData(byte[] data) throws IOException {
		if (data == null || data.length == 0) {
			compressedData = new byte[0];
		} else {
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			LZWCompressor lzw = new LZWCompressor(out, minKeySize + 1, 12, 2);
			lzw.writeReservedKey(0, true);
			lzw.write(data);
			lzw.writeReservedKey(1, true);
			lzw.finish();
			out.close();
			compressedData = out.toByteArray();
		}
	}
	
	public int[] getRGB(GIFFile gif, GIFGraphicControlExtension gce) throws IOException {
		int[] palette =
			(this.palette != null) ? this.palette :
			(gif.palette != null) ? gif.palette :
			new int[0];
		int transparencyIndex =
			(gce != null && gce.transparency) ?
			gce.transparencyIndex : -1;
		
		byte[] data = getUncompressedData();
		int[] pixels = new int[width * height];
		for (int i = 0; i < data.length && i < pixels.length; i++) {
			int index = data[i] & 0xFF;
			if (index < palette.length && index != transparencyIndex) {
				pixels[i] = palette[index];
			} else {
				pixels[i] = 0;
			}
		}
		return pixels;
	}
	
	public void setRGB(GIFFile gif, GIFGraphicControlExtension gce, int[] pixels) throws IOException {
		int[] palette =
			(this.palette != null) ? this.palette :
			(gif.palette != null) ? gif.palette :
			new int[0];
		int transparencyIndex =
			(gce != null && gce.transparency) ?
			gce.transparencyIndex : -1;
		
		byte[] data = new byte[pixels.length];
		for (int i = 0; i < pixels.length; i++) {
			if (pixels[i] < 0 || transparencyIndex < 0) {
				data[i] = (byte)closestIndexOf(pixels[i], palette, transparencyIndex);
			} else {
				data[i] = (byte)transparencyIndex;
			}
		}
		setUncompressedData(data);
	}
	
	public BufferedImage getImage(GIFFile gif, GIFGraphicControlExtension gce) throws IOException {
		BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		image.setRGB(0, 0, width, height, getRGB(gif, gce), 0, width);
		return image;
	}
	
	public void setImage(GIFFile gif, GIFGraphicControlExtension gce, BufferedImage image) throws IOException {
		width = image.getWidth();
		height = image.getHeight();
		int[] pixels = new int[width * height];
		image.getRGB(0, 0, width, height, pixels, 0, width);
		setRGB(gif, gce, pixels);
	}
	
	public void fixBounds(GIFFile gif) throws IOException {
		if (left < 0 || top < 0 || left + width > gif.width || top + height > gif.height) {
			int nx = Math.max(left, 0);
			int ny = Math.max(top, 0);
			int nw = Math.min(left + width, gif.width) - nx;
			int nh = Math.min(top + height, gif.height) - ny;
			byte[] ndata = new byte[nw * nh];
			byte[] data = getUncompressedData();
			for (int dy = 0, sy = (ny - top) * width; dy < ndata.length; dy += nw, sy += width) {
				for (int dx = 0, sx = nx - left; dx < nw; dx++, sx++) {
					if (sy + sx < data.length) {
						ndata[dy + dx] = data[sy + sx];
					}
				}
			}
			setUncompressedData(ndata);
			left = nx;
			top = ny;
			width = nw;
			height = nh;
		}
	}
	
	public static int indexOf(int color, int[] colors, int transparencyIndex) {
		for (int i = 0; i < colors.length; i++) {
			if (i == transparencyIndex) continue;
			if (colors[i] == color) return i;
		}
		return -1;
	}
	
	public static int closestIndexOf(int color, int[] colors, int transparencyIndex) {
		int ca = (color >> 24) & 0xFF;
		int cr = (color >> 16) & 0xFF;
		int cg = (color >>  8) & 0xFF;
		int cb = (color >>  0) & 0xFF;
		int index = -1;
		int distance = 262144;
		for (int i = 0; i < colors.length; i++) {
			if (i == transparencyIndex) continue;
			int c = colors[i];
			int da = ((c >> 24) & 0xFF) - ca;
			int dr = ((c >> 16) & 0xFF) - cr;
			int dg = ((c >>  8) & 0xFF) - cg;
			int db = ((c >>  0) & 0xFF) - cb;
			int d = da * da + dr * dr + dg * dg + db * db;
			if (d < distance) {
				index = i;
				distance = d;
			}
		}
		return index;
	}
	
	private static int exp(int size) {
		return (1 << (size + 1));
	}
	
	private static int log(int size) {
		if (size <=   2) return 0;
		if (size <=   4) return 1;
		if (size <=   8) return 2;
		if (size <=  16) return 3;
		if (size <=  32) return 4;
		if (size <=  64) return 5;
		if (size <= 128) return 6;
		return 7;
	}
}
