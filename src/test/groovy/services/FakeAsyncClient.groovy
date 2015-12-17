package services

import io.vertx.core.AsyncResult
import io.vertx.core.Future
import io.vertx.core.Handler

/**
 * Some typical Vert.x async client 
 */
class FakeAsyncClient {

	public FakeAsyncClient someAsyncMethod(String theParam, Handler<AsyncResult<String>> handler) {
		handler.handle Future.succeededFuture(theParam)
		this
	}

	public FakeAsyncClient someAsyncMethod(Handler<AsyncResult<String>> handler) {
		handler.handle Future.succeededFuture('void')
	}
	
}
