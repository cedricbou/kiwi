package com.emodroid.kiwi.model;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.emodroid.kiwi.metric.PointList;

public class PointListTest {

	@Test
	public void pointListSizeIsZeroByDefault() {
		final PointList points = new PointList(10);
		assertEquals(10, points.size());
	}
		
}
