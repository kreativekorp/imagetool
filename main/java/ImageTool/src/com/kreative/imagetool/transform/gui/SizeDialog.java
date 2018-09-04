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
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JRootPane;
import javax.swing.JSpinner;
import javax.swing.KeyStroke;
import javax.swing.SpinnerNumberModel;
import com.kreative.imagetool.transform.CanvasSize;
import com.kreative.imagetool.transform.ImageSize;
import com.kreative.imagetool.transform.ScaleDouble;
import com.kreative.imagetool.transform.ScaleInteger;

public class SizeDialog extends JDialog {
	private static final long serialVersionUID = 1L;
	
	private SpinnerNumberModel width;
	private SpinnerNumberModel height;
	private JLabel widthUnitLabel;
	private JLabel heightUnitLabel;
	private JRadioButton anw, an, ane;
	private JRadioButton aw, actr, ae;
	private JRadioButton asw, as, ase;
	private JPanel anchorPanel;
	private JButton okButton;
	private JButton cancelButton;
	private boolean ok;
	
	public SizeDialog(Frame parent) {
		super(parent, true);
		makeGUI();
	}
	
	public SizeDialog(Dialog parent) {
		super(parent, true);
		makeGUI();
	}
	
	public CanvasSize showCanvasSizeDialog(int width, int height) {
		this.width.setValue(width); this.width.setStepSize(1);
		this.height.setValue(height); this.height.setStepSize(1);
		this.widthUnitLabel.setText("px");
		this.heightUnitLabel.setText("px");
		this.anchorPanel.setVisible(true);
		pack();
		setLocationRelativeTo(null);
		setLocation(getX(), getY()/2);
		
		setTitle("Canvas Size");
		setVisible(true);
		if (!ok) return null;
		return new CanvasSize(
			this.width.getNumber().intValue(),
			this.height.getNumber().intValue(),
			this.anw.isSelected() ? CanvasSize.NORTH_WEST :
			this.an.isSelected() ? CanvasSize.NORTH :
			this.ane.isSelected() ? CanvasSize.NORTH_EAST :
			this.ae.isSelected() ? CanvasSize.EAST :
			this.aw.isSelected() ? CanvasSize.WEST :
			this.asw.isSelected() ? CanvasSize.SOUTH_WEST :
			this.as.isSelected() ? CanvasSize.SOUTH :
			this.ase.isSelected() ? CanvasSize.SOUTH_EAST :
			CanvasSize.CENTER
		);
	}
	
	public ImageSize showImageSizeDialog(int width, int height) {
		this.width.setValue(width); this.width.setStepSize(1);
		this.height.setValue(height); this.height.setStepSize(1);
		this.widthUnitLabel.setText("px");
		this.heightUnitLabel.setText("px");
		this.anchorPanel.setVisible(false);
		pack();
		setLocationRelativeTo(null);
		setLocation(getX(), getY()/2);
		
		setTitle("Image Size");
		setVisible(true);
		if (!ok) return null;
		return new ImageSize(
			this.width.getNumber().intValue(),
			this.height.getNumber().intValue()
		);
	}
	
	public ScaleInteger showScaleIntegerDialog() {
		this.width.setValue(1); this.width.setStepSize(1);
		this.height.setValue(1); this.height.setStepSize(1);
		this.widthUnitLabel.setText("X");
		this.heightUnitLabel.setText("X");
		this.anchorPanel.setVisible(false);
		pack();
		setLocationRelativeTo(null);
		setLocation(getX(), getY()/2);
		
		setTitle("Magnify");
		setVisible(true);
		if (!ok) return null;
		return new ScaleInteger(
			this.width.getNumber().intValue(),
			this.height.getNumber().intValue()
		);
	}
	
	public ScaleDouble showScaleDoubleDialog() {
		this.width.setValue(100.0); this.width.setStepSize(0.01);
		this.height.setValue(100.0); this.height.setStepSize(0.01);
		this.widthUnitLabel.setText("%");
		this.heightUnitLabel.setText("%");
		this.anchorPanel.setVisible(false);
		pack();
		setLocationRelativeTo(null);
		setLocation(getX(), getY()/2);
		
		setTitle("Scale");
		setVisible(true);
		if (!ok) return null;
		return new ScaleDouble(
			this.width.getNumber().doubleValue() / 100.0,
			this.height.getNumber().doubleValue() / 100.0
		);
	}
	
	private void makeGUI() {
		JPanel labelPanel = new JPanel(new GridLayout(0,1,4,4));
		labelPanel.add(new JLabel("Width:"));
		labelPanel.add(new JLabel("Height:"));
		
		JPanel inputPanel = new JPanel(new GridLayout(0,1,4,4));
		inputPanel.add(new JSpinner(width = new SpinnerNumberModel(1, 1, Integer.MAX_VALUE, 1)));
		inputPanel.add(new JSpinner(height = new SpinnerNumberModel(1, 1, Integer.MAX_VALUE, 1)));
		
		JPanel unitPanel = new JPanel(new GridLayout(0,1,4,4));
		unitPanel.add(widthUnitLabel = new JLabel("px"));
		unitPanel.add(heightUnitLabel = new JLabel("px"));
		
		JPanel sizePanel = new JPanel(new BorderLayout(4,4));
		sizePanel.add(labelPanel, BorderLayout.LINE_START);
		sizePanel.add(inputPanel, BorderLayout.CENTER);
		sizePanel.add(unitPanel, BorderLayout.LINE_END);
		
		JPanel abPanel = new JPanel(new GridLayout(3,3,4,4));
		abPanel.add(anw  = new JRadioButton());
		abPanel.add(an   = new JRadioButton());
		abPanel.add(ane  = new JRadioButton());
		abPanel.add(aw   = new JRadioButton());
		abPanel.add(actr = new JRadioButton());
		abPanel.add(ae   = new JRadioButton());
		abPanel.add(asw  = new JRadioButton());
		abPanel.add(as   = new JRadioButton());
		abPanel.add(ase  = new JRadioButton());
		abPanel.setBorder(BorderFactory.createEmptyBorder(4,4,4,4));
		
		ButtonGroup bg = new ButtonGroup();
		bg.add(anw); bg.add(an); bg.add(ane);
		bg.add(aw); bg.add(actr); bg.add(ae);
		bg.add(asw); bg.add(as); bg.add(ase);
		actr.setSelected(true);
		
		JPanel abfPanel = new JPanel(new FlowLayout());
		abfPanel.add(abPanel);
		
		anchorPanel = new JPanel(new BorderLayout());
		anchorPanel.add(new JLabel("Anchor:"), BorderLayout.PAGE_START);
		anchorPanel.add(abfPanel, BorderLayout.CENTER);
		anchorPanel.setBorder(BorderFactory.createEmptyBorder(8,0,0,0));
		
		JPanel controlPanel = new JPanel(new BorderLayout());
		controlPanel.add(sizePanel, BorderLayout.CENTER);
		controlPanel.add(anchorPanel, BorderLayout.PAGE_END);
		
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
