package com.github.aesteve.vertx.groovy.test.http

import io.vertx.groovy.core.buffer.Buffer

class BodyCheckHandler {

	Buffer buffer

	def asType(Class type) {
		buffer?.asType type
	}

}
