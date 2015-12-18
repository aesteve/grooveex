package com.github.aesteve.vertx.groovy.eventbus

import groovy.transform.TypeChecked
import io.vertx.core.Handler
import io.vertx.groovy.core.eventbus.EventBus

@TypeChecked
class Wire {

	@Delegate
	EventBus eb
	String address

	Wire rightShift(Handler handler) {
		eb.consumer address, handler
		this
	}

	Wire leftShift(Object msg) {
		eb.send address, msg
		this
	}

	Wire send(Object msg) {
		eb.send address, msg
		this
	}

	Wire send(Object msg, Map options) {
		eb.send address, msg, options
		this
	}

	Wire send(Object msg, Handler replyHandler) {
		eb.send address, msg, replyHandler
		this
	}

	Wire send(Object msg, Map options, Handler replyHandler) {
		eb.send address, msg, options, replyHandler
		this
	}

	Wire power(Object msg) {
		eb.publish address, msg
		this
	}

	Wire publish(Object msg) {
		eb.publish address, msg
		this
	}

	Wire publish(Object msg, Map options) {
		eb.publish address, msg, options
		this
	}


}
