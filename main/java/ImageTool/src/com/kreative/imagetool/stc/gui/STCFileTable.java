package com.kreative.imagetool.stc.gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FileDialog;
import java.awt.Frame;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;

import javax.swing.DefaultCellEditor;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.TableColumn;

import com.kreative.imagetool.ImageIO;
import com.kreative.imagetool.stc.STCEntry;
import com.kreative.imagetool.stc.STCFile;
import com.kreative.imagetool.stc.STCFileListener;

public class STCFileTable extends JTable {
	private static final long serialVersionUID = 1L;
	
	private final File root;
	private final STCFile stc;
	private final STCFileTableModel model;
	
	public STCFileTable(File root, STCFile stc) {
		this.root = root;
		this.stc = stc;
		setModel(model = new STCFileTableModel(stc));
		setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		setGridColor(new Color(255, 255, 255, 0));
		setIntercellSpacing(new Dimension(0,0));
		setRowHeight(20);
		setColumnWidth(0, 40);
		setColumnWidth(2, 60);
		getColumnModel().getColumn(2).setCellEditor(
			new DefaultCellEditor(new JComboBox(new String[] {
				 "5s", "10s", "15s", "20s", "25s", "30s",
				"35s", "40s", "45s", "50s", "55s", "60s"
			}))
		);
		
		stc.addSTCFileListener(new STCFileListener() {
			@Override
			public void stcNameChanged(STCFile stc) {
				// nop
			}
			@Override
			public void stcEntriesChanged(STCFile stc) {
				int index = getSelectedRow();
				model.fireTableDataChanged();
				if (index < model.getRowCount()) {
					getSelectionModel().setSelectionInterval(index, index);
				}
			}
			@Override
			public void stcSelectionChanged(STCFile stc) {
				int index = getSelectedRow();
				model.fireTableDataChanged();
				if (index < model.getRowCount()) {
					getSelectionModel().setSelectionInterval(index, index);
				}
			}
		});
		addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if (e.isControlDown() || e.isMetaDown()) {
					switch (e.getKeyCode()) {
						case KeyEvent.VK_BACK_SPACE:
						case KeyEvent.VK_DELETE:
							deleteFile(false);
							e.consume();
							break;
						case KeyEvent.VK_UP:
							moveUp(e.isShiftDown());
							e.consume();
							break;
						case KeyEvent.VK_DOWN:
							moveDown(e.isShiftDown());
							e.consume();
							break;
					}
				}
			}
		});
	}
	
	public void addFile(File file) {
		if (file == null) {
			FileDialog fd = new FileDialog(getParentFrame(), "Add", FileDialog.LOAD);
			fd.setVisible(true);
			if (fd.getDirectory() == null || fd.getFile() == null) return;
			file = new File(fd.getDirectory(), fd.getFile());
		}
		try {
			Object image = ImageIO.readFile(file);
			if (image == null) throw new IOException();
			
			String name = file.getName();
			int o = name.lastIndexOf('.');
			if (o > 0) name = name.substring(0, o);
			String n = name.replaceAll("[^A-Za-z0-9]", "").toUpperCase();
			if (n.length() == 0) n = "SPRITE00";
			String path = stc.uniquePath("SPRITES/", n, ".SMF");
			STCEntry entry = new STCEntry(path, name);
			
			ImageIO.writeFile(image, "smf", entry.file(root));
			
			int index = getSelectedRow();
			int count = stc.size();
			if (index >= 0 && index < count) index++;
			else index = count;
			stc.add(index, entry);
			getSelectionModel().setSelectionInterval(index, index);
		} catch (IOException e) {
			JOptionPane.showMessageDialog(
				this, "Could not add Ò" + file.getName() + "Ó.",
				"Add", JOptionPane.ERROR_MESSAGE
			);
		}
	}
	
	public void deleteFile(boolean force) {
		int index = getSelectedRow();
		int count = stc.size();
		if (index >= 0 && index < count && count > 1) {
			STCEntry e = stc.get(index);
			if (
				force ||
				JOptionPane.showConfirmDialog(
					this, "Are you sure you want to delete Ò" + e.name() + "Ó?",
					"Delete", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE
				) == JOptionPane.YES_OPTION
			) {
				if (e.file(root).delete()) {
					stc.remove(index);
				} else {
					JOptionPane.showMessageDialog(
						this, "Could not delete Ò" + e.name() + "Ó.",
						"Delete", JOptionPane.ERROR_MESSAGE
					);
				}
			}
		}
	}
	
	public void moveUp(boolean toTop) {
		int index = getSelectedRow();
		int count = stc.size();
		if (index > 0 && index < count) {
			int ni = toTop ? 0 : (index - 1);
			stc.add(ni, stc.remove(index));
			getSelectionModel().setSelectionInterval(ni, ni);
		}
	}
	
	public void moveDown(boolean toBottom) {
		int index = getSelectedRow();
		int count = stc.size();
		if (index >= 0 && index < count - 1) {
			int ni = toBottom ? (count - 1) : (index + 1);
			stc.add(ni, stc.remove(index));
			getSelectionModel().setSelectionInterval(ni, ni);
		}
	}
	
	private void setColumnWidth(int col, int width) {
		TableColumn c = getColumnModel().getColumn(col);
		c.setMinWidth(width);
		c.setMaxWidth(width);
		c.setWidth(width);
	}
	
	private Frame getParentFrame() {
		Component c = this;
		while (true) {
			if (c == null) return new Frame();
			if (c instanceof Frame) return (Frame)c;
			c = c.getParent();
		}
	}
}
