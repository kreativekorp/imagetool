package com.kreative.imagetool.transform;

import java.awt.image.BufferedImage;
import java.io.IOException;
import com.kreative.imagetool.animation.Animation;
import com.kreative.imagetool.animation.AnimationFrame;
import com.kreative.imagetool.gif.GIFBlock;
import com.kreative.imagetool.gif.GIFFile;
import com.kreative.imagetool.gif.GIFImageDescriptor;

public class FlipHorizontal implements Transform {
	public BufferedImage transform(BufferedImage image) {
		int w = image.getWidth();
		int h = image.getHeight();
		int[] pixels = new int[w * h];
		image.getRGB(0, 0, w, h, pixels, 0, w);
		
		int[] slexip = new int[w * h];
		for (int y = 0; y < slexip.length; y += w) {
			for (int dx = 0, sx = w - 1; dx < w; dx++, sx--) {
				slexip[y + dx] = pixels[y + sx];
			}
		}
		
		image.setRGB(0, 0, w, h, slexip, 0, w);
		return image;
	}
	
	public GIFFile transform(GIFFile gif) {
		for (GIFBlock block : gif.blocks) {
			if (block instanceof GIFImageDescriptor) {
				GIFImageDescriptor gid = (GIFImageDescriptor)block;
				int w = gid.width;
				int h = gid.height;
				byte[] data;
				try { data = gid.getUncompressedData(); }
				catch (IOException e) { return null; }
				
				byte[] atad = new byte[w * h];
				for (int y = 0; y < atad.length; y += w) {
					for (int dx = 0, sx = w - 1; dx < w; dx++, sx--) {
						if (y + sx < data.length) {
							atad[y + dx] = data[y + sx];
						}
					}
				}
				
				gid.left = gif.width - gid.left - w;
				try { gid.setUncompressedData(atad); }
				catch (IOException e) { return null; }
			}
		}
		return gif;
	}
	
	public Animation transform(Animation a) {
		for (AnimationFrame frame : a.frames) {
			frame.image = transform(frame.image);
		}
		return a;
	}
}
