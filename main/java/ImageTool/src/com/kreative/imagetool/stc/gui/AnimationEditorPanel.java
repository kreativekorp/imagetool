package com.kreative.imagetool.stc.gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import com.kreative.imagetool.animation.Animation;
import com.kreative.imagetool.transform.Colorize;
import com.kreative.imagetool.transform.FlipDiagonal;
import com.kreative.imagetool.transform.FlipHorizontal;
import com.kreative.imagetool.transform.FlipVertical;
import com.kreative.imagetool.transform.Grayscale;
import com.kreative.imagetool.transform.Invert;
import com.kreative.imagetool.transform.gui.ColorDialog;
import com.kreative.imagetool.transform.gui.MarginDialog;
import com.kreative.imagetool.transform.gui.SizeDialog;
import com.kreative.imagetool.transform.gui.SpeedDialog;
import com.kreative.imagetool.transform.gui.ThresholdDialog;

public class AnimationEditorPanel extends JPanel {
	private static final long serialVersionUID = 1L;
	
	private static Image icon(String name) {
		try { return ImageIO.read(AnimationEditorPanel.class.getResource(name)); }
		catch (IOException e) { return null; }
	}
	
	private static final Image UNDO_ICON = icon("edit-undo.png");
	private static final Image REDO_ICON = icon("edit-redo.png");
	private static final Image REVERT_ICON = icon("document-revert.png");
	private static final Image SAVE_ICON = icon("document-save.png");
	
	private static final Image TX_ADD_MARGIN_ICON = icon("tx-addmargin.png");
	private static final Image TX_CANVAS_SIZE_ICON = icon("tx-canvassize.png");
	private static final Image TX_COLORIZE_ICON = icon("tx-colorize.png");
	private static final Image TX_FLIP_DIAGONAL_ICON = icon("tx-flipdiagonal.png");
	private static final Image TX_FLIP_HORIZONTAL_ICON = icon("tx-fliphorizontal.png");
	private static final Image TX_FLIP_VERTICAL_ICON = icon("tx-flipvertical.png");
	private static final Image TX_GRAYSCALE_ICON = icon("tx-grayscale.png");
	private static final Image TX_IMAGE_SIZE_ICON = icon("tx-imagesize.png");
	private static final Image TX_INVERT_ICON = icon("tx-invert.png");
	private static final Image TX_INVERT_GRAYS_ICON = icon("tx-invertgrays.png");
	private static final Image TX_MAGNIFY_ICON = icon("tx-magnify.png");
	private static final Image TX_REMOVE_MARGIN_ICON = icon("tx-removemargin.png");
	private static final Image TX_ROTATE_180_ICON = icon("tx-rotate180.png");
	private static final Image TX_ROTATE_LEFT_ICON = icon("tx-rotateleft.png");
	private static final Image TX_ROTATE_RIGHT_ICON = icon("tx-rotateright.png");
	private static final Image TX_SCALE_ICON = icon("tx-scale.png");
	private static final Image TX_SEPIA_ICON = icon("tx-sepia.png");
	private static final Image TX_SPEED_ICON = icon("tx-speed.png");
	
	private final File file;
	private final AnimationEditor ae;
	private final STCAnimationPanel ap;
	private final JButton undoBtn;
	private final JButton redoBtn;
	private final JButton revertBtn;
	private final JButton saveBtn;
	
	public AnimationEditorPanel(File file, String format) throws IOException {
		this(file, new AnimationEditor(file, format));
	}
	
	public AnimationEditorPanel(File file, String format, Animation a) {
		this(file, new AnimationEditor(file, format, a));
	}
	
