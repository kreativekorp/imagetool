package com.kreative.imagetool.gif;

import java.awt.image.BufferedImage;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import javax.imageio.ImageIO;

public class UnmakeTrueColorGIF {
	public static void main(String[] args) {
		for (String arg : args) {
			System.out.print(arg + "... ");
			File inf = new File(arg);
			File outf = new File(inf.getParentFile(), inf.getName() + ".png");
			try {
				DataInputStream in = new DataInputStream(new FileInputStream(inf));
				GIFFile gif = new GIFFile();
				gif.read(in);
				in.close();
				
				int count = 0;
				for (GIFBlock block : gif.blocks) {
					if (block instanceof GIFImageDescriptor) {
						count++;
					}
				}
				
				int index = 0;
				BufferedImage image = null;
				GIFFrameIterator iter = new GIFFrameIterator(gif);
				while (iter.hasNext()) {
					image = iter.next().composedImage;
					index++;
					printbs(index + "/" + count);
				}
				
				ImageIO.write(image, "png", outf);
				System.out.println();
			} catch (IOException ioe) {
				System.out.println();
				ioe.printStackTrace();
			}
		}
	}
	
	private static void printbs(String s) {
		StringBuffer sb = new StringBuffer(s);
		for (int i = s.length(); i > 0; i--) sb.append('\b');
		System.out.print(sb.toString());
	}
}
