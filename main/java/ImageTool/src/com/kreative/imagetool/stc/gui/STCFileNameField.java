package com.kreative.imagetool.stc.gui;

import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import com.kreative.imagetool.stc.STCFile;

public class STCFileNameField extends JTextField {
	private static final long serialVersionUID = 1L;
	
	public STCFileNameField(final STCFile stc) {
		super(stc.getName());
		getDocument().addDocumentListener(new DocumentListener() {
			@Override
			public void changedUpdate(DocumentEvent e) {
				stc.setName(getText());
			}
			@Override
			public void insertUpdate(DocumentEvent e) {
				stc.setName(getText());
			}
			@Override
			public void removeUpdate(DocumentEvent e) {
				stc.setName(getText());
			}
		});
	}
}
