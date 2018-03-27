package com.kreative.imagetool;

import java.awt.Dimension;
import java.awt.Insets;
import java.awt.image.BufferedImage;
import java.io.File;
import com.kreative.imagetool.animation.Animation;
import com.kreative.imagetool.animation.AnimationIO;
import com.kreative.imagetool.gci.GCIFile;
import com.kreative.imagetool.gif.GIFBlock;
import com.kreative.imagetool.gif.GIFFile;
import com.kreative.imagetool.gif.GIFImageDescriptor;
import com.kreative.imagetool.transform.Trim;

public class ListImages {
	public static void main(String[] args) {
		try { System.setProperty("apple.awt.UIElement", "true"); } catch (Exception e) {}
		if (args.length == 0) {
			printHelp();
		} else {
			boolean parseOptions = true;
			boolean recursive = false;
			ListImages li = null;
			for (String arg : args) {
				if (parseOptions && arg.startsWith("-")) {
					if (arg.equals("--")) {
						parseOptions = false;
					} else if (arg.equalsIgnoreCase("-r")) {
						recursive = true;
					} else if (arg.equalsIgnoreCase("--help")) {
						printHelp();
					} else {
						System.err.println("Invalid option: " + arg);
						return;
					}
				} else {
					if (li == null) {
						li = new ListImages();
						li.printHeader();
					}
					li.listImages(new File(arg), recursive, "");
				}
			}
			if (li != null) {
				li.printFooter();
			}
		}
	}
	
	private static void printHelp() {
		System.out.println();
		System.out.println("ListImages - List images with their dimensions.");
		System.out.println();
		System.out.println("Options:");
		System.out.println();
		System.out.println("-r, -R");
		System.out.println("    Recursively list images in subdirectories encountered.");
		System.out.println();
	}
	
	private int count = 0;
	private Dimension minSize = null;
	private Dimension maxSize = null;
	private Insets minMargin = null;
	private Insets maxMargin = null;
	
	private void printHeader() {
		System.out.println("Width\tHeight\tLeft\tTop\tRight\tBottom\tFrames\tName");
	}
	
	private void listImages(File f, boolean recursive, String prefix) {
		if (f.isDirectory()) {
			StringBuffer sb = new StringBuffer();
			sb.append(prefix);
			sb.append(f.getName());
			sb.append(File.separator);
			prefix = sb.toString();
			for (File child : f.listFiles()) {
				if (!child.getName().startsWith(".")) {
					if (recursive || !child.isDirectory()) {
						listImages(child, recursive, prefix);
					}
				}
			}
		} else try {
			Object image = ImageIO.readFile(f);
			if (image instanceof BufferedImage) {
				BufferedImage bi = (BufferedImage)image;
				Insets margin = Trim.getMargin(bi);
				dataPoint(
					bi.getWidth(), bi.getHeight(),
					margin.top, margin.left,
					margin.bottom, margin.right
				);
				System.out.println(
					bi.getWidth() + "\t" + bi.getHeight() + "\t" +
					margin.left + "\t" + margin.top + "\t" +
					margin.right + "\t" + margin.bottom + "\t\t" +
					prefix + f.getName()
				);
			} else if (image instanceof GCIFile) {
				GCIFile gci = (GCIFile)image;
				dataPoint(gci.width, gci.height, 0, 0, 0, 0);
				System.out.println(
					gci.width + "\t" + gci.height + "\t0\t0\t0\t0\t" +
					gci.blocks.size() + "\t" + prefix + f.getName()
				);
			} else if (image instanceof GIFFile) {
				GIFFile gif = (GIFFile)image;
				Insets margin = Trim.getMargin(gif);
				int count = 0;
				for (GIFBlock block : gif.blocks) {
					if (block instanceof GIFImageDescriptor) {
						count++;
					}
				}
				dataPoint(
					gif.width, gif.height,
					margin.top, margin.left,
					margin.bottom, margin.right
				);
				System.out.println(
					gif.width + "\t" + gif.height + "\t" +
					margin.left + "\t" + margin.top + "\t" +
					margin.right + "\t" + margin.bottom + "\t" +
					count + "\t" + prefix + f.getName()
				);
			} else {
				Animation a = AnimationIO.fromObject(image);
				if (a != null) {
					Insets margin = Trim.getMargin(a);
					int count = a.frames.size();
					dataPoint(
						a.width, a.height,
						margin.top, margin.left,
						margin.bottom, margin.right
					);
					System.out.println(
						a.width + "\t" + a.height + "\t" +
						margin.left + "\t" + margin.top + "\t" +
						margin.right + "\t" + margin.bottom + "\t" +
						count + "\t" + prefix + f.getName()
					);
				}
			}
		} catch (Exception e) {
			// This space intentionally left blank.
		}
	}
	
	private void dataPoint(int width, int height, int top, int left, int bottom, int right) {
		count++;
		if (minSize == null) {
			minSize = new Dimension(width, height);
		} else {
			if (width < minSize.width) minSize.width = width;
			if (height < minSize.height) minSize.height = height;
		}
		if (maxSize == null) {
			maxSize = new Dimension(width, height);
		} else {
			if (width > maxSize.width) maxSize.width = width;
			if (height > maxSize.height) maxSize.height = height;
		}
		if (minMargin == null) {
			minMargin = new Insets(top, left, bottom, right);
		} else {
			if (top < minMargin.top) minMargin.top = top;
			if (left < minMargin.left) minMargin.left = left;
			if (bottom < minMargin.bottom) minMargin.bottom = bottom;
			if (right < minMargin.right) minMargin.right = right;
		}
		if (maxMargin == null) {
			maxMargin = new Insets(top, left, bottom, right);
		} else {
			if (top > maxMargin.top) maxMargin.top = top;
			if (left > maxMargin.left) maxMargin.left = left;
			if (bottom > maxMargin.bottom) maxMargin.bottom = bottom;
			if (right > maxMargin.right) maxMargin.right = right;
		}
	}
	
	private void printFooter() {
		System.out.println();
		System.out.println("total " + count);
		System.out.println();
		System.out.println("Width\tHeight\tLeft\tTop\tRight\tBottom\tFrames\tName");
		if (minSize != null && minMargin != null) {
			System.out.println(
				minSize.width + "\t" + minSize.height + "\t" +
				minMargin.left + "\t" + minMargin.top + "\t" +
				minMargin.right + "\t" + minMargin.bottom + "\t\t(min)"
			);
		}
		if (maxSize != null && maxMargin != null) {
			System.out.println(
				maxSize.width + "\t" + maxSize.height + "\t" +
				maxMargin.left + "\t" + maxMargin.top + "\t" +
				maxMargin.right + "\t" + maxMargin.bottom + "\t\t(max)"
			);
		}
	}
}
