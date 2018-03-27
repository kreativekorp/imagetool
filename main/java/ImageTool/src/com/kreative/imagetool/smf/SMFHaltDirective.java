package com.kreative.imagetool.smf;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class SMFHaltDirective implements SMFDirective {
	public int read(byte opcode, DataInput in) throws IOException {
		return 1;
	}
	
	public int write(DataOutput out) throws IOException {
		out.writeByte('Z');
		return 1;
	}
	
	public String toString() {
		return "Z";
	}
	
	public int opLength() {
		return 1;
	}
	
	public int dataLength() {
		return 0;
	}
	
	public boolean isTerminator() {
		return true;
	}
}
