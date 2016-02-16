package com.github.aesteve.vertx.groovy.builder

import groovy.transform.TypeChecked
import io.vertx.core.http.HttpMethod
import io.vertx.groovy.ext.web.handler.CorsHandler

@TypeChecked
class CorsDSL {

	@Delegate
	CorsHandler cors

	CorsDSL make(Closure clos) {
		clos.delegate = this
		clos.resolveStrategy = Closure.DELEGATE_FIRST
		clos()
		this
	}

	CorsDSL methods(HttpMethod... allowed) {
		allowed?.each { HttpMethod method ->
			cors.allowedMethod method
		}
		this
	}

	CorsDSL headers(Object... allowed) {
		allowed?.each {
			cors.allowedHeader(it as String)
		}
		this
	}
}
