package com.kreative.imagetool.gif;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Random;

public class LZWTest {
	public static void main(String[] args) throws IOException {
		Random ran = new Random();
		while (true) {
			int len = ran.nextInt(8192);
			int min = ran.nextInt(7) + 3;
			int max = ran.nextInt(7) + 10;
			int res = ran.nextInt(3) + 1;
			byte[] data = new byte[len];
			if (ran.nextBoolean()) {
				ran.nextBytes(data);
				int m = (1 << (min - 1)) - 1;
				for (int i = 0; i < len; i++) data[i] &= m;
			} else {
				Arrays.fill(data, (byte)ran.nextInt(1 << (min - 1)));
			}
			
			ByteArrayOutputStream cout = new ByteArrayOutputStream();
			LZWCompressor comp = new LZWCompressor(cout, min, max, res);
			comp.write(data);
			comp.finish();
			cout.close();
			byte[] cdata = cout.toByteArray();
			
			ByteArrayInputStream din = new ByteArrayInputStream(cdata);
			ByteArrayOutputStream dout = new ByteArrayOutputStream();
			LZWDecompressor decomp = new LZWDecompressor(din, dout, min, res);
			decomp.read();
			decomp.finish();
			dout.close();
			din.close();
			byte[] ddata = dout.toByteArray();
			
			System.out.print(len + "," + min + "," + max + "," + res + "\t");
			System.out.println(arrayStartsWith(ddata, data) ? "PASS" : "FAIL");
		}
	}
	
	private static boolean arrayStartsWith(byte[] a, byte[] b) {
		for (int i = 0; i < b.length; i++) {
			if (i >= a.length || a[i] != b[i]) {
				return false;
			}
		}
		return true;
	}
}
