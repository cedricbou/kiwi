package com.emodroid.commons.vertx;

import java.util.LinkedList;

import org.vertx.java.core.Handler;

public class ChainHandler<T> implements Handler<T> {

	private final LinkedList<Handler<T>> handlerChain = new LinkedList<Handler<T>>();
	
	public ChainHandler<T> chainWith(final Handler<T> handler) {
		handlerChain.add(handler);
		return this;
	}
	
	public void handle(T message) {
		for(final Handler<T> handler : handlerChain) {
			handler.handle(message);
		}
	};
}
