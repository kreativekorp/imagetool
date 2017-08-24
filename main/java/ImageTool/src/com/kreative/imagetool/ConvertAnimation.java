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

import com.kreative.imagetool.animation.Animation;
import com.kreative.imagetool.animation.AnimationIO;
import com.kreative.imagetool.animation.ArrayOptions;
import com.kreative.imagetool.animation.Range;
import com.kreative.imagetool.gif.GIFFile;
import com.kreative.imagetool.transform.Transform;
import com.kreative.imagetool.transform.TransformParser;

public class ConvertAnimation {
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
		System.out.println("ConvertAnimation - Convert between static images and animated GIFs.");
		System.out.println();
		System.out.println("Options for input:");
		System.out.println();
		System.out.println("-ica, --inputcellanchor <anchor>");
		System.out.println("    Sets the position of images read from a directory.");
		System.out.println("    <Anchor> is NW, N, NE, E, SE, S, SW, W, or CENTER.");
		ArrayOptions.printHelpForImage();
		System.out.println();
		System.out.println("Options for transformations:");
		System.out.println();
		System.out.println("-D, --durations <list of frame durations>");
		System.out.println("    Sets the duration of each frame in the final output, e.g. \"1;0.5;3.14\"");
		System.out.println("    to set the duration of frames 1, 2, and 3 to one second, half a second,");
		System.out.println("    and 3.14 seconds, respectively. The list is repeated until the number of");
		System.out.println("    frames in the animation is reached when applied.");
		System.out.println("-F, --frames <list of frame indices>");
		System.out.println("    Determines which frames will appear in the final output, e.g. \"1-4;7;2\"");
		System.out.println("    for frames 1, 2, 3, 4, 7, and 2 again, in that order.");
		TransformParser.printHelp();
		System.out.println();
		System.out.println("Options for output:");
		System.out.println();
		System.out.println("-f, --format <format>");
		System.out.println("    Specifies the output format of the converted animation.");
		System.out.println("    Default is \"d\" for a directory of still frames.");
		System.out.println("-n, --loopcount <count>");
		System.out.println("    Specifies the number of times an animation loops (for animated GIFs).");
		System.out.println("-o, --output <path>");
		System.out.println("    Specifies the output file or directory path for the converted animation.");
		ArrayOptions.printHelpForFrames();
		System.out.println();
	}
	
	private static class Options {
		public boolean parseOptions = true;
		public ArrayOptions inputOptions = new ArrayOptions();
		public int inputAnchor = AnimationIO.CENTER;
		public String frameIndices = null;
		public List<Double> frameDelayTimes = null;
		public List<Transform> transforms = new ArrayList<Transform>();
		public String outputFormat = "d";
		public ArrayOptions outputOptions = new ArrayOptions();
		public int outputRepeat = 0;
		public File outputFile = null;
		public String parseError = null;
		
		public int parseOption(String[] args, int i) {
			int ni;
			try {
				if (args[i].equals("--")) {
					parseOptions = false;
					return i + 1;
				} else if ((ni = inputOptions.parseForImage(args, i)) > i) {
					return ni;
				} else if (args[i].equals("-ica") || args[i].equalsIgnoreCase("--inputcellanchor")) {
					inputAnchor = TransformParser.parseAnchor(args[i + 1]);
					return i + 2;
				} else if (args[i].equals("-F") || args[i].equalsIgnoreCase("--frames")) {
					Range.parseRange((frameIndices = args[i + 1]), 0, null);
					return i + 2;
				} else if (args[i].equals("-D") || args[i].equalsIgnoreCase("--durations")) {
					frameDelayTimes = Range.parseDurations(args[i + 1], null);
					return i + 2;
				} else if ((ni = TransformParser.parseTransform(args, i, transforms)) > i) {
					return ni;
				} else if (args[i].equals("-f") || args[i].equalsIgnoreCase("--format")) {
					outputFormat = args[i + 1];
					outputFormat = outputFormat.replaceAll("[^A-Za-z0-9]", "");
					outputFormat = outputFormat.toLowerCase();
					return i + 2;
				} else if ((ni = outputOptions.parseForFrames(args, i)) > i) {
					return ni;
				} else if (args[i].equals("-n") || args[i].equalsIgnoreCase("--loopcount")) {
					outputRepeat = Integer.parseInt(args[i + 1]);
					return i + 2;
				} else if (args[i].equals("-o") || args[i].equalsIgnoreCase("--output")) {
					outputFile = new File(args[i + 1]);
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
		
		public void processFile(File inputFile) throws IOException {
			Animation a = readFile(inputFile, inputOptions, inputAnchor);
			if (frameIndices != null && !frameIndices.isEmpty()) {
				List<Integer> r = Range.parseRange(frameIndices, a.frames.size(), null);
				a = a.selectFrames(r);
			}
			if (frameDelayTimes != null && !frameDelayTimes.isEmpty()) {
				for (int i = 0, n = a.frames.size(); i < n; i++) {
					double d = frameDelayTimes.get(i % frameDelayTimes.size());
					a.frames.get(i).duration = d;
				}
			}
			if (transforms != null && !transforms.isEmpty()) {
				for (Transform tx : transforms) {
					a = tx.transform(a);
				}
			}
			writeFile(a, outputFormat, outputOptions, outputRepeat, outputFile(inputFile));
		}
		
		private File outputFile(File inputFile) {
			if (outputFile == null) {
				File parent = inputFile.getParentFile();
				String name = inputFile.getName() + "." + outputFormat;
				return new File(parent, name);
			} else if (outputFile.isDirectory()) {
				String name = inputFile.getName();
				int i = name.lastIndexOf('.');
				if (i > 0) name = name.substring(0, i) + "." + outputFormat;
				else name = name + "." + outputFormat;
				return new File(outputFile, name);
			} else {
				File f = outputFile;
				outputFile = null;
				return f;
			}
		}
	}
	
	private static Animation readFile(File f, ArrayOptions o, int anchor) throws IOException {
		if (f.isDirectory()) {
			return AnimationIO.fromDirectory(f, anchor);
		} else {
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
				return AnimationIO.fromGIFFile(gif);
			} else {
				BufferedImage image = ImageIO.read(bin);
				bin.close();
				fin.close();
				return AnimationIO.fromImageArray(image, o);
			}
		}
	}
	
	private static void writeFile(Animation a, String format, ArrayOptions o, int repeat, File output) throws IOException {
		if (format.equalsIgnoreCase("d") || format.equalsIgnoreCase("dir") || format.equalsIgnoreCase("directory")) {
			AnimationIO.toDirectory(a, output);
		} else if (format.equalsIgnoreCase("gif")) {
			GIFFile gif = AnimationIO.toGIFFile(a, repeat);
			FileOutputStream fout = new FileOutputStream(output);
			DataOutputStream dout = new DataOutputStream(fout);
			gif.write(dout);
			dout.flush();
			fout.flush();
			dout.close();
			fout.close();
		} else {
			BufferedImage image = AnimationIO.toImageArray(a, null, o);
			ImageIO.write(image, format, output);
		}
	}
}
