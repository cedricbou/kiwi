package com.emodroid.kiwi.metric;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Delay {
	
	private final long delayInSecond;
	
	private enum Units {
		h(3600), m(60), s(1);
		
		private final long toSecondCoeff;
		
		private Units(final long toSecondCoeff) {
			this.toSecondCoeff = toSecondCoeff;
		}
		
		public long convert(final long timeInUnit) {
			return timeInUnit * toSecondCoeff;
		}
	};
	
	public Delay(String delayExpr) {
		final Pattern pattern = Pattern.compile("(\\d+)([hms])");
		final Matcher matcher = pattern.matcher(delayExpr.toLowerCase()); 
				
		try {
			if(matcher.matches()) {
				final String time = matcher.group(1);
				final String unit = matcher.group(2);
			
				delayInSecond = Units.valueOf(unit).convert(Long.parseLong(time)); 
			}
			else {
				throw new IllegalArgumentException("Time expression did not match regex pattern : " + pattern.pattern());
			}
		}
		catch(Exception e) {
			throw new IllegalArgumentException("Expected time expressed as 24h, 32m or 10s for examples", e);
		}
	}
	
	public long delay() {
		return delayInSecond;
	}
}
