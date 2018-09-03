package com.kreative.imagetool.stc.gui;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.List;
import com.kreative.imagetool.ImageIO;
import com.kreative.imagetool.stc.STCEntry;
import com.kreative.imagetool.stc.STCFile;

public class STCDropTarget implements DropTargetListener {
	private File root;
	private STCFile stc;
	private STCFileTable table;
	
	public STCDropTarget(File root, STCFile stc, STCFileTable table) {
		this.root = root;
		this.stc = stc;
		this.table = table;
	}
	
	@Override
	public void drop(DropTargetDropEvent e) {
		try {
			e.acceptDrop(e.getDropAction());
			e.dropComplete(drop(e.getTransferable()));
		} catch (Exception ex) {
			e.dropComplete(false);
		}
	}
	
	private boolean drop(Transferable t) throws Exception {
		if (t.isDataFlavorSupported(DataFlavor.imageFlavor)) {
			Image image = (Image)t.getTransferData(DataFlavor.imageFlavor);
			Toolkit tk = Toolkit.getDefaultToolkit();
			
			long time = System.currentTimeMillis();
			boolean prep = tk.prepareImage(image, -1, -1, null);
			int width = image.getWidth(null);
			int height = image.getHeight(null);
			while (!prep || width < 0 || height < 0) {
				if ((System.currentTimeMillis() - time) > 1000) return false;
				prep = tk.prepareImage(image, -1, -1, null);
				width = image.getWidth(null);
				height = image.getHeight(null);
			}
			
			BufferedImage bi = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
			Graphics2D g = bi.createGraphics();
			g.drawImage(image, 0, 0, null);
			g.dispose();
			
			String path = stc.uniquePath("SPRITES/", "DROPPED0", ".SMF");
			STCEntry entry = new STCEntry(path, "Dropped Image");
			ImageIO.writeFile(bi, "smf", entry.file(root));
			
			int index = table.getSelectedRow();
			int count = stc.size();
			if (index >= 0 && index < count) index++;
			else index = count;
			stc.add(index, entry);
			table.getSelectionModel().setSelectionInterval(index, index);
			return true;
		} else if (t.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
			List<?> list = (List<?>)t.getTransferData(DataFlavor.javaFileListFlavor);
			for (Object o : list) {
				if (o instanceof File) {
					table.addFile((File)o);
				}
			}
			return true;
		} else if (t.isDataFlavorSupported(DataFlavor.stringFlavor)) {
			String s = t.getTransferData(DataFlavor.stringFlavor).toString();
			for (String l : s.split("\r\n|\r|\n")) {
				if ((l = l.trim()).length() > 0) {
					table.addFile(new File(l));
				}
			}
			return true;
		} else {
			return false;
		}
	}
	
	@Override public void dragEnter(DropTargetDragEvent e) {}
	@Override public void dragExit(DropTargetEvent e) {}
	@Override public void dragOver(DropTargetDragEvent e) {}
	@Override public void dropActionChanged(DropTargetDragEvent e) {}
}
