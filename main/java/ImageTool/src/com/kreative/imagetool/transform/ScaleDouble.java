package com.kreative.imagetool.transform;

import java.awt.image.BufferedImage;
import java.io.IOException;
import com.kreative.imagetool.animation.Animation;
import com.kreative.imagetool.animation.AnimationFrame;
import com.kreative.imagetool.animation.AnimationIO;
import com.kreative.imagetool.gci.GCIBlock;
import com.kreative.imagetool.gci.GCIFile;
import com.kreative.imagetool.gif.GIFBlock;
import com.kreative.imagetool.gif.GIFFile;
import com.kreative.imagetool.gif.GIFGraphicControlExtension;
import com.kreative.imagetool.gif.GIFImageDescriptor;
import com.kreative.imagetool.smf.SMFFile;

public class ScaleDouble implements Transform {
	private final double sx, sy;
	
	public ScaleDouble(double sx, double sy) {
		if (sx <= 0 || sy <= 0) throw new IllegalArgumentException();
		this.sx = sx;
		this.sy = sy;
	}
	
	public BufferedImage transform(BufferedImage image) {
		int nw = (int)Math.round(image.getWidth() * sx);
		int nh = (int)Math.round(image.getHeight() * sy);
		if (nw < 1) nw = 1;
		if (nh < 1) nh = 1;
		return new ImageSize(nw, nh).transform(image);
	}
	
	public GCIFile transform(GCIFile gci) {
		gci.width = (int)Math.round(gci.width * sx);
		gci.height = (int)Math.round(gci.height * sy);
		if (gci.width < 1) gci.width = 1;
		if (gci.height < 1) gci.height = 1;
		ImageSize r = new ImageSize(gci.width, gci.height);
		for (GCIBlock block : gci.blocks) {
			block.setImage(gci, r.transform(block.getImage(gci)));
		}
		return gci;
	}
	
	public GIFFile transform(GIFFile gif) {
		gif.width = (int)Math.round(gif.width * sx);
		gif.height = (int)Math.round(gif.height * sy);
		if (gif.width < 1) gif.width = 1;
		if (gif.height < 1) gif.height = 1;
		GIFGraphicControlExtension gce = null;
		for (GIFBlock block : gif.blocks) {
			if (block instanceof GIFGraphicControlExtension) {
				gce = (GIFGraphicControlExtension)block;
			} else if (block instanceof GIFImageDescriptor) {
				GIFImageDescriptor gid = (GIFImageDescriptor)block;
				BufferedImage img;
				try { img = gid.getImage(gif, gce); }
				catch (IOException e) { return null; }
				gid.left = (int)Math.round(gid.left * sx);
				gid.top = (int)Math.round(gid.top * sy);
				img = transform(img);
				try { gid.setImage(gif, gce, img); }
				catch (IOException e) { return null; }
			}
		}
		return gif;
	}
	
	public SMFFile transform(SMFFile smf) {
		Animation a = transform(AnimationIO.fromSMFFile(smf));
		return AnimationIO.toSMFFile(a, smf.isRepeating());
	}
	
	public Animation transform(Animation a) {
		a.width = (int)Math.round(a.width * sx);
		a.height = (int)Math.round(a.height * sy);
		if (a.width < 1) a.width = 1;
		if (a.height < 1) a.height = 1;
		ImageSize r = new ImageSize(a.width, a.height);
		for (AnimationFrame f : a.frames) {
			f.image = r.transform(f.image);
		}
		return a;
	}
}
