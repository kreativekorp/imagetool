package com.kreative.imagetool.stc;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public class STCFile implements List<STCEntry> {
	public static final String DEFAULT_NAME = "Carousel";
	
	private String name;
	private final List<STCEntry> entries;
	private STCSelection selection;
	private final List<STCFileListener> listeners;
	
	public STCFile() {
		this.name = DEFAULT_NAME;
		this.entries = new ArrayList<STCEntry>();
		this.selection = null;
		this.listeners = new ArrayList<STCFileListener>();
	}
	
	public STCFile(String name) {
		this.name = (name != null) ? name : DEFAULT_NAME;
		this.entries = new ArrayList<STCEntry>();
		this.selection = null;
		this.listeners = new ArrayList<STCFileListener>();
	}
	
	public String getName() {
		return (name != null) ? name : DEFAULT_NAME;
	}
	
	public void setName(String name) {
		this.name = (name != null) ? name : DEFAULT_NAME;
		for (STCFileListener l : listeners) l.stcNameChanged(this);
	}
	
	@Override
	public boolean add(STCEntry e) {
		if (entries.add(e)) {
			for (STCFileListener l : listeners) l.stcEntriesChanged(this);
			return true;
		} else {
			return false;
		}
	}
	
	@Override
	public void add(int index, STCEntry e) {
		entries.add(index, e);
		for (STCFileListener l : listeners) l.stcEntriesChanged(this);
	}
	
	@Override
	public boolean addAll(Collection<? extends STCEntry> c) {
		if (entries.addAll(c)) {
			for (STCFileListener l : listeners) l.stcEntriesChanged(this);
			return true;
		} else {
			return false;
		}
	}
	
	@Override
	public boolean addAll(int index, Collection<? extends STCEntry> c) {
		if (entries.addAll(index, c)) {
			for (STCFileListener l : listeners) l.stcEntriesChanged(this);
			return true;
		} else {
			return false;
		}
	}
	
	@Override
	public void clear() {
		entries.clear();
		for (STCFileListener l : listeners) l.stcEntriesChanged(this);
	}
	
	@Override
	public boolean contains(Object o) {
		return entries.contains(o);
	}
	
	public boolean containsPath(String path) {
		for (STCEntry e : entries) {
			if (e.path().equals(path)) {
				return true;
			}
		}
		return false;
	}
	
	public boolean containsName(String name) {
		for (STCEntry e : entries) {
			if (e.name().equals(name)) {
				return true;
			}
		}
		return false;
	}
	
	@Override
	public boolean containsAll(Collection<?> c) {
		return entries.containsAll(c);
	}
	
	@Override
	public STCEntry get(int index) {
		return entries.get(index);
	}
	
	@Override
	public int indexOf(Object o) {
		return entries.indexOf(o);
	}
	
	public int indexOfPath(String path) {
		for (int i = 0; i < entries.size(); i++) {
			if (entries.get(i).path().equals(path)) {
				return i;
			}
		}
		return -1;
	}
	
	public int indexOfName(String name) {
		for (int i = 0; i < entries.size(); i++) {
			if (entries.get(i).name().equals(name)) {
				return i;
			}
		}
		return -1;
	}
	
	@Override
	public boolean isEmpty() {
		return entries.isEmpty();
	}
	
	@Override
	public Iterator<STCEntry> iterator() {
		return Collections.unmodifiableList(entries).iterator();
	}
	
	@Override
	public int lastIndexOf(Object o) {
		return entries.lastIndexOf(o);
	}
	
	public int lastIndexOfPath(String path) {
		for (int i = entries.size() - 1; i >= 0; i--) {
			if (entries.get(i).path().equals(path)) {
				return i;
			}
		}
		return -1;
	}
	
	public int lastIndexOfName(String name) {
		for (int i = entries.size() - 1; i >= 0; i--) {
			if (entries.get(i).name().equals(name)) {
				return i;
			}
		}
		return -1;
	}
	
	@Override
	public ListIterator<STCEntry> listIterator() {
		return Collections.unmodifiableList(entries).listIterator();
	}
	
	@Override
	public ListIterator<STCEntry> listIterator(int index) {
		return Collections.unmodifiableList(entries).listIterator(index);
	}
	
	@Override
	public boolean remove(Object o) {
		if (entries.remove(o)) {
			for (STCFileListener l : listeners) l.stcEntriesChanged(this);
			return true;
		} else {
			return false;
		}
	}
	
	@Override
	public STCEntry remove(int index) {
		STCEntry ret = entries.remove(index);
		for (STCFileListener l : listeners) l.stcEntriesChanged(this);
		return ret;
	}
	
	@Override
	public boolean removeAll(Collection<?> c) {
		if (entries.removeAll(c)) {
			for (STCFileListener l : listeners) l.stcEntriesChanged(this);
			return true;
		} else {
			return false;
		}
	}
	
	@Override
	public boolean retainAll(Collection<?> c) {
		if (entries.retainAll(c)) {
			for (STCFileListener l : listeners) l.stcEntriesChanged(this);
			return true;
		} else {
			return false;
		}
	}
	
	@Override
	public STCEntry set(int index, STCEntry e) {
		STCEntry ret = entries.set(index, e);
		for (STCFileListener l : listeners) l.stcEntriesChanged(this);
		return ret;
	}
	
	@Override
	public int size() {
		return entries.size();
	}
	
	@Override
	public List<STCEntry> subList(int fromIndex, int toIndex) {
		return Collections.unmodifiableList(entries).subList(fromIndex, toIndex);
	}
	
	@Override
	public Object[] toArray() {
		return entries.toArray();
	}
	
	@Override
	public <T> T[] toArray(T[] a) {
		return entries.toArray(a);
	}
	
	public String uniquePath(String prefix, String name, String suffix) {
		String n = ((name.length() > 8) ? name.substring(0, 8) : name);
		String path = prefix + n + suffix;
		if (!containsPath(path)) return path;
		for (int d = 1, e = 10, l = 7; l >= 0; d *= 10, e *= 10, l--) {
			for (int i = d; i < e; i++) {
				n = ((name.length() > l) ? name.substring(0, l) : name);
				path = prefix + n + i + suffix;
				if (!containsPath(path)) return path;
			}
		}
		throw new IllegalStateException("The number of files is too damn high.");
	}
	
	public STCSelection getSelection() {
		return this.selection;
	}
	
	public void setSelection(STCSelection selection) {
		this.selection = selection;
		for (STCFileListener l : listeners) l.stcSelectionChanged(this);
	}
	
	public void addSTCFileListener(STCFileListener listener) {
		this.listeners.add(listener);
	}
	
	public void removeSTCFileListener(STCFileListener listener) {
		this.listeners.remove(listener);
	}
	
	public void read(DataInput in) throws IOException {
		int count = in.readUnsignedShort();
		/* start = */ in.readUnsignedShort();
		int index = in.readUnsignedShort();
		/* advmenu_start = */ in.readUnsignedShort();
		int advmenu_index = in.readUnsignedShort();
		in.readFully(new byte[22]);
		String name = readString(in, 32);
		List<STCEntry> entries = new ArrayList<STCEntry>();
		for (int i = 0; i < count; i++) {
			String entryPath = readString(in, 32);
			String entryName = readString(in, 32);
			entries.add(new STCEntry(entryPath, entryName));
		}
		
		this.name = name;
		this.entries.clear();
		this.entries.addAll(entries);
		if (index < count) {
			this.selection = entries.get(index);
		} else switch (index - count) {
			case 0: this.selection = new STCCycleTime(advmenu_index); break;
			case 1: this.selection = new STCRandomizeTime(advmenu_index); break;
			default: this.selection = null;
		}
		
		for (STCFileListener l : listeners) {
			l.stcNameChanged(this);
			l.stcEntriesChanged(this);
			l.stcSelectionChanged(this);
		}
	}
	
	public void write(DataOutput out) throws IOException {
		int count = entries.size();
		int index;
		int advmenu_index;
		if (entries.contains(selection)) {
			index = entries.indexOf(selection);
			advmenu_index = 0;
		} else if (selection instanceof STCCycleTime) {
			index = count;
			advmenu_index = ((STCCycleTime)selection).millis();
		} else if (selection instanceof STCRandomizeTime) {
			index = count + 1;
			advmenu_index = ((STCRandomizeTime)selection).millis();
		} else {
			index = 0;
			advmenu_index = 0;
		}
		
		int start = index - 3;
		if (start > count - 4) start = count - 4;
		if (start < 0) start = 0;
		int advmenu_start = (advmenu_index / 5000) - 3;
		if (advmenu_start > 6) advmenu_start = 6;
		if (advmenu_start < 0) advmenu_start = 0;
		
		out.writeShort(count);
		out.writeShort(start);
		out.writeShort(index);
		out.writeShort(advmenu_start);
		out.writeShort(advmenu_index);
		out.write(new byte[22]);
		writeString(out, name, 32);
		for (STCEntry e : entries) {
			writeString(out, e.path(), 32);
			writeString(out, e.name(), 32);
		}
	}
	
	private static String readString(DataInput in, int length) throws IOException {
		byte[] buf = new byte[length];
		in.readFully(buf, 0, length);
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < length && buf[i] != 0; i++) {
			sb.append((char)SuperLatin.fromSuperLatin(buf[i]));
		}
		return sb.toString();
	}
	
	private static void writeString(DataOutput out, String s, int length) throws IOException {
		byte[] dst = new byte[length];
		char[] src = s.toCharArray();
		for (int i = 0; i < length && i < src.length; i++) {
			dst[i] = (byte)SuperLatin.toSuperLatin(src[i]);
		}
		out.write(dst, 0, length);
	}
}
