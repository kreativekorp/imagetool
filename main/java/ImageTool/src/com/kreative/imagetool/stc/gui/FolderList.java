package com.kreative.imagetool.stc.gui;

import java.io.File;
import java.io.FileFilter;
import javax.swing.JList;

public class FolderList extends JList {
	private static final long serialVersionUID = 1L;
	
	public FolderList() {
		setCellRenderer(new FileListCellRenderer());
	}
	
	public FolderList(File root) {
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
				if (!f.isDirectory()) return false;
				String name = f.getName().toLowerCase();
				if (name.startsWith(".")) return false;
				return true;
			}
		}));
	}
}
