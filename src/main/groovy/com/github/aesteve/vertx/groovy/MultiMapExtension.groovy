package com.github.aesteve.vertx.groovy

import io.vertx.core.DeploymentOptions
import io.vertx.groovy.core.MultiMap
import org.codehaus.groovy.runtime.DefaultGroovyMethods

class MultiMapExtension {

	static Object getAt(MultiMap self, CharSequence key) {
		self.get key.toString()
	}

	static void putAt(MultiMap self, CharSequence key, Object value) {
		self.add key.toString(), value
	}
}
