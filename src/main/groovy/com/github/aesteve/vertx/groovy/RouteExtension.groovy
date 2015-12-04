package com.github.aesteve.vertx.groovy

import groovy.lang.Closure;
import io.vertx.core.Handler
import io.vertx.groovy.ext.web.Route;

class RouteExtension {

	static Route rightShift(Route self, Handler handler) {
		self.handler handler
	}

}
