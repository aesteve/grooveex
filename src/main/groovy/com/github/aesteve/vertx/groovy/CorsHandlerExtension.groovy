package com.github.aesteve.vertx.groovy

import groovy.transform.TypeChecked
import io.vertx.core.http.HttpHeaders
import io.vertx.core.http.HttpMethod
import io.vertx.groovy.ext.web.handler.CorsHandler

@TypeChecked
class CorsHandlerExtension {

	static CorsHandler allowedMethods(CorsHandler self, HttpMethod... methods) {
		methods?.each { self.allowedMethod(it as HttpMethod) }
		self
	}

	static CorsHandler allowedHeaders(CorsHandler self, Object... headers) {
		headers?.each { self.allowedHeader(it as String) }
		self
	}

}
