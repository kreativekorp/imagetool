package com.kreative.imagetool.smf;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class SMFFrameIterator implements Iterator<SMFFrame> {
	private final Iterator<SMFFrame> impl;
	
	public SMFFrameIterator(SMFFile smf) {
		this.impl = directivesToFrames(smf).iterator();
	}
	
	public boolean hasNext() {
		return impl.hasNext();
	}
	
	public SMFFrame next() {
		return impl.next();
	}
	
	public void remove() {
		throw new UnsupportedOperationException();
	}
	
	private static List<SMFFrame> directivesToFrames(SMFFile smf) {
		List<SMFFrame> frames = new ArrayList<SMFFrame>();
		BufferedImage image = new BufferedImage(128, 128, BufferedImage.TYPE_INT_ARGB);
		for (SMFDirective dir : smf.directives) {
			if (dir instanceof SMFAllocateDirective) {
				SMFAllocateDirective a = (SMFAllocateDirective)dir;
				image = new BufferedImage(a.width, a.height, BufferedImage.TYPE_INT_ARGB);
			} else if (dir instanceof SMFFillDirective) {
				SMFFillDirective f = (SMFFillDirective)dir;
				Graphics2D g = image.createGraphics();
				g.setColor(new Color(f.color));
				g.fillRect(f.x, f.y, f.width, f.height);
				g.dispose();
			} else if (dir instanceof SMFPushDirective) {
				SMFPushDirective p = (SMFPushDirective)dir;
				Graphics2D g = image.createGraphics();
				g.drawImage(p.getImage(), null, p.x, p.y);
				g.dispose();
			} else if (dir instanceof SMFWaitDirective) {
				SMFWaitDirective w = (SMFWaitDirective)dir;
				frames.add(new SMFFrame(smf, image, w.delay));
			} else if (dir instanceof SMFSeekDirective) {
				// Assume the seek is to either the first or second frame.
				// If the first frame (s.sector is zero),
				// the animation loops back to the beginning.
				// If the second frame (s.sector is nonzero),
				// the last frame is assumed equivalent to
				// the first frame and is removed.
				SMFSeekDirective s = (SMFSeekDirective)dir;
				if (s.sector != 0 && frames.size() > 1) {
					frames.remove(frames.size() - 1);
				}
				break;
			} else if (dir instanceof SMFHaltDirective) {
				frames.add(new SMFFrame(smf, image, 0));
				break;
			}
		}
		return frames;
	}
}
