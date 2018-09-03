package com.kreative.imagetool.stc.gui;

import java.io.File;
import java.io.IOException;
import javax.swing.JTable;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import com.kreative.imagetool.ImageIO;
import com.kreative.imagetool.animation.Animation;
import com.kreative.imagetool.animation.AnimationIO;
import com.kreative.imagetool.stc.STCFile;

public class STCFileTableListener implements ListSelectionListener {
	private final File root;
	private final STCFile stc;
	private final JTable table;
	private final STCAnimationPanel panel;
	private Thread loaderThread;
	
	public STCFileTableListener(File root, STCFile stc, JTable table, STCAnimationPanel panel) {
		this.root = root;
		this.stc = stc;
		this.table = table;
		this.panel = panel;
		this.loaderThread = null;
	}
	
	@Override
	public void valueChanged(ListSelectionEvent e) {
		if (e.getValueIsAdjusting()) return;
		if (loaderThread != null) {
			loaderThread.interrupt();
			loaderThread = null;
		}
		panel.setAnimation(null);
		loaderThread = new Thread() {
			@Override
			public void run() {
				int index = table.getSelectedRow();
				if (index >= 0 && index < stc.size()) {
					try {
						File file = stc.get(index).file(root);
						Object o = ImageIO.readFile(file);
						if (o == null || Thread.interrupted()) return;
						Animation a = AnimationIO.fromObject(o);
						if (a == null || Thread.interrupted()) return;
						panel.setAnimation(a);
					} catch (IOException ioe) {
						return;
					}
				}
			}
		};
		loaderThread.start();
	}
}
