package com.kreative.imagetool.transform;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import com.kreative.imagetool.animation.Animation;
import com.kreative.imagetool.animation.AnimationFrame;
import com.kreative.imagetool.animation.AnimationIO;
import com.kreative.imagetool.gci.GCIBlock;
import com.kreative.imagetool.gci.GCIFile;
import com.kreative.imagetool.gif.GIFFile;
import com.kreative.imagetool.smf.SMFFile;

public class BackgroundColor implements Transform {
	private final int color;
	
	public BackgroundColor(int color) {
		this.color = color;
	}
	
	public BufferedImage transform(BufferedImage image) {
		int w = image.getWidth();
		int h = image.getHeight();
		int[] pixels = new int[w * h];
		for (int i = 0; i < pixels.length; i++) pixels[i] = color;
		BufferedImage newImage = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
		newImage.setRGB(0, 0, w, h, pixels, 0, w);
		Graphics2D g = newImage.createGraphics();
		g.drawImage(image, null, 0, 0);
		g.dispose();
		return newImage;
	}
	
	public GCIFile transform(GCIFile gci) {
		for (GCIBlock block : gci.blocks) {
			block.setImage(gci, transform(block.getImage(gci)));
		}
		return gci;
	}
	
	public GIFFile transform(GIFFile gif) {
		Animation a = transform(AnimationIO.fromGIFFile(gif));
		return AnimationIO.toGIFFile(a, gif.getRepeatCount());
	}
	
	public SMFFile transform(SMFFile smf) {
		Animation a = transform(AnimationIO.fromSMFFile(smf));
		return AnimationIO.toSMFFile(a, smf.isRepeating());
	}
	
	public Animation transform(Animation a) {
		for (AnimationFrame frame : a.frames) {
			frame.image = transform(frame.image);
		}
		return a;
	}
}
