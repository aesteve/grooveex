package com.github.aesteve.vertx.groovy.builder

import com.github.aesteve.vertx.groovy.io.Marshaller
import io.vertx.core.Handler
import io.vertx.core.http.HttpMethod
import io.vertx.groovy.ext.web.Route
import io.vertx.groovy.ext.web.handler.BodyHandler
import io.vertx.groovy.ext.web.handler.SessionHandler

class VertxRoute {

	RouteDSL parent
	HttpMethod method
	String path
	List<Closure> expectations = []
	List<Checker> checkers = []
	List<String> consumes = []
	List<String> produces = []
	Map<String, Marshaller> marshallers = [:]
	List<Handler> extensions = []
	Closure handler

	def call() {
		def router = parent.parent.router
		if (parent.usesBody) {
			BodyHandler bodyHandler = parent.bodyHandler
			if (!bodyHandler) {
				bodyHandler = BodyHandler.create()
			}
			router.route(method, path).handler bodyHandler
		}
		if (parent.sessionStore) {
			router.route(path).handler(SessionHandler.create(parent.sessionStore))
		}
		expectations.each { expectation ->
			router.route(method, path).handler { ctx ->
				try {
					expectation.delegate = ctx
					boolean expected = expectation(ctx)?.asBoolean()
					if (!expected) {
						ctx.fail 400
					} else {
						ctx++
					}
				} catch (all) {
					ctx.fail 400
				}
			}
		}
		checkers.each {
			router.route(method, path).handler it as Handler
		}
		router.route(method, path).handler { ctx ->
			ctx.marshallers = marshallers + parent.marshallers
			ctx++
		}
		extensions.each { clos ->
			router.route(method, path).handler { ctx ->
				clos.delegate = ctx
				clos.call ctx
			}
			parent.pendingExtensions.remove clos
		}
		Route route = router.route method, path
		def consumes = consumes + parent.parent.consumes
		def produces = produces + parent.parent.produces
		consumes.each { route.consumes it }
		produces.each { route.produces it }
		parent.missingMethods.each { methodMissing ->
			parent.callMethodOnRoute(route, methodMissing[0], methodMissing[1])
		}
		if (!parent.blocking) {
			route.handler { context ->
				handler.delegate = context
				handler context
			}
		} else {
			route.blockingHandler { context ->
				handler.delegate = context
				handler context
			}
		}
		parent.routes << route
	}
}
