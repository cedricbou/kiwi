package com.emodroid.commons.vertx;

import org.vertx.java.core.Handler;
import org.vertx.java.core.buffer.Buffer;


public class TextLineHandler implements Handler<Buffer> {

	private Buffer currentLineBytes = new Buffer();

	private Handler<String> lineHandler = new Handler<String>() {
		@Override
		public void handle(String line) {
		}
	};
	
	public TextLineHandler lineHandler(final Handler<String> handler) {
		this.lineHandler = handler;
		return this;
	}
	
	
	@Override
	public void handle(final Buffer buffer) {

		if (buffer.length() > 0) {
			final Buffer[] buffers = BufferUtils.split(buffer,
					BufferUtils.lineDelimiters());

			currentLineBytes.appendBuffer(buffers[0]);

			if (buffers.length > 1
					|| BufferUtils.EMPTY_BUFFER.equals(buffers[0])) {
				
				lineHandler.handle(currentLineBytes.toString());

				for (int i = 1; i < buffers.length - 1; ++i) {
					lineHandler.handle(buffers[i].toString());
				}

				currentLineBytes = buffers[buffers.length - 1];
			}
		}

	}
}
