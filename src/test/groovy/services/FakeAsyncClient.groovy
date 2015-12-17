package services

import io.vertx.core.AsyncResult
import io.vertx.core.Future
import io.vertx.core.Handler

/**
 * Some typical Vert.x async client 
 */
class FakeAsyncClient {
	
	public FakeAsyncClient someAsyncMethod(boolean fail, Handler<AsyncResult<String>> handler) {
		Future fut
		if (fail) {
			fut = Future.failedFuture(new RuntimeException('failed'))
		}
		else {
			fut Future.succeededFuture('something')
		}
		handler.handle fut 
		this
	}
	
}
