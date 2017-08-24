package com.kreative.imagetool.gif;

import java.awt.image.BufferedImage;

public class GIFFrame {
	public final GIFFile gif;
	public final GIFApplicationExtension nab;
	public final GIFGraphicControlExtension gce;
	public final GIFImageDescriptor gid;
	public final BufferedImage rawImage;
	public final BufferedImage composedImage;
	
	public GIFFrame(
		GIFFile gif,
		GIFApplicationExtension nab,
		GIFGraphicControlExtension gce,
		GIFImageDescriptor gid,
		BufferedImage rawImage,
		BufferedImage composedImage
	) {
		this.gif = gif;
		this.nab = nab;
		this.gce = gce;
		this.gid = gid;
		this.rawImage = rawImage;
		this.composedImage = composedImage;
	}
}
