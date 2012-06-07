package com.emodroid.clock;

public interface Clock {
	public final static Clock SYSTEM = new Clock() {
		public long currentTimeMillis() {
			return System.currentTimeMillis();
		};
	};
	
	public long currentTimeMillis();
}
