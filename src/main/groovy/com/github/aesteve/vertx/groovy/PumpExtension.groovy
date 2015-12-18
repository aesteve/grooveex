package com.github.aesteve.vertx.groovy

import groovy.transform.TypeChecked
import io.vertx.groovy.core.streams.Pump

@TypeChecked
class PumpExtension {

	static Pump next(Pump self) {
		self.start()
	}

	static Pump previous(Pump self) {
		self.stop()
	}

}
