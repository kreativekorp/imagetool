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

public class FlipDiagonal implements Transform {
	public BufferedImage transform(BufferedImage image) {
		int sw = image.getWidth();
		int sh = image.getHeight();
		int[] pixels = new int[sw * sh];
		image.getRGB(0, 0, sw, sh, pixels, 0, sw);
		
		int dw = sh;
		int dh = sw;
		int[] slexip = new int[dw * dh];
		for (int dy = 0, sx = 0; dy < slexip.length; dy += dw, sx++) {
			for (int dx = 0, sy = 0; dx < dw; dx++, sy += sw) {
				slexip[dy + dx] = pixels[sy + sx];
			}
		}
		
		BufferedImage newImage = new BufferedImage(dw, dh, BufferedImage.TYPE_INT_ARGB);
		newImage.setRGB(0, 0, dw, dh, slexip, 0, dw);
		return newImage;
	}
	
	public GCIFile transform(GCIFile gci) {
		int nw = gci.height;
		int nh = gci.width;
		gci.width = nw;
		gci.height = nh;
		for (GCIBlock block : gci.blocks) {
			block.setImage(gci, transform(block.getImage(gci)));
		}
		return gci;
	}
	
	public GIFFile transform(GIFFile gif) {
		int nw = gif.height;
		int nh = gif.width;
		gif.width = nw;
		gif.height = nh;
		for (GIFBlock block : gif.blocks) {
			if (block instanceof GIFImageDescriptor) {
				GIFImageDescriptor gid = (GIFImageDescriptor)block;
				int sw = gid.width;
				int sh = gid.height;
				byte[] data;
				try { data = gid.getUncompressedData(); }
				catch (IOException e) { return null; }
				
				int dw = sh;
				int dh = sw;
				byte[] atad = new byte[dw * dh];
				for (int dy = 0, sx = 0; dy < atad.length; dy += dw, sx++) {
					for (int dx = 0, sy = 0; dx < dw; dx++, sy += sw) {
						if (sy + sx < data.length) {
							atad[dy + dx] = data[sy + sx];
						}
					}
				}
				
				int dx = gid.top;
				int dy = gid.left;
				gid.left = dx;
				gid.top = dy;
				gid.width = dw;
				gid.height = dh;
				try { gid.setUncompressedData(atad); }
				catch (IOException e) { return null; }
			}
		}
		return gif;
	}
	
	public Animation transform(Animation a) {
		int nw = a.height;
		int nh = a.width;
		a.width = nw;
		a.height = nh;
		for (AnimationFrame frame : a.frames) {
			frame.image = transform(frame.image);
		}
		return a;
	}
}
