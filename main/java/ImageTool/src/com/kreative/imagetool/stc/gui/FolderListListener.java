package com.kreative.imagetool.stc.gui;

import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

public class FolderListListener implements ListSelectionListener {
	private final FolderList folderList;
	private final FileList fileList;
	
	public FolderListListener(FolderList folderList, FileList fileList) {
		this.folderList = folderList;
		this.fileList = fileList;
	}
	
	@Override
	public void valueChanged(ListSelectionEvent e) {
		if (e.getValueIsAdjusting()) return;
		fileList.setListData(folderList.getSelectedFile());
	}
}
