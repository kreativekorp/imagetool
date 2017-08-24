package com.kreative.imagetool.gif;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

public class GIFApplicationExtension implements GIFBlock {
	public static final long NAB_APPLICATION_ID = 0x4E45545343415045L; // 'NETSCAPE'
	public static final int NAB_AUTHENTICATION_CODE = 0x322E30; // '2.0'
	public static final long KAB_APPLICATION_ID = 0x4B72656174697665L; // 'Kreative'
	public static final int KAB_AUTHENTICATION_CODE = 0x205377; // ' Sw'
	
	public long applicationId;
	public int authenticationCode;
	public final List<byte[]> data = new ArrayList<byte[]>();
	
	public void read(DataInput in) throws IOException {
		if (in.readByte() != 11) throw new IOException("bad subblock length");
		applicationId = in.readLong();
		authenticationCode = (in.readUnsignedByte() << 16);
		authenticationCode |= in.readUnsignedShort();
		data.clear();
		while (true) {
			int len = in.readUnsignedByte();
			if (len == 0) break;
			byte[] d = new byte[len];
			in.readFully(d);
			data.add(d);
		}
	}
	
	public void write(DataOutput out) throws IOException {
		out.writeByte(11);
		out.writeLong(applicationId);
		out.writeByte(authenticationCode >> 16);
		out.writeShort(authenticationCode);
		for (byte[] d : data) {
			if (d == null || d.length == 0) continue;
			int len = (d.length < 255) ? d.length : 255;
			out.writeByte(len);
			out.write(d, 0, len);
		}
		out.writeByte(0);
	}
	
	public void print(PrintStream out, String prefix) {
		String ais = new String(new char[]{
			(char)((applicationId >> 56) & 0xFF),
			(char)((applicationId >> 48) & 0xFF),
			(char)((applicationId >> 40) & 0xFF),
			(char)((applicationId >> 32) & 0xFF),
			(char)((applicationId >> 24) & 0xFF),
			(char)((applicationId >> 16) & 0xFF),
			(char)((applicationId >>  8) & 0xFF),
			(char)((applicationId >>  0) & 0xFF)
		});
		out.println(prefix + "Application ID: " + ais);
		
		String acs = new String(new char[]{
			(char)((authenticationCode >> 16) & 0xFF),
			(char)((authenticationCode >>  8) & 0xFF),
			(char)((authenticationCode >>  0) & 0xFF)
		});
		out.println(prefix + "Authentication Code: " + acs);

		for (byte[] d : data) {
			if (d == null || d.length == 0) continue;
			out.println(prefix + "Data Length: " + d.length);
		}
		
		if (isNAB()) {
			out.println(prefix + "Repeat Count: " + getRepeatCount());
		}
	}
	
	public boolean isNAB() {
		return applicationId == NAB_APPLICATION_ID
		    && authenticationCode == NAB_AUTHENTICATION_CODE
		    && data.size() == 1
		    && data.get(0) != null
		    && data.get(0).length == 3
		    && data.get(0)[0] == 1;
	}
	
	public int getRepeatCount() {
		if (isNAB()) {
			int repeatCount = (data.get(0)[1] & 0xFF);
			repeatCount |= ((data.get(0)[2] & 0xFF) << 8);
			return repeatCount;
		} else {
			return -1;
		}
	}
	
	public void setRepeatCount(int repeatCount) {
		applicationId = NAB_APPLICATION_ID;
		authenticationCode = NAB_AUTHENTICATION_CODE;
		data.clear();
		byte[] d = new byte[3];
		d[0] = 1;
		d[1] = (byte)(repeatCount);
		d[2] = (byte)(repeatCount >> 8);
		data.add(d);
	}
}
