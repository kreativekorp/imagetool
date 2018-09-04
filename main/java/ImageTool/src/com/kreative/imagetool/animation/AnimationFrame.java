package com.kreative.imagetool.animation;

import java.awt.image.BufferedImage;

public class AnimationFrame {
	public BufferedImage image;
	public double duration;
	
	public AnimationFrame(BufferedImage image, double duration) {
		this.image = image;
		this.duration = duration;
	}
	
	public AnimationFrame copy() {
		return new AnimationFrame(image, duration);
	}
	
	public AnimationFrame deepCopy() {
		if (image == null) {
			return new AnimationFrame(null, duration);
		} else {
			int w = image.getWidth();
			int h = image.getHeight();
			int[] rgb = new int[w * h];
			image.getRGB(0, 0, w, h, rgb, 0, w);
			BufferedImage bi = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
			bi.setRGB(0, 0, w, h, rgb, 0, w);
			return new AnimationFrame(bi, duration);
		}
	}
}
