package com.github.aesteve.vertx.groovy

import com.github.aesteve.vertx.groovy.eventbus.Wire
import groovy.transform.TypeChecked
import io.vertx.groovy.core.eventbus.EventBus

@TypeChecked
class EventBusExtension {

    static Wire getAt(EventBus self, String address) {
        new Wire(eb: self, address: address)
    }

}
