package com.kreative.imagetool.stc.gui;

import javax.swing.table.AbstractTableModel;
import com.kreative.imagetool.stc.STCCycleTime;
import com.kreative.imagetool.stc.STCEntry;
import com.kreative.imagetool.stc.STCFile;
import com.kreative.imagetool.stc.STCRandomizeTime;
import com.kreative.imagetool.stc.STCSelection;

public class STCFileTableModel extends AbstractTableModel {
	private static final long serialVersionUID = 1L;
	
	private final STCFile stc;
	
	public STCFileTableModel(STCFile stc) {
		this.stc = stc;
	}
	
	@Override
	public int getColumnCount() {
		return 3;
	}
	
	@Override
	public Class<?> getColumnClass(int col) {
		switch (col) {
			case 0: return Boolean.class;
			case 1: return String.class;
			case 2: return String.class;
			default: return null;
		}
	}
	
	@Override
	public String getColumnName(int col) {
		switch (col) {
			case 0: return "Def";
			case 1: return "Name";
			case 2: return "Time";
			default: return null;
		}
	}
	
	@Override
	public int getRowCount() {
		return stc.size() + 2;
	}
	
	@Override
	public Object getValueAt(int row, int col) {
		if (row < stc.size()) {
			switch (col) {
				case 0: return stc.get(row).equals(stc.getSelection());
				case 1: return stc.get(row).name();
				default: return null;
			}
		} else switch (row - stc.size()) {
			case 0:
				switch (col) {
					case 0: return stc.getSelection() instanceof STCCycleTime;
					case 1: return "Cycle Through";
					case 2: return (stc.getSelection() instanceof STCCycleTime) ?
					               ((STCCycleTime)stc.getSelection()).toString() : null;
					default: return null;
				}
			case 1:
				switch (col) {
					case 0: return stc.getSelection() instanceof STCRandomizeTime;
					case 1: return "Randomize";
					case 2: return (stc.getSelection() instanceof STCRandomizeTime) ?
					               ((STCRandomizeTime)stc.getSelection()).toString() : null;
					default: return null;
				}
			default: return null;
		}
	}
	
	@Override
	public boolean isCellEditable(int row, int col) {
		if (row < stc.size()) {
			return (col == 0 || col == 1);
		} else {
			return (col == 0 || col == 2);
		}
	}
	
	@Override
	public void setValueAt(Object value, int row, int col) {
		if (row < stc.size()) {
			switch (col) {
				case 0:
					stc.setSelection(stc.get(row));
					break;
				case 1:
					String newName = value.toString().trim();
					if (newName.length() > 0) {
						STCEntry oldEntry = stc.get(row);
						STCEntry newEntry = new STCEntry(oldEntry.path(), newName);
						boolean selected = oldEntry.equals(stc.getSelection());
						stc.set(row, newEntry);
						if (selected) stc.setSelection(newEntry);
					}
					break;
			}
		} else switch (row - stc.size()) {
			case 0:
				switch (col) {
					case 0:
						STCSelection sel = stc.getSelection();
						if (!(sel instanceof STCCycleTime)) {
							int millis = 10000;
							if (sel instanceof STCRandomizeTime) millis = ((STCRandomizeTime)sel).millis();
							stc.setSelection(new STCCycleTime(millis));
						}
						break;
					case 2:
						String ms = value.toString().replaceAll("[^0-9]", "");
						if (ms.length() > 0) {
							int millis = Integer.parseInt(ms) * 1000;
							stc.setSelection(new STCCycleTime(millis));
						}
						break;
				}
				break;
			case 1:
				switch (col) {
					case 0:
						STCSelection sel = stc.getSelection();
						if (!(sel instanceof STCRandomizeTime)) {
							int millis = 10000;
							if (sel instanceof STCCycleTime) millis = ((STCCycleTime)sel).millis();
							stc.setSelection(new STCRandomizeTime(millis));
						}
						break;
					case 2:
						String ms = value.toString().replaceAll("[^0-9]", "");
						if (ms.length() > 0) {
							int millis = Integer.parseInt(ms) * 1000;
							stc.setSelection(new STCRandomizeTime(millis));
						}
						break;
				}
				break;
		}
	}
}
