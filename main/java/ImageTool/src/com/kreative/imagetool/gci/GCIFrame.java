package com.kreative.imagetool.gci;

import java.awt.image.BufferedImage;

public class GCIFrame {
	public final GCIFile gci;
	public final GCIBlock block;
	public final BufferedImage image;
	
	public GCIFrame(GCIFile gci, GCIBlock block) {
		this.gci = gci;
		this.block = block;
		this.image = block.getImage(gci);
	}
}
