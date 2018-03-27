package com.kreative.imagetool.smf;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class SMFWaitDirective implements SMFDirective {
	public int delay;
	
	public SMFWaitDirective() {
		this.delay = 0;
	}
	
	public SMFWaitDirective(int delay) {
		this.delay = delay;
	}
	
	public int read(byte opcode, DataInput in) throws IOException {
		if (opcode < 96) {
			delay = in.readUnsignedByte();
			return 2;
		} else {
			delay = in.readUnsignedShort();
			return 3;
		}
	}
	
	public int write(DataOutput out) throws IOException {
		if (delay < 256) {
			out.writeByte('W');
			out.writeByte((delay > 0) ? delay : 0);
			return 2;
		} else {
			out.writeByte('w');
			out.writeShort((delay < 65535) ? delay : 65535);
			return 3;
		}
	}
	
	public String toString() {
		StringBuffer s = new StringBuffer();
		s.append((delay < 256) ? 'W' : 'w');
		s.append(' '); s.append(delay);
		return s.toString();
	}
	
	public int opLength() {
		return (delay < 256) ? 2 : 3;
	}
	
	public int dataLength() {
		return 0;
	}
	
	public boolean isTerminator() {
		return false;
	}
}
