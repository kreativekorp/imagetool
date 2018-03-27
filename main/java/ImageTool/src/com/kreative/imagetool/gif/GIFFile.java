package com.kreative.imagetool.gif;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

public class GIFFile {
	public static boolean matchMagic(byte[] m) {
		return (
			m[0] == 'G' && m[1] == 'I' && m[2] == 'F' &&
			m[3] == '8' && (m[4] == '7' || m[4] == '9') && m[5] == 'a'
		);
	}
	
	public boolean version89a = true;
	public int width = 0;
	public int height = 0;
	public int depth = 8;
	public int[] palette = null;
	public boolean paletteSorted = false;
	public int backgroundIndex = 0;
	public double pixelAspectRatio = 0;
	public final List<GIFBlock> blocks = new ArrayList<GIFBlock>();
	
	public void read(DataInput in) throws IOException {
		// Header
		if (in.readInt() != 0x47494638) {
			throw new IOException("bad magic number");
		}
		switch (in.readShort()) {
			case 0x3761: version89a = false; break;
			case 0x3961: version89a = true; break;
			default: throw new IOException("bad magic number");
		}
		
		// Logical Screen Descriptor
		width = Short.reverseBytes(in.readShort()) & 0xFFFF;
		height = Short.reverseBytes(in.readShort()) & 0xFFFF;
		int flags = in.readUnsignedByte();
		depth = (((flags & 0x70) >> 4) + 1);
		palette = ((flags & 0x80) != 0) ? new int[exp(flags & 0x07)] : null;
		paletteSorted = ((flags & 0x08) != 0);
		backgroundIndex = in.readUnsignedByte();
		int par = in.readUnsignedByte();
		pixelAspectRatio = ((par == 0) ? 0 : ((par + 15) / 64.0));
		
		// Global Color Table
		if (palette != null) {
			for (int i = 0; i < palette.length; i++) {
				int r = in.readUnsignedByte();
				int g = in.readUnsignedByte();
				int b = in.readUnsignedByte();
				palette[i] = 0xFF000000 | (r << 16) | (g << 8) | b;
			}
		}
		
		// Blocks
		blocks.clear();
		while (true) {
			switch (in.readUnsignedByte()) {
				case 0x2C:
					// Image Descriptor
					GIFImageDescriptor idBlock = new GIFImageDescriptor();
					idBlock.read(in);
					blocks.add(idBlock);
					break;
				case 0x21:
					// Extension
					switch (in.readUnsignedByte()) {
						case 0xF9:
							// Graphic Control Extension
							GIFGraphicControlExtension gceBlock = new GIFGraphicControlExtension();
							gceBlock.read(in);
							blocks.add(gceBlock);
							break;
						case 0xFE:
							// Comment Extension
							GIFCommentExtension ceBlock = new GIFCommentExtension();
							ceBlock.read(in);
							blocks.add(ceBlock);
							break;
						case 0x01:
							// Plain Text Extension
							GIFPlainTextExtension pteBlock = new GIFPlainTextExtension();
							pteBlock.read(in);
							blocks.add(pteBlock);
							break;
						case 0xFF:
							// Application Extension
							GIFApplicationExtension aeBlock = new GIFApplicationExtension();
							aeBlock.read(in);
							blocks.add(aeBlock);
							break;
						default:
							// Unknown Extension
							throw new IOException("bad extension identifier");
					}
					break;
				case 0x3B:
					// Trailer
					return;
				default:
					// Unknown Block
					throw new IOException("bad block identifier");
			}
		}
	}
	
	public void write(DataOutput out) throws IOException {
		// Header
		out.writeInt(0x47494638);
		out.writeShort(version89a ? 0x3961 : 0x3761);
		
		// Logical Screen Descriptor
		out.writeShort(Short.reverseBytes((short)width));
		out.writeShort(Short.reverseBytes((short)height));
		int flags = (((depth - 1) << 4) & 0x70);
		if (palette != null) flags |= (0x80 | log(palette.length));
		if (paletteSorted) flags |= 0x08;
		out.writeByte(flags);
		out.writeByte(backgroundIndex);
		int par = (int)Math.round(pixelAspectRatio * 64) - 15;
		out.writeByte((par < 0) ? 0 : (par > 255) ? 255 : par);
		
		// Global Color Table
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
		
		// Blocks
		for (GIFBlock block : blocks) {
			if (block instanceof GIFImageDescriptor) {
				// Image Descriptor
				out.writeByte(0x2C);
				block.write(out);
			} else {
				// Extension
				out.writeByte(0x21);
				if (block instanceof GIFGraphicControlExtension) {
					// Graphic Control Extension
					out.writeByte(0xF9);
					block.write(out);
				} else if (block instanceof GIFCommentExtension) {
					// Comment Extension
					out.writeByte(0xFE);
					block.write(out);
				} else if (block instanceof GIFPlainTextExtension) {
					// Plain Text Extension
					out.writeByte(0x01);
					block.write(out);
				} else if (block instanceof GIFApplicationExtension) {
					// Application Extension
					out.writeByte(0xFF);
					block.write(out);
				} else {
					throw new IOException("bad extension class");
				}
			}
		}
		
		// Trailer
		out.writeByte(0x3B);
	}
	
	public void print(PrintStream out, String prefix) {
		out.println(prefix + "Header:");
		out.println(prefix + "\tVersion: " + (version89a ? "89a" : "87a"));
		out.println(prefix + "\tWidth: " + width);
		out.println(prefix + "\tHeight: " + height);
		out.println(prefix + "\tDepth: " + depth);
		out.println(prefix + "\tPalette Present: " + ((palette == null) ? "No" : "Yes"));
		out.println(prefix + "\tPalette Size: " + ((palette == null) ? 0 : palette.length));
		out.println(prefix + "\tPalette Sorted: " + (paletteSorted ? "Yes" : "No"));
		out.println(prefix + "\tBackground Index: " + backgroundIndex);
		out.println(prefix + "\tPixel Aspect Ratio: " + pixelAspectRatio);
		for (GIFBlock block : blocks) {
			if (block instanceof GIFImageDescriptor) out.println(prefix + "Image Descriptor:");
			if (block instanceof GIFGraphicControlExtension) out.println(prefix + "Graphic Control Extension:");
			if (block instanceof GIFCommentExtension) out.println(prefix + "Comment Extension:");
			if (block instanceof GIFPlainTextExtension) out.println(prefix + "Plain Text Extension:");
			if (block instanceof GIFApplicationExtension) out.println(prefix + "Application Extension:");
			block.print(out, prefix + "\t");
		}
	}
	
	public int getRepeatCount() {
		for (GIFBlock block : blocks) {
			if (block instanceof GIFApplicationExtension) {
				GIFApplicationExtension gae = (GIFApplicationExtension)block;
				if (gae.isNAB()) return gae.getRepeatCount();
			}
		}
		return 1;
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
