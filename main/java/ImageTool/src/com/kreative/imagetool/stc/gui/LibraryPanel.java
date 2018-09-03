package com.kreative.imagetool.stc.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

public class LibraryPanel extends JPanel {
	private static final long serialVersionUID = 1L;
	
	private static final Image ADD_ICON;
	static {
		Image addIcon = null;
		try { addIcon = ImageIO.read(STCFilePanel.class.getResource("list-add.png")); } catch (IOException e) {}
		ADD_ICON = addIcon;
	}
	
	public LibraryPanel(final File library, final STCFileTable table) {
		final FolderList folderList = new FolderList(library);
		final FileList fileList = new FileList();
		final STCAnimationPanel ap = new STCAnimationPanel();
		final JButton addBtn = new JButton("Add", new ImageIcon(ADD_ICON));
		addBtn.setHorizontalAlignment(JButton.LEADING);
		
		folderList.setPreferredSize(new Dimension(180, folderList.getPreferredSize().height));
		folderList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		fileList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		
		JScrollPane folderPane = new JScrollPane(
			folderList,
			JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
			JScrollPane.HORIZONTAL_SCROLLBAR_NEVER
		);
		JScrollPane filePane = new JScrollPane(
			fileList,
			JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
			JScrollPane.HORIZONTAL_SCROLLBAR_NEVER
		);
		
		JPanel buttonPanel = new JPanel(new GridLayout(0,1,4,4));
		buttonPanel.add(addBtn);
		
		JPanel rightPanel = new JPanel(new BorderLayout(8,8));
		rightPanel.add(ap, BorderLayout.PAGE_START);
		rightPanel.add(buttonPanel, BorderLayout.PAGE_END);
		
		setLayout(new BorderLayout(16,16));
		add(folderPane, BorderLayout.LINE_START);
		add(filePane, BorderLayout.CENTER);
		add(rightPanel, BorderLayout.LINE_END);
		
		FolderListListener folderL = new FolderListListener(folderList, fileList);
		FileListListener fileL = new FileListListener(fileList, ap);
		folderList.addListSelectionListener(folderL);
		fileList.addListSelectionListener(fileL);
		
		fileList.addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				if (e.getValueIsAdjusting()) return;
				File f = fileList.getSelectedFile();
				addBtn.setEnabled(f != null);
			}
		});
		
		File f = fileList.getSelectedFile();
		addBtn.setEnabled(f != null);
		
		addBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				File f = fileList.getSelectedFile();
				if (f == null) return;
				table.addFile(f);
			}
		});
		fileList.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() > 1) {
					File f = fileList.getSelectedFile();
					if (f == null) return;
					table.addFile(f);
				}
			}
		});
	}
}
