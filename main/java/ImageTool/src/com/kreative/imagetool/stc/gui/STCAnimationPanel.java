package com.kreative.imagetool.stc.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import com.kreative.imagetool.animation.Animation;
import com.kreative.imagetool.animation.AnimationIO;
import com.kreative.imagetool.animation.AnimationThread;

public class STCAnimationPanel extends JPanel {
	private static final long serialVersionUID = 1L;
	
	private final JLabel animationLabel;
	private Animation animation;
	private Thread animationThread;
	
	public STCAnimationPanel() {
		animationLabel = new JLabel();
		animation = null;
		animationThread = null;
		
		animationLabel.setOpaque(true);
		animationLabel.setBackground(Color.black);
		animationLabel.setHorizontalAlignment(JLabel.CENTER);
		animationLabel.setVerticalAlignment(JLabel.CENTER);
		Dimension d = new Dimension(132, 132);
		animationLabel.setMinimumSize(d);
		animationLabel.setPreferredSize(d);
		animationLabel.setMaximumSize(d);
		
		JPanel p1 = new JPanel(new GridLayout());
		p1.add(animationLabel);
		p1.setBorder(BorderFactory.createLoweredBevelBorder());
		JPanel p2 = new JPanel(new GridLayout());
		p2.add(p1);
		p2.setBorder(BorderFactory.createMatteBorder(32, 32, 32, 32, Color.black));
		JPanel p3 = new JPanel(new GridLayout());
		p3.add(p2);
		p3.setBorder(BorderFactory.createRaisedBevelBorder());
		d = p3.getPreferredSize();
		p3.setMinimumSize(d);
		p3.setPreferredSize(d);
		p3.setMaximumSize(d);
		
		setLayout(new FlowLayout());
		add(p3);
	}
	
	public Animation getAnimation() {
		return animation;
	}
	
	public void setAnimation(Animation a) {
		if (animationThread != null) {
			animationThread.interrupt();
			animationThread = null;
		}
		animation = a;
		if (a != null) {
			animationLabel.setIcon(new ImageIcon(AnimationIO.toBufferedImage(a)));
			if (a.frames.size() > 1) {
				animationThread = new AnimationThread(a, animationLabel);
				animationThread.start();
			}
		} else {
			animationLabel.setIcon(null);
		}
		repaint();
	}
}
