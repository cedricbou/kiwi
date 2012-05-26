package com.emodroid.kiwi.metric;

import org.vertx.java.core.json.JsonObject;

public class Point {
	public static float measure(final JsonObject json) {
		return ((Double)json.getNumber("m")).floatValue();
	}

	public static String name(final JsonObject json) {
		return json.getString("n");
	}

	public static long epoch(final JsonObject json) {
		return (Long)json.getNumber("e");
	}

}
