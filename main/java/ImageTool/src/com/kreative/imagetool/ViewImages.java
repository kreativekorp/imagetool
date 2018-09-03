package com.kreative.imagetool;

import java.awt.image.BufferedImage;
import java.io.File;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import com.kreative.imagetool.animation.Animation;
import com.kreative.imagetool.animation.AnimationIO;
import com.kreative.imagetool.animation.AnimationThread;

public class ViewImages extends JFrame {
	private static final long serialVersionUID = 1L;
	
	public static void main(String[] args) {
		if (args.length == 0) {
			printHelp();
		} else {
			boolean parseOptions = true;
			boolean recursive = false;
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
					viewImages(new File(arg), recursive);
				}
			}
		}
	}
	
	private static void printHelp() {
		System.out.println();
		System.out.println("ViewImages - View images in windows.");
		System.out.println();
		System.out.println("Options:");
		System.out.println();
		System.out.println("-r, -R");
		System.out.println("    Recursively view images in subdirectories encountered.");
		System.out.println();
	}
	
	private static void viewImages(File f, boolean recursive) {
		if (f.isDirectory()) {
			for (File child : f.listFiles()) {
				if (!child.getName().startsWith(".")) {
					if (recursive || !child.isDirectory()) {
						viewImages(child, recursive);
					}
				}
			}
		} else try {
			Object image = ImageIO.readFile(f);
			if (image != null) new ViewImages(f.getName(), image).setVisible(true);
		} catch (Exception e) {
			// This space intentionally left blank.
		}
	}
	
	public ViewImages(String title, Object image) {
		super(title);
		if (image instanceof BufferedImage) {
			JLabel l = new JLabel(new ImageIcon((BufferedImage)image));
			setContentPane(new JScrollPane(l));
		} else {
			Animation a = AnimationIO.fromObject(image);
			JLabel l = new JLabel(new ImageIcon(AnimationIO.toBufferedImage(a)));
			setContentPane(new JScrollPane(l));
			if (a.frames.size() > 1) new AnimationThread(a, l).start();
		}
		pack();
		setLocationRelativeTo(null);
	}
}
