package playground;

import org.vertx.java.core.Handler;
import org.vertx.java.core.json.JsonObject;
import org.vertx.java.deploy.Verticle;

public class Init extends Verticle {
	@Override
	public void start() throws Exception {
		container.deployVerticle("work-queue", new JsonObject(
			"{ \"address\": \"queue\" }"), 1, new Handler<Void>() {
				@Override
				public void handle(Void v) {
					container.getLogger().info("deploy processor");
					container.deployVerticle(Processor.class.getName(), null, 3, new Handler<Void>() {
						@Override
						public void handle(Void arg0) {
							// container.deployVerticle(Producer.class.getName(), 10);
							container.deployVerticle(Test.class.getName(), 2);
						}
					});
					
				}
			});
		
	}
}
