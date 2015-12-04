package com.github.aesteve.vertx.groovy

import groovy.transform.TypeChecked
import io.vertx.groovy.core.http.HttpClient
import io.vertx.groovy.core.http.HttpClientRequest

@TypeChecked
class HttpClientExtension {
	static HttpClientRequest getAt(HttpClient self, String path) {
		self.get path
	}
}
