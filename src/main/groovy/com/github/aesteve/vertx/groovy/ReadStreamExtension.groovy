package com.github.aesteve.vertx.groovy

import io.vertx.groovy.core.streams.Pump
import io.vertx.groovy.core.streams.ReadStream
import io.vertx.groovy.core.streams.WriteStream

class ReadStreamExtension {

	static <T> ReadStream rightShift(ReadStream<T> self, T data) {
		self.handler data
	}

	static <T> Pump or(ReadStream self, WriteStream writer) {
		Pump.pump(self, writer)
	}

}
