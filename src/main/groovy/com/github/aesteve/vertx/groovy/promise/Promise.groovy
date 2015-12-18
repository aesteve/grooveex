package com.github.aesteve.vertx.groovy.promise

import io.vertx.core.AsyncResult
import io.vertx.groovy.ext.web.RoutingContext
import org.codehaus.groovy.runtime.CurriedClosure
import rx.Observable

class Promise {

	CurriedClosure closure

	Promise(CurriedClosure closure) {
		this.closure = closure
	}

	Observable call(RoutingContext ctx = null) {
		Observable.create { observer ->
			closure.call { AsyncResult res ->
				if (res.succeeded()) {
					def result = res.result()
					observer.onNext result
					if (ctx) {
						ctx.yield result
					}
				} else {
					Throwable cause = res.cause()
					observer.onError(cause)
					if (ctx) {
						ctx.fail cause
					}
				}
				observer.onCompleted()
			}
		}
	}
}
