package com.emodroid.kiwi.metric;

import org.vertx.java.core.logging.Logger;

public class MetricPointReceiver {
	
	private final Logger LOG;
	
	public MetricPointReceiver(final Logger logger) {
		this.LOG = logger;
	}
	
	public void put(final String name, final float measure, final long epoch) {
		LOG.info("got <" + name + "> <" + measure + "> <" + epoch + ">");
	}
}
