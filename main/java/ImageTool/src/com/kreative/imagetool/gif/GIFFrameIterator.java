package com.kreative.imagetool.gif;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.Iterator;

public class GIFFrameIterator implements Iterator<GIFFrame> {
	private GIFFile gif;
	private GIFApplicationExtension nab;
	private GIFGraphicControlExtension gce;
	private BufferedImage ci;
	private int[] db;
	private int[] cb;
	private int blockIndex;
	private GIFFrame nextFrame;
	
	public GIFFrameIterator(GIFFile gif) {
		this.gif = gif;
		this.nab = null;
		this.gce = null;
		this.ci = new BufferedImage(gif.width, gif.height, BufferedImage.TYPE_INT_ARGB);
		this.db = new int[gif.width * gif.height];
		this.cb = new int[gif.width * gif.height];
		this.blockIndex = 0;
		this.nextFrame = advance();
	}
	
	public boolean hasNext() {
		return nextFrame != null;
	}
	
	public GIFFrame next() {
		GIFFrame f = nextFrame;
		nextFrame = advance();
		return f;
	}
	
	public void remove() {
		throw new UnsupportedOperationException();
	}
	
	private GIFFrame advance() {
		while (blockIndex < gif.blocks.size()) {
			GIFBlock block = gif.blocks.get(blockIndex++);
			if (block instanceof GIFApplicationExtension) {
				GIFApplicationExtension gae = (GIFApplicationExtension)block;
				if (gae.isNAB()) nab = gae;
			} else if (block instanceof GIFGraphicControlExtension) {
				gce = (GIFGraphicControlExtension)block;
			} else if (block instanceof GIFImageDescriptor) {
				GIFImageDescriptor gid = (GIFImageDescriptor)block;
				BufferedImage rawImage;
				try { rawImage = gid.getImage(gif, gce); }
				catch (Exception e) { rawImage = null; }
				if (rawImage != null) {
					if (gce != null && gce.disposalMethod != null) {
						switch (gce.disposalMethod) {
							case RESTORE_TO_BACKGROUND:
								for (int i = 0; i < db.length; i++) db[i] = 0;
								break;
							case RESTORE_TO_PREVIOUS:
								ci.getRGB(0, 0, gif.width, gif.height, db, 0, gif.width);
								break;
							default:
								break;
						}
					}
					Graphics2D g = ci.createGraphics();
					g.drawImage(rawImage, null, gid.left, gid.top);
					g.dispose();
				}
				BufferedImage composedImage = new BufferedImage(gif.width, gif.height, BufferedImage.TYPE_INT_ARGB);
				ci.getRGB(0, 0, gif.width, gif.height, cb, 0, gif.width);
				composedImage.setRGB(0, 0, gif.width, gif.height, cb, 0, gif.width);
				if (rawImage != null) {
					if (gce != null && gce.disposalMethod != null) {
						switch (gce.disposalMethod) {
							case RESTORE_TO_BACKGROUND:
							case RESTORE_TO_PREVIOUS:
								ci.setRGB(0, 0, gif.width, gif.height, db, 0, gif.width);
								break;
							default:
								break;
						}
					}
				}
				return new GIFFrame(gif, nab, gce, gid, rawImage, composedImage);
			}
		}
		return null;
	}
}
