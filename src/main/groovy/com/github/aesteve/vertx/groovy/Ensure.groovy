package com.github.aesteve.vertx.groovy

import io.vertx.core.AsyncResult
import io.vertx.core.Handler
import io.vertx.groovy.ext.web.RoutingContext


class Ensure {

	RoutingContext ctx
	Closure expected
	Closure then

	Ensure and(Closure clos) {
		then = clos
		this
	}

	Handler or(int status) {
		return { AsyncResult res ->
			if (res.failed()) {
				ctx.fail res.cause()
			} else {
				expected.delegate = ctx
				def result = expected(res.result)
				boolean b = result?.asBoolean()
				if (b) {
					if (then) {
						then.delegate = ctx
						then(result)
					}
					ctx++
				} else ctx.fail(status)
			}
		} as Handler
	}

}
