package com.github.aesteve.vertx.groovy

import groovy.transform.TypeChecked 
import io.vertx.core.Handler 
import io.vertx.groovy.core.MultiMap 
import io.vertx.groovy.core.http.HttpServerRequest

@TypeChecked
class HttpServerRequestExtension {

	static MultiMap getParams(HttpServerRequest self) {
		self.params()
	}

	static MultiMap getHeaders(HttpServerRequest self) {
		self.headers()
	}

	static HttpServerRequest rightShiftUnsigned(HttpServerRequest self, Handler handler) {
		self.bodyHandler handler
	}

}
