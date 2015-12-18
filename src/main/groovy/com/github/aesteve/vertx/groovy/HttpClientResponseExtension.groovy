package com.github.aesteve.vertx.groovy

import groovy.transform.TypeChecked
import io.vertx.core.Handler
import io.vertx.groovy.core.MultiMap
import io.vertx.groovy.core.http.HttpClientResponse

@TypeChecked
class HttpClientResponseExtension {
	static void rightShiftUnsigned(HttpClientResponse self, Handler handler) {
		self.bodyHandler handler
	}

	static MultiMap getHeaders(HttpClientResponse self) {
		self.headers()
	}

	static int getStatusCode(HttpClientResponse self) {
		self.statusCode()
	}

	static String getStatusMessage(HttpClientResponse self) {
		self.statusMessage()
	}

}
