package com.github.aesteve.vertx.groovy.promise

import groovy.transform.TypeChecked
import io.vertx.core.AsyncResult
import org.codehaus.groovy.runtime.CurriedClosure

@TypeChecked
class Promise {

	CurriedClosure closure
	Closure successHandler
	Closure failureHandler

	public Promise(CurriedClosure closure) {
		this.closure = closure
	}

	public Promise onSucceed(Closure clos) {
		successHandler = clos
		this
	}

	public Promise onFail(Closure clos) {
		failureHandler = clos
		this
	}

	public void call() {
		closure.call { AsyncResult res ->
			if (res.succeeded()) {
				if (successHandler) successHandler.call res.result()
			} else {
				if (failureHandler) failureHandler.call res.cause()
			}
		}
	}
}
