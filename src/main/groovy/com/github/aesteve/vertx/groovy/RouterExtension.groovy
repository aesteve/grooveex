package com.github.aesteve.vertx.groovy

import groovy.transform.TypeChecked
import io.vertx.core.http.HttpMethod
import io.vertx.groovy.ext.web.Route
import io.vertx.groovy.ext.web.Router

import java.util.regex.Pattern

@TypeChecked
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

	static Route route(Router self, HttpMethod method, Pattern path) {
		self.routeWithRegex method, path.toString()
	}

}
