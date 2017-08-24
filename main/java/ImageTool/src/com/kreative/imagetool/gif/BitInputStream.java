package com.kreative.imagetool.gif;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

public class BitInputStream extends FilterInputStream {
	private int value;
	private int shift;
	private int markValue;
	private int markShift;
	
	public BitInputStream(InputStream in) {
		super(in);
		value = 0;
		shift = 0;
		markValue = 0;
		markShift = 0;
	}
	
	@Override
	public void mark(int readLimit) {
		super.mark(readLimit);
		markValue = value;
		markShift = shift;
	}
	
	@Override
	public void reset() throws IOException {
		super.reset();
		value = markValue;
		shift = markShift;
	}
	
	public int readBit() throws IOException {
		if (shift == 0) value = in.read();
		if (value < 0) return -1;
		int bit = ((value >> shift) & 1);
		shift = ((shift + 1) & 7);
		return bit;
	}
	
	public boolean readBooleanBit() throws IOException {
		return (readBit() > 0);
	}
	
	public int readIntegerBits(int width) throws IOException {
		int value = 0;
		for (int i = 0, m = 1; i < width; i++, m <<= 1) {
			int bit = readBit();
			if (bit < 0) return -1;
			if (bit > 0) value |= m;
		}
		return value;
	}
	
	public long readLongBits(int width) throws IOException {
		long value = 0;
		for (long i = 0, m = 1; i < width; i++, m <<= 1) {
			int bit = readBit();
			if (bit < 0) return -1;
			if (bit > 0) value |= m;
		}
		return value;
	}
	
	public void finish() {
		value = 0;
		shift = 0;
	}
}
