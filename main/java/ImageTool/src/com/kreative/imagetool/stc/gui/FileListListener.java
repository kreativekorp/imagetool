package com.kreative.imagetool.stc.gui;

import java.io.File;
import java.io.IOException;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import com.kreative.imagetool.ImageIO;
import com.kreative.imagetool.animation.Animation;
import com.kreative.imagetool.animation.AnimationIO;

public class FileListListener implements ListSelectionListener {
	private final FileList list;
	private final STCAnimationPanel panel;
	private Thread loaderThread;
	
	public FileListListener(FileList list, STCAnimationPanel panel) {
		this.list = list;
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
				File f = list.getSelectedFile();
				if (f != null) {
					try {
						Object o = ImageIO.readFile(f);
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
