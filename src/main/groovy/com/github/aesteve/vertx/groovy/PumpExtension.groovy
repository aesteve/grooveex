package com.github.aesteve.vertx.groovy

import io.vertx.core.Handler
import io.vertx.groovy.core.streams.Pump
import io.vertx.groovy.ext.web.Route;
import groovy.transform.TypeChecked

@TypeChecked
class PumpExtension {

	static Pump next(Pump self) {
		self.start()
	}
	
	static Pump previous(Pump self) {
		self.stop()
	}

}
