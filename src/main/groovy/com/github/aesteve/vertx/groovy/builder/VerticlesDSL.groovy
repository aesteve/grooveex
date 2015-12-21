package com.github.aesteve.vertx.groovy.builder

import io.vertx.core.Future
import io.vertx.groovy.core.Vertx

class VerticlesDSL {

	Vertx vertx
	Map<String, Map> verticles = [:]
	int deployedVerticles = 0
	int triedVerticles = 0
	int failedVerticles = 0
	Throwable failureCause

	VerticlesDSL make(Closure closure) {
		closure.delegate = this
		closure()
		this
	}

	VerticlesDSL verticle(String name, Closure closure = null) {
		VerticleDSL dsl = new VerticleDSL()
		Map options = [:]
		if (closure) {
			options = dsl.make closure
		}
		verticles[name] = options
		this
	}

	def start(Closure clos) {
		Iterator<Map.Entry<String, Map>> iterator = verticles.iterator()
		deployNext clos, iterator
	}

	private deployNext(Closure closure, Iterator<Map.Entry<String, Map>> iterator) {
		def entry = iterator.next()
		vertx.deployVerticle entry.key, entry.value, { res ->
			if (res.failed()) {
				closure.call Future.failedFuture(res.cause())
			} else {
				if (iterator.hasNext()) {
					deployNext closure, iterator
				} else {
					closure.call Future.succeededFuture()
				}
			}
		}
	}

}
