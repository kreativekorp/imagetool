package com.kreative.imagetool.animation;

import java.util.ArrayList;
import java.util.List;

public class Animation {
	public int width;
	public int height;
	public List<AnimationFrame> frames;
	
	public Animation(int width, int height) {
		this.width = width;
		this.height = height;
		this.frames = new ArrayList<AnimationFrame>();
	}
	
	public Animation selectFrames(int... indices) {
		Animation a = new Animation(width, height);
		for (int i : indices) {
			AnimationFrame frame = frames.get(i);
			frame = new AnimationFrame(frame.image, frame.duration);
			a.frames.add(frame);
		}
		return a;
	}
	
	public Animation selectFrames(List<Integer> indices) {
		Animation a = new Animation(width, height);
		for (int i : indices) {
			AnimationFrame frame = frames.get(i);
			frame = new AnimationFrame(frame.image, frame.duration);
			a.frames.add(frame);
		}
		return a;
	}
}
