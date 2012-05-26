package com.emodroid.kiwi.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Test;

import com.emodroid.kiwi.metric.Delay;

public class DelayTest {
	
	@Test
	public void delayExpressedInHours() {
		final Delay d = new Delay("27h");
		assertEquals(27 * 3600, d.delay());
	}

	@Test
	public void delayExpressedInMinutes() {
		final Delay d = new Delay("185m");
		assertEquals(185 * 60, d.delay());
	}

	@Test
	public void delayExpressedInSeconds() {
		final Delay d = new Delay("999s");
		assertEquals(999, d.delay());
	}
	
	@Test
	public void delayWronglyExpressed() {
		try {
			new Delay("365a");
			fail("should have thrown an expression");
		}
		catch(Exception e) {
			e.printStackTrace();
			assertTrue(true);
		}
	}
}
