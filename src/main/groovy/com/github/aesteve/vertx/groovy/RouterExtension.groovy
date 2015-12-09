package com.github.aesteve.vertx.groovy

import io.vertx.groovy.ext.web.Route 
import io.vertx.groovy.ext.web.Router

class RouterExtension {

	static Route putAt(Router self, String path, Closure handler) {
		self.route(path).handler { ctx ->
			handler.delegate = ctx
			handler ctx
		}
	}

	static Route getAt(Router self, String path) {
		self.route(path)
	}

}
