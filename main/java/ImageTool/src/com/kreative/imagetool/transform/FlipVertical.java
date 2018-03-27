package com.kreative.imagetool.transform;

import java.awt.image.BufferedImage;
import java.io.IOException;
import com.kreative.imagetool.animation.Animation;
import com.kreative.imagetool.animation.AnimationFrame;
import com.kreative.imagetool.gci.GCIBlock;
import com.kreative.imagetool.gci.GCIFile;
import com.kreative.imagetool.gif.GIFBlock;
import com.kreative.imagetool.gif.GIFFile;
import com.kreative.imagetool.gif.GIFImageDescriptor;
import com.kreative.imagetool.smf.SMFAllocateDirective;
import com.kreative.imagetool.smf.SMFDirective;
import com.kreative.imagetool.smf.SMFFile;
import com.kreative.imagetool.smf.SMFFillDirective;
import com.kreative.imagetool.smf.SMFPushDirective;

public class FlipVertical implements Transform {
	public BufferedImage transform(BufferedImage image) {
		int w = image.getWidth();
		int h = image.getHeight();
		int[] pixels = new int[w * h];
		image.getRGB(0, 0, w, h, pixels, 0, w);
		
		int[] slexip = new int[w * h];
		for (int dy = 0, sy = w * (h - 1); dy < slexip.length; dy += w, sy -= w) {
			for (int x = 0; x < w; x++) {
				slexip[dy + x] = pixels[sy + x];
			}
		}
		
		image.setRGB(0, 0, w, h, slexip, 0, w);
		return image;
	}
	
	public GCIFile transform(GCIFile gci) {
		for (GCIBlock block : gci.blocks) {
			block.setImage(gci, transform(block.getImage(gci)));
		}
		return gci;
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
				for (int dy = 0, sy = w * (h - 1); dy < atad.length; dy += w, sy -= w) {
					for (int x = 0; x < w; x++) {
						if (sy + x < data.length) {
							atad[dy + x] = data[sy + x];
						}
					}
				}
				
				gid.top = gif.height - gid.top - h;
				try { gid.setUncompressedData(atad); }
				catch (IOException e) { return null; }
			}
		}
		return gif;
	}
	
	public SMFFile transform(SMFFile smf) {
		int smfHeight = 128;
		for (SMFDirective dir : smf.directives) {
			if (dir instanceof SMFAllocateDirective) {
				SMFAllocateDirective a = (SMFAllocateDirective)dir;
				smfHeight = a.height;
			} else if (dir instanceof SMFFillDirective) {
				SMFFillDirective f = (SMFFillDirective)dir;
				f.y = smfHeight - f.y - f.height;
			} else if (dir instanceof SMFPushDirective) {
				SMFPushDirective p = (SMFPushDirective)dir;
				p.setImage(transform(p.getImage()));
				p.y = smfHeight - p.y - p.height;
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
