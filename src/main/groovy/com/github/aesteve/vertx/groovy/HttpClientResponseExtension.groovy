package com.github.aesteve.vertx.groovy

import groovy.transform.TypeChecked
import io.vertx.core.Handler
import io.vertx.groovy.core.http.HttpClientRequest
import io.vertx.groovy.core.http.HttpClientResponse

@TypeChecked
class HttpClientResponseExtension {
	static void rightShiftUnsigned(HttpClientResponse self, Handler handler) {
		self.bodyHandler handler
	}
}
