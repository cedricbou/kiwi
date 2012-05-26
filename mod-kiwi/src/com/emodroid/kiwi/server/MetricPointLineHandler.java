package com.emodroid.kiwi.server;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.vertx.java.core.Handler;
import org.vertx.java.core.logging.Logger;

import com.emodroid.kiwi.metric.MetricPointReceiver;

public class MetricPointLineHandler implements Handler<String> {

	private static final Pattern METRIC_POINT_PATTERN = Pattern.compile("([\\[\\]\\w\\.\\-\\_]+)\\s+((\\d*(\\.\\d+){0,1})|NaN)\\s+(\\d+)\\s*");
	
	private static final int NAME_PATTERN_GROUP = 1;
	private static final int MEASURE_PATTERN_GROUP = 2;
	private static final int EPOCH_PATTERN_GROUP = 5;
	
	private final MetricPointReceiver receiver;
	
	private final Logger LOG;
	
	public MetricPointLineHandler(final MetricPointReceiver receiver) {
		this(receiver, null);
	}
	
	public MetricPointLineHandler(final MetricPointReceiver receiver, final Logger logger) {
		this.receiver = receiver;
		this.LOG = logger;
	}
	
	@Override
	public void handle(String metricLine) {
		final Matcher matcher = METRIC_POINT_PATTERN.matcher(metricLine);

		try {
			if(matcher.matches()) {
				final String name = matcher.group(NAME_PATTERN_GROUP);
				final String measureAsString = matcher.group(MEASURE_PATTERN_GROUP);
				final float measure;
				if("NaN".equalsIgnoreCase(measureAsString)) {
					measure = Float.NaN;
				}
				else {
					measure = Float.parseFloat(measureAsString);
				}
				final long epoch = Long.parseLong(matcher.group(EPOCH_PATTERN_GROUP));
				receiver.put(name, measure, epoch);
			}
			else
			{
				throw new IllegalArgumentException("line does not match pattern <name(string) measure(float) epoch(long)");
			}
		} catch(Exception e) {
			if(null != LOG) {
				LOG.error("wrong line format for line <" + metricLine + ">", e);
			}
		}

	}
}
