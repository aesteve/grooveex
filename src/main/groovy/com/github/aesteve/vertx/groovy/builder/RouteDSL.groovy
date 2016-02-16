package com.github.aesteve.vertx.groovy.builder

import com.github.aesteve.vertx.groovy.io.Marshaller
import io.vertx.core.Handler
import io.vertx.core.http.HttpMethod
import io.vertx.groovy.ext.web.Route
import io.vertx.groovy.ext.web.handler.CorsHandler
import io.vertx.groovy.ext.web.sstore.ClusteredSessionStore
import io.vertx.groovy.ext.web.sstore.LocalSessionStore

class RouteDSL {

	RouterDSL parent
	def path
	def bodyHandler
	def usesBody = true
	boolean cookies
	def sessionStore
	private List<Route> routes = []
	private List<List<Object>> missingMethods = []
	boolean blocking
	List<Closure> expectations = []
	List<Checker> checkers = []
	List<String> consumes = []
	List<String> produces = []
	Map<String, Marshaller> marshallers = [:]
	List<Handler> extensions = []
	List<VertxRoute> vertxRoutes = []
	List<Handler> pendingExtensions = []


	def static make(RouterDSL parent, def path, boolean cookies, String parentPath = null) {
		String completePath = ''
		if (parentPath) {
			completePath += parentPath
		}
		completePath += path
		new RouteDSL(path: completePath, parent: parent, cookies: cookies)
	}

	void call(Closure closure) {
		closure.resolveStrategy = Closure.DELEGATE_FIRST
		closure.delegate = this
		closure.call this
		this.finish()
	}

	def cors(String origin, Closure closure = null) {
		CorsHandler cors = CorsHandler.create(origin)
		if (closure) {
			new CorsDSL(cors: cors).make closure
		}
		parent.router.route(path).handler cors
	}

	def expect(Closure expectation) {
		expectations << expectation
	}

	Checker check(Closure check) {
		Checker checker = new Checker(check: check)
		checkers << checker
		checker
	}

	def session(Map options) {
		if (options.store) {
			sessionStore = options.store
		} else if (options.clustered) {
			sessionStore = LocalSessionStore.create(parent.vertx)
		} else {
			sessionStore = ClusteredSessionStore.create(parent.vertx)
		}
	}

	def consumes(String contentType) {
		consumes << contentType
	}

	def produces(String contentType) {
		produces << contentType
	}

	def route(String path, Closure clos) {
		RouteDSL.make(parent, path, cookies, this.path)(clos)
	}

	def all(Closure clos) {
		extensions << clos
		pendingExtensions << clos
	}

	private void finish() {
		vertxRoutes.each { it() }
		def methods = vertxRoutes.collect { it.method } as Set
		pendingExtensions.each { handler ->
			methods.each { method ->
				parent.router.route(method, path).handler { ctx ->
					handler.delegate = ctx
					handler ctx
				}
			}
		}
	}

	def getMarshallers() {
		marshallers + parent.marshallers
	}

	def createRoute(HttpMethod method, String subPath = null, Closure handler) {
		String path = this.path
		if (subPath) path += subPath
		vertxRoutes << new VertxRoute(
			parent: this,
			method: method,
			path: path,
			handler: handler,
			// snapshot from when the route was declared, everything declared after won't be taken into account
			expectations: expectations.findAll(),
			checkers: checkers.findAll(),
			consumes: consumes.findAll(),
			produces: produces.findAll(),
			marshallers: marshallers.findAll { true },
			extensions: extensions.findAll(),
		)
		this.&createRoute.rcurry(subPath, handler)
	}

	def methodMissing(String name, args) {
		Closure extension = parent.extensions[name]
		if (extension) {
			def handler = extension.call(*args)
			if (handler) {
				extensions << handler
				pendingExtensions << handler
			}
			return
		}
		HttpMethod method
		try {
			method = HttpMethod.valueOf name?.toUpperCase()
		} catch (all) {
		}
		if (method) {
			createRoute(method, *args)
		} else {
			if (routes.empty) {
				// delay
				missingMethods << [name, args]
			}
			routes.each { Route route ->
				callMethodOnRoute(route, name, args)
			}
		}
	}

	def authority(String authority) {
		parent.authHandlers.each { auth ->
			parent.router.route(path).handler {
				auth.handle(it)
			}
		}
	}

	def callMethodOnRoute(route, name, args) {
		route."$name"(*args)
	}

}
