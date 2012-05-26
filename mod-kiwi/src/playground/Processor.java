package playground;

import org.vertx.java.core.Handler;
import org.vertx.java.core.eventbus.Message;
import org.vertx.java.core.json.JsonObject;
import org.vertx.java.deploy.Verticle;

public class Processor extends Verticle {

	@Override
	public void start() throws Exception {
		final String commonAddr = "common";
		final String addr = "proc-" + 
				new Integer((int)(Math.random() * 10000.0)).toString().trim();

		vertx.eventBus().send("queue.register", new JsonObject("{ \"processor\" : \"" + addr + "\"}"));
		
		vertx.eventBus().registerHandler(addr, new Handler<Message<JsonObject>>() {
			public void handle(Message<JsonObject> arg0) {
				System.out.println(addr + " : " + arg0.body.getString("v"));
				arg0.reply();
			};
		});
		
		vertx.eventBus().registerHandler(commonAddr, new Handler<Message<String>>() {
			@Override
			public void handle(Message<String> arg0) {
				System.out.println("[" + addr + "] " + arg0.body);
			}
		});
	}
}
