package com.emodroid.kiwi.app;

import org.vertx.java.core.Handler;
import org.vertx.java.core.eventbus.Message;
import org.vertx.java.core.json.JsonObject;
import org.vertx.java.deploy.Verticle;

import com.emodroid.kiwi.metric.MetricRepository;
import com.emodroid.kiwi.routing.RoutingProcessor;

public class Kiwi extends Verticle {

	public static final String PROCESSING_QUEUE_NAME = "routing.processing.queue";

	@Override
	public void start() throws Exception {
		final JsonObject queueConfig = new JsonObject().putString("address",
				PROCESSING_QUEUE_NAME);

		container.deployVerticle("work-queue", queueConfig, 1,
				new Handler<Void>() {

					@Override
					public void handle(Void v) {
						container.deployVerticle(
								RoutingProcessor.class.getName(), null, 3,
								new Handler<Void>() {

									@Override
									public void handle(Void nothing) {
										container
												.deployVerticle(MetricRepository.class
														.getName());
										container
												.deployVerticle(MetricRepository.class
														.getName());

										// Simulator
										vertx.setPeriodic(100,
												new Handler<Long>() {
													final String[] names = new String[] {
															"toto.all",
															"titi.all",
															"fifi.all",
															"ruru.count" };

													@Override
													public void handle(Long time) {
														vertx.eventBus()
																.send(PROCESSING_QUEUE_NAME,
																		new JsonObject()
																				.putString(
																						"n",
																						names[(int) (Math
																								.random() * names.length)])
																				.putNumber(
																						"m",
																						Math.random() * 1000.0f)
																				.putNumber(
																						"e",
																						System.currentTimeMillis()),
																		new Handler<Message<JsonObject>>() {

																			public void handle(
																					Message<JsonObject> arg0) {
																			};
																		});
													}
												});

									}
								});
					}
				});

	}
}