package com.kreative.imagetool.smf;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class SMFSeekDirective implements SMFDirective {
	public int sector;
	
	public SMFSeekDirective() {
		this.sector = 0;
	}
	
	public SMFSeekDirective(int sector) {
		this.sector = sector;
	}
	
	public int read(byte opcode, DataInput in) throws IOException {
		if (opcode < 96) {
			sector = in.readUnsignedByte();
			return 2;
		} else {
			sector = in.readUnsignedShort();
			return 3;
		}
	}
	
	public int write(DataOutput out) throws IOException {
		if (sector < 256) {
			out.writeByte('S');
			out.writeByte((sector > 0) ? sector : 0);
			return 2;
		} else {
			out.writeByte('s');
			out.writeShort((sector < 65535) ? sector : 65535);
			return 3;
		}
	}
	
	public String toString() {
		StringBuffer s = new StringBuffer();
		s.append((sector < 256) ? 'S' : 's');
		s.append(' '); s.append(sector);
		return s.toString();
	}
	
	public int opLength() {
		return (sector < 256) ? 2 : 3;
	}
	
	public int dataLength() {
		return 0;
	}
	
	public boolean isTerminator() {
		return true;
	}
}
