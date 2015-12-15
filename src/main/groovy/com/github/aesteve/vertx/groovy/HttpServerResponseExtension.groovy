package com.github.aesteve.vertx.groovy

import groovy.json.JsonBuilder
import groovy.transform.TypeChecked
import io.vertx.groovy.core.MultiMap
import io.vertx.groovy.core.buffer.Buffer
import io.vertx.groovy.core.http.HttpServerResponse

@TypeChecked
class HttpServerResponseExtension {

    static MultiMap getHeaders(HttpServerResponse self) {
        self.headers()
    }

    static void next(HttpServerResponse self) {
        self.end()
    }
	
	static HttpServerResponse leftShift(HttpServerResponse self, JsonBuilder json) {
		self.end(json as Buffer)
	}
}
