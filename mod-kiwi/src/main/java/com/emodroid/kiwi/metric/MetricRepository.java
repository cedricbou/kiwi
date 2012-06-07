package com.emodroid.kiwi.metric;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.vertx.java.core.Handler;
import org.vertx.java.core.eventbus.Message;
import org.vertx.java.core.json.JsonObject;
import org.vertx.java.deploy.Verticle;

import com.emodroid.kiwi.routing.RoutingProcessor;
import com.hazelcast.core.Hazelcast;

public class MetricRepository extends Verticle {

	private final static String METRIC_REPO_DEFAULT_BUS_ADDRESS = "metric-repo";
	
	private final String address = METRIC_REPO_DEFAULT_BUS_ADDRESS + "-" + UUID.randomUUID().toString(); 
	
	private final Map<String, Metric> metrics = new HashMap<String, Metric>();
	
	@Override
	public void start() throws Exception {
		container.getLogger().info("starting metric repository " + address + ", is worker : " + vertx.isWorker());
		
		vertx.eventBus().registerHandler(address, new Handler<Message<JsonObject>>() {
			final Map<String, String> metricRepos = Hazelcast.getMap(RoutingProcessor.CLUSTER_MAP_METRIC_REPOS); 
			final Map<String, Integer> metricLoads = Hazelcast.getMap(RoutingProcessor.CLUSTER_MAP_METRIC_REPOS_LOAD);

			@Override
			public void handle(Message<JsonObject> metric) {
				container.getLogger().info("received metric as Json : " + metric.body.toString());
				
				final String name = Point.name(metric.body);
				
				if(!metricRepos.containsKey(name)) {
					createMetric(name);
				}
				
				pushPoint(name,Point.measure(metric.body), Point.epoch(metric.body));
			}
			
			private void pushPoint(final String name, float measure, long epoch) {
				System.out.println("point for " + name + ":" + measure + "/" + epoch);
				metrics.get(name).push(epoch, measure);
			}
			
			private void createMetric(final String name) {
				// TODO : instanciate a metric
				container.getLogger().info("creating metric " + name + " in repository " + address);
				
				final Metric metric = new Metric(10, 300);
				metrics.put(name, metric);

				// declare metric in this repository
				metricRepos.put(name, address);
				
				// increment load counter;
				int load = 1;
				
				if(metricLoads.containsKey(address)) {
					load += metricLoads.get(address) + 1;
				}
				
				metricLoads.put(address, load);
			}
		});
		
		vertx.eventBus().send(RoutingProcessor.METRIC_REPOSITORY_REGISTERING_ADDRESS, address);
	}
}
