package com.github.aesteve.vertx.groovy.builder

import groovy.transform.TypeChecked
import io.vertx.groovy.core.Vertx

@TypeChecked
class VerticleBuilder {

	Vertx vertx

	VerticlesDSL build(Binding binding = null, File serverFile) {
		if (!binding) binding = new Binding()
		def shell = new GroovyShell(binding)
		VerticlesDSL dsl = new VerticlesDSL(vertx: vertx)
		shell.setVariable("verticles", dsl.&make)
		shell.evaluate serverFile
		dsl
	}

	VerticlesDSL build(Binding binding = null, InputStream stream) {
		if (!binding) binding = new Binding()
		def shell = new GroovyShell(binding)
		VerticlesDSL dsl = new VerticlesDSL(vertx: vertx)
		shell.setVariable("verticles", dsl.&make)
		stream.withReader { shell.evaluate it }
		dsl
	}
}
