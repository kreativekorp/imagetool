package com.kreative.imagetool.transform;

public class Invert extends ColorFilter {
	public int transform(int pixel) {
		return pixel ^ 0xFFFFFF;
	}
}
