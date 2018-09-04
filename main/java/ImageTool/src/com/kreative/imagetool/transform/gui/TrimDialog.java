package com.kreative.imagetool.transform.gui;

import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JRootPane;
import javax.swing.KeyStroke;
import com.kreative.imagetool.transform.Trim;

public class TrimDialog extends JDialog {
	private static final long serialVersionUID = 1L;
	
	private JCheckBox top;
	private JCheckBox left;
	private JCheckBox right;
	private JCheckBox bottom;
	private JButton okButton;
	private JButton cancelButton;
	private boolean ok;
	
	public TrimDialog(Frame parent) {
		super(parent, true);
		makeGUI();
	}
	
	public TrimDialog(Dialog parent) {
		super(parent, true);
		makeGUI();
	}
	
	public Trim showTrimDialog() {
		setTitle("Trim");
		setVisible(true);
		if (!ok) return null;
		return new Trim(
			top.isSelected(),
			left.isSelected(),
			bottom.isSelected(),
			right.isSelected()
		);
	}
	
	private void makeGUI() {
		JPanel controlPanel = new JPanel(new GridLayout(2,2,4,4));
		controlPanel.add(top = new JCheckBox("Top"));
		controlPanel.add(left = new JCheckBox("Left"));
		controlPanel.add(bottom = new JCheckBox("Bottom"));
		controlPanel.add(right = new JCheckBox("Right"));
		
		top.setSelected(true);
		left.setSelected(true);
		right.setSelected(true);
		bottom.setSelected(true);
		
		JPanel buttonPanel = new JPanel(new FlowLayout());
		buttonPanel.add(okButton = new JButton("OK"));
		buttonPanel.add(cancelButton = new JButton("Cancel"));
		
		JPanel mainPanel = new JPanel(new BorderLayout(8,8));
		mainPanel.add(controlPanel, BorderLayout.CENTER);
		mainPanel.add(buttonPanel, BorderLayout.PAGE_END);
		mainPanel.setBorder(BorderFactory.createEmptyBorder(20,20,20,20));
		
		setContentPane(mainPanel);
		setDefaultButton(getRootPane(), okButton);
		setCancelButton(getRootPane(), cancelButton);
		setResizable(false);
		pack();
		setLocationRelativeTo(null);
		setLocation(getX(), getY()/2);
		
		okButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				ok = true;
				dispose();
			}
		});
		cancelButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				ok = false;
				dispose();
			}
		});
	}
	
	private static void setDefaultButton(final JRootPane rp, final JButton b) {
		rp.setDefaultButton(b);
	}
	
	private static void setCancelButton(final JRootPane rp, final JButton b) {
		rp.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "cancel");
		rp.getActionMap().put("cancel", new AbstractAction() {
			private static final long serialVersionUID = 1L;
			@Override
			public void actionPerformed(ActionEvent ev) {
				b.doClick();
			}
		});
	}
}
