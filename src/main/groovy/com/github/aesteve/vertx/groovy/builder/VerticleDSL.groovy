package com.github.aesteve.vertx.groovy.builder

class VerticleDSL {

	Map options = [:]

	Map make(Closure closure) {
		closure.delegate = this
		closure()
		options
	}

	def methodMissing(String name, def args) {
		if (args.size() == 1) options[name] = args[0]
	}

	def propertyMissing(String name, def value) {
		options[name] = value
	}
}
