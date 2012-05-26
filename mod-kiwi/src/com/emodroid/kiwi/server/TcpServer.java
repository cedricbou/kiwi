package com.emodroid.kiwi.server;

import org.vertx.java.core.Handler;
import org.vertx.java.core.net.NetSocket;
import org.vertx.java.deploy.Verticle;

import com.emodroid.commons.vertx.TextLineHandler;
import com.emodroid.kiwi.metric.MetricPointReceiver;

public class TcpServer extends Verticle {

	@Override
	public void start() throws Exception {
		vertx.createNetServer().connectHandler(new Handler<NetSocket>() {
			@Override
			public void handle(final NetSocket sock) {

				sock.dataHandler(new TextLineHandler()
						.lineHandler(new MetricPointLineHandler(
								new MetricPointReceiver(container.getLogger()),
								container.getLogger())));
			}
		}).listen(9112);
	}

}
