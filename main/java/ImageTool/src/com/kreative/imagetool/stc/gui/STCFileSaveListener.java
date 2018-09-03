package com.kreative.imagetool.stc.gui;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import com.kreative.imagetool.stc.STCFile;
import com.kreative.imagetool.stc.STCFileListener;

public class STCFileSaveListener implements STCFileListener {
	private final File file;
	
	public STCFileSaveListener(File file) {
		this.file = file;
	}
	
	private void write(STCFile stc) {
		try {
			FileOutputStream out = new FileOutputStream(file);
			stc.write(new DataOutputStream(out));
			out.close();
		} catch (IOException ioe) {
			System.err.println("Failed to write SPRITES.STC.");
		}
	}
	
	@Override
	public void stcNameChanged(STCFile stc) {
		write(stc);
	}
	
	@Override
	public void stcEntriesChanged(STCFile stc) {
		write(stc);
	}
	
	@Override
	public void stcSelectionChanged(STCFile stc) {
		write(stc);
	}
}
