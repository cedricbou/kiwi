package com.emodroid.kiwi.routing;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.vertx.java.core.Handler;
import org.vertx.java.core.eventbus.Message;
import org.vertx.java.core.json.JsonObject;
import org.vertx.java.deploy.Verticle;

import com.emodroid.kiwi.app.Kiwi;
import com.emodroid.kiwi.metric.Point;
import com.hazelcast.core.Hazelcast;

public class RoutingProcessor extends Verticle {

	private static final String QUEUE_REGISTER_ADDRESS = Kiwi.PROCESSING_QUEUE_NAME
			+ ".register";

	public static final String METRIC_REPOSITORY_REGISTERING_ADDRESS = "routing-processor.register";

	public static final String CLUSTER_MAP_METRIC_REPOS = "metric-repos";
	public static final String CLUSTER_MAP_METRIC_REPOS_LOAD = "metric-repos-load";

	private final Set<String> metricRepoAddresses = Hazelcast
			.getSet("metric-repos-addresses");

	@Override
	public void start() throws Exception {
		// Create unique name for this processor.
		final String processorAddr = "routing-proc-"
				+ UUID.randomUUID().toString();
		container.getLogger().info(
				"starting routing processor " + processorAddr);

		vertx.eventBus().registerHandler(METRIC_REPOSITORY_REGISTERING_ADDRESS,
				new Handler<Message<String>>() {
					@Override
					public void handle(Message<String> message) {
						if (!metricRepoAddresses.contains(message.body)) {
							metricRepoAddresses.add(message.body);
							container.getLogger().info("[" + processorAddr + "] metric repository " + message.body + " registered");
						}
						for(final String address : metricRepoAddresses) {
							System.out.println("[" + processorAddr + "] registered repo : " + address);
						}
					}
				});

		// Filter and route metric points.
		vertx.eventBus().registerHandler(processorAddr,
				new Handler<Message<JsonObject>>() {
					@Override
					public void handle(Message<JsonObject> point) {
						final String name = Point.name(point.body);
						final float measure = Point.measure(point.body);
						final long epoch = Point.epoch(point.body);

						// Filter out unwanted metrics by name.
						// TODO : refactor (get out of there, abstract to do
						// more complex things.
						if (name.endsWith("all")) {

							// Find the repository.
							final Map<String, String> metricsMap = Hazelcast
									.getMap(CLUSTER_MAP_METRIC_REPOS);

							final String repoName;

							if (!metricsMap.containsKey(name)) {
								// Elect free repository.
								repoName = electLeastLoadedRepo();
							} else {
								repoName = metricsMap.get(name);
							}

							if (null != repoName) {
								vertx.eventBus().send(repoName, point.body);
							} else {
								container.getLogger().error(
										"[audit] no metric repository found for "
												+ point.body);
							}
						} else {
							container.getLogger().info(
									"[audit] evicted metric : " + name + " "
											+ measure + " " + epoch);

						}
						// This is a work queue processor so we have to reply.
						point.reply();
					}

					private String electLeastLoadedRepo() {
						final Map<String, Integer> loadsMap = Hazelcast
								.getMap(CLUSTER_MAP_METRIC_REPOS_LOAD);

						String candidateRepoAddress = null;
						int leastLoadFactor = Integer.MAX_VALUE;
						String lastSeen = null;
						
						for (final String repoAddress : metricRepoAddresses) {
							if (loadsMap.containsKey(repoAddress)) {
								final int load = loadsMap.get(repoAddress);

								if (load < leastLoadFactor) {
									leastLoadFactor = load;
									candidateRepoAddress = repoAddress;
								}
							}
							lastSeen = repoAddress;
						}

						if(candidateRepoAddress == null) {
							candidateRepoAddress = lastSeen;
						}
						
						return candidateRepoAddress;
					}
				});

		// Register with the processing queue.
		vertx.eventBus().send(QUEUE_REGISTER_ADDRESS,
				new JsonObject().putString("processor", processorAddr));
	}
}
