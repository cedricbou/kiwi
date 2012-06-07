package com.emodroid.kiwi.routing;

import org.vertx.java.core.Handler;
import org.vertx.java.core.eventbus.Message;
import org.vertx.java.core.json.JsonObject;
import org.vertx.java.core.logging.Logger;

public class RouterHandler implements Handler<Message<JsonObject>>  {

	private final Logger LOG;
	
	public RouterHandler(final Logger logger) {
		this.LOG = logger;
	}
	
	public void handle(final Message<JsonObject> message) {
		LOG.info("router handling message : " + message.body.toString());
	}

}
