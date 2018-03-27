package com.kreative.imagetool.smf;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class SMFFillDirective implements SMFDirective {
	public int x;
	public int y;
	public int width;
	public int height;
	public int color;
	
	public SMFFillDirective() {
		this.x = 0;
		this.y = 0;
		this.width = 0;
		this.height = 0;
		this.color = 0;
	}
	
	public SMFFillDirective(int x, int y, int width, int height, int color) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.color = color;
	}
	
	public int read(byte opcode, DataInput in) throws IOException {
		int len;
		if (opcode < 96) {
			x = in.readUnsignedByte();
			y = in.readUnsignedByte();
			width = in.readUnsignedByte();
			height = in.readUnsignedByte();
			len = 7;
		} else {
			x = in.readUnsignedShort();
			y = in.readUnsignedShort();
			width = in.readUnsignedShort();
			height = in.readUnsignedShort();
			len = 11;
		}
		short rgb16 = in.readShort();
		int r = rgb16 & 0xF800; r = (r >> 8) | (r >> 13);
		int g = rgb16 & 0x07E0; g = (g >> 3) | (g >>  9);
		int b = rgb16 & 0x001F; b = (b << 3) | (b >>  2);
		color = 0xFF000000 | (r << 16) | (g << 8) | b;
		return len;
	}
	
	public int write(DataOutput out) throws IOException {
		int len;
		if (x < 256 && y < 256 && width < 256 && height < 256) {
			out.writeByte('F');
			out.writeByte((x > 0) ? x : 0);
			out.writeByte((y > 0) ? y : 0);
			out.writeByte((width > 0) ? width : 0);
			out.writeByte((height > 0) ? height : 0);
			len = 7;
		} else {
			out.writeByte('f');
			out.writeShort((x < 65535) ? x : 65535);
			out.writeShort((y < 65535) ? y : 65535);
			out.writeShort((width < 65535) ? width : 65535);
			out.writeShort((height < 65535) ? height : 65535);
			len = 11;
		}
		int r = (color >> 19) & 0x1F;
		int g = (color >> 10) & 0x3F;
		int b = (color >>  3) & 0x1F;
		out.writeShort((r << 11) | (g << 5) | b);
		return len;
	}
	
	public String toString() {
		StringBuffer s = new StringBuffer();
		s.append((x < 256 && y < 256 && width < 256 && height < 256) ? 'F' : 'f');
		s.append(' '); s.append(x);
		s.append(' '); s.append(y);
		s.append(' '); s.append(width);
		s.append(' '); s.append(height);
		s.append(' '); s.append(Integer.toHexString(color | 0xFF000000).substring(2).toUpperCase());
		return s.toString();
	}
	
	public int opLength() {
		return (x < 256 && y < 256 && width < 256 && height < 256) ? 7 : 11;
	}
	
	public int dataLength() {
		return 0;
	}
	
	public boolean isTerminator() {
		return false;
	}
}
