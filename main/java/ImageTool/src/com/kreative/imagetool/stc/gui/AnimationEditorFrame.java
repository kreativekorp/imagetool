package com.kreative.imagetool.stc.gui;

import java.io.File;
import java.io.IOException;
import javax.swing.BorderFactory;
import javax.swing.JFrame;
import com.kreative.imagetool.animation.Animation;

public class AnimationEditorFrame extends JFrame {
	private static final long serialVersionUID = 1L;
	
	public AnimationEditorFrame(File file, String format) throws IOException {
		this(new AnimationEditorPanel(file, format));
	}
	
	public AnimationEditorFrame(File file, String format, Animation a) {
		this(new AnimationEditorPanel(file, format, a));
	}
	
	private AnimationEditorFrame(AnimationEditorPanel panel) {
		panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
		setContentPane(panel);
		setResizable(false);
		pack();
		setLocationRelativeTo(null);
	}
}
