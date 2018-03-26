package com.kreative.imagetool;

import java.awt.image.BufferedImage;
import java.io.File;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import com.kreative.imagetool.animation.Animation;

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
			final Animation a = ImageIO.toAnimation(image);
			final JLabel l = new JLabel(new ImageIcon(a.frames.get(0).image));
			setContentPane(new JScrollPane(l));
			if (a.frames.size() > 1) {
				(new Thread() {
					int fc = 0;
					public void run() {
						while (!Thread.interrupted()) {
							if (isVisible()) {
								try {
									l.setIcon(new ImageIcon(a.frames.get(fc).image));
									Thread.sleep((long)(a.frames.get(fc).duration * 1000));
									fc = (fc + 1) % a.frames.size();
								} catch (InterruptedException e) {
									break;
								}
							}
						}
					}
				}).start();
			}
		}
		pack();
		setLocationRelativeTo(null);
	}
}
