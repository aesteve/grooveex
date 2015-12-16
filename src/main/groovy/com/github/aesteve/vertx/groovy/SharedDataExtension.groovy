package com.github.aesteve.vertx.groovy

import groovy.transform.TypeChecked
import io.vertx.groovy.core.shareddata.LocalMap
import io.vertx.groovy.core.shareddata.SharedData

@TypeChecked
class SharedDataExtension {
	
	static LocalMap getAt(SharedData self, String name) {
		self.getLocalMap name
	}
	
}
