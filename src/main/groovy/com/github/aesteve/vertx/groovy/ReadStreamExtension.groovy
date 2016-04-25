package com.github.aesteve.vertx.groovy

import groovy.transform.TypeChecked
import io.vertx.core.Handler
import io.vertx.groovy.core.streams.Pump
import io.vertx.groovy.core.streams.ReadStream
import io.vertx.groovy.core.streams.WriteStream

@TypeChecked
class ReadStreamExtension {

	static <T> ReadStream<T> rightShift(ReadStream<T> self, Handler<T> handler) {
		self.handler handler
	}

	static <T> ReadStream<T> rightShift(ReadStream<T> self, Closure<?> closure) {
		self.handler { read ->
			closure.delegate = read
			closure read
		}
	}

	static <T> ReadStream<T> unsignedRightShift(ReadStream<T> self, Closure closure) {
		self.endHandler(closure as Handler<Void>)
	}

	static <T> Pump or(ReadStream<T> self, WriteStream<T> writer) {
		Pump.pump(self, writer)
	}

}
