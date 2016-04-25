package com.github.aesteve.vertx.groovy

import groovy.transform.TypeChecked
import io.vertx.groovy.core.MultiMap

@TypeChecked
class MultiMapExtension {

	static Object getAt(MultiMap self, CharSequence key) {
		self.get key.toString()
	}

	static void putAt(MultiMap self, CharSequence key, String value) {
		self.add key.toString(), value
	}

	static void each(MultiMap self, Closure clos) {
		self.names().each {
			clos it, self.get(it)
		}
	}

	static MultiMap minus(MultiMap self, CharSequence toRemove) {
		self.remove toRemove.toString()
	}

	static MultiMap minus(MultiMap self, String toRemove) {
		self.remove toRemove
	}

}
