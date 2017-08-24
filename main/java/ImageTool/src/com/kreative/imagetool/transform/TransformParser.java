package com.kreative.imagetool.transform;

import java.util.List;
import javax.swing.SwingConstants;

public class TransformParser implements SwingConstants {
	public static void printHelp() {
		System.out.println("-a, --addmargin <top> <left> <bottom> <right>");
		System.out.println("    Adds the specified number of empty pixels to each edge of the image.");
		System.out.println("-b, --backgroundcolor <color>");
		System.out.println("    Renders the image on top of the specified background color.");
		System.out.println("    <Color> must be a string of 3, 4, 6, or 8 hexadecimal digits.");
		System.out.println("    (Not currently supported for GIFs.)");
		System.out.println("-c, --canvassize <width> <height> <anchor>");
		System.out.println("    Changes the width and height of the image without scaling the image.");
		System.out.println("    <Anchor> is NW, N, NE, E, SE, S, SW, W, or CENTER.");
		System.out.println("-d, --flipdiagonal");
		System.out.println("    Flips the image about its diagonal, i.e. swaps the x and y axes.");
		System.out.println("-g, --grayscale");
		System.out.println("    Converts the image to grayscale according to the following formula:");
		System.out.println("        gray = 0.30 * red + 0.59 * green + 0.11 * blue");
		System.out.println("-h, --fliphorizontal");
		System.out.println("    Flips the image horizontally, i.e. about the vertical axis.");
		System.out.println("-i, --invert");
		System.out.println("    Inverts the colors of the image.");
		System.out.println("-j, --invertgrays <threshold>");
		System.out.println("    Inverts each color of the image only if it is sufficiently desaturated.");
		System.out.println("    <Threshold> varies from 0 (invert nothing) to 256 (invert everything).");
		System.out.println("-k, --colorize <color>");
		System.out.println("    Recolors the image using tints and shades of the specified color.");
		System.out.println("    <Color> must be a string of 3, 4, 6, or 8 hexadecimal digits.");
		System.out.println("-l, --rotateleft");
		System.out.println("    Rotates the image counter-clockwise.");
		System.out.println("    Alias for --flipdiagonal --flipvertical.");
		System.out.println("-m, --removemargin <top> <left> <bottom> <right>");
		System.out.println("    Removes the specified number of pixels from each edge of the image.");
		System.out.println("-p, --pebble");
		System.out.println("    Converts the colors of the image to the Pebble color palette.");
		System.out.println("-r, --rotateright");
		System.out.println("    Rotates the image clockwise.");
		System.out.println("    Alias for --flipdiagonal --fliphorizontal.");
		System.out.println("-s, --scale <sx> <sy>");
		System.out.println("    Scales the image by the specified factors horizontally and vertically.");
		System.out.println("-t, --trim <top> <left> <bottom> <right>");
		System.out.println("    Removes transparent pixels from the specified edges of the image.");
		System.out.println("    Each parameter is either TRUE or FALSE.");
		System.out.println("-u, --rotate180");
		System.out.println("    Rotates the image 180 degrees.");
		System.out.println("    Alias for --fliphorizontal --flipvertical.");
		System.out.println("-v, --flipvertical");
		System.out.println("    Flips the image vertically, i.e. about the horizontal axis.");
		System.out.println("-w, --websafe");
		System.out.println("    Converts the colors of the image to the web-safe color palette.");
		System.out.println("-x, --speed <multiplier>");
		System.out.println("    Speeds up or slows down an animation. Has no effect on static images.");
		System.out.println("-z, --imagesize <width> <height>");
		System.out.println("    Scales the image to the specified width and height.");
	}
	
