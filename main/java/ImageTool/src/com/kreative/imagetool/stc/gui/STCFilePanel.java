package com.kreative.imagetool.stc.gui;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.dnd.DropTarget;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import com.kreative.imagetool.stc.STCEntry;
import com.kreative.imagetool.stc.STCFile;

public class STCFilePanel extends JPanel {
	private static final long serialVersionUID = 1L;
	
	private static Image icon(String name) {
		try { return ImageIO.read(STCFilePanel.class.getResource(name)); }
		catch (IOException e) { return null; }
	}
	
	private static final Image ADD_ICON = icon("list-add.png");
	private static final Image COPY_ICON = icon("edit-copy.png");
	private static final Image EDIT_ICON = icon("tango_style_pencil.png");
	private static final Image UP_ICON = icon("go-up.png");
	private static final Image DOWN_ICON = icon("go-down.png");
	private static final Image DELETE_ICON = icon("user-trash.png");
	
	public STCFilePanel(final File root, final File file, final STCFile stc) {
		final STCFileNameField nf = new STCFileNameField(stc);
		final STCFileTable table = new STCFileTable(root, stc);
		final STCAnimationPanel ap = new STCAnimationPanel();
		final JButton addFromLibBtn = new JButton("Add From Library...", new ImageIcon(ADD_ICON));
		final JButton addFromFileBtn = new JButton("Add From File...", new ImageIcon(ADD_ICON));
		final JButton copyBtn = new JButton("Duplicate", new ImageIcon(COPY_ICON));
		final JButton editBtn = new JButton("Edit", new ImageIcon(EDIT_ICON));
		final JButton moveUpBtn = new JButton("Move Up", new ImageIcon(UP_ICON));
		final JButton moveDownBtn = new JButton("Move Down", new ImageIcon(DOWN_ICON));
		final JButton deleteBtn = new JButton("Delete", new ImageIcon(DELETE_ICON));
		addFromLibBtn.setHorizontalAlignment(JButton.LEADING);
		addFromFileBtn.setHorizontalAlignment(JButton.LEADING);
		copyBtn.setHorizontalAlignment(JButton.LEADING);
		editBtn.setHorizontalAlignment(JButton.LEADING);
		moveUpBtn.setHorizontalAlignment(JButton.LEADING);
		moveDownBtn.setHorizontalAlignment(JButton.LEADING);
		deleteBtn.setHorizontalAlignment(JButton.LEADING);
		
		JScrollPane jsp = new JScrollPane(
			table,
			JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
			JScrollPane.HORIZONTAL_SCROLLBAR_NEVER
		);
		
		JPanel namePanel = new JPanel(new BorderLayout(4,4));
		namePanel.add(new JLabel("Name:"), BorderLayout.LINE_START);
		namePanel.add(nf, BorderLayout.CENTER);
		
		JPanel leftPanel = new JPanel(new BorderLayout(4,4));
		leftPanel.add(namePanel, BorderLayout.PAGE_START);
		leftPanel.add(jsp, BorderLayout.CENTER);
		
		JPanel buttonPanel = new JPanel(new GridLayout(0,1,4,4));
		buttonPanel.add(addFromLibBtn);
		buttonPanel.add(addFromFileBtn);
		buttonPanel.add(copyBtn);
		buttonPanel.add(editBtn);
		buttonPanel.add(moveUpBtn);
		buttonPanel.add(moveDownBtn);
		buttonPanel.add(deleteBtn);
		
		JPanel rightPanel = new JPanel(new BorderLayout(8,8));
		rightPanel.add(ap, BorderLayout.PAGE_START);
		rightPanel.add(buttonPanel, BorderLayout.PAGE_END);
		
		setLayout(new BorderLayout(16,16));
		add(leftPanel, BorderLayout.CENTER);
		add(rightPanel, BorderLayout.LINE_END);
		
		STCFileTableListener selL = new STCFileTableListener(root, stc, table, ap);
		STCFileSaveListener saveL = new STCFileSaveListener(file);
		table.getSelectionModel().addListSelectionListener(selL);
		stc.addSTCFileListener(saveL);
		
		File library = new File("LIBRARY");
		if (library.isDirectory()) {
			final LibraryFrame lf = new LibraryFrame(library, table);
			lf.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
			lf.setTitle("Sprite Library");
			addFromLibBtn.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					lf.setVisible(true);
				}
			});
		} else {
			addFromLibBtn.setEnabled(false);
		}
		
		table.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				if (e.getValueIsAdjusting()) return;
				int index = table.getSelectedRow();
				int count = stc.size();
				copyBtn.setEnabled(index >= 0 && index < count);
				editBtn.setEnabled(index >= 0 && index < count);
				moveUpBtn.setEnabled(index > 0 && index < count);
				moveDownBtn.setEnabled(index >= 0 && index < count - 1);
				deleteBtn.setEnabled(index >= 0 && index < count && count > 1);
			}
		});
		
		int index = table.getSelectedRow();
		int count = stc.size();
		copyBtn.setEnabled(index >= 0 && index < count);
		editBtn.setEnabled(index >= 0 && index < count);
		moveUpBtn.setEnabled(index > 0 && index < count);
		moveDownBtn.setEnabled(index >= 0 && index < count - 1);
		deleteBtn.setEnabled(index >= 0 && index < count && count > 1);
		
		addFromFileBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				table.addFile(null);
			}
		});
		copyBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				int index = table.getSelectedRow();
				int count = stc.size();
				if (index >= 0 && index < count) {
					STCEntry oldEntry = stc.get(index);
					STCEntry newEntry = table.addFile(oldEntry.file(root));
					if (newEntry != null) {
						stc.set(
							stc.indexOf(newEntry),
							new STCEntry(newEntry.path(), oldEntry.name())
						);
					}
				}
			}
		});
		editBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				int index = table.getSelectedRow();
				int count = stc.size();
				if (index >= 0 && index < count) {
					STCEntry entry = stc.get(index);
					File file = entry.file(root);
					try {
						AnimationEditorFrame f = new AnimationEditorFrame(file, "smf");
						f.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
						f.setTitle(entry.name());
						f.setVisible(true);
					} catch (IOException ex) {
						JOptionPane.showMessageDialog(
							STCFilePanel.this,
							"Could not open \u201C" + file.getName() + "\u201D.",
							"Edit", JOptionPane.ERROR_MESSAGE
						);
					}
				}
			}
		});
		moveUpBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				table.moveUp((e.getModifiers() & ActionEvent.SHIFT_MASK) != 0);
			}
		});
		moveDownBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				table.moveDown((e.getModifiers() & ActionEvent.SHIFT_MASK) != 0);
			}
		});
		deleteBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				table.deleteFile(false);
			}
		});
		
		STCDropTarget dt = new STCDropTarget(root, stc, table);
		new DropTarget(table, dt);
		new DropTarget(ap, dt);
	}
}
