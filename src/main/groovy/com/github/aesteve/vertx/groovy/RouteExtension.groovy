package com.github.aesteve.vertx.groovy

import groovy.transform.TypeChecked
import io.vertx.core.Handler
import io.vertx.groovy.ext.web.Route

import java.util.regex.Pattern;

@TypeChecked
class RouteExtension {

	static Route rightShift(Route self, Closure clos) {
		self.handler { ctx ->
			clos.delegate = ctx
			clos(ctx)
		}
	}

	static Route rightShift(Route self, Handler handler) {
		self.handler handler
	}

	static Route call(Route self, Pattern regex) {
		self.pathRegex regex.toString()
	}

}
