package com.github.aesteve.vertx.groovy

import groovy.transform.TypeChecked
import io.vertx.groovy.core.http.HttpServerRequest
import io.vertx.groovy.core.http.HttpServerResponse
import io.vertx.groovy.ext.web.Route
import io.vertx.groovy.ext.web.RoutingContext

@TypeChecked
class RoutingContextExtension {
	
	static RoutingContext putAt(RoutingContext self, String key, Object obj) {
		self.put key, obj
	}
	
	static Object getAt(RoutingContext self, String path) {
		self.get path
	}
	
	static HttpServerRequest getRequest(RoutingContext self) {
		self.request()
	}
	
	static HttpServerResponse getResponse(RoutingContext self) {
		self.response()
	}
}
