package com.kreative.imagetool.gci;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

public class GCIFile {
	public static boolean matchMagic(byte[] m) {
		short w = m[0]; w <<= 8; w |= m[1] & 0xFF;
		short h = m[2]; h <<= 8; h |= m[3] & 0xFF;
		return (w > 0 && h > 0 && m[4] == 0x10);
	}
	
	public int width = 0;
	public int height = 0;
	public int delay = 0;
	public final List<GCIBlock> blocks = new ArrayList<GCIBlock>();
	
	public void read(DataInput in) throws IOException {
		width = in.readShort(); if (width < 1) throw new IOException("bad image size");
		height = in.readShort(); if (height < 1) throw new IOException("bad image size");
		if (in.readByte() != 0x10) throw new IOException("bad magic number");
		delay = in.readUnsignedByte();
		int n = (delay == 0) ? 1 : in.readUnsignedShort();
		blocks.clear();
		while (n --> 0) {
			GCIBlock block = new GCIBlock();
			block.read(this, in);
			blocks.add(block);
		}
	}
	
	public void write(DataOutput out) throws IOException {
		out.writeShort(width);
		out.writeShort(height);
		out.writeByte(0x10);
		out.writeByte(delay);
		if (delay != 0) out.writeShort(blocks.size());
		for (GCIBlock block : blocks) block.write(this, out);
	}
	
	public void print(PrintStream out, String prefix) {
		out.println(prefix + "Header:");
		out.println(prefix + "\tWidth: " + width);
		out.println(prefix + "\tHeight: " + height);
		out.println(prefix + "\tDelay: " + delay);
		out.println(prefix + "\tFrames: " + blocks.size());
	}
}
