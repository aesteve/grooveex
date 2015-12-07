package com.github.aesteve.vertx.groovy

import groovy.transform.TypeChecked
import io.vertx.groovy.core.MultiMap
import io.vertx.groovy.core.eventbus.Message

@TypeChecked
class MessageExtension {
	
	static String getAddress(Message self) {
		self.address()
	}
	
	static String getReplyAddress(Message self) {
		self.replyAddress()
	}
	
	static MultiMap headers(Message self) {
		self.headers()
	}
	
	static Object getBody(Message self) {
		self.body()
	}
	
	static void leftShift(Message self, Object reply) {
		self.reply reply
	}
	
}
