package com.kreative.imagetool.gif;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

public class LZWCompressor {
	private final BitOutputStream out;
	private final int minKeySize;
	private final int maxKeySize;
	private final int clearKey;
	private final int reservedKeys;
	private List<byte[]> dict;
	private int keySize;
	
	public LZWCompressor(OutputStream out) {
		this(out, 9, 12, 1);
	}
	
	public LZWCompressor(OutputStream out, int maxKeySize) {
		this(out, 9, maxKeySize, 1);
	}
	
	public LZWCompressor(OutputStream out, int minKeySize, int maxKeySize, int reservedKeys) {
		if (minKeySize < 1) throw new IllegalArgumentException("minKeySize must be at least 1");
		if (maxKeySize < minKeySize) throw new IllegalArgumentException("maxKeySize must be at least minKeySize");
		if (reservedKeys < 1) throw new IllegalArgumentException("reservedKeys must be at least 1");
		this.out = new BitOutputStream(out);
		this.minKeySize = minKeySize;
		this.maxKeySize = maxKeySize;
		this.clearKey = (1 << (minKeySize - 1));
		this.reservedKeys = reservedKeys;
		this.dict = newDictionary(clearKey, reservedKeys);
		this.keySize = minKeySize;
	}
	
	public void write(byte[] a) throws IOException {
		if (a == null) throw new IllegalArgumentException("a cannot be null");
		int offset = 0;
		while (offset < a.length) {
			int i = indexOfLongestPrefix(a, offset, dict);
			if (i < 0) throw new IOException("no key found in dictionary");
			out.writeBits(i, keySize);
			int l = dict.get(i).length;
			if (offset + l < a.length) {
				dict.add(arrayCopy(a, offset, l + 1));
				if (keySize < maxKeySize) {
					if (dict.size() > (1 << keySize)) {
						keySize++;
					}
				} else {
					if (dict.size() >= (1 << keySize)) {
						out.writeBits(clearKey, keySize);
						dict = newDictionary(clearKey, reservedKeys);
						keySize = minKeySize;
					}
				}
			}
			offset += l;
		}
	}
	
	public void writeReservedKey(int i, boolean clearDictionary) throws IOException {
		if (i < 0) throw new IllegalArgumentException("i must be at least 0");
		if (i >= reservedKeys) throw new IllegalArgumentException("i must be less than reservedKeys");
		out.writeBits(clearKey + i, keySize);
		if (i == 0 || clearDictionary) {
			dict = newDictionary(clearKey, reservedKeys);
			keySize = minKeySize;
		}
	}
	
	public void finish() throws IOException {
		out.finish();
		dict = newDictionary(clearKey, reservedKeys);
		keySize = minKeySize;
	}
	
	private static List<byte[]> newDictionary(int numValues, int numReserved) {
		List<byte[]> dict = new ArrayList<byte[]>();
		for (int i = 0; i < numValues; i++) {
			dict.add(new byte[]{(byte)i});
		}
		for (int i = 0; i < numReserved; i++) {
			dict.add(null);
		}
		return dict;
	}
	
	private static int indexOfLongestPrefix(byte[] a, int offset, List<byte[]> b) {
		int index = -1;
		int length = -1;
		for (int i = 0; i < b.size(); i++) {
			byte[] c = b.get(i);
			if (c != null && c.length > length && arrayStartsWith(a, offset, c)) {
				index = i;
				length = c.length;
			}
		}
		return index;
	}
	
	private static boolean arrayStartsWith(byte[] a, int offset, byte[] b) {
		for (int i = 0; i < b.length; i++, offset++) {
			if (offset >= a.length || a[offset] != b[i]) {
				return false;
			}
		}
		return true;
	}
	
	private static byte[] arrayCopy(byte[] a, int offset, int length) {
		byte[] b = new byte[length];
		for (int i = 0; i < length; i++, offset++) {
			if (offset < a.length) {
				b[i] = a[offset];
			}
		}
		return b;
	}
}
