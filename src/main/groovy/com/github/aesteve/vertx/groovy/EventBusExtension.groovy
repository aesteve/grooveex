package com.github.aesteve.vertx.groovy

import groovy.transform.TypeChecked
import io.vertx.groovy.core.eventbus.EventBus
import eventbus.Wire

@TypeChecked
class EventBusExtension {
	
	static Wire getAt(EventBus self, String address) {
		new Wire(eb:self, address:address)
	}

}
