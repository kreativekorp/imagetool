package com.kreative.imagetool.smf;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public interface SMFDirective {
	public int read(byte opcode, DataInput in) throws IOException;
	public int write(DataOutput out) throws IOException;
	public String toString();
	public int opLength();
	public int dataLength();
	public boolean isTerminator();
}
