package com.github.aesteve.vertx.groovy

import groovy.transform.TypeChecked
import io.vertx.groovy.core.shareddata.LocalMap

@TypeChecked
class LocalMapExtension {

	static def getAt(LocalMap self, String name) {
		self.get name
	}

	static def putAt(LocalMap self, String name, Object value) {
		self.put name, value
	}

}
