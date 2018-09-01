package com.kreative.imagetool.stc;

public class STCEntry implements STCSelection {
	private final String path;
	private final String name;
	
	public STCEntry(String path, String name) {
		this.path = path;
		this.name = name;
	}
	
	public String path() {
		return path;
	}
	
	public String name() {
		return name;
	}
	
	@Override
	public boolean equals(Object o) {
		if (o == this) {
			return true;
		} else if (o instanceof STCEntry) {
			STCEntry that = (STCEntry)o;
			return this.path.equals(that.path)
			    && this.name.equals(that.name); 
		} else {
			return false;
		}
	}
	
	@Override
	public int hashCode() {
		return path.hashCode() ^ name.hashCode();
	}
	
	@Override
	public String toString() {
		return name;
	}
}
