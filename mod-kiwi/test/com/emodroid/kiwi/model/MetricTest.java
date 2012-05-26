package com.emodroid.kiwi.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.Ignore;
import org.junit.Test;

import com.emodroid.clock.ManualClock;
import com.emodroid.clock.ManualClock.TimeUnit;
import com.emodroid.kiwi.metric.Delay;
import com.emodroid.kiwi.metric.Metric;
import com.emodroid.kiwi.metric.PointList;

public class MetricTest {

	@Test
	public void pushAPoint() {
		Metric metric = new Metric(1, 10);
		
		metric.push(System.currentTimeMillis(), 3.45f);

		final PointList points = metric.fetch(new Delay("5s"));
		assertEquals(1, points.size());
	}

	@Test
	public void pushTwoPoint() {
		Metric metric = new Metric(1, 300);
		
		metric.push(System.currentTimeMillis() - 3000, 3.45f);
		metric.push(System.currentTimeMillis() - 1000, 45.23f);
		
		final PointList points = metric.fetch(new Delay("5s"));
		assertEquals(2, points.size());
	}

	@Test
	public void pushAPointUseRoundRobin() {
		final ManualClock clock = new ManualClock();
				
		Metric metric = new Metric(clock, 1, 3);
		
		final long tsPoint1 = clock.currentTimeMillis();  
		metric.push(tsPoint1, 3.45f);
		
		clock.add(1, TimeUnit.SECONDS);
		final long tsPoint2 = clock.currentTimeMillis();  
		metric.push(tsPoint2, 5.45f);

		clock.add(1, TimeUnit.SECONDS);
		final long tsPoint3 = clock.currentTimeMillis();  
		metric.push(tsPoint3, 10.45f);
		
		final PointList points = metric.fetch(new Delay("5s"));
		
		assertEquals(3, points.size());
		assertEquals(tsPoint1 , points.getTimeStamp(0));
		assertEquals(3.45f, points.getValue(0), 0.01);
		
		clock.add(1, TimeUnit.SECONDS);
		final long tsPoint4 = clock.currentTimeMillis();  
		metric.push(tsPoint4, 13.45f);

		final PointList pointsAfter = metric.fetch(new Delay("5s"));

		assertEquals(3, pointsAfter.size());
		assertEquals(5.45f, pointsAfter.getValue(0), 0.01);
		assertEquals(tsPoint2, pointsAfter.getTimeStamp(0));
		assertEquals(10.45f, pointsAfter.getValue(1), 0.01);
		assertEquals(tsPoint3, pointsAfter.getTimeStamp(1));
		assertEquals(13.45f, pointsAfter.getValue(2), 0.01);
		assertEquals(tsPoint4, pointsAfter.getTimeStamp(2));
		
	}
	
	@Test
	public void fetchOrderedFromAnywhereInARoundRobinedList() {
		final long baseTime = System.currentTimeMillis(); 
		
		Metric metric = new Metric(1, 3);
		
		metric.push(baseTime - 3000, 3.45f);
		metric.push(baseTime - 2000, 5.45f);
		metric.push(baseTime - 1000, 10.45f);			
		metric.push(baseTime, 13.45f);

		final PointList pointsAfter = metric.fetch(new Delay("5s"));

		assertEquals(3, pointsAfter.size());

		assertEquals(5.45f, pointsAfter.getValue(0), 0.01);
		assertEquals(10.45f, pointsAfter.getValue(1), 0.01);
		assertEquals(13.45f, pointsAfter.getValue(2), 0.01);
	}
	
	@Test
	@Ignore
	public void pushAListOfPoint() {
		fail("todo");
	}
}
