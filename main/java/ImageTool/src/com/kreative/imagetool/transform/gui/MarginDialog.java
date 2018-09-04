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
import com.kreative.imagetool.transform.AddMargin;
import com.kreative.imagetool.transform.RemoveMargin;

public class MarginDialog extends JDialog {
	private static final long serialVersionUID = 1L;
	
	private SpinnerNumberModel top;
	private SpinnerNumberModel left;
	private SpinnerNumberModel right;
	private SpinnerNumberModel bottom;
	private JButton okButton;
	private JButton cancelButton;
	private boolean ok;
	
	public MarginDialog(Frame parent) {
		super(parent, true);
		makeGUI();
	}
	
	public MarginDialog(Dialog parent) {
		super(parent, true);
		makeGUI();
	}
	
	public AddMargin showAddMarginDialog() {
		setTitle("Add Margin");
		setVisible(true);
		if (!ok) return null;
		return new AddMargin(
			top.getNumber().intValue(),
			left.getNumber().intValue(),
			bottom.getNumber().intValue(),
			right.getNumber().intValue()
		);
	}
	
	public RemoveMargin showRemoveMarginDialog() {
		setTitle("Remove Margin");
		setVisible(true);
		if (!ok) return null;
		return new RemoveMargin(
			top.getNumber().intValue(),
			left.getNumber().intValue(),
			bottom.getNumber().intValue(),
			right.getNumber().intValue()
		);
	}
	
	private void makeGUI() {
		JPanel labelPanel = new JPanel(new GridLayout(0,1,4,4));
		labelPanel.add(new JLabel("Top:"));
		labelPanel.add(new JLabel("Left:"));
		labelPanel.add(new JLabel("Right:"));
		labelPanel.add(new JLabel("Bottom:"));
		
		JPanel inputPanel = new JPanel(new GridLayout(0,1,4,4));
		inputPanel.add(new JSpinner(top = new SpinnerNumberModel(0, Integer.MIN_VALUE, Integer.MAX_VALUE, 1)));
		inputPanel.add(new JSpinner(left = new SpinnerNumberModel(0, Integer.MIN_VALUE, Integer.MAX_VALUE, 1)));
		inputPanel.add(new JSpinner(right = new SpinnerNumberModel(0, Integer.MIN_VALUE, Integer.MAX_VALUE, 1)));
		inputPanel.add(new JSpinner(bottom = new SpinnerNumberModel(0, Integer.MIN_VALUE, Integer.MAX_VALUE, 1)));
		
		JPanel unitPanel = new JPanel(new GridLayout(0,1,4,4));
		unitPanel.add(new JLabel("px"));
		unitPanel.add(new JLabel("px"));
		unitPanel.add(new JLabel("px"));
		unitPanel.add(new JLabel("px"));
		
		JPanel controlPanel = new JPanel(new BorderLayout(4,4));
		controlPanel.add(labelPanel, BorderLayout.LINE_START);
		controlPanel.add(inputPanel, BorderLayout.CENTER);
		controlPanel.add(unitPanel, BorderLayout.LINE_END);
		
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
