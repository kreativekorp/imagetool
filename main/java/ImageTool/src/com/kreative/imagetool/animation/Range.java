package com.kreative.imagetool.animation;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Range {
	public static List<Double> parseDurations(String s, List<Double> ld) {
		if (ld == null) ld = new ArrayList<Double>();
		String[] parts = s.split("[:;]");
		for (String part : parts) {
			double d = Double.parseDouble(part);
			if (d < 0) throw new IllegalArgumentException();
			ld.add(d);
		}
		return ld;
	}
	
	public static List<Integer> range(int start, int end, List<Integer> li) {
		if (li == null) li = new ArrayList<Integer>();
		for (int i = start; i < end; i++) li.add(i);
		return li;
	}
	
	public static List<Integer> parseRange(String s, int n, List<Integer> li) {
		if (li == null) li = new ArrayList<Integer>();
		String[] parts = s.split("[.,:;]");
		for (String part : parts) {
			String[] subparts = part.split("-", 2);
			switch (subparts.length) {
				case 2:
					int start = parseRangeInt(subparts[0].trim(), n);
					int end = parseRangeInt(subparts[1].trim(), n);
					if (start > end) {
						for (int i = start; i >= end; i--) {
							if (i >= 0 && i < n) li.add(i);
						}
					} else {
						for (int i = start; i <= end; i++) {
							if (i >= 0 && i < n) li.add(i);
						}
					}
					break;
				case 1:
					int i = parseRangeInt(subparts[0].trim(), n);
					if (i >= 0 && i < n) li.add(i);
					break;
			}
		}
		return li;
	}
	
	private static int parseRangeInt(String s, int n) {
		if (s.equals("A") || s.equals("a")) {
			return RANDOM.nextInt(n);
		}
		if (s.endsWith("L") || s.endsWith("l")) {
			s = s.substring(0, s.length() - 1).trim();
			if (s.length() == 0) return n - 1;
			return n - Integer.parseInt(s);
		}
		if (s.endsWith("M") || s.endsWith("m")) {
			s = s.substring(0, s.length() - 1).trim();
			if (s.length() == 0) return n / 2;
			return (n + Integer.parseInt(s) - 1) / 2;
		}
		if (s.endsWith("F") || s.endsWith("f")) {
			s = s.substring(0, s.length() - 1).trim();
			if (s.length() == 0) return 0;
		}
		return Integer.parseInt(s) - 1;
	}
	
	private static final Random RANDOM = new Random();
}
