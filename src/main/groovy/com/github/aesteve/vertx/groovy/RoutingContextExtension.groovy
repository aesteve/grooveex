package com.github.aesteve.vertx.groovy

import io.vertx.groovy.ext.web.Route
import io.vertx.groovy.ext.web.RoutingContext

class RoutingContextExtension {
	
	static Route putAt(RoutingContext self, String key, Object obj) {
		self.put key, obj
	}
	
	static Route getAt(RoutingContext self, String path) {
		self.get path
	}
	
}
