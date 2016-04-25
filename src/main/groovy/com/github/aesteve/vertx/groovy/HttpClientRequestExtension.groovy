package com.github.aesteve.vertx.groovy

import groovy.json.JsonBuilder
import groovy.transform.TypeChecked
import io.vertx.core.http.HttpMethod
import io.vertx.groovy.core.MultiMap
import io.vertx.groovy.core.buffer.Buffer
import io.vertx.groovy.core.http.HttpClientRequest

@TypeChecked
class HttpClientRequestExtension {

	static void next(HttpClientRequest self) {
		self.end()
	}

	static MultiMap getHeaders(HttpClientRequest self) {
		self.headers()
	}

	static HttpClientRequest putAt(HttpClientRequest self, CharSequence header, String val) {
		self.headers().add(header.toString(), val)
		self
	}

	static HttpClientRequest leftShift(HttpClientRequest self, JsonBuilder json) {
		self.end(json as Buffer)
		self
	}

	static HttpClientRequest rightShift(HttpClientRequest self, Closure closure) {
		self.handler { ctx ->
			closure.delegate = ctx
			closure ctx
		}
	}


	static HttpMethod getMethod(HttpClientRequest self) {
		self.method()
	}

	static MultiMap setHeaders(HttpClientRequest req, MultiMap headers) {
		MultiMap reqHeaders = req.headers()
		reqHeaders.clear()
		reqHeaders.addAll headers
		reqHeaders
	}

}
