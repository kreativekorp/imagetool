package com.kreative.imagetool.smf;

import java.awt.image.BufferedImage;

public class SMFFrame {
	public final SMFFile smf;
	public final BufferedImage image;
	public final int delay;
	
	public SMFFrame(SMFFile smf, BufferedImage image, int delay) {
		int w = image.getWidth();
		int h = image.getHeight();
		int[] rgb = new int[w * h];
		image.getRGB(0, 0, w, h, rgb, 0, w);
		image = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
		image.setRGB(0, 0, w, h, rgb, 0, w);
		this.smf = smf;
		this.image = image;
		this.delay = delay;
	}
}
