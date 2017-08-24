package com.kreative.imagetool.transform;

import java.awt.image.BufferedImage;
import com.kreative.imagetool.animation.Animation;
import com.kreative.imagetool.animation.AnimationFrame;
import com.kreative.imagetool.gif.GIFBlock;
import com.kreative.imagetool.gif.GIFFile;
import com.kreative.imagetool.gif.GIFGraphicControlExtension;

public class Speed implements Transform {
	private final double x;
	
	public Speed(double x) {
		if (x <= 0) throw new IllegalArgumentException();
		this.x = x;
	}
	
	public BufferedImage transform(BufferedImage image) {
		// Not supported for static images.
		return image;
	}
	
	public GIFFile transform(GIFFile gif) {
		for (GIFBlock block : gif.blocks) {
			if (block instanceof GIFGraphicControlExtension) {
				GIFGraphicControlExtension gce = (GIFGraphicControlExtension)block;
				gce.delayTime = (int)Math.round(gce.delayTime / x);
			}
		}
		return gif;
	}
	
	public Animation transform(Animation a) {
		for (AnimationFrame frame : a.frames) {
			frame.duration /= x;
		}
		return a;
	}
}
