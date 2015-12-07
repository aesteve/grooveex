package com.github.aesteve.vertx.groovy

import org.codehaus.groovy.runtime.DefaultGroovyMethods;

import io.vertx.core.DeploymentOptions


class MapExtension {

	static Object asType(Map self, Class c) {
		if (c == DeploymentOptions.class) {
			DeploymentOptions options = new DeploymentOptions()
			self.each { key, value -> 
				options[key] = value
			}
			return options
		}
		DefaultGroovyMethods.asType self, c
	}

}
