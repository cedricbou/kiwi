package playground;

import java.util.UUID;

import org.vertx.java.core.Handler;
import org.vertx.java.deploy.Verticle;

public class Test extends Verticle {

	final String uuid = UUID.randomUUID().toString();

	int memberVar = 0;
	static int staticVar = 0;
	
	@Override
	public void start() throws Exception {
		
		vertx.setPeriodic(100, new Handler<Long>() {
			@Override
			public void handle(Long arg0) {
				memberVar++;
				staticVar++;
				container.getLogger().info(uuid + " -> " + memberVar + " " + staticVar);
				vertx.eventBus().send("common", memberVar + "|" + staticVar);
			}
		});
	}
}
