package com.github.aesteve.vertx.groovy

import groovy.transform.TypeChecked
import io.vertx.core.Handler
import io.vertx.core.http.HttpMethod
import io.vertx.groovy.core.MultiMap
import io.vertx.groovy.core.buffer.Buffer
import io.vertx.groovy.core.http.HttpServerRequest

@TypeChecked
class HttpServerRequestExtension {

	static MultiMap getParams(HttpServerRequest self) {
		self.params()
	}

	static MultiMap getHeaders(HttpServerRequest self) {
		self.headers()
	}

	static HttpServerRequest rightShiftUnsigned(HttpServerRequest self, Handler<Buffer> handler) {
		self.bodyHandler handler
	}

	static HttpServerRequest rightShiftUnsigned(HttpServerRequest self, Closure handler) {
		self.bodyHandler { buffer ->
			handler.delegate = buffer
			handler buffer
		}
	}

	static HttpMethod getMethod(HttpServerRequest self) {
		self.method()
	}

	static String getPath(HttpServerRequest self) {
		self.path()
	}

	static String minus(HttpServerRequest self, String prefix) {
		String reqPath = self.path()
		if (!reqPath.startsWith(prefix)) return reqPath
		reqPath - prefix
	}

}
