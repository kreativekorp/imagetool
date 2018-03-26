package com.kreative.imagetool.transform;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.IOException;
import com.kreative.imagetool.animation.Animation;
import com.kreative.imagetool.animation.AnimationFrame;
import com.kreative.imagetool.gci.GCIBlock;
import com.kreative.imagetool.gci.GCIFile;
import com.kreative.imagetool.gif.GIFBlock;
import com.kreative.imagetool.gif.GIFFile;
import com.kreative.imagetool.gif.GIFGraphicControlExtension;
import com.kreative.imagetool.gif.GIFImageDescriptor;

public class ImageSize implements Transform {
	private final int width, height;
	
	public ImageSize(int width, int height) {
		if (width < 1 || height < 1) throw new IllegalArgumentException();
		this.width = width;
		this.height = height;
	}
	
	public BufferedImage transform(BufferedImage image) {
		int w = image.getWidth();
		int h = image.getHeight();
		while (w != width || h != height) {
			if (w > width) {
				w /= 2; if (w < width) w = width;
			} else if (w < width) {
				w *= 2; if (w > width) w = width;
			}
			if (h > height) {
				h /= 2; if (h < height) h = height;
			} else if (h < height) {
				h *= 2; if (h > height) h = height;
			}
			BufferedImage bi = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
			Graphics2D g = bi.createGraphics();
			g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
			g.drawImage(image, 0, 0, w, h, null);
			g.dispose();
			image = bi;
		}
		return image;
	}
	
	public GCIFile transform(GCIFile gci) {
		gci.width = width;
		gci.height = height;
		for (GCIBlock block : gci.blocks) {
			block.setImage(gci, transform(block.getImage(gci)));
		}
		return gci;
	}
	
	public GIFFile transform(GIFFile gif) {
		double sx = (double)width / (double)gif.width;
		double sy = (double)height / (double)gif.height;
		ScaleDouble scaler = new ScaleDouble(sx, sy);
		gif.width = width;
		gif.height = height;
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
				img = scaler.transform(img);
				try { gid.setImage(gif, gce, img); }
				catch (IOException e) { return null; }
			}
		}
		return gif;
	}
	
	public Animation transform(Animation a) {
		a.width = width;
		a.height = height;
		for (AnimationFrame frame : a.frames) {
			frame.image = transform(frame.image);
		}
		return a;
	}
}
