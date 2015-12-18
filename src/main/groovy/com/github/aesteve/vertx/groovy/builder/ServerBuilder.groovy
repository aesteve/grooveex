package com.github.aesteve.vertx.groovy.builder

import io.vertx.groovy.core.Vertx
import io.vertx.groovy.core.http.HttpServer

class ServerBuilder {
	Vertx vertx

	HttpServer buildServer(Binding binding = null, File serverFile) {
		if (!binding) binding = new Binding()
		def shell = new GroovyShell(binding)
		ServerDSL dsl = new ServerDSL(vertx: vertx)
		shell.setVariable("server", dsl.&make)
		shell.evaluate serverFile
		dsl.server
	}
}
