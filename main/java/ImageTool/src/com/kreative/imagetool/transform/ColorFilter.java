package com.kreative.imagetool.transform;

import java.awt.image.BufferedImage;
import com.kreative.imagetool.animation.Animation;
import com.kreative.imagetool.animation.AnimationFrame;
import com.kreative.imagetool.gci.GCIBlock;
import com.kreative.imagetool.gci.GCIFile;
import com.kreative.imagetool.gif.GIFBlock;
import com.kreative.imagetool.gif.GIFFile;
import com.kreative.imagetool.gif.GIFImageDescriptor;
import com.kreative.imagetool.smf.SMFDirective;
import com.kreative.imagetool.smf.SMFFile;
import com.kreative.imagetool.smf.SMFFillDirective;
import com.kreative.imagetool.smf.SMFPushDirective;

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
	
	public GCIFile transform(GCIFile gci) {
		for (GCIBlock block : gci.blocks) {
			block.setImage(gci, transform(block.getImage(gci)));
		}
		return gci;
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
	
	public SMFFile transform(SMFFile smf) {
		for (SMFDirective dir : smf.directives) {
			if (dir instanceof SMFFillDirective) {
				SMFFillDirective f = (SMFFillDirective)dir;
				f.color = transform(f.color);
			} else if (dir instanceof SMFPushDirective) {
				SMFPushDirective p = (SMFPushDirective)dir;
				p.setImage(transform(p.getImage()));
			}
		}
		return smf;
	}
	
	public Animation transform(Animation a) {
		for (AnimationFrame frame : a.frames) {
			frame.image = transform(frame.image);
		}
		return a;
	}
}
