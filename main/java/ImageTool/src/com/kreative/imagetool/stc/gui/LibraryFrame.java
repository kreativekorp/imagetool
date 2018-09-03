package com.kreative.imagetool.stc.gui;

import java.io.File;
import javax.swing.BorderFactory;
import javax.swing.JFrame;

public class LibraryFrame extends JFrame {
	private static final long serialVersionUID = 1L;
	
	public LibraryFrame(File library, STCFileTable table) {
		LibraryPanel panel = new LibraryPanel(library, table);
		panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
		setContentPane(panel);
		pack();
		setLocationRelativeTo(null);
	}
}
