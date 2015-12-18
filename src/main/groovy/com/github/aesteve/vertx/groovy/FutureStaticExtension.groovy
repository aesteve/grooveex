package com.github.aesteve.vertx.groovy

import groovy.transform.TypeChecked
import io.vertx.core.Future

@TypeChecked
class FutureStaticExtension {

	static Future getFuture(Future self) {
		Future.future()
	}

}
