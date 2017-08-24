package com.kreative.imagetool.transform;

import java.awt.image.BufferedImage;
import com.kreative.imagetool.animation.Animation;
import com.kreative.imagetool.animation.AnimationFrame;
import com.kreative.imagetool.gif.GIFBlock;
import com.kreative.imagetool.gif.GIFFile;
import com.kreative.imagetool.gif.GIFImageDescriptor;

public abstract class ColorFilter implements Transform {
	public abstract int transform(int pixel);
	
	public BufferedImage transform(BufferedImage image) {
		int w = image.getWidth();
		int h = image.getHeight();
		int[] pixels = new int[w * h];
		image.getRGB(0, 0, w, h, pixels, 0, w);
		for (int i = 0; i < pixels.length; i++) {
			pixels[i] = transform(pixels[i]);
		}
		BufferedImage newImage = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
		newImage.setRGB(0, 0, w, h, pixels, 0, w);
		return newImage;
	}
	
	public GIFFile transform(GIFFile gif) {
		if (gif.palette != null) {
			for (int i = 0; i < gif.palette.length; i++) {
				gif.palette[i] = transform(gif.palette[i]);
			}
		}
		for (GIFBlock block : gif.blocks) {
			if (block instanceof GIFImageDescriptor) {
				GIFImageDescriptor gid = (GIFImageDescriptor)block;
				if (gid.palette != null) {
					for (int i = 0; i < gid.palette.length; i++) {
						gid.palette[i] = transform(gid.palette[i]);
					}
				}
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
