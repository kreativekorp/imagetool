package com.kreative.imagetool.gif;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class BitOutputStream extends FilterOutputStream {
	private int value;
	private int shift;
	
	public BitOutputStream(OutputStream out) {
		super(out);
		value = 0;
		shift = 0;
	}
	
	public void writeBit(boolean bit) throws IOException {
		if (bit) value |= (1 << shift);
		shift = ((shift + 1) & 7);
		if (shift == 0) {
			out.write(value);
			value = 0;
		}
	}
	
	public void writeBit(int bit) throws IOException {
		writeBit(bit != 0);
	}
	
	public void writeBits(int bits, int width) throws IOException {
		for (int i = 0, m = 1; i < width; i++, m <<= 1) {
			writeBit((bits & m) != 0);
		}
	}
	
	public void writeBits(long bits, int width) throws IOException {
		for (long i = 0, m = 1; i < width; i++, m <<= 1) {
			writeBit((bits & m) != 0);
		}
	}
	
	public void finish() throws IOException {
		if (shift != 0) {
			out.write(value);
			value = 0;
			shift = 0;
		}
	}
}
