package com.kreative.imagetool.gci;

import java.util.Iterator;

public class GCIFrameIterator implements Iterator<GCIFrame> {
	private GCIFile gci;
	private Iterator<GCIBlock> bi;
	
	public GCIFrameIterator(GCIFile gci) {
		this.gci = gci;
		this.bi = gci.blocks.iterator();
	}
	
	public boolean hasNext() {
		return bi.hasNext();
	}
	
	public GCIFrame next() {
		return new GCIFrame(gci, bi.next());
	}
	
	public void remove() {
		bi.remove();
	}
}
