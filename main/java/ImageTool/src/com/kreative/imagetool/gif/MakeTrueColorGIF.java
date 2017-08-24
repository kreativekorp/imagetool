package com.kreative.imagetool.gif;

import java.awt.image.BufferedImage;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import javax.imageio.ImageIO;

public class MakeTrueColorGIF {
	public static void main(String[] args) {
		for (String arg : args) {
			System.out.print(arg + "... ");
			File inf = new File(arg);
			File outf = new File(inf.getParentFile(), inf.getName() + ".gif");
			try {
				BufferedImage image = ImageIO.read(inf);
				int width = image.getWidth();
				int height = image.getHeight();
				int[] pixels = new int[width * height];
				image.getRGB(0, 0, width, height, pixels, 0, width);
				
				Histogram<Integer> h = new Histogram<Integer>();
				for (int p : pixels) if (p < 0) h.add(p | 0xFF000000);
				List<Integer> palette = h.toList();
				Collections.sort(palette, h.byCount(true));
				
				GIFFile gif = new GIFFile();
				gif.width = width;
				gif.height = height;
				
				GIFApplicationExtension kab = new GIFApplicationExtension();
				kab.applicationId = GIFApplicationExtension.KAB_APPLICATION_ID;
				kab.authenticationCode = GIFApplicationExtension.KAB_AUTHENTICATION_CODE;
				kab.data.add("TrueColorGIF".getBytes());
				gif.blocks.add(kab);
				
				byte[] f0 = new byte[pixels.length];
				int s0 = Math.min(palette.size(), 255);
				int[] p0 = new int[s0 + 1];
				for (int i = 0; i < s0; i++) {
					p0[i] = palette.get(i);
				}
				for (int i = 0; i < pixels.length; i++) {
					if (pixels[i] < 0) {
						int index = GIFImageDescriptor.closestIndexOf(pixels[i], p0, s0);
						f0[i] = (byte)index;
					} else {
						f0[i] = (byte)s0;
					}
				}
				appendFrame(gif, f0, p0);
				printbs(s0 + "/" + palette.size());
				
				int off = s0;
				while (off < palette.size()) {
					byte[] f = new byte[pixels.length];
					int s = Math.min(palette.size() - off, 255);
					int[] p = new int[s + 1];
					for (int i = 0; i < s; i++) {
						p[i] = palette.get(off + i);
					}
					for (int i = 0; i < pixels.length; i++) {
						if (pixels[i] < 0) {
							int index = GIFImageDescriptor.indexOf(pixels[i], p, s);
							f[i] = (byte)((index >= 0) ? index : s);
						} else {
							f[i] = (byte)s;
						}
					}
					appendFrame(gif, f, p);
					off += s;
					printbs(off + "/" + palette.size());
				}
				
				DataOutputStream out = new DataOutputStream(new FileOutputStream(outf));
				gif.write(out);
				out.close();
				System.out.println();
			} catch (IOException ioe) {
				System.out.println();
				ioe.printStackTrace();
			}
		}
	}
	
	private static void appendFrame(GIFFile gif, byte[] f, int[] p) throws IOException {
		GIFGraphicControlExtension gce = new GIFGraphicControlExtension();
		gce.disposalMethod = GIFDisposalMethod.DO_NOT_DISPOSE;
		gce.transparency = true;
		gce.transparencyIndex = p.length - 1;
		gif.blocks.add(gce);
		
		GIFImageDescriptor gid = new GIFImageDescriptor();
		gid.width = gif.width;
		gid.height = gif.height;
		gid.palette = p;
		gid.paletteSorted = true;
		gid.minKeySize = GIFImageDescriptor.bestMinKeySize(p.length);
		gid.setUncompressedData(f);
		gif.blocks.add(gid);
	}
	
	private static void printbs(String s) {
		StringBuffer sb = new StringBuffer(s);
		for (int i = s.length(); i > 0; i--) sb.append('\b');
		System.out.print(sb.toString());
	}
}
