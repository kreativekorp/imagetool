package com.kreative.imagetool.gif;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

public class LZWDecompressor {
	public static final int EXIT_LOOP = 0x01;
	public static final int CLEAR_DICTIONARY = 0x02;
	public static final int CALL_HANDLER = 0x04;
	
	private final BitInputStream in;
	private final OutputStream out;
	private final int minKeySize;
	private final int clearKey;
	private final int reservedKeys;
	private final int[] reservedKeyBehavior;
	private LZWReservedKeyHandler reservedKeyHandler;
	private List<byte[]> dict;
	private int keySize;
	private byte[] prevWord;
	
	public LZWDecompressor(InputStream in, OutputStream out) {
		this(in, out, 9, 1);
	}
	
	public LZWDecompressor(InputStream in, OutputStream out, int minKeySize, int reservedKeys) {
		if (minKeySize < 1) throw new IllegalArgumentException("minKeySize must be at least 1");
		if (reservedKeys < 1) throw new IllegalArgumentException("reservedKeys must be at least 1");
		this.in = new BitInputStream(in);
		this.out = out;
		this.minKeySize = minKeySize;
		this.clearKey = (1 << (minKeySize - 1));
		this.reservedKeys = reservedKeys;
		this.reservedKeyBehavior = new int[reservedKeys];
		this.reservedKeyHandler = null;
		this.dict = newDictionary(clearKey, reservedKeys);
		this.keySize = minKeySize;
		this.prevWord = null;
	}
	
	public int read() throws IOException {
		int bytesRead = 0;
		while (true) {
			int i = in.readIntegerBits(keySize);
			if (i < 0) {
				return bytesRead;
			} else if (i == clearKey) {
				dict = newDictionary(clearKey, reservedKeys);
				keySize = minKeySize;
				prevWord = null;
			} else if (i > clearKey && i < clearKey + reservedKeys) {
				int behavior = reservedKeyBehavior[i - clearKey];
				if ((behavior & CALL_HANDLER) != 0) {
					if (reservedKeyHandler != null) {
						reservedKeyHandler.reservedKeyFound(i - clearKey);
					}
				}
				if ((behavior & CLEAR_DICTIONARY) != 0) {
					dict = newDictionary(clearKey, reservedKeys);
					keySize = minKeySize;
					prevWord = null;
				}
				if ((behavior & EXIT_LOOP) != 0) {
					return bytesRead;
				}
			} else {
				if (prevWord != null) {
					if (i < dict.size()) {
						dict.add(arrayAppend(prevWord, dict.get(i)[0]));
					} else {
						dict.add(arrayAppend(prevWord, prevWord[0]));
					}
					if (dict.size() >= (1 << keySize)) {
						keySize++;
					}
				}
				if (i < dict.size()) {
					prevWord = dict.get(i);
					out.write(prevWord);
					bytesRead += prevWord.length;
				}
			}
		}
	}
	
	public int getReservedKeyBehavior(int i) {
		if (i < 1) throw new IllegalArgumentException("i must be at least 1");
		if (i >= reservedKeys) throw new IllegalArgumentException("i must be less than reservedKeys");
		return reservedKeyBehavior[i];
	}
	
	public void setReservedKeyBehavior(int i, int behavior) {
		if (i < 1) throw new IllegalArgumentException("i must be at least 1");
		if (i >= reservedKeys) throw new IllegalArgumentException("i must be less than reservedKeys");
		reservedKeyBehavior[i] = behavior;
	}
	
	public LZWReservedKeyHandler getReservedKeyHandler() {
		return reservedKeyHandler;
	}
	
	public void setReservedKeyHandler(LZWReservedKeyHandler handler) {
		reservedKeyHandler = handler;
	}
	
	public void finish() {
		in.finish();
		dict = newDictionary(clearKey, reservedKeys);
		keySize = minKeySize;
		prevWord = null;
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
	
	private static byte[] arrayAppend(byte[] a, byte b) {
		byte[] c = new byte[a.length + 1];
		for (int i = 0; i < a.length; i++) {
			c[i] = a[i];
		}
		c[a.length] = b;
		return c;
	}
}
