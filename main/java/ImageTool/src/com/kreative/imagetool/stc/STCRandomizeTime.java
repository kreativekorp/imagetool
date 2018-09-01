package com.kreative.imagetool.stc;

public class STCRandomizeTime implements STCSelection {
	public static STCRandomizeTime[] values() {
		return new STCRandomizeTime[] {
			new STCRandomizeTime( 5000), new STCRandomizeTime(10000), new STCRandomizeTime(15000),
			new STCRandomizeTime(20000), new STCRandomizeTime(25000), new STCRandomizeTime(30000),
			new STCRandomizeTime(35000), new STCRandomizeTime(40000), new STCRandomizeTime(45000),
			new STCRandomizeTime(50000), new STCRandomizeTime(55000), new STCRandomizeTime(60000),
		};
	}
	
	private final int millis;
	
	public STCRandomizeTime(int millis) {
		this.millis = millis;
	}
	
	public int millis() {
		return millis;
	}
	
	@Override
	public boolean equals(Object o) {
		if (o == this) {
			return true;
		} else if (o instanceof STCRandomizeTime) {
			STCRandomizeTime that = (STCRandomizeTime)o;
			return this.millis == that.millis;
		} else {
			return false;
		}
	}
	
	@Override
	public int hashCode() {
		return millis;
	}
	
	@Override
	public String toString() {
		return Integer.toString(millis / 1000) + "s";
	}
}
