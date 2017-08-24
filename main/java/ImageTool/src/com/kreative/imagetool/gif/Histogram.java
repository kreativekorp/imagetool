package com.kreative.imagetool.gif;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Histogram<T> {
	private final Map<T,Integer> m = new HashMap<T,Integer>();
	
	public void add(T t) {
		if (m.containsKey(t)) {
			m.put(t, m.get(t) + 1);
		} else {
			m.put(t, 1);
		}
	}
	
	public void add(T t, int count) {
		if (count < 0) remove(t, -count);
		if (count <= 0) return;
		if (m.containsKey(t)) {
			m.put(t, m.get(t) + count);
		} else {
			m.put(t, count);
		}
	}
	
	public Comparator<T> byCount(boolean descending) {
		if (descending) {
			return new Comparator<T>() {
				public int compare(T a, T b) {
					return get(b) - get(a);
				}
			};
		} else {
			return new Comparator<T>() {
				public int compare(T a, T b) {
					return get(a) - get(b);
				}
			};
		}
	}
	
	public void clear() {
		m.clear();
	}
	
	public void clear(T t) {
		m.remove(t);
	}
	
	public int get(T t) {
		if (m.containsKey(t)) {
			return m.get(t);
		} else {
			return 0;
		}
	}
	
	public boolean isEmpty() {
		return m.isEmpty();
	}
	
	public void remove(T t) {
		if (m.containsKey(t)) {
			int newCount = m.get(t) - 1;
			if (newCount > 0) {
				m.put(t, newCount);
			} else {
				m.remove(t);
			}
		}
	}
	
	public void remove(T t, int count) {
		if (count < 0) add(t, -count);
		if (count <= 0) return;
		if (m.containsKey(t)) {
			int newCount = m.get(t) - count;
			if (newCount > 0) {
				m.put(t, newCount);
			} else {
				m.remove(t);
			}
		}
	}
	
	public int size() {
		return m.size();
	}
	
	public T[] toArray(T[] a) {
		return m.keySet().toArray(a);
	}
	
	public List<T> toList() {
		List<T> list = new ArrayList<T>();
		list.addAll(m.keySet());
		return list;
	}
}
