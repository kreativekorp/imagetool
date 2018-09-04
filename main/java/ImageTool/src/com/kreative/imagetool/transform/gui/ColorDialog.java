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
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRootPane;
import javax.swing.JSpinner;
import javax.swing.KeyStroke;
import javax.swing.SpinnerNumberModel;
import com.kreative.imagetool.transform.BackgroundColor;
import com.kreative.imagetool.transform.Colorize;

public class ColorDialog extends JDialog {
	private static final long serialVersionUID = 1L;
	
	private SpinnerNumberModel red;
	private SpinnerNumberModel green;
	private SpinnerNumberModel blue;
	private JButton okButton;
	private JButton cancelButton;
	private boolean ok;
	
	public ColorDialog(Frame parent) {
		super(parent, true);
		makeGUI();
	}
	
	public ColorDialog(Dialog parent) {
		super(parent, true);
		makeGUI();
	}
	
	public BackgroundColor showBackgroundColorDialog() {
		setTitle("Background Color");
		setVisible(true);
		if (!ok) return null;
		return new BackgroundColor(
			0xFF000000 |
			(red.getNumber().intValue() << 16) |
			(green.getNumber().intValue() << 8) |
			blue.getNumber().intValue()
		);
	}
	
	public Colorize showColorizeDialog() {
		setTitle("Colorize");
		setVisible(true);
		if (!ok) return null;
		return new Colorize(
			0xFF000000 |
			(red.getNumber().intValue() << 16) |
			(green.getNumber().intValue() << 8) |
			blue.getNumber().intValue()
		);
	}
	
	private void makeGUI() {
		JPanel labelPanel = new JPanel(new GridLayout(0,1,4,4));
		labelPanel.add(new JLabel("Red:"));
		labelPanel.add(new JLabel("Green:"));
		labelPanel.add(new JLabel("Blue:"));
		
		JPanel inputPanel = new JPanel(new GridLayout(0,1,4,4));
		inputPanel.add(new JSpinner(red = new SpinnerNumberModel(0, 0, 255, 1)));
		inputPanel.add(new JSpinner(green = new SpinnerNumberModel(0, 0, 255, 1)));
		inputPanel.add(new JSpinner(blue = new SpinnerNumberModel(0, 0, 255, 1)));
		
		JPanel controlPanel = new JPanel(new BorderLayout(4,4));
		controlPanel.add(labelPanel, BorderLayout.LINE_START);
		controlPanel.add(inputPanel, BorderLayout.CENTER);
		
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