	public static int parseTransform(String[] a, int i, List<Transform> l) {
		if (a == null || i < 0 || i >= a.length || l == null) {
			return -1;
		} else if (a[i].equals("-a") || a[i].equalsIgnoreCase("--addmargin")) {
			int top = Integer.parseInt(a[i + 1]);
			int left = Integer.parseInt(a[i + 2]);
			int bottom = Integer.parseInt(a[i + 3]);
			int right = Integer.parseInt(a[i + 4]);
			l.add(new AddMargin(top, left, bottom, right));
			return i + 5;
		} else if (a[i].equals("-b") || a[i].equalsIgnoreCase("--backgroundcolor")) {
			int color = parseColor(a[i + 1]);
			l.add(new BackgroundColor(color));
			return i + 2;
		} else if (a[i].equals("-c") || a[i].equalsIgnoreCase("--canvassize")) {
			int width = Integer.parseInt(a[i + 1]);
			int height = Integer.parseInt(a[i + 2]);
			int anchor = parseAnchor(a[i + 3]);
			l.add(new CanvasSize(width, height, anchor));
			return i + 4;
		} else if (a[i].equals("-d") || a[i].equalsIgnoreCase("--flipdiagonal")) {
			l.add(new FlipDiagonal());
			return i + 1;
		} else if (a[i].equals("-g") || a[i].equalsIgnoreCase("--grayscale")) {
			l.add(new Grayscale());
			return i + 1;
		} else if (a[i].equals("-h") || a[i].equalsIgnoreCase("--fliphorizontal")) {
			l.add(new FlipHorizontal());
			return i + 1;
		} else if (a[i].equals("-i") || a[i].equalsIgnoreCase("--invert")) {
			l.add(new Invert());
			return i + 1;
		} else if (a[i].equals("-j") || a[i].equalsIgnoreCase("--invertgrays")) {
			int threshold = Integer.parseInt(a[i + 1]);
			l.add(new InvertGrays(threshold));
			return i + 2;
		} else if (a[i].equals("-k") || a[i].equalsIgnoreCase("--colorize")) {
			int color = parseColor(a[i + 1]);
			l.add(new Colorize(color));
			return i + 2;
		} else if (a[i].equals("-l") || a[i].equalsIgnoreCase("--rotateleft")) {
			l.add(new FlipDiagonal());
			l.add(new FlipVertical());
			return i + 1;
		} else if (a[i].equals("-m") || a[i].equalsIgnoreCase("--removemargin")) {
			int top = Integer.parseInt(a[i + 1]);
			int left = Integer.parseInt(a[i + 2]);
			int bottom = Integer.parseInt(a[i + 3]);
			int right = Integer.parseInt(a[i + 4]);
			l.add(new RemoveMargin(top, left, bottom, right));
			return i + 5;
		} else if (a[i].equals("-p") || a[i].equalsIgnoreCase("--pebble")) {
			l.add(new PebblePalette());
			return i + 1;
		} else if (a[i].equals("-r") || a[i].equalsIgnoreCase("--rotateright")) {
			l.add(new FlipDiagonal());
			l.add(new FlipHorizontal());
			return i + 1;
		} else if (a[i].equals("-s") || a[i].equalsIgnoreCase("--scale")) {
			try {
				int sx = Integer.parseInt(a[i + 1]);
				int sy = Integer.parseInt(a[i + 2]);
				l.add(new ScaleInteger(sx, sy));
				return i + 3;
			} catch (NumberFormatException e) {
				double sx = Double.parseDouble(a[i + 1]);
				double sy = Double.parseDouble(a[i + 2]);
				l.add(new ScaleDouble(sx, sy));
				return i + 3;
			}
		} else if (a[i].equals("-t") || a[i].equalsIgnoreCase("--trim")) {
			boolean top = parseBoolean(a[i + 1]);
			boolean left = parseBoolean(a[i + 2]);
			boolean bottom = parseBoolean(a[i + 3]);
			boolean right = parseBoolean(a[i + 4]);
			l.add(new Trim(top, left, bottom, right));
			return i + 5;
		} else if (a[i].equals("-u") || a[i].equalsIgnoreCase("--rotate180")) {
			l.add(new FlipHorizontal());
			l.add(new FlipVertical());
			return i + 1;
		} else if (a[i].equals("-v") || a[i].equalsIgnoreCase("--flipvertical")) {
			l.add(new FlipVertical());
			return i + 1;
		} else if (a[i].equals("-w") || a[i].equalsIgnoreCase("--websafe")) {
			l.add(new WebsafePalette());
			return i + 1;
		} else if (a[i].equals("-x") || a[i].equalsIgnoreCase("--speed")) {
			double x = Double.parseDouble(a[i + 1]);
			l.add(new Speed(x));
			return i + 2;
		} else if (a[i].equals("-z") || a[i].equalsIgnoreCase("--imagesize")) {
			int width = Integer.parseInt(a[i + 1]);
			int height = Integer.parseInt(a[i + 2]);
			l.add(new ImageSize(width, height));
			return i + 3;
		} else {
			return -1;
		}
	}
	
	public static int parseColor(String s) {
		s = s.trim().toLowerCase();
		int a, r, g, b;
		switch (s.length()) {
			case 3:
				a = 0xFF;
				r = Integer.parseInt(s.substring(0, 1), 16) * 0x11;
				g = Integer.parseInt(s.substring(1, 2), 16) * 0x11;
				b = Integer.parseInt(s.substring(2, 3), 16) * 0x11;
				break;
			case 4:
				a = Integer.parseInt(s.substring(0, 1), 16) * 0x11;
				r = Integer.parseInt(s.substring(1, 2), 16) * 0x11;
				g = Integer.parseInt(s.substring(2, 3), 16) * 0x11;
				b = Integer.parseInt(s.substring(3, 4), 16) * 0x11;
				break;
			case 6:
				a = 0xFF;
				r = Integer.parseInt(s.substring(0, 2), 16);
				g = Integer.parseInt(s.substring(2, 4), 16);
				b = Integer.parseInt(s.substring(4, 6), 16);
				break;
			case 8:
				a = Integer.parseInt(s.substring(0, 2), 16);
				r = Integer.parseInt(s.substring(2, 4), 16);
				g = Integer.parseInt(s.substring(4, 6), 16);
				b = Integer.parseInt(s.substring(6, 8), 16);
				break;
			default:
				throw new NumberFormatException();
		}
		return ((a << 24) | (r << 16) | (g << 8) | b);
	}
	
	public static int parseAnchor(String s) {
		s = s.trim().toLowerCase();
		if (s.equals("center") || s.equals("c")) return CENTER;
		if (s.equals("northwest") || s.equals("nw")) return NORTH_WEST;
		if (s.equals("north") || s.equals("n")) return NORTH;
		if (s.equals("northeast") || s.equals("ne")) return NORTH_EAST;
		if (s.equals("east") || s.equals("e")) return EAST;
		if (s.equals("southeast") || s.equals("se")) return SOUTH_EAST;
		if (s.equals("south") || s.equals("s")) return SOUTH;
		if (s.equals("southwest") || s.equals("sw")) return SOUTH_WEST;
		if (s.equals("west") || s.equals("w")) return WEST;
		return Integer.parseInt(s);
	}
	
	public static boolean parseBoolean(String s) {
		s = s.trim().toLowerCase();
		if (s.equals("true") || s.equals("yes")) return true;
		if (s.equals("false") || s.equals("no")) return false;
		if (s.equals("t") || s.equals("y")) return true;
		if (s.equals("f") || s.equals("n")) return false;
		return (Integer.parseInt(s) != 0);
	}
}
