package com.github.aesteve.vertx.groovy

import groovy.transform.TypeChecked
import io.vertx.groovy.core.streams.WriteStream;

@TypeChecked
class WriteStreamExtension {

	static <T> WriteStream plus(WriteStream self, T data) {
		self.write data
	}

	static <T> WriteStream leftShift(WriteStream self, T data) {
		self.end data
	}

}
