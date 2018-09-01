package com.kreative.imagetool.stc;

public interface STCFileListener {
	public void stcNameChanged(STCFile stc);
	public void stcEntriesChanged(STCFile stc);
	public void stcSelectionChanged(STCFile stc);
}
