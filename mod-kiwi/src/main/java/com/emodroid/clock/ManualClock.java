package com.emodroid.clock;

public class ManualClock implements Clock {

	private long currentTimeMillis = System.currentTimeMillis();
	
	public static enum TimeUnit {
		MILLIS(1), SECONDS(1000), MINUTES(1000*60), HOURS(1000*3600);
		
		private final long numberOfMillisInUnit;
		
		private TimeUnit(final long numberOfMillisInUnit) {
			this.numberOfMillisInUnit = numberOfMillisInUnit;
		}
		
		protected long quantifyInMillis(final long quantity) {
			return quantity * numberOfMillisInUnit;
		}
		
	}
	
	@Override
	public long currentTimeMillis() {
		return this.currentTimeMillis;
	}
	
	public void add(int timeQuantity, TimeUnit unit) {
		this.currentTimeMillis += unit.quantifyInMillis(timeQuantity);
	}
}
