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
import com.kreative.imagetool.gif.GIFImageDescriptor;
import com.kreative.imagetool.smf.SMFFile;

public class ScaleInteger implements Transform {
	private final int sx, sy;
	
	public ScaleInteger(int sx, int sy) {
		if (sx < 1 || sy < 1) throw new IllegalArgumentException();
		this.sx = sx;
		this.sy = sy;
	}
	
	public BufferedImage transform(BufferedImage image) {
		int w = image.getWidth();
		int h = image.getHeight();
		int[] p = new int[w * h];
		image.getRGB(0, 0, w, h, p, 0, w);
		
		int nw = w * sx;
		int nh = h * sy;
		int[] np = new int[nw * nh];
		for (int y = 0, ny = 0; y < p.length; y += w) {
			for (int j = 0; j < sy; j++, ny += nw) {
				for (int x = 0, nx = 0; x < w; x++) {
					for (int i = 0; i < sx; i++, nx++) {
						np[ny + nx] = p[y + x];
					}
				}
			}
		}
		
		BufferedImage newImage = new BufferedImage(nw, nh, BufferedImage.TYPE_INT_ARGB);
		newImage.setRGB(0, 0, nw, nh, np, 0, nw);
		return newImage;
	}
	
	public GCIFile transform(GCIFile gci) {
		gci.width *= sx;
		gci.height *= sy;
		for (GCIBlock block : gci.blocks) {
			block.setImage(gci, transform(block.getImage(gci)));
		}
		return gci;
	}
	
	public GIFFile transform(GIFFile gif) {
		gif.width *= sx;
		gif.height *= sy;
		for (GIFBlock block : gif.blocks) {
			if (block instanceof GIFImageDescriptor) {
				GIFImageDescriptor gid = (GIFImageDescriptor)block;
				int w = gid.width;
				int h = gid.height;
				byte[] d;
				try { d = gid.getUncompressedData(); }
				catch (IOException e) { return null; }
				
				int nw = w * sx;
				int nh = h * sy;
				byte[] nd = new byte[nw * nh];
				for (int y = 0, ny = 0; y < d.length; y += w) {
					for (int j = 0; j < sy; j++, ny += nw) {
						for (int x = 0, nx = 0; x < w; x++) {
							for (int i = 0; i < sx; i++, nx++) {
								if (y + x < d.length) {
									nd[ny + nx] = d[y + x];
								}
							}
						}
					}
				}
				
				gid.left *= sx;
				gid.top *= sy;
				gid.width = nw;
				gid.height = nh;
				try { gid.setUncompressedData(nd); }
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
		a.width *= sx;
		a.height *= sy;
		for (AnimationFrame frame : a.frames) {
			frame.image = transform(frame.image);
		}
		return a;
	}
}
