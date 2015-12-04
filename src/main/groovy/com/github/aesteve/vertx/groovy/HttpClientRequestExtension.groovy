package com.github.aesteve.vertx.groovy

import groovy.transform.TypeChecked
import io.vertx.groovy.core.http.HttpClientRequest

@TypeChecked
class HttpClientRequestExtension {
	static void next(HttpClientRequest self) {
		self.end()
	}
}
