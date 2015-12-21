package com.github.aesteve.vertx.groovy.builder

import io.vertx.groovy.core.Vertx
import io.vertx.groovy.core.http.HttpServer
import io.vertx.groovy.ext.web.Router

class ServerDSL {

	Vertx vertx
	Map options = [:]
	HttpServer server
	List routingFiles = []
	Binding binding

	HttpServer make(Closure clos) {
		clos.delegate = this
		clos.resolveStrategy = Closure.DELEGATE_FIRST
		clos() // fulfill options
		if (vertx) {
			server = vertx.createHttpServer(options)
			if (!routingFiles.empty) {
				RouterBuilder builder = new RouterBuilder(vertx: vertx)
				Router router = builder binding, routingFiles
				server.requestHandler router.&accept
			}
		}
		server
	}

	def routingFile(String file) {
		routingFiles << file
	}

	def routingFiles(Collection<String> files) {
		routingFiles.addAll files
	}

	def routingFiles(String dir, String pattern = '**/*routes*.groovy') {
		routingFiles.addAll new FileNameFinder().getFileNames(dir, pattern)
	}

	def routingResource(String resource) {
		routingFiles << this.class.getResourceAsStream(resource)
	}

	def routingResources(Collection<String> resources) {
		routingFiles.addAll resources.collect { this.class.getResourceAsStream it }
	}

	def methodMissing(String name, def args) {
		if (args.size() == 1) options[name] = args[0]
	}
}
