package com.github.aesteve.vertx.groovy.builder

import com.github.aesteve.vertx.groovy.builder.http.MultiMethod
import io.vertx.core.http.HttpMethod
import io.vertx.groovy.core.Vertx
import io.vertx.groovy.ext.web.Router
import org.codehaus.groovy.control.CompilerConfiguration
import org.codehaus.groovy.control.customizers.CompilationCustomizer
import org.codehaus.groovy.control.customizers.ImportCustomizer

class RouterBuilder {

	Vertx vertx
	Map<String, Closure> extensions = [:]
	RouterDSL routerDSL

	def extension(String name, Closure clos) {
		extensions[name] = clos
	}

	static Binding getBindings() {
		new Binding()
	}

	static Binding injectMethods(Binding binding) {
		HttpMethod.values().each {
			binding.setVariable it.name().toLowerCase(), new MultiMethod(it)
		}
		binding
	}

	Router call(Binding binding = null, File... routingFiles) {
		if (!binding) binding = bindings
		injectMethods binding
		def shell = createShell binding
		if (!routerDSL) routerDSL = new RouterDSL(vertx: vertx, extensions: extensions)
		shell.setVariable "router", routerDSL.&make
		routingFiles.each { shell.evaluate it as File }
		routerDSL.finish()
		routerDSL.router
	}

	Router call(Binding binding = null, Collection routingFiles) {
		if (routingFiles.empty) return null
		if (!binding) binding = bindings
		injectMethods binding
		def shell = createShell binding
		if (!routerDSL) routerDSL = new RouterDSL(vertx: vertx, extensions: extensions)
		shell.setVariable("router", routerDSL.&make)
		routingFiles.each { file ->
			if (file instanceof File) {
				shell.evaluate file as File
			}
			if (file instanceof InputStream) {
				file.withReader {
					shell.evaluate it
				}
			}
			if (file instanceof String) {
				shell.evaluate new File(file)
			}
		}
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
		if (!binding) binding = bindings
		injectMethods binding
		def shell = createShell binding
		RouterDSL routerDSL = new RouterDSL(vertx: vertx)
		shell.setVariable "router", routerDSL.&make
		routingFiles.each { shell.evaluate it as File }
		routerDSL.finish()
		routerDSL.router
	}

	static Router buildRouter(Binding binding = null, Vertx vertx, InputStream is) {
		if (!is) {
			throw new IllegalArgumentException("Routing file is null")
		}
		if (!binding) binding = bindings
		injectMethods binding
		def shell = createShell binding
		RouterDSL routerDSL = new RouterDSL(vertx: vertx)
		shell.setVariable "router", routerDSL.&make
		is.withReader { shell.evaluate(it) }
		routerDSL.finish()
		routerDSL.router
	}

	static Router buildRouter(Vertx vertx, Closure closure) {
		RouterDSL routerDSL = new RouterDSL(vertx: vertx)
		routerDSL.make closure
		routerDSL.finish()
		routerDSL.router
	}

	private static GroovyShell createShell(Binding binding) {
		CompilerConfiguration config = new CompilerConfiguration()
		CompilationCustomizer imports = new ImportCustomizer()
		imports.addStaticStars 'io.vertx.core.http.HttpMethod'
		imports.addStaticStars 'io.vertx.core.http.HttpHeaders'
		config.addCompilationCustomizers imports
		new GroovyShell(binding, config)
	}
}
