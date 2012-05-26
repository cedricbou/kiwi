package playground;

import org.vertx.java.core.Handler;
import org.vertx.java.core.eventbus.Message;
import org.vertx.java.core.json.JsonObject;
import org.vertx.java.deploy.Verticle;

public class Producer extends Verticle {
	
	
	@Override
	public void start() throws Exception {
			
		vertx.setPeriodic(2000, new Handler<Long>() {
			private final long id = (long)(Math.random() * 1000.0);
			private long count = 0;

			@Override
			public void handle(Long arg0) {
				vertx.eventBus().send("queue", new JsonObject("{ \"v\" : \"" + id + "-" + count++ + "\"}"), new Handler<Message<JsonObject>>() {
					@Override
					public void handle(Message<JsonObject> reply) {
						// container.getLogger().info(reply.body.getField("status"));
					}
				});
			}			
		});
	}

}
