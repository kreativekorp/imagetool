package com.kreative.imagetool.gif;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.io.PrintStream;

public class GIFGraphicControlExtension implements GIFBlock {
	public GIFDisposalMethod disposalMethod = GIFDisposalMethod.NO_DISPOSAL_SPECIFIED;
	public boolean userInput = false;
	public int delayTime = 0;
	public boolean transparency = false;
	public int transparencyIndex = 0;
	
	public void read(DataInput in) throws IOException {
		if (in.readByte() != 4) throw new IOException("bad subblock length");
		int flags = in.readUnsignedByte();
		switch (flags & 0x0C) {
			case 0x00: disposalMethod = GIFDisposalMethod.NO_DISPOSAL_SPECIFIED; break;
			case 0x04: disposalMethod = GIFDisposalMethod.DO_NOT_DISPOSE; break;
			case 0x08: disposalMethod = GIFDisposalMethod.RESTORE_TO_BACKGROUND; break;
			case 0x0C: disposalMethod = GIFDisposalMethod.RESTORE_TO_PREVIOUS; break;
			default: throw new IOException("this should never happen");
		}
		userInput = ((flags & 0x02) != 0);
		transparency = ((flags & 0x01) != 0);
		delayTime = Short.reverseBytes(in.readShort()) & 0xFFFF;
		transparencyIndex = in.readUnsignedByte();
		if (in.readByte() != 0) throw new IOException("bad subblock length");
	}
	
	public void write(DataOutput out) throws IOException {
		out.writeByte(4);
		int flags;
		if (disposalMethod == null) flags = 0;
		else switch (disposalMethod) {
			case NO_DISPOSAL_SPECIFIED: flags = 0x00; break;
			case DO_NOT_DISPOSE: flags = 0x04; break;
			case RESTORE_TO_BACKGROUND: flags = 0x08; break;
			case RESTORE_TO_PREVIOUS: flags = 0x0C; break;
			default: throw new IOException("this should never happen");
		}
		if (userInput) flags |= 0x02;
		if (transparency) flags |= 0x01;
		out.writeByte(flags);
		out.writeShort(Short.reverseBytes((short)delayTime));
		out.writeByte(transparencyIndex);
		out.writeByte(0);
	}
	
	public void print(PrintStream out, String prefix) {
		out.println(prefix + "Disposal Method: " + disposalMethod);
		out.println(prefix + "User Input: " + (userInput ? "Yes" : "No"));
		out.println(prefix + "Delay Time: " + delayTime);
		out.println(prefix + "Transparency: " + (transparency ? "Yes" : "No"));
		out.println(prefix + "Transparency Index: " + transparencyIndex);
	}
}
