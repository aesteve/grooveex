package com.github.aesteve.vertx.groovy.promise

import io.vertx.core.AsyncResult
import io.vertx.groovy.ext.web.RoutingContext
import org.codehaus.groovy.runtime.CurriedClosure
import rx.Observable

class Promise {

	CurriedClosure closure

	@Delegate
	Observable obs

	RoutingContext context

	Promise(CurriedClosure closure) {
		this.closure = closure
		createObservable()
	}

	private void createObservable() {
		obs = Observable.create { observer ->
			closure.call { AsyncResult res ->
				if (res.succeeded()) {
					def result = res.result()
					observer.onNext result
					if (context) {
						context.yield result
					}
				} else {
					Throwable cause = res.cause()
					observer.onError(cause)
					if (context) {
						context.fail cause
					}
				}
				observer.onCompleted()
			}
		}
	}
}
