package com.kreative.imagetool.stc.gui;

import java.io.File;
import java.io.IOException;
import java.util.Stack;
import com.kreative.imagetool.ImageIO;
import com.kreative.imagetool.animation.Animation;
import com.kreative.imagetool.animation.AnimationIO;
import com.kreative.imagetool.transform.Transform;

public class AnimationEditor {
	private Animation original;
	private final File file;
	private final String format;
	private Animation animation;
	private final Stack<Animation> undoStack;
	private final Stack<Animation> redoStack;
	private boolean changed;
	
	public AnimationEditor(File file, String format) throws IOException {
		Object o = ImageIO.readFile(file);
		if (o == null) throw new IOException();
		Animation a = AnimationIO.fromObject(o);
		if (a == null) throw new IOException();
		this.original = a;
		this.file = file;
		this.format = format;
		this.animation = a.deepCopy();
		this.undoStack = new Stack<Animation>();
		this.redoStack = new Stack<Animation>();
		this.changed = false;
	}
	
	public AnimationEditor(File file, String format, Animation a) {
		this.original = a;
		this.file = file;
		this.format = format;
		this.animation = a.deepCopy();
		this.undoStack = new Stack<Animation>();
		this.redoStack = new Stack<Animation>();
		this.changed = false;
	}
	
	public Animation getAnimation() { return animation; }
	public boolean canUndo() { return !undoStack.isEmpty(); }
	public boolean canRedo() { return !redoStack.isEmpty(); }
	public boolean canRevert() { return changed; }
	public boolean canSave() { return changed; }
	
	public boolean applyTransform(Transform... transforms) {
		for (Transform anyTx : transforms) {
			if (anyTx != null) {
				undoStack.push(animation);
				animation = animation.deepCopy();
				for (Transform tx : transforms) {
					animation = tx.transform(animation);
				}
				redoStack.clear();
				changed = true;
				return true;
			}
		}
		return false;
	}
	
	public boolean undo() {
		if (undoStack.isEmpty()) return false;
		redoStack.push(animation);
		animation = undoStack.pop();
		changed = true;
		return true;
	}
	
	public boolean redo() {
		if (redoStack.isEmpty()) return false;
		undoStack.push(animation);
		animation = redoStack.pop();
		changed = true;
		return true;
	}
	
	public boolean revert() {
		if (!changed) return false;
		animation = original.deepCopy();
		undoStack.clear();
		redoStack.clear();
		changed = false;
		return true;
	}
	
	public boolean save() throws IOException {
		if (!changed) return false;
		ImageIO.writeFile(animation, format, file);
		original = animation.deepCopy();
		changed = false;
		return true;
	}
}
