package com.github.aesteve.vertx.groovy

import groovy.transform.TypeChecked
import io.vertx.groovy.core.Vertx
import io.vertx.groovy.core.eventbus.EventBus

@TypeChecked
class VertxStaticExtension {

	static Vertx getVertx(Vertx self) {
		Vertx.vertx()
	}

}
