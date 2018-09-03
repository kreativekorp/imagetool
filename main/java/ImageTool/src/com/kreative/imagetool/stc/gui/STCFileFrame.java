package com.kreative.imagetool.stc.gui;

import java.io.File;
import javax.swing.BorderFactory;
import javax.swing.JFrame;
import com.kreative.imagetool.stc.STCFile;

public class STCFileFrame extends JFrame {
	private static final long serialVersionUID = 1L;
	
	public STCFileFrame(File root, File file, STCFile stc) {
		STCFilePanel panel = new STCFilePanel(root, file, stc);
		panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
		setContentPane(panel);
		pack();
		setLocationRelativeTo(null);
	}
}
