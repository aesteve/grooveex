package com.github.aesteve.vertx.groovy

import io.vertx.core.Handler
import io.vertx.groovy.ext.web.Route;

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

}
