package com.kreative.imagetool.transform;

import java.awt.Graphics2D;
import java.awt.Insets;
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

public class RemoveMargin implements Transform {
	private final int top, left, bottom, right;
	
	public RemoveMargin(int top, int left, int bottom, int right) {
		this.top = top;
		this.left = left;
		this.bottom = bottom;
		this.right = right;
	}
	
	public RemoveMargin(Insets i) {
		this.top = i.top;
		this.left = i.left;
		this.bottom = i.bottom;
		this.right = i.right;
	}
	
	public BufferedImage transform(BufferedImage image) {
		int w = image.getWidth() - left - right;
		int h = image.getHeight() - top - bottom;
		if (w < 1) w = 1;
		if (h < 1) h = 1;
		BufferedImage newImage = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = newImage.createGraphics();
		g.drawImage(image, null, -left, -top);
		g.dispose();
		return newImage;
	}
	
	public GCIFile transform(GCIFile gci) {
		gci.width -= (left + right);
		gci.height -= (top + bottom);
		if (gci.width < 1) gci.width = 1;
		if (gci.height < 1) gci.height = 1;
		for (GCIBlock block : gci.blocks) {
			block.setImage(gci, transform(block.getImage(gci)));
		}
		return gci;
	}
	
	public GIFFile transform(GIFFile gif) {
		gif.width -= (left + right);
		gif.height -= (top + bottom);
		if (gif.width < 1) gif.width = 1;
		if (gif.height < 1) gif.height = 1;
		for (GIFBlock block : gif.blocks) {
			if (block instanceof GIFImageDescriptor) {
				GIFImageDescriptor gid = (GIFImageDescriptor)block;
				gid.left -= left;
				gid.top -= top;
				try { gid.fixBounds(gif); }
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
		a.width -= (left + right);
		a.height -= (top + bottom);
		if (a.width < 1) a.width = 1;
		if (a.height < 1) a.height = 1;
		for (AnimationFrame frame : a.frames) {
			frame.image = transform(frame.image);
		}
		return a;
	}
}
