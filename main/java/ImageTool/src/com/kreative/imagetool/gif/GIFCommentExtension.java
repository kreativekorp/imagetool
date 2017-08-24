package com.kreative.imagetool.gif;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

public class GIFCommentExtension implements GIFBlock {
	public final List<byte[]> comments = new ArrayList<byte[]>();
	
	public void read(DataInput in) throws IOException {
		comments.clear();
		while (true) {
			int len = in.readUnsignedByte();
			if (len == 0) break;
			byte[] comment = new byte[len];
			in.readFully(comment);
			comments.add(comment);
		}
	}
	
	public void write(DataOutput out) throws IOException {
		for (byte[] comment : comments) {
			if (comment == null || comment.length == 0) continue;
			int len = (comment.length < 255) ? comment.length : 255;
			out.writeByte(len);
			out.write(comment, 0, len);
		}
		out.writeByte(0);
	}
	
	public void print(PrintStream out, String prefix) {
		for (byte[] comment : comments) {
			if (comment == null || comment.length == 0) continue;
			out.println(prefix + "Comment Length: " + comment.length);
			out.println(prefix + "Comment: " + new String(comment));
		}
	}
}
