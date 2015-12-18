package com.github.aesteve.vertx.groovy

import groovy.transform.TypeChecked
import io.vertx.core.AsyncResult
import io.vertx.core.Future

@TypeChecked
class FutureExtension {

	static boolean isFailed(AsyncResult self) {
		self.failed()
	}

	static boolean isSucceeded(AsyncResult self) {
		self.succeeded()
	}

	static <T> T getResult(AsyncResult<T> self) {
		self.result()
	}

	static Throwable getCause(AsyncResult self) {
		self.cause()
	}

	static boolean positive(AsyncResult self) {
		self.succeeded()
	}

	static boolean negative(AsyncResult self) {
		self.failed()
	}

	static boolean asBoolean(AsyncResult self) {
		self?.succeeded()
	}

	static <T> Closure<AsyncResult<T>> completeOrFail(Future<T> self) {
		return { AsyncResult<T> res ->
			if (res.failed()) {
				self.fail(res.cause())
			} else {
				self.complete(res.result())
			}
		}
	}

	static void next(Future self) {
		self.complete()
	}

	static void next(io.vertx.groovy.core.Future self) {
		self.complete()
	}

	static <T> Future<T> plus(Future<T> self, T result) {
		self.complete(result)
		self
	}

	static <T> io.vertx.groovy.core.Future<T> plus(io.vertx.groovy.core.Future<T> self, T result) {
		self.complete(result)
		self
	}

	static Future minus(Future self, Throwable cause) {
		self.fail(cause)
		self
	}

	static Future minus(Future self, String cause) {
		self.fail(cause)
		self
	}

	static io.vertx.groovy.core.Future minus(io.vertx.groovy.core.Future self, String cause) {
		self.fail(cause)
		self
	}

}
