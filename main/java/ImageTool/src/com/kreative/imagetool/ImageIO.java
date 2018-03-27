package com.kreative.imagetool;

import java.awt.image.BufferedImage;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import com.kreative.imagetool.animation.Animation;
import com.kreative.imagetool.animation.AnimationIO;
import com.kreative.imagetool.gci.GCIFile;
import com.kreative.imagetool.gif.GIFFile;
import com.kreative.imagetool.smf.SMFFile;
import com.kreative.imagetool.transform.Transform;

public class ImageIO {
	public static Object readFile(File f) throws IOException {
		FileInputStream in = new FileInputStream(f);
		byte[] m = new byte[6];
		in.read(m);
		in.close();
		
		if (GIFFile.matchMagic(m)) {
			in = new FileInputStream(f);
			GIFFile gif = new GIFFile();
			gif.read(new DataInputStream(in));
			in.close();
			return gif;
		}
		
		in = new FileInputStream(f);
		BufferedImage image = javax.imageio.ImageIO.read(in);
		in.close();
		if (image != null) return image;
		
		if (SMFFile.matchMagic(m)) {
			in = new FileInputStream(f);
			SMFFile smf = new SMFFile();
			smf.read(new DataInputStream(in));
			in.close();
			return smf;
		}
		
		if (GCIFile.matchMagic(m)) {
			in = new FileInputStream(f);
			GCIFile gci = new GCIFile();
			gci.read(new DataInputStream(in));
			in.close();
			return gci;
		}
		
		return null;
	}
	
	public static Object transform(Object image, List<Transform> txs) {
		if (image instanceof Animation) {
			Animation a = (Animation)image;
			for (Transform tx : txs) a = tx.transform(a);
			return a;
		} else if (image instanceof BufferedImage) {
			BufferedImage bi = (BufferedImage)image;
			for (Transform tx : txs) bi = tx.transform(bi);
			return bi;
		} else if (image instanceof GCIFile) {
			GCIFile gci = (GCIFile)image;
			for (Transform tx : txs) gci = tx.transform(gci);
			return gci;
		} else if (image instanceof GIFFile) {
			GIFFile gif = (GIFFile)image;
			for (Transform tx : txs) gif = tx.transform(gif);
			return gif;
		} else if (image instanceof SMFFile) {
			SMFFile smf = (SMFFile)image;
			for (Transform tx : txs) smf = tx.transform(smf);
			return smf;
		} else {
			return null;
		}
	}
	
	public static void writeFile(Object image, String format, File output) throws IOException {
		if (format.equalsIgnoreCase("gci")) {
			GCIFile gci;
			if (image instanceof GCIFile) gci = (GCIFile)image;
			else gci = AnimationIO.toGCIFile(AnimationIO.fromObject(image));
			FileOutputStream out = new FileOutputStream(output);
			gci.write(new DataOutputStream(out));
			out.flush(); out.close();
		} else if (format.equalsIgnoreCase("gif")) {
			GIFFile gif;
			if (image instanceof GIFFile) gif = (GIFFile)image;
			else gif = AnimationIO.toGIFFile(AnimationIO.fromObject(image), 0);
			FileOutputStream out = new FileOutputStream(output);
			gif.write(new DataOutputStream(out));
			out.flush(); out.close();
		} else if (format.equalsIgnoreCase("smf")) {
			SMFFile smf;
			if (image instanceof SMFFile) smf = (SMFFile)image;
			else smf = AnimationIO.toSMFFile(AnimationIO.fromObject(image), true);
			FileOutputStream out = new FileOutputStream(output);
			smf.write(new DataOutputStream(out));
			out.flush(); out.close();
		} else {
			BufferedImage bi;
			if (image instanceof BufferedImage) bi = (BufferedImage)image;
			else bi = AnimationIO.toBufferedImage(AnimationIO.fromObject(image));
			if (bi == null) throw new IOException("No frames");
			else javax.imageio.ImageIO.write(bi, format, output);
		}
	}
}
