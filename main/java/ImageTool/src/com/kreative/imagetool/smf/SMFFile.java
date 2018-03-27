package com.kreative.imagetool.smf;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.EOFException;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

public class SMFFile {
	public static boolean matchMagic(byte[] m) {
		return (
			(m[0] == 'A') ? (m[3] == 'P' || m[3] == 'p' || m[3] == 'F' || m[3] == 'f') :
			(m[0] == 'a') ? (m[5] == 'P' || m[5] == 'p' || m[5] == 'F' || m[5] == 'f') :
			false
		);
	}
	
	public final List<SMFDirective> directives = new ArrayList<SMFDirective>();
	
	public void read(DataInput in) throws IOException {
		int ptr = 0;
		directives.clear();
		while (true) {
			try {
				SMFDirective dir = null;
				byte cmd = in.readByte();
				switch (cmd) {
					case 'A': case 'a': dir = new SMFAllocateDirective(); break;
					case 'F': case 'f': dir = new SMFFillDirective(); break;
					case 'P': case 'p': dir = new SMFPushDirective(); break;
					case 'W': case 'w': dir = new SMFWaitDirective(); break;
					case 'S': case 's': dir = new SMFSeekDirective(); break;
					case 'N': case 'n': dir = new SMFNextDirective(); break;
					case 'Z': case 'z': dir = new SMFHaltDirective(); break;
				}
				if (dir == null) {
					ptr = (ptr + 1) & 0x1FF;
				} else {
					ptr = (ptr + dir.read(cmd, in)) & 0x1FF;
					directives.add(dir);
					if (ptr > 0 && dir.isTerminator()) {
						in.skipBytes(512 - ptr);
						ptr = 0;
					}
				}
			} catch (EOFException e) {
				return;
			}
		}
	}
	
	public void write(DataOutput out) throws IOException {
		int ptr = 0;
		for (SMFDirective dir : directives) {
			if (ptr + dir.opLength() > 512) {
				out.writeByte('N');
				ptr++;
				while (ptr < 512) {
					out.writeByte(0);
					ptr++;
				}
				ptr = 0;
			}
			ptr = (ptr + dir.write(out)) & 0x1FF;
			if (ptr > 0 && dir.isTerminator()) {
				while (ptr < 512) {
					out.writeByte(0);
					ptr++;
				}
				ptr = 0;
			}
		}
		if (ptr > 0) {
			out.writeByte('Z');
			ptr++;
			while (ptr < 512) {
				out.writeByte(0);
				ptr++;
			}
		}
	}
	
	public void print(PrintStream out, String prefix) {
		out.println(prefix + "Directives: " + directives.size());
		for (SMFDirective dir : directives) out.println(prefix + "\t" + dir);
	}
	
	public boolean isRepeating() {
		for (SMFDirective dir : directives) {
			if (dir instanceof SMFSeekDirective) {
				return true;
			}
		}
		return false;
	}
}
