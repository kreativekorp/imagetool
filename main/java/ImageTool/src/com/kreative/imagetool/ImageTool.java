package com.kreative.imagetool;

public class ImageTool {
	public static void main(String[] args) {
		if (args.length == 0) {
			printHelp();
		} else if (args[0].equalsIgnoreCase("ls") || args[0].equalsIgnoreCase("ListImages")) {
			ListImages.main(args1(args));
		} else if (args[0].equalsIgnoreCase("tx") || args[0].equalsIgnoreCase("TransformImages")) {
			TransformImages.main(args1(args));
		} else if (args[0].equalsIgnoreCase("ca") || args[0].equalsIgnoreCase("ConvertAnimation")) {
			ConvertAnimation.main(args1(args));
		} else {
			printHelp();
		}
	}
	
	private static void printHelp() {
		System.out.println();
		System.out.println("ImageTool - Perform simple operations on images and animated GIFs.");
		System.out.println();
		System.out.println("ImageTool ls <paths>");
		System.out.println("ImageTool ListImages <paths>");
		System.out.println("    List images with their dimensions.");
		System.out.println();
		System.out.println("ImageTool tx <options> <images>");
		System.out.println("ImageTool TransformImages <options> <images>");
		System.out.println("    Perform simple transformations on images and animated GIFs.");
		System.out.println();
		System.out.println("ImageTool ca <options> <images>");
		System.out.println("ImageTool ConvertAnimation <options> <images>");
		System.out.println("    Convert between static images and animated GIFs.");
		System.out.println();
		System.out.println("For more help, pass --help to a subcommand, e.g.:");
		System.out.println();
		System.out.println("ImageTool ListImages --help");
		System.out.println("ImageTool TransformImages --help");
		System.out.println("ImageTool ConvertAnimation --help");
		System.out.println();
	}
	
	private static String[] args1(String[] args) {
		String[] args1 = new String[args.length - 1];
		for (int i = 1; i < args.length; i++) args1[i - 1] = args[i];
		return args1;
	}
}
