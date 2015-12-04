package com.github.aesteve.vertx.groovy

import groovy.lang.Closure;
import groovy.transform.TypeChecked
import io.vertx.core.Handler
import io.vertx.groovy.core.streams.Pump
import io.vertx.groovy.core.streams.ReadStream
import io.vertx.groovy.core.streams.WriteStream
import io.vertx.groovy.ext.web.Route;

class ReadStreamExtension {

	static <T> ReadStream rightShift(ReadStream<T> self, T data) {
		self.handler data
	}
	
	static <T> Pump or(ReadStream self, WriteStream writer) {
		Pump.pump(self, writer)
	}

}
