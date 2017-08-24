package com.kreative.imagetool.transform;

import java.awt.image.BufferedImage;
import com.kreative.imagetool.animation.Animation;
import com.kreative.imagetool.gif.GIFFile;

public interface Transform {
	public BufferedImage transform(BufferedImage image);
	public GIFFile transform(GIFFile gif);
	public Animation transform(Animation a);
}
