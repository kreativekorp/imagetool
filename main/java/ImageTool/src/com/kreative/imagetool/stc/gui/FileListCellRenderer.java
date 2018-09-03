package com.kreative.imagetool.stc.gui;

import java.awt.Component;
import java.io.File;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;

public class FileListCellRenderer extends DefaultListCellRenderer {
	private static final long serialVersionUID = 1L;
	
	@Override
	public Component getListCellRendererComponent(JList l, Object v, int i, boolean s, boolean f) {
		if (v instanceof File) {
			String name = ((File)v).getName();
			int o = name.lastIndexOf('.');
			if (o > 0) name = name.substring(0, o);
			v = name;
		}
		return super.getListCellRendererComponent(l, v, i, s, f);
	}
}
