package com.kreative.imagetool.stc;

public class STCCycleTime implements STCSelection {
	public static STCCycleTime[] values() {
		return new STCCycleTime[] {
			new STCCycleTime( 5000), new STCCycleTime(10000), new STCCycleTime(15000),
			new STCCycleTime(20000), new STCCycleTime(25000), new STCCycleTime(30000),
			new STCCycleTime(35000), new STCCycleTime(40000), new STCCycleTime(45000),
			new STCCycleTime(50000), new STCCycleTime(55000), new STCCycleTime(60000),
		};
	}
	
	private final int millis;
	
	public STCCycleTime(int millis) {
		this.millis = millis;
	}
	
	public int millis() {
		return millis;
	}
	
	@Override
	public boolean equals(Object o) {
		if (o == this) {
			return true;
		} else if (o instanceof STCCycleTime) {
			STCCycleTime that = (STCCycleTime)o;
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
