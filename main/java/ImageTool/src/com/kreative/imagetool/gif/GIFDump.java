package com.kreative.imagetool.gif;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintStream;
import javax.imageio.ImageIO;

public class GIFDump {
	public static void main(String[] args) {
		if (args.length == 0) {
			printHelp();
		} else {
			Options o = new Options();
			for (String arg : args) {
				if (o.parseOptions && arg.startsWith("-")) {
					o.parseOption(arg);
				} else {
					System.out.println(o.outputTextFile ? arg : (arg + ":"));
					try { processFile(o, arg); }
					catch (Exception e) { e.printStackTrace(); }
				}
			}
		}
	}
	
	private static void printHelp() {
		System.out.println();
		System.out.println("GIFDump - decompile GIF file");
		System.out.println();
		System.out.println("Options:");
		System.out.println("  -T  write to text file instead of stdout");
		System.out.println("  -I  write raw images from image descriptors");
		System.out.println("  -F  write animation frames");
		System.out.println("  -V  implies -T -I -F");
		System.out.println("  --  remaining arguments are file names");
		System.out.println();
	}
	
	private static class Options {
		public boolean parseOptions = true;
		public boolean outputTextFile = false;
		public boolean outputImages = false;
		public boolean outputFrames = false;
		
		public void parseOption(String arg) {
			if (arg.equals("--")) {
				parseOptions = false;
			} else if (arg.equals("-T")) {
				outputTextFile = true;
			} else if (arg.equals("-I")) {
				outputImages = true;
			} else if (arg.equals("-F")) {
				outputFrames = true;
			} else if (arg.equals("-V")) {
				outputTextFile = outputImages = outputFrames = true;
			} else if (arg.equals("--help")) {
				printHelp();
			} else {
				System.err.println("Unknown option: " + arg);
			}
		}
	}
	
	private static void processFile(Options o, String arg) throws IOException {
		GIFFile gif = new GIFFile();
		File inf = new File(arg);
		DataInputStream in = new DataInputStream(new FileInputStream(inf));
		gif.read(in);
		in.close();
		
		if (o.outputTextFile) {
			File outf = new File(inf.getParentFile(), inf.getName() + ".gifdump.txt");
			PrintStream out = new PrintStream(outf);
			gif.print(out, "");
			out.close();
		} else {
			gif.print(System.out, "\t");
		}
		
		if (o.outputImages || o.outputFrames) {
			int index = 0;
			GIFFrameIterator iter = new GIFFrameIterator(gif);
			while (iter.hasNext()) {
				GIFFrame frame = iter.next();
				if (o.outputImages && frame.rawImage != null) {
					File outf = new File(inf.getParentFile(), inf.getName() + ".gifdump.i" + index + ".png");
					ImageIO.write(frame.rawImage, "png", outf);
				}
				if (o.outputFrames && frame.composedImage != null) {
					File outf = new File(inf.getParentFile(), inf.getName() + ".gifdump.f" + index + ".png");
					ImageIO.write(frame.composedImage, "png", outf);
				}
				index++;
			}
		}
	}
}
