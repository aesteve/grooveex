package eventbus

import io.vertx.core.Handler
import io.vertx.groovy.core.eventbus.EventBus

class Wire {
	EventBus eb
	String address
	
	Wire leftShift(Object msg) {
		eb.publish address, msg
		this
	}
	
	Wire rightShift(Handler handler) {
		eb.consumer address, handler
		this
	}
}
