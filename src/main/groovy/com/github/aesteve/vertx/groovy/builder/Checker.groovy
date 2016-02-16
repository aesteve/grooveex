package com.github.aesteve.vertx.groovy.builder

import io.vertx.core.Handler
import io.vertx.groovy.ext.web.RoutingContext

class Checker {

	Closure check
	int status

	Checker or(int status) {
		this.status = status
		this
	}

	def asType(Class c) {
		if (c == Handler) {
			return { RoutingContext ctx ->
				check.delegate = ctx
				def res = check(ctx)
				boolean result = res?.asBoolean()
				if (result) {
					ctx.yield res
				} else {
					ctx.fail status
				}
			} as Handler
		}
	}
}
