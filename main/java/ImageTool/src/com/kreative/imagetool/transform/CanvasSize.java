package com.kreative.imagetool.transform;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.swing.SwingConstants;
import com.kreative.imagetool.animation.Animation;
import com.kreative.imagetool.animation.AnimationFrame;
import com.kreative.imagetool.gif.GIFBlock;
import com.kreative.imagetool.gif.GIFFile;
import com.kreative.imagetool.gif.GIFImageDescriptor;

public class CanvasSize implements Transform, SwingConstants {
	private final int width, height, anchor;
	
	public CanvasSize(int width, int height, int anchor) {
		if (width < 1 || height < 1) throw new IllegalArgumentException();
		getAnchorX(anchor, 0, 0); getAnchorY(anchor, 0, 0);
		this.width = width;
		this.height = height;
		this.anchor = anchor;
	}
	
	public BufferedImage transform(BufferedImage image) {
		BufferedImage newImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = newImage.createGraphics();
		g.drawImage(
			image, null,
			getAnchorX(anchor, width, image.getWidth()),
			getAnchorY(anchor, height, image.getHeight())
		);
		g.dispose();
		return newImage;
	}
	
	public GIFFile transform(GIFFile gif) {
		int x = getAnchorX(anchor, width, gif.width);
		int y = getAnchorY(anchor, height, gif.height);
		gif.width = width;
		gif.height = height;
		for (GIFBlock block : gif.blocks) {
			if (block instanceof GIFImageDescriptor) {
				GIFImageDescriptor gid = (GIFImageDescriptor)block;
				gid.left += x;
				gid.top += y;
				try { gid.fixBounds(gif); }
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
	
	private static int getAnchorX(int anchor, int outerWidth, int innerWidth) {
		switch (anchor) {
			case NORTH_WEST: case WEST: case SOUTH_WEST: return 0;
			case NORTH_EAST: case EAST: case SOUTH_EAST: return outerWidth - innerWidth;
			case NORTH: case CENTER: case SOUTH: return (outerWidth - innerWidth) / 2;
			default: throw new IllegalArgumentException("invalid anchor");
		}
	}
	
	private static int getAnchorY(int anchor, int outerHeight, int innerHeight) {
		switch (anchor) {
			case NORTH_WEST: case NORTH: case NORTH_EAST: return 0;
			case SOUTH_WEST: case SOUTH: case SOUTH_EAST: return outerHeight - innerHeight;
			case WEST: case CENTER: case EAST: return (outerHeight - innerHeight) / 2;
			default: throw new IllegalArgumentException("invalid anchor");
		}
	}
}
