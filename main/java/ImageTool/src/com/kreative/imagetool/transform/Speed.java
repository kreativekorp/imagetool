package com.kreative.imagetool.transform;

import java.awt.image.BufferedImage;
import com.kreative.imagetool.animation.Animation;
import com.kreative.imagetool.animation.AnimationFrame;
import com.kreative.imagetool.gci.GCIFile;
import com.kreative.imagetool.gif.GIFBlock;
import com.kreative.imagetool.gif.GIFFile;
import com.kreative.imagetool.gif.GIFGraphicControlExtension;
import com.kreative.imagetool.smf.SMFDirective;
import com.kreative.imagetool.smf.SMFFile;
import com.kreative.imagetool.smf.SMFWaitDirective;

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
	
	public GCIFile transform(GCIFile gci) {
		if (gci.delay > 0) {
			gci.delay = (int)Math.round(gci.delay / x);
			if (gci.delay < 1) gci.delay = 1;
		}
		return gci;
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
	
	public SMFFile transform(SMFFile smf) {
		for (SMFDirective dir : smf.directives) {
			if (dir instanceof SMFWaitDirective) {
				SMFWaitDirective w = (SMFWaitDirective)dir;
				w.delay = (int)Math.round(w.delay / x);
			}
		}
		return smf;
	}
	
	public Animation transform(Animation a) {
		for (AnimationFrame frame : a.frames) {
			frame.duration /= x;
		}
		return a;
	}
}
