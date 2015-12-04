package com.github.aesteve.vertx.groovy

import groovy.transform.TypeChecked
import io.vertx.groovy.core.MultiMap
import io.vertx.groovy.core.buffer.Buffer
import io.vertx.groovy.core.http.HttpServerResponse

@TypeChecked
class HttpServerResponseExtension {
	
	static MultiMap getHeaders(HttpServerResponse self) {
		self.headers()
	}
	
	static void leftShift(HttpServerResponse self, Buffer buff) {
		self.end buff
	}

	static void leftShift(HttpServerResponse self, String s) {
		self.end s
	}
	
	static void next(HttpServerResponse self) {
		self.end()
	}
}
