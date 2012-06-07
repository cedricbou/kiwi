package com.emodroid.kiwi.metric;

import com.emodroid.clock.Clock;
import com.emodroid.kiwi.metric.PointList.PointListVisitor;

public class Metric {

	private static final int ONE_SECOND_IN_MILLIS = 1000;

	private final PointList points;

	private final int numberOfPoints;

	private final long milliSecondsOfRetention;

	private final int milliSecondsPerPoints;
	
	private final Clock clock;

	private static class SelectVisitedPoints implements PointListVisitor {
		private final long fromTime;
		private final PointList points;

		public SelectVisitedPoints(final long fromTimestamp) {
			this.fromTime = fromTimestamp;
			this.points = new PointList();
		}

		@Override
		public void visit(long timestamp, float value) {
			if (timestamp >= fromTime) {
				points.add(timestamp, value);
			}
		}

		public PointList selectedPoints() {
			return points;
		}

	}

	public Metric(final int resolutionPPS, final int secondsOfRetention) {
		this(Clock.SYSTEM, resolutionPPS, secondsOfRetention);
	}
	
	public Metric(final Clock clock, final int resolutionPPS, final int secondsOfRetention) {
		this.numberOfPoints = resolutionPPS * secondsOfRetention;
		this.milliSecondsOfRetention = secondsOfRetention * 1000;
		this.milliSecondsPerPoints = (int) (ONE_SECOND_IN_MILLIS / resolutionPPS);
		points = new PointList(numberOfPoints);
		this.clock = clock;
	}

	public void push(final long timestamp, final float value) {
		final int distance = distance(timestamp);

		if (distance >= 0) {
			points.put(distance % numberOfPoints, timestamp, value);
		}
	}

	public PointList fetch(final Delay from) {
		if (isEmpty()) {
			return new PointList();
		}

		final long now = clock.currentTimeMillis();

		long oldestTimestamp = now - milliSecondsOfRetention;

		long fromTimestamp = now - (from.delay() * ONE_SECOND_IN_MILLIS);

		if (fromTimestamp < oldestTimestamp) {
			fromTimestamp = oldestTimestamp;
		}

		int fromDistance = distance(alignNextInterval(fromTimestamp));
				
		final int untilDistance = distance(now);

		final SelectVisitedPoints pointSelector = new SelectVisitedPoints(
				fromTimestamp);

		if (fromDistance < untilDistance) {
			points.visit(pointSelector, fromDistance, untilDistance);
		} 
		else {
			points.visit(pointSelector, fromDistance);
			points.visit(pointSelector, 0, untilDistance);
		}

		return pointSelector.selectedPoints();
	}

	private int distance(final long timestamp) {
		if (isEmpty()) {
			return 0;
		}

		final long alignedBaseTimestamp = align(baseTimestamp());
		final long alignedTimestamp = align(timestamp);

		final int distance = (int) ((alignedTimestamp - alignedBaseTimestamp) / milliSecondsPerPoints)
				% numberOfPoints;
		
		if(distance < 0) {
			return numberOfPoints + distance;
		}
		
		return distance;
	}

	/**
	 * Align the timestamp at the start of the interval the timestamp is
	 * contained in.
	 * 
	 * @param timestamp
	 *            the timestamp to align
	 * @return the timestamp aligned at the start of interval
	 */
	private long align(final long timestamp) {
		return timestamp - (timestamp % milliSecondsPerPoints);
	}

	/**
	 * Align the timestamp at the beginning of the next interval after this
	 * timestamp interval.
	 * 
	 * @param timestamp
	 * @return
	 */
	private long alignNextInterval(final long timestamp) {
		return align(timestamp) + milliSecondsPerPoints;
	}

	private boolean isEmpty() {
		return points.getTimeStamp(0) == 0 && Float.isNaN(points.getValue(0));
	}

	private long baseTimestamp() {
		return points.getTimeStamp(0);
	}

}
