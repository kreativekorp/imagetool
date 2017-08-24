package com.kreative.imagetool.gif;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

public class GIFPlainTextExtension implements GIFBlock {
	public int left;
	public int top;
	public int width;
	public int height;
	public int charWidth;
	public int charHeight;
	public int foregroundIndex;
	public int backgroundIndex;
	public final List<byte[]> textStrings = new ArrayList<byte[]>();
	
	public void read(DataInput in) throws IOException {
		if (in.readByte() != 12) throw new IOException("bad subblock length");
		left = Short.reverseBytes(in.readShort()) & 0xFFFF;
		top = Short.reverseBytes(in.readShort()) & 0xFFFF;
		width = Short.reverseBytes(in.readShort()) & 0xFFFF;
		height = Short.reverseBytes(in.readShort()) & 0xFFFF;
		charWidth = in.readUnsignedByte();
		charHeight = in.readUnsignedByte();
		foregroundIndex = in.readUnsignedByte();
		backgroundIndex = in.readUnsignedByte();
		textStrings.clear();
		while (true) {
			int len = in.readUnsignedByte();
			if (len == 0) break;
			byte[] textString = new byte[len];
			in.readFully(textString);
			textStrings.add(textString);
		}
	}
	
	public void write(DataOutput out) throws IOException {
		out.writeByte(12);
		out.writeShort(Short.reverseBytes((short)left));
		out.writeShort(Short.reverseBytes((short)top));
		out.writeShort(Short.reverseBytes((short)width));
		out.writeShort(Short.reverseBytes((short)height));
		out.writeByte(charWidth);
		out.writeByte(charHeight);
		out.writeByte(foregroundIndex);
		out.writeByte(backgroundIndex);
		for (byte[] textString : textStrings) {
			if (textString == null || textString.length == 0) continue;
			int len = (textString.length < 255) ? textString.length : 255;
			out.writeByte(len);
			out.write(textString, 0, len);
		}
		out.writeByte(0);
	}
	
	public void print(PrintStream out, String prefix) {
		out.println(prefix + "Left: " + left);
		out.println(prefix + "Top: " + top);
		out.println(prefix + "Width: " + width);
		out.println(prefix + "Height: " + height);
		out.println(prefix + "Char Width: " + charWidth);
		out.println(prefix + "Char Height: " + charHeight);
		out.println(prefix + "Foreground Index: " + foregroundIndex);
		out.println(prefix + "Background Index: " + backgroundIndex);
		for (byte[] textString : textStrings) {
			if (textString == null || textString.length == 0) continue;
			out.println(prefix + "Text String Length: " + textString.length);
			out.println(prefix + "Text String: " + new String(textString));
		}
	}
}
