package com.kreative.imagetool.stc.gui;

import java.awt.Toolkit;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import javax.swing.JFrame;
import javax.swing.UIManager;
import com.kreative.imagetool.stc.STCEntry;
import com.kreative.imagetool.stc.STCFile;

public class Main {
	public static void main(String[] args) {
		try { System.setProperty("com.apple.mrj.application.apple.menu.about.name", "Sprite Loader"); } catch (Exception e) {}
		try { System.setProperty("apple.laf.useScreenMenuBar", "true"); } catch (Exception e) {}
		try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); } catch (Exception e) {}
		
		try {
			Method getModule = Class.class.getMethod("getModule");
			Object javaDesktop = getModule.invoke(Toolkit.getDefaultToolkit().getClass());
			Object allUnnamed = getModule.invoke(Main.class);
			Class<?> module = Class.forName("java.lang.Module");
			Method addOpens = module.getMethod("addOpens", String.class, module);
			addOpens.invoke(javaDesktop, "sun.awt.X11", allUnnamed);
		} catch (Exception e) {}
		
		try {
			Toolkit tk = Toolkit.getDefaultToolkit();
			Field aacn = tk.getClass().getDeclaredField("awtAppClassName");
			aacn.setAccessible(true);
			aacn.set(tk, "Sprite Loader");
		} catch (Exception e) {}
		
		File sprites = new File("SPRITES");
		if (!sprites.exists()) sprites.mkdir();
		File stcRoot = sprites.getParentFile();
		File stcFile = new File(sprites, "SPRITES.STC");
		
		STCFile stc = new STCFile();
		try {
			FileInputStream in = new FileInputStream(stcFile);
			stc.read(new DataInputStream(in));
			in.close();
		} catch (IOException ioe) {
			System.err.println("Failed to read SPRITES.STC.");
		}
		
		if (stc.isEmpty()) {
			try {
				InputStream in = Main.class.getResourceAsStream("TestPattern.smf");
				File smfFile = new File(sprites, "TESTPATT.SMF");
				FileOutputStream out = new FileOutputStream(smfFile);
				int read; byte[] buf = new byte[65536];
				while ((read = in.read(buf, 0, buf.length)) > 0) out.write(buf, 0, read);
				out.close();
				in.close();
				stc.add(new STCEntry("SPRITES/TESTPATT.SMF", "Test Pattern"));
				try {
					out = new FileOutputStream(stcFile);
					stc.write(new DataOutputStream(out));
					out.close();
				} catch (IOException e2) {
					System.err.println("Failed to write SPRITES.STC.");
				}
			} catch (IOException e1) {
				System.err.println("Failed to write TESTPATT.SMF.");
			}
		}
		
		STCFileFrame f = new STCFileFrame(stcRoot, stcFile, stc);
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.setTitle("Sprite Loader");
		f.setVisible(true);
	}
}
