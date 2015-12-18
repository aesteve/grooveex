package com.github.aesteve.vertx.groovy.builder

import io.vertx.groovy.core.Vertx
import io.vertx.groovy.core.http.HttpServer
import io.vertx.groovy.ext.web.Router

class ServerDSL {

	Vertx vertx
	Map options = [:]
	HttpServer server
	List<String> routingFiles = []

	HttpServer make(Closure clos) {
		clos.delegate = this
		clos.resolveStrategy = Closure.DELEGATE_FIRST
		clos() // fulfill options
		if (vertx) {
			server = vertx.createHttpServer(options)
			if (routingFiles && !routingFiles.empty) {
				RouterBuilder builder = new RouterBuilder(vertx: vertx)
				Router router = builder routingFiles
				server.requestHandler router.&accept
			}
		}
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

	def methodMissing(String name, def args) {
		if (args.size() == 1) options[name] = args[0]
	}
}
