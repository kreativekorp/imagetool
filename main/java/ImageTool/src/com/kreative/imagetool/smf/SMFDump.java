package com.kreative.imagetool.smf;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;

public class SMFDump {
	public static void main(String[] args) {
		for (String arg : args) {
			System.out.println(arg + ":");
			try {
				SMFFile smf = new SMFFile();
				FileInputStream in = new FileInputStream(new File(arg));
				smf.read(new DataInputStream(in));
				in.close();
				smf.print(System.out, "\t");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
