package com.github.aesteve.vertx.groovy.builder.http

import io.vertx.core.http.HttpMethod

class MultiMethod {

	List<HttpMethod> methods = []

	public MultiMethod(HttpMethod http) {
		methods << http
	}

	MultiMethod or(Closure clos) {
		methods.each { clos.call it }
		this
	}

	MultiMethod or(MultiMethod other) {
		methods += other.methods
		this
	}

	@Override
	String toString() {
		methods.iterator().join("|")
	}

}
