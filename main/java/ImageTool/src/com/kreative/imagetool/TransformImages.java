package com.kreative.imagetool;

import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.imageio.ImageIO;
import com.kreative.imagetool.gif.GIFFile;
import com.kreative.imagetool.gif.GIFFrameIterator;
import com.kreative.imagetool.transform.Transform;
import com.kreative.imagetool.transform.TransformParser;

public class TransformImages {
	public static void main(String[] args) {
		try { System.setProperty("apple.awt.UIElement", "true"); } catch (Exception e) {}
		if (args.length == 0) {
			printHelp();
		} else {
			Options o = new Options();
			int argi = 0;
			while (argi < args.length) {
				if (o.parseOptions && args[argi].startsWith("-")) {
					argi = o.parseOption(args, argi);
					if (o.parseError != null) {
						System.err.println("Error: " + o.parseError);
						o.parseError = null;
					}
				} else {
					System.out.println(args[argi]);
					try {
						o.processFile(new File(args[argi]));
					} catch (IOException e) {
						System.err.println(
							"Error: " +
							e.getClass().getSimpleName() + ": " +
							e.getMessage()
						);
					}
					argi++;
				}
			}
		}
	}
	
	private static void printHelp() {
		System.out.println();
		System.out.println("TransformImages - Perform simple transformations on images and animated GIFs.");
		System.out.println();
		System.out.println("Options for transformations:");
		System.out.println();
		TransformParser.printHelp();
		System.out.println();
		System.out.println("Options for output:");
		System.out.println();
		System.out.println("-f, --format <format>");
		System.out.println("    Specifies the output format of the transformed image.");
		System.out.println("    Default is gif if the original is gif or png otherwise.");
		System.out.println("-o, --output <path>");
		System.out.println("    Specifies the output file or directory path for the transformed image.");
		System.out.println();
	}
	
	private static class Options {
		public boolean parseOptions = true;
		public List<Transform> transforms = new ArrayList<Transform>();
		public String format = "auto";
		public File output = null;
		public String parseError = null;
		
		public int parseOption(String[] args, int i) {
			int ni;
			try {
				if (args[i].equals("--")) {
					parseOptions = false;
					return i + 1;
				} else if ((ni = TransformParser.parseTransform(args, i, transforms)) > i) {
					return ni;
				} else if (args[i].equals("-f") || args[i].equalsIgnoreCase("--format")) {
					format = args[i + 1];
					format = format.replaceAll("[^A-Za-z0-9]", "");
					format = format.toLowerCase();
					return i + 2;
				} else if (args[i].equals("-o") || args[i].equalsIgnoreCase("--output")) {
					output = new File(args[i + 1]);
					return i + 2;
				} else if (args[i].equalsIgnoreCase("--help")) {
					printHelp();
					return i + 1;
				} else {
					parseError = "Invalid option: " + args[i];
					return args.length;
				}
			} catch (ArrayIndexOutOfBoundsException e) {
				parseError = "Not enough arguments for " + args[i];
				return args.length;
			} catch (NumberFormatException e) {
				parseError = "Invalid arguments for " + args[i];
				return args.length;
			} catch (IllegalArgumentException e) {
				parseError = "Invalid arguments for " + args[i];
				return args.length;
			}
		}
		
		public void processFile(File input) throws IOException {
			Object o = readFile(input);
			if (o instanceof GIFFile) {
				GIFFile gif = (GIFFile)o;
				for (Transform tx : transforms) {
					gif = tx.transform(gif);
				}
				String fmt = format;
				if (fmt.equalsIgnoreCase("auto")) fmt = "gif";
				writeFile(gif, fmt, outputFile(input, fmt));
			} else if (o instanceof BufferedImage) {
				BufferedImage image = (BufferedImage)o;
				for (Transform tx : transforms) {
					image = tx.transform(image);
				}
				String fmt = format;
				if (fmt.equalsIgnoreCase("auto")) fmt = "png";
				ImageIO.write(image, fmt, outputFile(input, fmt));
			} else {
				throw new IOException("Unrecognized format");
			}
		}
		
		private File outputFile(File inputFile, String fmt) {
			if (output == null) {
				File parent = inputFile.getParentFile();
				String name = inputFile.getName() + "." + fmt;
				return new File(parent, name);
			} else if (output.isDirectory()) {
				String name = inputFile.getName();
				int i = name.lastIndexOf('.');
				if (i > 0) name = name.substring(0, i) + "." + fmt;
				else name = name + "." + fmt;
				return new File(output, name);
			} else {
				File outputFile = output;
				output = null;
				return outputFile;
			}
		}
	}
	
	private static Object readFile(File f) throws IOException {
		FileInputStream fin = new FileInputStream(f);
		BufferedInputStream bin = new BufferedInputStream(fin);
		byte[] m = new byte[6];
		bin.mark(8);
		bin.read(m);
		bin.reset();
		if (
			m[0] == 'G' && m[1] == 'I' && m[2] == 'F' &&
			m[3] == '8' && (m[4] == '7' || m[4] == '9') && m[5] == 'a'
		) {
			GIFFile gif = new GIFFile();
			gif.read(new DataInputStream(bin));
			bin.close();
			fin.close();
			return gif;
		} else {
			BufferedImage image = ImageIO.read(bin);
			bin.close();
			fin.close();
			return image;
		}
	}
	
	private static void writeFile(GIFFile gif, String format, File output) throws IOException {
		if (format.equalsIgnoreCase("gif")) {
			FileOutputStream fout = new FileOutputStream(output);
			DataOutputStream dout = new DataOutputStream(fout);
			gif.write(dout);
			dout.flush();
			fout.flush();
			dout.close();
			fout.close();
		} else {
			GIFFrameIterator iter = new GIFFrameIterator(gif);
			if (iter.hasNext()) {
				BufferedImage image = iter.next().composedImage;
				ImageIO.write(image, format, output);
			} else {
				throw new IOException("No GIF frames");
			}
		}
	}
}
