package com.emodroid.kiwi.metric;

import org.apache.commons.collections.primitives.ArrayFloatList;
import org.apache.commons.collections.primitives.ArrayLongList;
import org.apache.commons.collections.primitives.FloatList;
import org.apache.commons.collections.primitives.LongList;

// @JsonSerialize(using = PointListJacksonSerializer.class)
public class PointList {

	public interface PointListVisitor {
		public void visit(final long timestamp, final float value);
	}
	
	public PointList() {
		this(0);
	}

	private final LongList timestamps;
	private final FloatList values;
	
	public PointList(int numberOfPoints) {
		this.timestamps = new ArrayLongList(numberOfPoints==0?100:numberOfPoints);
		this.values = new ArrayFloatList(numberOfPoints==0?100:numberOfPoints);
		
		for(int i = 0; i < numberOfPoints; ++i) {
			timestamps.add(0);
			values.add(Float.NaN);
		}
	}
	
	public void add(long timestamp, float value) {
		timestamps.add(timestamp);
		values.add(value);
	}
	
	public void put(int index, long timestamp, float value) {
		timestamps.set(index, timestamp);
		values.set(index, value);
	}
	
	public void visit(PointListVisitor visitor, int from, int to) {
		for(int i = from; i <= to; ++i) {
			visitor.visit(timestamps.get(i), values.get(i));
		}
	}
	
	public void visit(PointListVisitor visitor, int from) {
		visit(visitor, from, timestamps.size() - 1);
	}

	public void visit(PointListVisitor visitor) {
		visit(visitor, 0, timestamps.size() - 1);
	}
		
	public int size() {
		return timestamps.size();
	}
	
	public long getTimeStamp(int index) {
		return timestamps.get(index);
	}
	
	public float getValue(int index) {
		return values.get(index);
	}
	
	@Override
	public String toString() {
		String result = "[";
		for(int i = 0; i < timestamps.size(); ++i) {
			result += "[" + timestamps.get(i) + "," + values.get(i) + "]";
			if(i < timestamps.size() - 1) {
				result += ",";
			}
		}
		return result + "]";
	}
}
