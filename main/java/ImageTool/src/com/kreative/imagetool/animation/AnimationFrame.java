package com.kreative.imagetool.animation;

import java.awt.image.BufferedImage;

public class AnimationFrame {
	public BufferedImage image;
	public double duration;
	
	public AnimationFrame(BufferedImage image, double duration) {
		this.image = image;
		this.duration = duration;
	}
}
