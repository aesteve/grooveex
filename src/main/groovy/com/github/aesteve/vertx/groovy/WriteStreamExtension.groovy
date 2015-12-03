package com.github.aesteve.vertx.groovy

import groovy.lang.Closure;
import io.vertx.core.Handler
import io.vertx.groovy.core.streams.WriteStream
import io.vertx.groovy.ext.web.Route;

class WriteStreamExtension {

	static <T> WriteStream leftShift(WriteStream self, T data) {
		self.write data
	}

}
