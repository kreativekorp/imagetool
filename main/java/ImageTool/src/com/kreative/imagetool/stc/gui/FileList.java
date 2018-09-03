package com.kreative.imagetool.stc.gui;

import java.io.File;
import java.io.FileFilter;
import javax.swing.JList;

public class FileList extends JList {
	private static final long serialVersionUID = 1L;
	
	public FileList() {
		setCellRenderer(new FileListCellRenderer());
	}
	
	public FileList(File root) {
		setCellRenderer(new FileListCellRenderer());
		setListData(root);
	}
	
	public File getSelectedFile() {
		Object o = getSelectedValue();
		if (o instanceof File) return (File)o;
		return null;
	}
	
	public void setListData(File root) {
		if (root == null) setListData(new Object[0]);
		else setListData(root.listFiles(new FileFilter() {
			@Override
			public boolean accept(File f) {
				if (!f.isFile()) return false;
				String name = f.getName().toLowerCase();
				if (name.startsWith(".")) return false;
				if (name.endsWith(".db")) return false;
				if (name.endsWith(".desktop")) return false;
				if (name.endsWith("\r")) return false;
				return true;
			}
		}));
	}
}