	private AnimationEditorPanel(File file, AnimationEditor animationEditor) {
		this.file = file;
		this.ae = animationEditor;
		this.ap = new STCAnimationPanel();
		this.undoBtn = new AnimationEditorButton("Undo", new ImageIcon(UNDO_ICON)) {
			private static final long serialVersionUID = 1L;
			@Override public boolean doIt() { return ae.undo(); }
		};
		this.redoBtn = new AnimationEditorButton("Redo", new ImageIcon(REDO_ICON)) {
			private static final long serialVersionUID = 1L;
			@Override public boolean doIt() { return ae.redo(); }
		};
		this.revertBtn = new AnimationEditorButton("Revert", new ImageIcon(REVERT_ICON)) {
			private static final long serialVersionUID = 1L;
			@Override public boolean doIt() { return ae.revert(); }
		};
		this.saveBtn = new AnimationEditorButton("Save", new ImageIcon(SAVE_ICON)) {
			private static final long serialVersionUID = 1L;
			@Override public boolean doIt() {
				try { return ae.save(); }
				catch (IOException e) {
					String fileName = AnimationEditorPanel.this.file.getName();
					JOptionPane.showMessageDialog(
						AnimationEditorPanel.this,
						"Could not save Ò" + fileName + "Ó.",
						"Save", JOptionPane.ERROR_MESSAGE
					);
					return false;
				}
			}
		};
		updateState();
		
		JPanel txp1 = new JPanel(new GridLayout(0,1,4,4));
		txp1.add(new AnimationEditorButton("Add Margin...", new ImageIcon(TX_ADD_MARGIN_ICON)) {
			private static final long serialVersionUID = 1L;
			@Override public boolean doIt() { return ae.applyTransform(new MarginDialog(p()).showAddMarginDialog()); }
		});
		txp1.add(new AnimationEditorButton("Remove Margin...", new ImageIcon(TX_REMOVE_MARGIN_ICON)) {
			private static final long serialVersionUID = 1L;
			@Override public boolean doIt() { return ae.applyTransform(new MarginDialog(p()).showRemoveMarginDialog()); }
		});
		txp1.add(new AnimationEditorButton("Canvas Size...", new ImageIcon(TX_CANVAS_SIZE_ICON)) {
			private static final long serialVersionUID = 1L;
			@Override public boolean doIt() {
				int w = ae.getAnimation().width, h = ae.getAnimation().height;
				return ae.applyTransform(new SizeDialog(p()).showCanvasSizeDialog(w,h));
			}
		});
		txp1.add(new AnimationEditorButton("Image Size...", new ImageIcon(TX_IMAGE_SIZE_ICON)) {
			private static final long serialVersionUID = 1L;
			@Override public boolean doIt() {
				int w = ae.getAnimation().width, h = ae.getAnimation().height;
				return ae.applyTransform(new SizeDialog(p()).showImageSizeDialog(w,h));
			}
		});
		txp1.add(new AnimationEditorButton("Scale...", new ImageIcon(TX_SCALE_ICON)) {
			private static final long serialVersionUID = 1L;
			@Override public boolean doIt() { return ae.applyTransform(new SizeDialog(p()).showScaleDoubleDialog()); }
		});
		txp1.add(new AnimationEditorButton("Magnify...", new ImageIcon(TX_MAGNIFY_ICON)) {
			private static final long serialVersionUID = 1L;
			@Override public boolean doIt() { return ae.applyTransform(new SizeDialog(p()).showScaleIntegerDialog()); }
		});
		
		JPanel txp2 = new JPanel(new GridLayout(0,1,4,4));
		txp2.add(new AnimationEditorButton("Flip Horizontal", new ImageIcon(TX_FLIP_HORIZONTAL_ICON)) {
			private static final long serialVersionUID = 1L;
			@Override public boolean doIt() { return ae.applyTransform(new FlipHorizontal()); }
		});
		txp2.add(new AnimationEditorButton("Flip Vertical", new ImageIcon(TX_FLIP_VERTICAL_ICON)) {
			private static final long serialVersionUID = 1L;
			@Override public boolean doIt() { return ae.applyTransform(new FlipVertical()); }
		});
		txp2.add(new AnimationEditorButton("Flip Diagonal", new ImageIcon(TX_FLIP_DIAGONAL_ICON)) {
			private static final long serialVersionUID = 1L;
			@Override public boolean doIt() { return ae.applyTransform(new FlipDiagonal()); }
		});
		txp2.add(new AnimationEditorButton("Rotate Left", new ImageIcon(TX_ROTATE_LEFT_ICON)) {
			private static final long serialVersionUID = 1L;
			@Override public boolean doIt() { return ae.applyTransform(new FlipDiagonal(), new FlipVertical()); }
		});
		txp2.add(new AnimationEditorButton("Rotate Right", new ImageIcon(TX_ROTATE_RIGHT_ICON)) {
			private static final long serialVersionUID = 1L;
			@Override public boolean doIt() { return ae.applyTransform(new FlipDiagonal(), new FlipHorizontal()); }
		});
		txp2.add(new AnimationEditorButton("Rotate 180\u00B0", new ImageIcon(TX_ROTATE_180_ICON)) {
			private static final long serialVersionUID = 1L;
			@Override public boolean doIt() { return ae.applyTransform(new FlipHorizontal(), new FlipVertical()); }
		});
		
		JPanel txp3 = new JPanel(new GridLayout(0,1,4,4));
		txp3.add(new AnimationEditorButton("Invert", new ImageIcon(TX_INVERT_ICON)) {
			private static final long serialVersionUID = 1L;
			@Override public boolean doIt() { return ae.applyTransform(new Invert()); }
		});
		txp3.add(new AnimationEditorButton("Invert Grays...", new ImageIcon(TX_INVERT_GRAYS_ICON)) {
			private static final long serialVersionUID = 1L;
			@Override public boolean doIt() { return ae.applyTransform(new ThresholdDialog(p()).showInvertGraysDialog()); }
		});
		txp3.add(new AnimationEditorButton("Grayscale", new ImageIcon(TX_GRAYSCALE_ICON)) {
			private static final long serialVersionUID = 1L;
			@Override public boolean doIt() { return ae.applyTransform(new Grayscale()); }
		});
		txp3.add(new AnimationEditorButton("Sepia", new ImageIcon(TX_SEPIA_ICON)) {
			private static final long serialVersionUID = 1L;
			@Override public boolean doIt() { return ae.applyTransform(new Colorize(0xFF704214)); }
		});
		txp3.add(new AnimationEditorButton("Colorize...", new ImageIcon(TX_COLORIZE_ICON)) {
			private static final long serialVersionUID = 1L;
			@Override public boolean doIt() { return ae.applyTransform(new ColorDialog(p()).showColorizeDialog()); }
		});
		txp3.add(new AnimationEditorButton("Speed...", new ImageIcon(TX_SPEED_ICON)) {
			private static final long serialVersionUID = 1L;
			@Override public boolean doIt() { return ae.applyTransform(new SpeedDialog(p()).showSpeedDialog()); }
		});
		
		JPanel txp = new JPanel(new GridLayout(1,0,4,4));
		txp.add(txp1);
		txp.add(txp2);
		txp.add(txp3);
		
		JPanel actionPanel = new JPanel(new GridLayout(1,0,4,4));
		actionPanel.add(undoBtn);
		actionPanel.add(redoBtn);
		actionPanel.add(revertBtn);
		actionPanel.add(saveBtn);
		
		JPanel buttonPanel = new JPanel(new BorderLayout(4,4));
		buttonPanel.add(txp, BorderLayout.CENTER);
		buttonPanel.add(actionPanel, BorderLayout.PAGE_END);
		
		setLayout(new BorderLayout(12,12));
		add(ap, BorderLayout.CENTER);
		add(buttonPanel, BorderLayout.PAGE_END);
	}
	
	private void updateState() {
		ap.setAnimation(ae.getAnimation());
		undoBtn.setEnabled(ae.canUndo());
		redoBtn.setEnabled(ae.canRedo());
		revertBtn.setEnabled(ae.canRevert());
		saveBtn.setEnabled(ae.canSave());
	}
	
	private Frame p() {
		Component c = this;
		while (true) {
			if (c == null) return null;
			if (c instanceof Frame) return (Frame)c;
			c = c.getParent();
		}
	}
	
	private abstract class AnimationEditorButton extends JButton {
		private static final long serialVersionUID = 1L;
		public AnimationEditorButton(String name, ImageIcon icon) {
			super(name, icon);
			makeGUI();
		}
		private void makeGUI() {
			setHorizontalAlignment(JButton.LEADING);
			addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					if (doIt()) updateState();
				}
			});
		}
		public abstract boolean doIt();
	}
}
