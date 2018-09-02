package com.kreative.imagetool.animation;

import java.awt.Component;
import javax.swing.ImageIcon;
import javax.swing.JLabel;

public class AnimationThread extends Thread {
	private final Animation a;
	private final JLabel l;
	private int fc = 0;
	
	public AnimationThread(Animation a, JLabel l) {
		this.a = a;
		this.l = l;
	}
	
	public void run() {
		while (!Thread.interrupted()) {
			try { update(); }
			catch (InterruptedException e) { break; }
		}
	}
	
	private void update() throws InterruptedException {
		for (Component c = l; c != null; c = c.getParent()) {
			if (!c.isVisible()) return;
		}
		AnimationFrame af = a.frames.get(fc);
		l.setIcon(new ImageIcon(af.image));
		Thread.sleep((long)(af.duration * 1000));
		fc = (fc + 1) % a.frames.size();
	}
}
