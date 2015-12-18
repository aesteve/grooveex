package com.github.aesteve.vertx.groovy

import groovy.transform.TypeChecked
import io.vertx.groovy.ext.unit.Async

@TypeChecked
class AsyncExtension {

	static void next(Async self) {
		self.complete()
	}

}
