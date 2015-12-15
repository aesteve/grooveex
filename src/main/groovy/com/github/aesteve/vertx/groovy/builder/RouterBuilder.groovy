package com.github.aesteve.vertx.groovy.builder

import groovy.transform.TypeChecked
import io.vertx.groovy.core.Vertx
import io.vertx.groovy.ext.web.Router

@TypeChecked
class RouterBuilder {

	Vertx vertx
	Map<String, Closure> extensions = [:]
	
	def extension(String name, Closure clos) {
		extensions[name] = clos
	}
	
	Router call(Binding binding = null, File... routingFiles) {
		if (!binding) binding = new Binding()
		def shell = new GroovyShell(binding)
		RouterDSL routerDSL = new RouterDSL(vertx: vertx, extensions: extensions)
		shell.setVariable("router", routerDSL.&make)
		routingFiles.each { shell.evaluate it as File }
		routerDSL.finish()
		routerDSL.router
	}
	
	Router call(Binding binding = null, Collection<File> routingFiles) {
		if (!binding) binding = new Binding()
		def shell = new GroovyShell(binding)
		RouterDSL routerDSL = new RouterDSL(vertx: vertx, extensions: extensions)
		shell.setVariable("router", routerDSL.&make)
		routingFiles.each { shell.evaluate it as File }
		routerDSL.finish()
		routerDSL.router
	}
	
	Router call(Binding binding = null, InputStream is) {
		if (!is) {
			throw new IllegalArgumentException("Routing file is null")
		}
		if (!binding) binding = new Binding()
		def shell = new GroovyShell(binding)
		RouterDSL routerDSL = new RouterDSL(vertx: vertx, extensions: extensions)
		shell.setVariable("router", routerDSL.&make)
		is.withReader { shell.evaluate(it) }
		routerDSL.finish()
		routerDSL.router
	}
	
	Router call(Closure closure) {
		RouterDSL routerDSL = new RouterDSL(vertx: vertx, extensions: extensions)
		routerDSL.make(closure)
		routerDSL.finish()
		routerDSL.router
	}
	
    static Router buildRouter(Binding binding = null, Vertx vertx, File... routingFiles) {
		if (!binding) binding = new Binding()
        def shell = new GroovyShell(binding)
        RouterDSL routerDSL = new RouterDSL(vertx: vertx)
        shell.setVariable("router", routerDSL.&make)
        routingFiles.each { shell.evaluate it as File }
        routerDSL.finish()
        routerDSL.router
    }

    static Router buildRouter(Binding binding = null, Vertx vertx, InputStream is) {
        if (!is) {
            throw new IllegalArgumentException("Routing file is null")
        }
		if (!binding) binding = new Binding()
        def shell = new GroovyShell(binding)
        RouterDSL routerDSL = new RouterDSL(vertx: vertx)
        shell.setVariable("router", routerDSL.&make)
        is.withReader { shell.evaluate(it) }
        routerDSL.finish()
        routerDSL.router
    }

    static Router buildRouter(Vertx vertx, Closure closure) {
        RouterDSL routerDSL = new RouterDSL(vertx: vertx)
        routerDSL.make(closure)
        routerDSL.finish()
        routerDSL.router
    }
}
