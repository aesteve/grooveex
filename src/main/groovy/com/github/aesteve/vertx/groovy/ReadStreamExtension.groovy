package com.github.aesteve.vertx.groovy

import groovy.lang.Closure;
import io.vertx.core.Handler
import io.vertx.groovy.core.streams.ReadStream
import io.vertx.groovy.core.streams.WriteStream
import io.vertx.groovy.ext.web.Route;

class ReadStreamExtension {

	static <T> ReadStream rightShift(ReadStream self, T data) {
		self.handler data
	}

}
