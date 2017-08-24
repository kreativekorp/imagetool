package com.kreative.imagetool.gif;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.io.PrintStream;

public interface GIFBlock {
	public void read(DataInput in) throws IOException;
	public void write(DataOutput out) throws IOException;
	public void print(PrintStream out, String prefix);
}
