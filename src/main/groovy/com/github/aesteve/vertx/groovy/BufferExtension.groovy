package com.github.aesteve.vertx.groovy

import groovy.lang.Closure;
import groovy.transform.TypeChecked
import io.vertx.core.Handler
import io.vertx.groovy.core.buffer.Buffer
import io.vertx.groovy.core.streams.Pump
import io.vertx.groovy.core.streams.ReadStream
import io.vertx.groovy.core.streams.WriteStream
import io.vertx.groovy.ext.web.Route;

@TypeChecked
class BufferExtension {

	static Buffer leftShift(Buffer self, Buffer other) {
		self.appendBuffer other
	}
	
	static Buffer leftShift(Buffer self, String other) {
		self.appendString other
	}
	
	static Buffer plus(Buffer self, Buffer other) {
		self.appendBuffer other
	}
	
	static Buffer plus(Buffer self, String other) {
		self.appendString other
	}

	static int compareTo(Buffer self, Object other) {
		if (!(other instanceof Buffer)) {
			return 1
		}
		Buffer otherBuff = (Buffer)other
		self.length().compareTo otherBuff.length()
	}
}
