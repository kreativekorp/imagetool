package com.kreative.imagetool.transform;

import java.awt.image.BufferedImage;
import com.kreative.imagetool.animation.Animation;
import com.kreative.imagetool.gci.GCIFile;
import com.kreative.imagetool.gif.GIFFile;
import com.kreative.imagetool.smf.SMFFile;

public interface Transform {
	public BufferedImage transform(BufferedImage image);
	public GCIFile transform(GCIFile gci);
	public GIFFile transform(GIFFile gif);
	public SMFFile transform(SMFFile smf);
	public Animation transform(Animation a);
}
