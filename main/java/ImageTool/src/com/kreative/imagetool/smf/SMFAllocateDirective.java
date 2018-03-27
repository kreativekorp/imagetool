package com.kreative.imagetool.smf;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class SMFAllocateDirective implements SMFDirective {
	public int width;
	public int height;
	
	public SMFAllocateDirective() {
		this.width = 0;
		this.height = 0;
	}
	
	public SMFAllocateDirective(int width, int height) {
		this.width = width;
		this.height = height;
	}
	
	public int read(byte opcode, DataInput in) throws IOException {
		if (opcode < 96) {
			width = in.readUnsignedByte();
			height = in.readUnsignedByte();
			return 3;
		} else {
			width = in.readUnsignedShort();
			height = in.readUnsignedShort();
			return 5;
		}
	}
	
	public int write(DataOutput out) throws IOException {
		if (width < 256 && height < 256) {
			out.writeByte('A');
			out.writeByte((width > 0) ? width : 0);
			out.writeByte((height > 0) ? height : 0);
			return 3;
		} else {
			out.writeByte('a');
			out.writeShort((width < 65535) ? width : 65535);
			out.writeShort((height < 65535) ? height : 65535);
			return 5;
		}
	}
	
	public String toString() {
		StringBuffer s = new StringBuffer();
		s.append((width < 256 && height < 256) ? 'A' : 'a');
		s.append(' '); s.append(width);
		s.append(' '); s.append(height);
		return s.toString();
	}
	
	public int opLength() {
		return (width < 256 && height < 256) ? 3 : 5;
	}
	
	public int dataLength() {
		return 0;
	}
	
	public boolean isTerminator() {
		return false;
	}
}
