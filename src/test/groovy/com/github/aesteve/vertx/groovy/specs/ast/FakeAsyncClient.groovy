package com.github.aesteve.vertx.groovy.specs.ast

import io.vertx.core.AsyncResult
import io.vertx.core.Future
import io.vertx.core.Handler

/**
 * Some typical Vert.x async client 
 */
class FakeAsyncClient {

	FakeAsyncClient someAsyncMethod(String theParam, Handler<AsyncResult<String>> handler) {
		handler.handle Future.succeededFuture(theParam)
		this
	}

	FakeAsyncClient someAsyncMethod(Handler<AsyncResult<String>> handler) {
		handler.handle Future.succeededFuture('void')
	}

}
